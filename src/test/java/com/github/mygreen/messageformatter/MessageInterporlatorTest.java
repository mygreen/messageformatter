package com.github.mygreen.messageformatter;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelEvaluationException;

import com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator;

/**
 * {@link MessageInterpolator}のテスタ
 *
 * @author T.TSUCHIE
 *
 */
class MessageInterporlatorTest {

    private MessageInterpolator interpolator;

    @BeforeEach
    void setUp() {
        this.interpolator = new MessageInterpolator(new SpelExpressionEvaluator());
    }

    @DisplayName("変数のみ - EL式なし")
    @Test
    void testInterpolate_var() {

        String message = "{validatedValue} は、{min}～{max}の範囲で入力してください。";

        int validatedValue = 3;

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("3 は、1～10の範囲で入力してください。");

    }

    @DisplayName("EL式あり - 数値のフォーマット")
    @Test
    void testInterpolate_el01() {

        String message = "${#formatter.format('%1.1f', #validatedValue)}は、${#min}～${#max}の範囲で入力してください。";

        double validatedValue = 3;

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("3.0は、1～10の範囲で入力してください。");

    }

    @DisplayName("EL式あり - 日付のフォーマット")
    @Test
    void testInterpolate_el02() {

        String message = "現在の日付「${#formatter.format('%1$tY/%1$tm/%1$td', #validatedValue)}」は未来日です。";

        Date validatedValue = Timestamp.valueOf("2015-05-01 12:31:49.000");

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("現在の日付「2015/05/01」は未来日です。");

    }

    @DisplayName("EL式中にエスケープ文字あり")
    @Test
    void testInterpolate_escape01() {

        String message = "\\${#formatter.format('%1.1f',#validatedValue)}は、\\{min}～${#max}の範囲で入力してください。";

        double validatedValue = 3;

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("${#formatter.format('%1.1f',#validatedValue)}は、{min}～10の範囲で入力してください。");

    }

    @DisplayName("EL式中にエスケープ文字あり")
    @Test
    void testInterpolate_escape02() {

        String message = "${'Helo World\\}' + #formatter.format('%1.1f', #validatedValue)}は、{min}～${#max}の範囲で入力してください。";

        double validatedValue = 3;

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("Helo World}3.0は、1～10の範囲で入力してください。");

    }

    @DisplayName("メッセージ中の式が途中で終わる場合")
    @Test
    void testInterpolate_lack_end() {

        String message = "${'Helo World\\}' += formatter.format('%1.1f', validatedValue)";

        double validatedValue = 3;

        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);

        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("${'Helo World}' += formatter.format('%1.1f', validatedValue)");
    }

    @DisplayName("再起的にメッセージを評価する - 変数の再起")
    @Test
    void testInterpolate_recursive_vars() {

        String message = "{abc} : {message}";

        Map<String, Object> vars = new HashMap<>();
        vars.put("message", "${1+2}");

        String actual = interpolator.interpolate(message, vars, 0);
        assertThat(actual).isEqualTo("{abc} : 3");

    }

    @DisplayName("再起的にメッセージを評価する - 評価の再帰")
    @Test
    void testInterpolate_recursive_el() {

        String message = "{abc} : ${#value}";

        Map<String, Object> vars = new HashMap<>();
        vars.put("value", "{min}");
        vars.put("min", 3);

        String actual = interpolator.interpolate(message, vars, 0);
        assertThat(actual).isEqualTo("{abc} : 3");

    }

    @DisplayName("再起的にメッセージを評価する - 評価の再帰(最大回数)")
    @Test
    void testInterpolate_recursive_max() {

        String message = "{abc} : ${#value}";

        Map<String, Object> vars = new HashMap<>();
        vars.put("value", "{min}");
        vars.put("min", "{value}");

        String actual = interpolator.interpolate(message, vars, 5);
        assertThat(actual).isEqualTo("{abc} : {min}");

    }

    @DisplayName("変数の値がない場合")
    @Test
    void testInterpolate_no_define_vars() {

        String message = "{rowNumber}";

        Map<String, Object> vars = new HashMap<>();

        String actual = interpolator.interpolate(message, vars, 0);
        assertThat(actual).isEqualTo("{rowNumber}");

    }

    @DisplayName("EL式中の変数の値がない場合 - EL式")
    @Test
    void testInterpolate_no_define_vars2() {

        String message = "${rowNumber}";

        Map<String, Object> vars = new HashMap<>();

        assertThatThrownBy(() -> interpolator.interpolate(message, vars, 0))
            .isInstanceOf(SpelEvaluationException.class);

    }


}
