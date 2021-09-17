package com.spring.aop.proxy;

import com.spring.aop.JoinPoint;
import com.spring.aop.MethodWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * <p>
 * Proxy工厂
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class ProxyFactory {

    /**
     * AOP 前置通知方法过滤器
     */
    private List<MethodWrapper> beforeFilters = new LinkedList<>();

    /**
     * 方法过滤类型枚举
     */
    public enum MethodFilterType{
        /**
         * 前置类型
         */
        BEFORE,
        /**
         * 后置类型
         */
        AFTER_RETURN,
        /**
         * 返回前类型
         */
        BEFORE_RETURN,
        /**
         * 异常捕获类型
         */
        THROWS_EXCEPTION

    }

    /**
     * aop 后置通知 map
     */
    private HashMap<String, Method> afterMap = new HashMap<>(256);

    public Object createProxyInstance(Class<?> targetClass, Object target){
        // 类是否实现接口
        if(hasInterface(targetClass)){
            // 有实现接口，使用JDK Proxy生成代理对象
            return Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), (proxy, method, args) -> {
                List<MethodWrapper> beforeMethods = getBeforeChain(method);
                JoinPoint beforeJoinPoint = new JoinPoint(target, targetClass, args);
                for (MethodWrapper beforeMethod : beforeMethods) {
                    beforeMethod.invoke(beforeJoinPoint);
                }

                // 业务逻辑
                Object result = method.invoke(target, args);



                return result;
            });
        }
        return target;
    }

    private boolean hasInterface(Class<?> clazz){
        return clazz.getInterfaces().length > 0;
    }

    /**
     * 获取 前置 执行链
     * @param method 被代理方法
     * @return List
     */
    public List<MethodWrapper> getBeforeChain(Method method){
        List<MethodWrapper> beforeMethods = new ArrayList<>();
        for (MethodWrapper m : beforeFilters) {
            if(methodMatches(m.getPattern(), method)){
                beforeMethods.add(m);
            }
        }
        return beforeMethods;
    }

    /**
     * 方法匹配
     * @param pattern 匹配逻辑
     * @param method 待匹配方法
     * @return boolean
     */
    private boolean methodMatches(String pattern, Method method){
        String[] patterns = pattern.split(" ");
        String returnType = method.getReturnType().getName();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        // 返回值类型不匹配
        if(matches(patterns[0], returnType) || matches(patterns[1], methodName)){
            return false;
        }
        String sub1 = patterns[2].substring(patterns[2].indexOf("(") + 1);
        String paramPattern = sub1.substring(0, sub1.indexOf(")"));
        if("*".equals(paramPattern)){
            return true;
        }
        String[] paramPatterns = paramPattern.split(",");
        for(int i = 0; i < paramPatterns.length; i++){
            String param = paramPatterns[i].trim();
            if(i >= method.getParameters().length){
                return false;
            }
            String paramType = method.getParameters()[i].getType().getName();
            if(matches(param, paramType)){
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串匹配
     * @param pattern 匹配串
     * @param s string
     * @return boolean
     */
    private boolean matches(String pattern, String s){
        int symbolIdx = pattern.indexOf('*');
        if(symbolIdx != -1 && symbolIdx == pattern.lastIndexOf('*')){
            String beforeSymbol = pattern.substring(0, symbolIdx);
            String afterSymbol = pattern.substring(symbolIdx + 1);
            if(beforeSymbol.length() > 0 && afterSymbol.length() > 0){
                int beforeEnd = s.indexOf(beforeSymbol) + beforeSymbol.length();
                int endStart = s.indexOf(afterSymbol);
                return beforeEnd >= endStart;
            }
            else if(beforeSymbol.length() > 0){
                return s.indexOf(beforeSymbol) != 0;
            }
            else if(afterSymbol.length() > 0){
                return !s.contains(afterSymbol);
            }
            else{
                return false;
            }
        }
        return true;
    }

    public void registerMethodFilter(MethodWrapper methodWrapper, MethodFilterType type){
        if(type == MethodFilterType.BEFORE){
            beforeFilters.add(methodWrapper);
        }
    }

}
