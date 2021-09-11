package com.spring.bean;

/**
 * <p>
 * Bean Definition
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class BeanDefinition {
    private Class<?> beanClass;
    private int scope;
    private boolean lazyInit;
    public static final int SINGLETON = 1;
    public  static final int PROTOTYPE = 2;
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BeanDefinition(Class<?> beanClass, int scope, boolean lazyInit) {
        this.beanClass = beanClass;
        this.scope = scope;
        this.lazyInit = lazyInit;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", scope='" + scope + '\'' +
                ", lazyInit=" + lazyInit +
                '}';
    }
}
