package com.github.mygreen.messagebuilder;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    @Test
    void testVarWithAnno() {

        String result = messageBuilder.create("test.varWithAnno")
                .varWithAnno("anno", Entity.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：アノテーション「@Entity」");

    }

    @Test
    void testVarWithEnum() {

        String result = messageBuilder.create("test.varWithEnum")
                .varWithEnum("enum", Role.Admin)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：列挙型「Role#Admin」");

    }

    @Test
    void testVarWithClass() {
        String result = messageBuilder.create("test.varWithClass")
                .varWithClass("class", MessageBuilder.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：クラス「com.github.mygreen.messagebuilder.MessageBuilder」");
    }

    @Test
    void testVarWithClasses() {
        String result = messageBuilder.create("test.varWithClasses")
                .varWithClass("classes", MessageBuilder.class, SpelExpressionEvaluator.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：クラス「com.github.mygreen.messagebuilder.MessageBuilder, com.github.mygreen.messagebuilder.expression.SpelExpressionEvaluator」");
    }

    /**
     * テスト用アノテーション
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Documented
    @interface Entity {

        String name() default "";

    }

    /**
     * テスト用列挙型
     *
     */
    enum Role {

        Normal,
        Admin
    }

}
