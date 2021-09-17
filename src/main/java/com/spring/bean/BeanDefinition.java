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
    /**
     * bean 类型
     */
    private Class<?> beanClass;
    /**
     * 作用域
     */
    private int scope;
    /**
     * 是否懒加载
     */
    private boolean lazyInit;

    /**
     * 实现的接口
     */
    private Class<?>[] interfaces;

    /**
     * 是否有后置处理器
     */
    private boolean postProcess;

    /**
     * 是否是切面
     */
    private boolean aspect;

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

    public boolean isPostProcess() {
        return postProcess;
    }

    public void setPostProcess(boolean postProcess) {
        this.postProcess = postProcess;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public boolean isAspect() {
        return aspect;
    }

    public void setAspect(boolean aspect) {
        this.aspect = aspect;
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
