package com.github.mygreen.messageformatter.expression;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationException;


/**
 * {@link JexlExpressionEvaluator}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
class JexlExpressionEvaluatorTest {

    private JexlExpressionEvaluator expressionEvaluator;

    @BeforeEach
    void setUp() throws Exception {
        this.expressionEvaluator = new JexlExpressionEvaluator();
    }

    @DisplayName("数式を評価する")
    @Test
    void testEvaluate_num() {

        String expression = "1 + add";
        Map<String, Object> variables = Map.of("add", 20);

        Object result = expressionEvaluator.evaluate(expression, variables);
        assertThat(result).isEqualTo(21);

    }

    @DisplayName("空判定を式評価する")
    @Test
    void testEvaluate_empty() {

        String expression = "empty(label) ? '空です' : label";

        {
            Map<String, Object> variables = new HashMap<>();

            Object result = (String) expressionEvaluator.evaluate(expression, variables);
            assertThat(result).isEqualTo("空です");
        }

        {
            Map<String, Object> variables = new HashMap<>();

            variables.put("label", "Hello world.");
            Object result = (String) expressionEvaluator.evaluate(expression, variables);
            assertThat(result).isEqualTo("Hello world.");
        }

    }

    @DisplayName("式の評価に失敗した場合 - 式中の変数が存在しない")
    @Test
    void testEvaluation_fail() {

        String expression = "aaa ?  label";

        Map<String, Object> variables = new HashMap<>();

        assertThatThrownBy(() -> expressionEvaluator.evaluate(expression, variables))
            .isInstanceOf(EvaluationException.class);

    }

    @DisplayName("名前空間付きの関数の呼び出し")
    @Test
    void testEvaluation_function() {

        String expression = "f:join(array, ', ')";

        Map<String, Object> variables = new HashMap<>();
        variables.put("array", new int[]{1,2,3});

        Object result = expressionEvaluator.evaluate(expression, variables);
        assertThat(result).isEqualTo("1, 2, 3");

    }
}
