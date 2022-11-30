package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author qin
 * @Date 2022/11/29 20:48
 * @version 1.0
 * 组件扫描
 * @ComponentScan 的作用就是根据定义的扫描路径，把符合扫描规则的类装配到spring容器中，注解定义如下。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {
    String value();
}