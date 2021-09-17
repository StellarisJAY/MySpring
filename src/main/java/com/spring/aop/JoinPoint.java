package com.spring.aop;

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
}
