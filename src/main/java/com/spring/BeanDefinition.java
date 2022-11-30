package com.spring;

/**
 * @version 1.0
 * @Author qin
 * @Date 2022/11/29 23:35
 * 用于解析class对象,这里主要定义了相关于定义所有的Bean的Class对象和作用域
 */
public class BeanDefinition {

    private Class clazz;
    private String scope;

    public BeanDefinition(Class clazz, String scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public BeanDefinition() {

    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
