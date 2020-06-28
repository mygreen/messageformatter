package com.github.mygreen.messagebuilder.expression;

import java.util.Map;

import org.springframework.expression.ExpressionException;

/**
 * EL式を評価するためのインタフェース。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface ExpressionEvaluator {

    /**
     * 引数で与えた式を評価します。
     * @param expression 評価対象の式。
     * @param variables 式中で利用可な変数。
     * @return 評価した結果。
     * @throws NullPointerException {@literal expression or values is null.}
     * @throws ExpressionException 式のパースや評価に失敗した場合にスローされます。
     */
    Object evaluate(String expression, Map<String, Object> variables);
}
