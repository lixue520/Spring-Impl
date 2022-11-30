package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0
 * @Author qin
 * @Date 2022/11/29 22:47
 * 创建Bean的作用域
 */
@Retention(RetentionPolicy.RUNTIME)//运行时，也就是加载到Jvm时也会被扫描到
@Target(ElementType.TYPE)//作用到类上
public @interface Scope {
    /**
     * 1.单例Bean:每次创建Bean都是同一个对象，创建了3次Bean，三次Bean的堆地址一致
     * 2.原型Bean:每次创建对象所有的Bean都是不同的对象，三个Bean的堆地址不同
     */
    String value() default ""; //注解参数，默认下为""
}
