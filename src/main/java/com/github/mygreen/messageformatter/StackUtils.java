package com.github.mygreen.messageformatter;

import java.util.Deque;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;


/**
 * {@link Deque}に対するユーティリティクラス。
 *
 * @author T.TSUCHIE
 *
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class StackUtils {

    /**
     *  スタックの最後の要素（一番下の要素）が引数で指定した文字列の何れかと等しいかどうか比較する。
     * @param stack スタック
     * @param strs 比較対象の文字列の配列。
     * @return スタックの最後の要素が引数 {@code strs} の何れかと等しいとき {@code true} を返す。
     */
    public static boolean equalsAnyBottomElement(
            @NonNull final Deque<String> stack, @NonNull final String[] strs) {

        if(stack.isEmpty()) {
            return false;
        }

        final String bottom = stack.peekLast();
        for(String str : strs) {
            if(str.equals(bottom)) {
                return true;
            }
        }

        return false;

    }

    /**
     * スタックの先頭の要素（一番上の要素）が引数で指定した文字列と等しいかどうか比較する。
     * @param stack スタック
     * @param str 比較対象の文字列
     * @return スタックの先頭の要素が引数 {@code str} と等しいとき {@code true} を返す。
     */
    public static  boolean equalsTopElement(final Deque<String> stack, final String str) {

        if(stack.isEmpty()) {
            return false;
        }

        return stack.peekFirst().equals(str);

    }

    /**
     * スタックの値を先頭から全て取り出し、文字列として結合する。
     * @param stack スタック
     * @return スタックの要素を結合した文字列。
     */
    public static String popupAndConcat(final Deque<String> stack) {

        StringBuilder value = new StringBuilder();

        while(!stack.isEmpty()) {
            value.append(stack.pollLast());
        }

        return value.toString();

    }

    /**
     * スタックから先頭の値を取り出す。
     * @param stack スタック
     * @return スタックが空の場合は空文字を返す。
     */
    public static String popup(final Deque<String> stack) {

        if(stack.isEmpty()) {
            return "";
        }

        return stack.pollFirst();
    }

}
