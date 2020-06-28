package com.github.mygreen.messagebuilder;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.mygreen.messagebuilder.expression.SpelExpressionEvaluator;

/**
 * {@link MessageBuilder}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MessageBuilderTest {

    private MessageBuilder messageBuilder;

    @BeforeEach
    void setUp() throws Exception {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("test_messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        MessageInterpolator messageInterpolator = new MessageInterpolator(new SpelExpressionEvaluator());

        this.messageBuilder = new MessageBuilder(messageSource, messageInterpolator);
    }

    @Test
    void testFormat() {

        String result = messageBuilder.create("test.message01")
                .var("validatedValue", 3.1)
                .var("min", 1)
                .var("max", 10)
                .format();

        assertThat(result).isEqualTo("メッセージ：3.10は、1～10の範囲で入力してください。");

    }

    @Test
    void testFormatRecursively() {

        String result = messageBuilder.create("test.recursive")
                .var("value", "{min}")
//                .var("min", 3)    // min はプロパティファイルに定義
                .formatRecursively();

        assertThat(result).isEqualTo("再帰的なメッセージ：{abc}=3");

    }

    @Test
    void testFormatRecursivelyMax() {

        String result = messageBuilder.create("test.recursiveMax")
                .var("value", "{max}")
//                .var("max", "{value}")    // max はプロパティファイルに定義
                .formatRecursively(5);

        assertThat(result).isEqualTo("再帰的なメッセージ：{abc}={max}");

    }
}
