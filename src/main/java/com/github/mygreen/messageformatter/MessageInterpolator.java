package com.github.mygreen.messageformatter;

import java.util.Deque;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import com.github.mygreen.messageformatter.expression.ExpressionEvaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 名前付き変数のメッセージをフォーマットするクラス。
 * <p><code>{...}</code>の場合、変数を単純に置換する。</p>
 * <p><code>${...}</code>の場合、EL式を利用し処理する。</p>
 * <p>文字'$', '{', '}'は特殊文字のため、<code>\</code>でエスケープを行う。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
@RequiredArgsConstructor
public class MessageInterpolator {

    /**
     * EL式を評価する処理
     */
    private final ExpressionEvaluator expressionEvaluator;

    /**
     * メッセージを引数varsで指定した変数で補完する。
     *
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars) {
        return parse(message, vars, false, 0, 0, null);
    }

    /**
     * メッセージを引数varsで指定した変数で補完する。
     *
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param messageSource メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, final MessageSourceAccessor messageSource) {
        return parse(message, vars, false, 0, 0, messageSource);
    }

    /**
     * メッセージを引数varsで指定した変数で補完する。
     * <p>変換したメッセージに対しても再帰的に処理します。</p>
     *
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param maxRecursion 再帰的にメッセージを処理する最大回数。0以下を指定したときは再帰回数の制限はありません。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, int maxRecursion) {
        return parse(message, vars, true, maxRecursion, 0, null);
    }

    /**
     * メッセージを引数varsで指定した変数で補完する。
     * <p>変換したメッセージに対しても再帰的に処理します。</p>
     * <p>{@link MessageSourceAccessor}を指定した場合、メッセージ中の変数をメッセージコードとして解決します。
     *
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param maxRecursion 再帰的にメッセージを処理する最大回数。0以下を指定したときは再帰回数の制限はありません。
     * @param messageSource メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, int maxRecursion,
            final MessageSourceAccessor messageSource) {
        return parse(message, vars, true, maxRecursion, 0, messageSource);
    }

    /**
     * メッセージをパースし、変数に値を差し込み、EL式を評価する。
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか。
     * @param maxRecursion 再帰的にメッセージを処理する最大回数。0以下を指定したときは再帰回数の制限はありません。
     * @param recursiveCount 現在の再帰回数
     * @param messageSource メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    protected String parse(final String message, final Map<String, ?> vars, boolean recursive, int maxRecursion,
            int recursiveCount, final MessageSourceAccessor messageSource) {

        // 評価したメッセージを格納するバッファ。
        final StringBuilder sb = new StringBuilder(message.length());

        /*
         * 変数とEL式を解析する際に使用する、スタック変数。
         * 式の開始が現れたらスタックに積み、式の終了が現れたらスタックから全てを取り出す。
         * スタックに積まれるのは、1つ文の変数またはEL式。
         */
        final Deque<String> stack = new LinkedList<>();

        final int length = message.length();

        for(int i=0; i < length; i++) {
            final char c = message.charAt(i);

            if(StackUtils.equalsTopElement(stack, "\\")) {
                // 直前の文字がエスケープ文字の場合、エスケープ文字として結合する。
                String escapedChar = StackUtils.popup(stack) + c;

                if(!stack.isEmpty()) {
                    // 取り出した後もスタックがある場合は、式の途中であるため、再度スタックに積む。
                    stack.push(escapedChar);

                } else {
                    // 取り出した後にスタックがない場合は、エスケープを解除して通常の文字として積む。
                    sb.append(c);

                }

            } else if(c == '\\') {
                // エスケープ文字の場合はスタックに積む。
                stack.push(String.valueOf(c));

            } else if(c == '$') {
                stack.push(String.valueOf(c));

            } else if(c == '{') {

                if(!stack.isEmpty() && !StackUtils.equalsAnyBottomElement(stack, new String[]{"$", "{"})) {
                    // スタックの先頭が式の開始形式でない場合
                    throw new MessageParseException(message, "expression not start with '{' or '$'");

                } else {
                    stack.push(String.valueOf(c));
                }


            } else if(c == '}') {

                if(StackUtils.equalsAnyBottomElement(stack, new String[]{"{", "$"})) {
                    // 式の終わりの場合は、式を取り出し評価する。
                    String expression = StackUtils.popupAndConcat(stack) + c;

                    // エスケープを解除する
                    expression = removeEscapeChar(expression, '\\');

                    String result = evaluate(expression, vars, recursive, maxRecursion, recursiveCount, messageSource);
                    sb.append(result);

                } else {
                    sb.append(c);

                }

            } else {

                if(stack.isEmpty()) {
                    sb.append(c);

                } else {
                    stack.push(String.valueOf(c));
                }

            }

        }

