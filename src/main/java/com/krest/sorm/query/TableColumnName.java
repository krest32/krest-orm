package com.krest.sorm.query;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dux
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) // 添加到方法的注解
public @interface TableColumnName {
    String value();
}
