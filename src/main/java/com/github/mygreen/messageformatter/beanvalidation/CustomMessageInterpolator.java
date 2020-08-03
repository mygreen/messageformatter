package com.github.mygreen.messageformatter.beanvalidation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import com.github.mygreen.messageformatter.MessageInterpolator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * {@link MessageInterpolator}とBeanValidationの{@link javax.validation.MessageInterpolator}をブリッジする。
 * <p>BeanValidationのメッセージ処理をカスタマイズするために利用する。</p>
 * <p>BeanValidationのHibernateValidatorの実装と比べ、追加で以下の変数がメッセージ中で利用が可能。</p>
 * <ul>
 *   <li>{@code propertyPath} : エラーとなったプロパティのパス。ネストしている場合は、{@code address.tel}のようにドットで区切られる。</li>
 *   <li>{@code propertyName} : エラーとなったプロパティの名称。メッセージソースからプロパティ名を取得したもの。メッセージソースに定義されていなければ値は{@code null}となる。</li>
 * </ul>
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class CustomMessageInterpolator implements javax.validation.MessageInterpolator {

    /**
     * メッセージソース。
     */
    @Getter
    private final MessageSource messageSource;

    /**
     * パラメータ付きのメッセージのフォーマッタ。
     */
    @Getter
    private final MessageInterpolator messageInterpolator;

    /**
     * プロパティ名のメッセージコードの候補を生成する。
     */
    @Setter
    @Getter
    private MessageCodesResolver messageCodeResolver = new DefaultMessageCodesResolver();

    /**
     * プロパティ名のメッセージコードの候補をを生成するときのコード名。
     * デフォルトは、{@code propertyName} です。
     * {@code null} を設定すると、コード名は付与されません。
     */
    @Getter
    @Setter
    private String propertyNameCode = "propertyName";

    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        return messageInterpolator.interpolate(messageTemplate,
                createMessageVariables(context, Locale.getDefault()),
                0, new MessageSourceAccessor(messageSource));
    }

    @Override
    public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
        return messageInterpolator.interpolate(messageTemplate,
                createMessageVariables(context, locale),
                0, new MessageSourceAccessor(messageSource, locale));
    }

    /**
     * メッセージ中で利用可能な変数を作成する
     * @param context コンテキスト
     * @param locale ロケール
     * @return メッセージ変数のマップ
     */
    protected Map<String, Object> createMessageVariables(final Context context, final Locale locale) {

        final Map<String, Object> vars = new HashMap<>();

        if(context instanceof MessageInterpolatorContext) {
            MessageInterpolatorContext mic = (MessageInterpolatorContext)context;
            vars.putAll(mic.getMessageParameters());

            // プロパティの情報を設定する
            Path path = mic.getPropertyPath();
            vars.put("propertyPath", path.toString());

            resolvePropertyName(mic, locale).ifPresent(name -> vars.put("propertyName", name));
        }

        final ConstraintDescriptor<?> descriptor = context.getConstraintDescriptor();
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();

            vars.put(attrName, attrValue);
        }

        // 検証対象の値
        vars.computeIfAbsent("validatedValue", key -> context.getValidatedValue());

        // デフォルトのメッセージ
        final String defaultCode = String.format("%s.message", descriptor.getAnnotation().annotationType().getCanonicalName());
        final String defaultMessage = messageSource.getMessage(defaultCode, null, locale);

        vars.put(defaultCode, defaultMessage);

        return vars;

    }

    /**
     * プロパティの名称をメッセージソースから解決する。
     * <p>{@link MessageCodesResolver} で生成したコードを元に取得する。
     * @param context コンテキスト
     * @param locale ロケール
     * @return プロパティの名称。解決できない場合は空を返す。
     */
    protected Optional<String> resolvePropertyName(final MessageInterpolatorContext context, final Locale locale) {

        final String objectName = context.getRootBeanType().getSimpleName();
        final String field = context.getPropertyPath().toString();
        final Class<?> fieldType = context.getValidatedValue() != null ? context.getValidatedValue().getClass() : null;

        String[] codes = messageCodeResolver.resolveMessageCodes(propertyNameCode, objectName, field, fieldType);

        for(String code : codes) {
            try {
                String result = messageSource.getMessage(code, null, locale);
                if(StringUtils.hasLength(result)) {
                    return Optional.of(result);
                }
            } catch(NoSuchMessageException e) {
                /*
                 * MessageSource#setUseCodeAsDefaultMessageの設定によって、
                 * 対応するコードが見つからない場合、例外が発生するか戻り値がnullになったりする。
                 */
                continue;
            }
        }

        return Optional.empty();
    }

}
