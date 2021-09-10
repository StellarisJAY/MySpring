package com.spring;

/**
 * <p>
 * Application Context
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class SimpleApplicationContext {
    /**
     * 配置类
     */
    private Class<?> configClass;

    public SimpleApplicationContext(Class<?> configClass) {
        this.configClass = configClass;
    }

    public Object getBean(String beanName){
        return null;
    }

    public <T> T getBean(String beanName, Class<T> type){
        return null;
    }

}
