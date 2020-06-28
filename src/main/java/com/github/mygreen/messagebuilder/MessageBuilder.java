package com.github.mygreen.messagebuilder;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * メッセージを組み立てフォーマットするクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MessageBuilder {

    /**
     * メッセージソース
     */
    @Getter
    private final MessageSource messageSource;

    /**
     * 名前付き変数のメッセージをフォーマットする
     */
    @Getter
    private final MessageInterpolator messageInterpolator;

    /**
     * メッセージ変数中のクラス型や列挙型のフォーマッター
     */
    @Getter
    private final MessageVariableFormatter messageVariableFormatter;

    /**
     * インスタンスを作成します。
     * @param messageSource メッセージソース
     * @param messageInterpolator 名前付き変数のメッセージのフォーマッタです。
     */
    public MessageBuilder(@NonNull MessageSource messageSource, @NonNull MessageInterpolator messageInterpolator) {
        this(messageSource, messageInterpolator, new MessageVariableFormatter());
    }

    /**
     * インスタンスを作成します。
     * @param messageSource メッセージソース
     * @param messageInterpolator 名前付き変数のメッセージのフォーマッタです。
     * @param messageVariableFormatter メッセージ変数中のクラス型や列挙型のフォーマッターです
     */
    public MessageBuilder(@NonNull MessageSource messageSource, @NonNull MessageInterpolator messageInterpolator,
            @NonNull MessageVariableFormatter messageVariableFormatter) {
        this.messageSource = messageSource;
        this.messageInterpolator = messageInterpolator;
        this.messageVariableFormatter = messageVariableFormatter;
    }

    /**
     * メッセージコード（キー）を指定し、メッセージの組み立ての開始します。
     * @param code メッセージコード（メッセージキー）
     * @return 変数を組み立てるためのビルダー。
     */
    public Builder create(final String code) {
        return create(code, null);
    }

    /**
     * メッセージコード（キー）を指定し、メッセージの組み立ての開始します。
     * @param code メッセージコード（メッセージキー）
     * @param locale ロケールを指定します。
     * @return 変数を組み立てるためのビルダー。
     */
    public Builder create(final String code, final Locale locale) {
        Assert.hasLength(code, "code should not be empty.");

        return new Builder(messageSource, messageInterpolator, messageVariableFormatter, code, locale);
    }

    /**
     * メッセージ中に埋め込む変数を組み立てるビルダークラス。
     *
     * @author T.TSUCHIE
     *
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private final MessageSource messageSource;

        private final MessageInterpolator messageInterpolator;

        private final MessageVariableFormatter messageVariableFormatter;

        private final String code;

        private final Locale locale;

        /**
         * 組み立てた変数のマップ
         */
        private Map<String, Object> vars = new HashMap<>();

        /**
         * メッセージ変数を追加する。
         * @param key 変数名
         * @param value 値
         * @return 自身のインスタンス
         */
        public Builder var(final String key, final Object value) {
            vars.put(key, value);
            return this;
        }

        /**
         * メッセージ変数として配列を追加する。
         * @param key 変数名
         * @param values 値
         * @return 自身のインスタンス
         */
        public Builder varWithArrays(final String key, final Object... values) {
            vars.put(key, values);
            return this;
        }

        /**
         * メッセージ変数としてアノテーション名を追加する。
         * @param key 変数名
         * @param annoClass アノテーションのクラス名
         * @return 自身のインスタンス
         */
        public Builder varWithAnno(final String key, final Class<? extends Annotation> annoClass) {
            return var(key, messageVariableFormatter.formatWithAnno(annoClass));
        }

        /**
         * メッセージ変数としてクラス名を追加する。
         * <p>クラス名は、FQCNの形式</p>
         * @param key 変数名
         * @param clazz クラスタイプ
         * @return 自身のインスタンス
         */
        public Builder varWithClass(final String key, final Class<?> clazz) {
            return var(key, messageVariableFormatter.formatWithClass(clazz));
        }

        /**
         * メッセージ変数としてクラス名を追加する。
         * <p>クラス名は、FQCNの形式</p>
         * @param key 変数名
         * @param classes クラスタイプ
         * @return 自身のインスタンス
         */
        public Builder varWithClass(final String key, final Class<?>... classes) {
            return var(key, messageVariableFormatter.formatWithClasses(classes));
        }

        /**
         * メッセージ変数として列挙型を追加する。
         * @param key 変数名
         * @param enums 列挙型の要素
         * @return 自身のインスタンス
         */
        public Builder varWithEnum(final String key, final Enum<?> enums) {
            return var(key, messageVariableFormatter.formatWithEnum(enums));

        }

        /**
         * メッセージをフォーマットして値を取得します。
         * <p>変換したメッセージに対しても再帰的に処理しません。</p>
         * @return フォーマットしたメッセージ
         * @throws IllegalArgumentException 指定したメッセージコードが見つからない場合
         */
        public String format() {

            final MessageSourceAccessor msa = new MessageSourceAccessor(messageSource, locale);
            final String message = msa.getMessage(code);
            return messageInterpolator.interpolate(message, vars, msa);
        }

        /**
         * メッセージをフォーマットして値を取得します。
         * <p>変換したメッセージに対しても再帰的に処理します</p>
         * @return フォーマットしたメッセージ
         * @throws NoSuchMessageException 指定したメッセージコードが見つからない場合
         */
        public String formatRecursively() {

            final MessageSourceAccessor msa = new MessageSourceAccessor(messageSource, locale);
            final String message = msa.getMessage(code);
            return messageInterpolator.interpolate(message, vars, 0, msa);
        }

        /**
         * メッセージをフォーマットして値を取得します。
         * <p>変換したメッセージに対しても再帰的に処理します</p>
         * @param maxRecursion メッセージを再帰的に処理する最大回数。0以下を指定すると再帰回数の制限はありません。
         * @return フォーマットしたメッセージ
         * @throws NoSuchMessageException 指定したメッセージコードが見つからない場合
         */
        public String formatRecursively(final int maxRecursion) {

            final MessageSourceAccessor msa = new MessageSourceAccessor(messageSource, locale);
            final String message = msa.getMessage(code);
            return messageInterpolator.interpolate(message, vars, maxRecursion, msa);
        }

    }

}
