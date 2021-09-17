package com.spring.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * <p>
 *     切面方法封装
 * </p>
 *
 * @author Jay
 * @date 2021/9/17
 **/
public class MethodWrapper {
    /**
     * 方法匹配字符串
     */
    private String pattern;
    /**
     * 执行实例
     */
    private Object instance;
    /**
     * 方法对象
     */
    private Method method;

    /**
     * 执行通知方法
     * @param joinPoint 切点
     * @return 返回值
     * @throws InvocationTargetException 方法调用对象错误
     * @throws IllegalAccessException 方法无法访问
     */
    public Object invoke(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        // 判断通知方法是否有切点参数
        boolean hasJoinPoint = false;
        for (Parameter parameter : method.getParameters()) {
            if(parameter.getType() == JoinPoint.class){
                hasJoinPoint = true;
                break;
            }
        }
        method.setAccessible(true);
        return hasJoinPoint ? method.invoke(instance, joinPoint) : method.invoke(instance);
    }

    public MethodWrapper(String pattern, Object instance, Method method) {
        this.pattern = pattern;
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
