package com.github.mygreen.messagebuilder.expression;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.springframework.expression.EvaluationException;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * JEXLによる式を評価する {@link ExpressionEvaluator} の実装。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class JexlExpressionEvaluator implements ExpressionEvaluator {

    /**
     * パースしたEL式のキャッシュ
     */
    protected final ObjectCache<String, Expression> expressionCache = new ObjectCache<>();

    /**
     * JEXLの処理エンジン。
     */
    @Getter
    private final JexlEngine jexlEngine;

    /**
     * EL式の処理エンジンを指定してインスタンスを作成します。
     * @param jexlEngine JEXLの処理エンジン。
     */
    public JexlExpressionEvaluator(@NonNull JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }

    /**
     * 標準設定の処理エンジンを元にインスタンスを作成します。
     * <p>プレフィックス{@code f} でカスタム関数 {@link CustomFunctions} が登録されています。</p>
     */
    public JexlExpressionEvaluator() {
        JexlEngine engine = new JexlEngine();

        // EL式中で使用可能な関数の登録
        Map<String, Object> functions = new HashMap<>();
        functions.put("f", CustomFunctions.class);
        engine.setFunctions(functions);

        this.jexlEngine = engine;
    }

    @Override
    public Object evaluate(@NonNull String expression, @NonNull Map<String, Object> variables) {

        Assert.hasLength(expression, "expression should not be empty.");

        if(log.isDebugEnabled()) {
            log.debug("Evaluating JEXL expression: {}", expression);
        }

        try {
            Expression expr = expressionCache.get(expression);
            if (expr == null) {
                expr = jexlEngine.createExpression(expression);
                expressionCache.put(expression, expr);
            }

            return expr.evaluate(new MapContext((Map<String, Object>) variables));

        } catch(Exception ex) {
            throw new EvaluationException(String.format("Evaluating [%s] script with JEXL failed.", expression), ex);
        }
    }
}
