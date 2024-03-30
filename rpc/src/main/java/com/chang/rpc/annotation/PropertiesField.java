package com.chang.rpc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后缀
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertiesField {

    /**
     * 默认为属性名 例如: registryType = registry-type  遵守配置文件规则
     *
     * @return
     */
    String value() default "";
}