package com.github.mygreen.messageformatter.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * EL式中で利用可能なEL関数。
 *
 * @author T.TSUCHIE
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomFunctions {

    /**
     * 文字列がnullの場合に空文字に変換する。
     * <pre class="highlight"><code class="java">
     *     CustomFunctions.defaultString(null) = ""
     *     CustomFunctions.defaultString("") = ""
     *     CustomFunctions.defaultString("abc") = "abc"
     * </code></pre>
     *
     * @param text 判定対象の文字列
     * @return 非nullの場合は、引数の値をそのまま返す。
     */
    public static String defaultString(final CharSequence text) {
        if(text == null) {
            return "";
        }

        return text.toString();
    }

    /**
     * 配列やコレクションの値を結合する。
     * @param value 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    @SuppressWarnings("rawtypes")
    public static String join(final Object value, final String delimiter) {

        if(value == null) {
            return "";
        }

        if(value instanceof Collection) {
            return join((Collection)value, delimiter);

        } else if(value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            if(componentType.equals(Integer.TYPE)) {
                return join((int[])value, delimiter);
            } else {
                return join((Object[])value, delimiter);
            }

        }

        throw new IllegalArgumentException(String.format("arg type ('%s') is not support.", value.getClass()));

    }

    /**
     * int型の配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    private static String join(final int[] array, final String delimiter) {

        if(array == null || array.length == 0) {
            return "";
        }

        String value = Arrays.stream(array)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(defaultString(delimiter)));

        return value;
    }

    /**
     * 配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    private static String join(final Object[] array, final String delimiter) {

        if(array == null || array.length == 0) {
            return "";
        }

        String value = Arrays.stream(array)
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));

        return value;
    }

    /**
     * コレクションの値を結合する。
     * @param collection 結合対象のコレクション
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象のコレクションがnulの場合、空文字を返す。
     */
    private static String join(final Collection<?> collection, final String delimiter) {

        if(collection == null || collection.isEmpty()) {
            return "";
        }

        String value = collection.stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));

        return value;
    }

    /**
     * 引数が空かどうか判定する。
     * <p>文字列の場合は長さが0かどうか判定する。</p>
     *
     * @param value 判定対象の値
     * @return 空の場合はtrueを返します。
     */
    @SuppressWarnings("rawtypes")
    public static boolean empty(final Object value) {

        if(value == null) {
            return true;
        }

        if(value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;

        } else if(value instanceof Collection) {
            return ((Collection) value).isEmpty();

        } else if(value instanceof Map) {
            return ((Map) value).isEmpty();

        } else if(value.getClass().isArray()) {
            return ((Object[]) value).length == 0;
        }

        return value.toString().isEmpty();

    }

    /**
     * 引数の値のサイズを取得します。
     * <p>文字列の場合は文字長を返します。</p>
     *
     * @param value 取得対象の値。
     * @return nullの場合は0を返します。
     */
    @SuppressWarnings("rawtypes")
    public static int size(final Object value) {

        if(value == null) {
            return 0;
        }

        if(value instanceof CharSequence) {
            return ((CharSequence) value).length();

        } else if(value instanceof Collection) {
            return ((Collection) value).size();

        } else if(value instanceof Map) {
            return ((Map) value).size();

        } else if(value.getClass().isArray()) {
            return ((Object[]) value).length;
        }

        return value.toString().length();

    }

}
