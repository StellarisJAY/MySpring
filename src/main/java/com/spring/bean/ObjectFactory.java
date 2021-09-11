package com.spring.bean;

/**
 * @author Jay
 */
@FunctionalInterface
public interface ObjectFactory {
    /**
     * 通过该方法获取bean对象
     * @return Object
     */
    Object getObject();
}
