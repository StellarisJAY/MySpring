package com.spring.bean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 单例bean注册
 * </p>
 *
 * @author Jay
 * @date 2021/9/10
 **/
public class BeanRegistry {
    /**
     * 单例池
     */
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * bean Definition map
     */
    public final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    /**
     * 注册单例bean
     * @param beanName beanName
     * @param bean bean实例
     */
    public void registrySingleton(String beanName, Object bean) throws IllegalStateException {
        /*
            对单例池加锁
            保证同步代码块中的两个操作的原子性
         */
        synchronized (singletonObjects){
            // 必须保证get和put两个操作的原子性，避免其他线程
            Object oldBean = singletonObjects.get(beanName);
            if(oldBean != null){
                throw new IllegalStateException("无法注册名为：" + beanName + "的实例，因为beanName已经被 " + oldBean + " 使用");
            }
            singletonObjects.put(beanName, bean);
        }
    }

    /**
     * 添加beanDefinition
     * @param beanName beanName
     * @param beanDefinition beanDef
     */
    protected void addBeanDefinition(String beanName, BeanDefinition beanDefinition){
        synchronized (beanDefinitionMap){
            BeanDefinition oldBeanDefinition = beanDefinitionMap.get(beanName);
            if(oldBeanDefinition != null){
                throw new IllegalStateException("无法注册名为：" + beanName + "的BeanDefinition，因为beanName已经被 " + oldBeanDefinition + " 使用");
            }
            beanDefinitionMap.put(beanName, beanDefinition);
        }
    }

    protected Object createBean(String beanName, BeanDefinition beanDefinition){
        return null;
    }
}
