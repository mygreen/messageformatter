package com.github.mygreen.messageformatter.expression;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.mygreen.messageformatter.expression.CustomFunctions;


/**
 * {@link CustomFunctions}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CustomFunctionsTest extends CustomFunctions {

    /**
     * {@link CustomFunctions#defaultString(String)}
     */
    @Test
    public void testDefaultString() {

        assertThat(defaultString(null)).isEqualTo("");
        assertThat(defaultString("")).isEqualTo("");
        assertThat(defaultString("abc")).isEqualTo("abc");

    }

    /**
     * {@link CustomFunctions#join(int[], String)}
     */
    @Test
    public void testJoin_int_array() {

        assertThat(join((int[])null, ", ")).isEqualTo("");
        assertThat(join(new int[]{}, ", ")).isEqualTo("");
        assertThat(join(new int[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(join(new int[]{1,2,3}, null)).isEqualTo("123");

    }

    /**
     * {@link CustomFunctions#join(Object[], String)}
     */
    @Test
    public void testJoin_object_array() {

        assertThat(join((Object[])null, ", ")).isEqualTo("");
        assertThat(join(new Object[]{}, ", ")).isEqualTo("");
        assertThat(join(new Object[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(join(new Object[]{1,2,3}, null)).isEqualTo("123");

    }

    /**
     * {@link CustomFunctions#join(java.util.Collection, String)}
     */
    @Test
    public void testJoin_collection() {

        Collection<Integer> input = Arrays.asList(1000, 2000, 3000);

        assertThat(join((Collection<Integer>)null, ", ")).isEqualTo("");
        assertThat(join(Collections.emptyList(), ", ")).isEqualTo("");
        assertThat(join(input, ", ")).isEqualTo("1000, 2000, 3000");
        assertThat(join(input, null)).isEqualTo("100020003000");

    }

    @Test
    public void testEmpty() {

        assertThat(empty(null)).isTrue();

        assertThat(empty("")).isTrue();
        assertThat(empty("a")).isFalse();

        assertThat(empty(Collections.emptyList())).isTrue();
        assertThat(empty(List.of("a"))).isFalse();

        assertThat(empty(Map.of())).isTrue();
        assertThat(empty(Map.of("a", 1))).isFalse();

        assertThat(empty(new Object[] {})).isTrue();
        assertThat(empty(new Object[] {"1"})).isFalse();


    }
}
