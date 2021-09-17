package com.spring.aop;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/9/17
 **/
public class JoinPoint {
    private Object target;
    private Class<?> targetClass;
    private Object[] targetArgs;
    private Object targetReturn;
    private Exception exception;

    public JoinPoint(Object target, Class<?> targetClass, Exception exception) {
        this.target = target;
        this.targetClass = targetClass;
        this.exception = exception;
    }

    public JoinPoint(Object target, Class<?> targetClass, Object targetReturn) {
        this.target = target;
        this.targetClass = targetClass;
        this.targetReturn = targetReturn;
    }

    public JoinPoint(Object target, Class<?> targetClass, Object[] targetArgs) {
        this.target = target;
        this.targetClass = targetClass;
        this.targetArgs = targetArgs;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object[] getTargetArgs() {
        return targetArgs;
    }

    @Override
    public String toString() {
        return "JoinPoint{" +
                "target=" + target +
                ", targetClass=" + targetClass +
                ", targetArgs=" + Arrays.toString(targetArgs) +
                '}';
    }
}
