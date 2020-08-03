package com.github.mygreen.messageformatter.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * EL式中で使用するためのstaticメソッドのマッピング情報を作成します。
 *
 * @author T.TSUCHIE
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticMethodMapCreator {

    /**
     * ユーティリティクラスからメソッド情報を抽出する。
     * EL式中の独自関数として使用する。
     * @param targetClass 抽出対象のクラス。
     * @return メソッド名とメソッド情報のマップ。
     */
    public static Map<String, Method> create(final Class<?> targetClass) {
        return create(targetClass, null);
    }

    /**
     * ユーティリティクラスからメソッド情報を抽出する。
     * EL式中の独自関数として使用する。
     * @param targetClass 抽出対象のクラス。
     * @param prefix メソッドの接頭語(指定しない場合はnull)
     * @return メソッド名とメソッド情報のマップ。
     */
    public static Map<String, Method> create(final Class<?> targetClass, final String prefix) {

        Map<String, Method> map = new HashMap<>();

        final String p = prefix == null ? "" : prefix;

        ReflectionUtils.doWithMethods(targetClass,
                method -> {
                    ReflectionUtils.makeAccessible(method);
                    map.put(p + method.getName(), method);
                },
                method -> Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()));

        return map;

    }
}
