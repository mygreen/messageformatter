package com.github.mygreen.messageformatter.beanvalidation;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.Data;


/**
 * {@link CustomMessageInterpolator}のテスタ。
 * <p>BeanValidationからメッセ―ジを変更する。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=ValidationTestConfig.class)
class CustomMessageInterpolatorTest {

    @Autowired
    Validator validator;

    @Test
    void testValidate() {

        SampleBean targetBean = new SampleBean();
        targetBean.setName("Yamada Taro");
        targetBean.setAge(-1);

        Set<ConstraintViolation<SampleBean>> results = validator.validate(targetBean);
        for(ConstraintViolation<SampleBean> violation : results) {

            String message = violation.getMessage();
//            System.out.println(message);

            Annotation anno = violation.getConstraintDescriptor().getAnnotation();
            if(anno.annotationType().isAssignableFrom(Max.class)) {
                assertThat(message).isEqualTo("サンプルの名前の値は、10より同じか小さい値を設定してください。");
            } else if(anno.annotationType().isAssignableFrom(PositiveOrZero.class)) {
                assertThat(message).isEqualTo("年齢の値は、0以上の値を設定してください。");
            }

        }

    }

    /**
     * テスト対象のBean
     *
     */
    @Data
    static class SampleBean {

        @NotBlank
        @Max(10)
        private String name;

        @PositiveOrZero
        private Integer age;

    }
}
