package com.spring.aop.proxy;

import com.spring.aop.Aspect;
import com.spring.aop.Before;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>
 * Proxy工厂
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class ProxyFactory {

    public Object createProxyInstance(Object target){
        Class<?> targetClass = target.getClass();
        // 类是否实现接口
        if(targetClass.getInterfaces().length > 0){
            // 有实现接口，使用JDK Proxy生成代理对象
            Object proxyInstance = Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    Object result = method.invoke(target, args);


                    return result;
                }
            });
            return proxyInstance;
        }
        return target;
    }
}
