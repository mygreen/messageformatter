package com.github.mygreen.messageformatter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * アノテーション型や列挙型などの特定のパラメータをフォーマットします。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ParameterFormatter {

    /**
     * アノテーションをフォーマットします。
     * @param annoClass アノテーションのクラス
     * @return フォーマットした結果。
     */
    public String formatWithAnno(final Class<? extends Annotation> annoClass) {
        return "@" + annoClass.getSimpleName();
    }

    /**
     * クラス名をフォーマットします。
     * @param clazz クラス。
     * @return フォーマットした結果。
     */
    public String formatWithClass(final Class<?> clazz) {

        final String className;
        if(clazz.isArray()) {
            // 配列の場合
            Class<?> elementType = clazz.getComponentType();
            className = elementType.getName() + "[]";

        } else {
            className = clazz.getName();

        }

        return className;
    }

    /**
     * 複数のクラス名をフォーマットします。
     * @param classes クラスの配列
     * @return フォーマットした結果
     */
    public List<String> formatWithClasses(final Class<?>... classes) {

        List<String> list = new ArrayList<>();
        for(Class<?> clazz : classes) {
            final String className;
            if(clazz.isArray()) {
                // 配列の場合
                Class<?> elementType = clazz.getComponentType();
                className = elementType.getName() + "[]";

            } else {
                className = clazz.getName();
            }

            list.add(className);
        }

        return list;

    }

    /**
     * 列挙型をフォーマットします。
     * @param enums 列挙型の要素
     * @return フォーマットした結果
     */
    public String formatWithEnum(final Enum<?> enums) {
        return enums.getClass().getSimpleName() + "#" + enums.name();
    }

}
