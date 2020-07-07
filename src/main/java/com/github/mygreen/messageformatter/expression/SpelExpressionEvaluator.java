package com.github.mygreen.messagebuilder.expression;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * SpELいよる式を評価する {@link ExpressionEvaluator} の実装。
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class SpelExpressionEvaluator implements ExpressionEvaluator {

    /**
     * パースしたEL式のキャッシュ
     */
    protected final ObjectCache<String, Expression> expressionCache = new ObjectCache<>();

    /**
     * EL式のパーサ
     */
    @Getter
    private final ExpressionParser expressionParser;

    /**
     * EL式中で利用可能なカスタム関数の情報
     */
    @Getter
    private final Map<String, Method> customFunctions;

    /**
     * EL式中で使用するカスタム関数を指定してインスタンスを作成します。
     * @param expressionParser EL式のパーサ
     * @param customFunctions EL式中で使用するカスタム関数
     */
    public SpelExpressionEvaluator(@NonNull ExpressionParser expressionParser, @NonNull Map<String, Method> customFunctions) {
        this.expressionParser = expressionParser;
        this.customFunctions = new ConcurrentHashMap<>(customFunctions);
    }

    /**
     * インスタンスを作成します。
     * @param expressionParser EL式のパーサ
     */
    public SpelExpressionEvaluator(@NonNull ExpressionParser expressionParser) {
        this(expressionParser, Collections.emptyMap());
    }

    /**
     * 標準設定のEL式パーサを元にインスタンスを作成します。
     * <p>カスタム関数 {@link CustomFunctions} が登録されています。</p>
     */
    public SpelExpressionEvaluator() {
        this(new SpelExpressionParser(), StaticMethodMapCreator.create(CustomFunctions.class));
    }

    @Override
    public Object evaluate(@NonNull final String expression, @NonNull final Map<String, Object> variables) {

        Assert.hasLength(expression, "expression should not be empty.");

        if(log.isDebugEnabled()) {
            log.debug("Evaluating SpEL expression: {}", expression);
        }

        Expression expr = expressionCache.get(expression);
        if(expr == null) {
            expr = expressionParser.parseExpression(expression);
            expressionCache.put(expression, expr);
        }

        final EvaluationContext context = createEvaluationContext(variables);
        return expr.getValue(context);

    }

    /**
     * コンテキストを作成します。
     * @param variables 式中で利用する変数
     * @return SpELのコンテキスト
     */
    protected EvaluationContext createEvaluationContext(final Map<String, Object> variables) {

        final StandardEvaluationContext  context = new StandardEvaluationContext();

        // 変数の登録
        context.setVariables(variables);

        // カスタム関数の登録
        customFunctions.forEach((k, v) -> context.registerFunction(k, v));

        return context;
    }

}