        if(!stack.isEmpty()) {
            String val = StackUtils.popupAndConcat(stack);
            val = removeEscapeChar(val, '\\');
            sb.append(val);
        }

        return sb.toString();
    }

    private String evaluate(final String expression, final Map<String, ?> values, final boolean recursive,
            final int maxRecursion, final int recursiveCount, final MessageSourceAccessor messageSource) {

        if(expression.startsWith("{")) {
            // 変数の置換の場合
            final String varName = expression.substring(1, expression.length()-1);

            if(values.containsKey(varName)) {
                // 該当するキーが存在する場合
                final Object value = values.get(varName);
                final String eval = (value == null) ? "" : value.toString();
                if(!eval.isEmpty() && recursivable(recursive, maxRecursion, recursiveCount, eval)) {
                    return parse(eval, values, recursive, maxRecursion, recursiveCount+1, messageSource);
                } else {
                    return eval;
                }

            } else if(messageSource != null) {
                // メッセージコードをとして解決をする。
                String eval;
                try {
                    eval = messageSource.getMessage(varName);
                } catch(NoSuchMessageException e) {
                    // 該当するキーが存在しない場合は、値をそのまま返す。
                    return String.format("{%s}", varName);
                }

                if(recursivable(recursive, maxRecursion, recursiveCount, eval)) {
                    return parse(eval, values, recursive, maxRecursion, recursiveCount+1, messageSource);
                } else {
                    return eval;
                }

            } else {
                // 該当するキーが存在しない場合は、値をそのまま返す。
                return String.valueOf(expression);
            }

        } else if(expression.startsWith("${")) {
            // EL式で処理する
            final String expr = expression.substring(2, expression.length()-1);
            final String eval = evaluateExpression(expr, values);
            if(recursivable(recursive, maxRecursion, recursiveCount, eval)) {
                return parse(eval, values, recursive, maxRecursion, recursiveCount+1, messageSource);
            } else {
                return eval;
            }

        }

        throw new MessageParseException(expression, "not support expression.");

    }

    /**
     * 現在の再帰回数が最大回数に達しているかどうか。
     * @param recursive 再帰的に処理するかどうか。
     * @param maxRecursion 最大再帰回数
     * @param currentCount 再帰回数
     * @param message 再帰対象のメッセージ
     * @return 最大再帰回数を超えていなければfalseを返す。
     */
    private boolean recursivable(final boolean recursive, final int maxRecursion, final int currentCount,
            String message) {

        if(!recursive) {
            return false;
        }

        if(maxRecursion <= 0) {
            // 再帰回数の制限なし。
            return true;
        }

        if(currentCount <= maxRecursion) {
            return true;
        }

        log.warn("Over recursive count : currentCount={}, maxCount={}, message={}.", currentCount, maxRecursion, message);

        return false;

    }

    /**
     * EL式を評価する。
     * @param expression EL式
     * @param values EL式中の変数。
     * @return 評価した式。
     */
    protected String evaluateExpression(final String expression, final Map<String, ?> values) {

        final Map<String, Object> context = new LinkedHashMap<>();
        context.putAll(values);

        // フォーマッターの追加
        context.computeIfAbsent("formatter", key -> new Formatter());

        /*
         * SpELで存在しない変数名の場合、nullが帰ってくるため、null判定を行う。
         */
        Object eval = expressionEvaluator.evaluate(expression, context);
        String value = eval == null ? "" : eval.toString();

        if(log.isTraceEnabled()) {
            log.trace("evaluate expression language: expression='{}' ===> value='{}'", expression, value);
        }

        return value;
    }

    /**
     * エスケープ文字を除去した文字列を取得する。
     * @param str
     * @param escapeChar
     * @return
     */
    private static String removeEscapeChar(final String str, final char escapeChar) {

        if(str == null || str.isEmpty()) {
            return str;
        }

        final String escapeStr = String.valueOf(escapeChar);
        StringBuilder sb = new StringBuilder();

        LinkedList<String> stack = new LinkedList<>();

        final int length = str.length();
        for(int i=0; i < length; i++) {
            final char c = str.charAt(i);

            if(StackUtils.equalsTopElement(stack, escapeStr)) {
                // スタックの一番上がエスケープ文字の場合
                StackUtils.popup(stack);
                sb.append(c);

            } else if(c == escapeChar) {
                // スタックに積む
                stack.push(String.valueOf(c));

            } else {
                sb.append(c);
            }

        }

        if(!stack.isEmpty()) {
            sb.append(StackUtils.popupAndConcat(stack));
        }

        return sb.toString();

    }

}
