package com.github.mygreen.messagebuilder.beanvalidation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import com.github.mygreen.messagebuilder.MessageInterpolator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * {@link MessageInterpolator}とBeanValidationの{@link javax.validation.MessageInterpolator}をブリッジする。
 * <p>BeanValidationのメッセージ処理をカスタマイズするために利用する。</p>
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class MessageInterpolatorAdapter implements javax.validation.MessageInterpolator {

    @Getter
    private final MessageSource messageSource;

    @Getter
    private final MessageInterpolator messageInterpolator;

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
     * @return メッセージ変数のマップ
     */
    private Map<String, Object> createMessageVariables(final Context context, final Locale locale) {

        final Map<String, Object> vars = new HashMap<>();

        if(context instanceof MessageInterpolatorContext) {
            MessageInterpolatorContext mic = (MessageInterpolatorContext)context;
            vars.putAll(mic.getMessageParameters());
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
        if(defaultMessage == null) {
            throw new RuntimeException(String.format("not found message code '%s'", defaultCode));
        }

        vars.put(defaultCode, defaultMessage);


        return vars;

    }

}
