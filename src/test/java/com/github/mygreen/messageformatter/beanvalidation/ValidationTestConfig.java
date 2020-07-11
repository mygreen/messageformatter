package com.github.mygreen.messageformatter.beanvalidation;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.mygreen.messageformatter.MessageInterpolator;
import com.github.mygreen.messageformatter.beanvalidation.CustomMessageInterpolator;
import com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator;

@Configuration
public class ValidationTestConfig {

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("test_messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    @Description("Validator of BeanValidation")
    @Bean
    public LocalValidatorFactoryBean validator() {
        final LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();

        // メッセージの処理を独自の形式に変更する。
        CustomMessageInterpolator messageInterpolator = new CustomMessageInterpolator(
                messageSource(),
                new MessageInterpolator(new SpelExpressionEvaluator()));

        factory.setMessageInterpolator(messageInterpolator);

        return factory;
    }

}
