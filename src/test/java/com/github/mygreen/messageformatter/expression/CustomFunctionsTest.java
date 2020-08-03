package com.github.mygreen.messageformatter.expression;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;


/**
 * {@link CustomFunctions}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
class CustomFunctionsTest {

    /**
     * {@link CustomFunctions#defaultString(CharSequence)}
     */
    @Test
    void testDefaultString() {

        assertThat(CustomFunctions.defaultString(null)).isEqualTo("");
        assertThat(CustomFunctions.defaultString("")).isEqualTo("");
        assertThat(CustomFunctions.defaultString("abc")).isEqualTo("abc");

    }

    /**
     * {@link CustomFunctions#join(int[], String)}
     */
    @Test
    void testJoin_int_array() {

        assertThat(CustomFunctions.join((int[])null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new int[]{}, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new int[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(CustomFunctions.join(new int[]{1,2,3}, null)).isEqualTo("123");

    }

    /**
     * {@link CustomFunctions#join(Object[], String)}
     */
    @Test
    void testJoin_object_array() {

        assertThat(CustomFunctions.join((Object[])null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new Object[]{}, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new Object[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(CustomFunctions.join(new Object[]{1,2,3}, null)).isEqualTo("123");

    }

    /**
     * {@link CustomFunctions#join(java.util.Collection, String)}
     */
    @Test
    void testJoin_collection() {

        Collection<Integer> input = Arrays.asList(1000, 2000, 3000);

        assertThat(CustomFunctions.join((Collection<Integer>)null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(Collections.emptyList(), ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(input, ", ")).isEqualTo("1000, 2000, 3000");
        assertThat(CustomFunctions.join(input, null)).isEqualTo("100020003000");

    }

    /**
     * {@link CustomFunctions#empty(Object)}
     */
    @Test
    void testEmpty() {

        assertThat(CustomFunctions.empty(null)).isTrue();

        assertThat(CustomFunctions.empty("")).isTrue();
        assertThat(CustomFunctions.empty("a")).isFalse();

        assertThat(CustomFunctions.empty(Collections.emptyList())).isTrue();
        assertThat(CustomFunctions.empty(List.of("a"))).isFalse();

        assertThat(CustomFunctions.empty(Map.of())).isTrue();
        assertThat(CustomFunctions.empty(Map.of("a", 1))).isFalse();

        assertThat(CustomFunctions.empty(new Object[] {})).isTrue();
        assertThat(CustomFunctions.empty(new Object[] {"1"})).isFalse();

    }

    /**
     * {@link CustomFunctions#empty(Object)}
     */
    @Test
    void testSize() {

        assertThat(CustomFunctions.size(null)).isEqualTo(0);

        assertThat(CustomFunctions.size("")).isEqualTo(0);
        assertThat(CustomFunctions.size("abc")).isEqualTo(3);

        assertThat(CustomFunctions.size(Collections.emptyList())).isEqualTo(0);
        assertThat(CustomFunctions.size(List.of("a", "b", "c"))).isEqualTo(3);

        assertThat(CustomFunctions.size(Map.of())).isEqualTo(0);
        assertThat(CustomFunctions.size(Map.of("a", 1, "b", 2, "c", 3))).isEqualTo(3);

        assertThat(CustomFunctions.size(new Object[] {})).isEqualTo(0);
        assertThat(CustomFunctions.size(new Object[] {"1", "2", "3"})).isEqualTo(3);


    }
}
