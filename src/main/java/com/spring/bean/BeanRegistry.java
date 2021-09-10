package com.spring.bean;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
     * 二级缓存
     */
    private final HashMap<String, Object> earlySingletonObjects = new HashMap<>(256);

    /**
     * 正在创建的 bean
     */
    private final Set<String> inCreation = new HashSet<>(256);
    /**
     * bean Definition map
     */
    public final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    /**
     * 注册单例bean
     * @param beanName beanName
     * @param bean bean实例
     */
    public void registerSingleton(String beanName, Object bean) throws IllegalStateException {
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

    /**
     * 通过beanName和beanDefinition创建bean
     * @param beanName beanName
     * @param beanDefinition beanDefinition
     * @return bean对象
     */
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 设置bean inCreation
        inCreation.add(beanName);
        Object instance;
        // 构造方法推断
        Constructor<?> constructor = chooseConstructor(beanClass);

        try {
            // 实例化
            instance = createInstance(constructor);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException("创建bean：" + beanName + "异常，无法实例化");
        }

        inCreation.remove(beanName);
        if("singleton".equals(beanDefinition.getScope())){
            registerSingleton(beanName, instance);
        }
        return instance;
    }

    /**
     * 构造器推断
     * 这里使用最简单的构造器推断方法
     * 1、如果有带有@Autowired注解的构造器，选择第一个所有参数都是由spring管理的构造器
     * 2、没有@Autowired则选择无参构造器
     * 3、没有无参构造器则抛出异常
     * @param beanClass beanClass
     * @return Constructor
     */
    private Constructor<?> chooseConstructor(Class<?> beanClass){
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        Constructor<?> noArgsConstructor = null;
        Constructor<?> autowiredConstructor;
        for (Constructor<?> constructor : constructors) {

            // 有 @Autowired，且不是无参构造器
            if(constructor.isAnnotationPresent(Autowired.class) && constructor.getParameterCount() != 0){
                Parameter[] parameters = constructor.getParameters();
                autowiredConstructor = constructor;
                // 检查每一个参数是否都有@Component注解
                for (Parameter parameter : parameters) {
                    if(!parameter.getType().isAnnotationPresent(Component.class)){
                        autowiredConstructor = null;
                        break;
                    }
                }
                // 该构造器符合要求
                if(autowiredConstructor != null){
                    return autowiredConstructor;
                }
            }
            // 无参构造器
            else if(constructor.getParameterCount() == 0){
                noArgsConstructor = constructor;
            }
        }
        if(noArgsConstructor != null){
            return noArgsConstructor;
        }
        throw new RuntimeException(beanClass.getName() + " 没有可选用的构造器");
    }

    /**
     * 使用构造器创建 bean 原始对象
     * @param constructor 构造方法
     * @return bean 原始对象
     */
    private Object createInstance(Constructor<?> constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] parameters = new Object[constructor.getParameterCount()];
        int index = 0;
        for (Parameter parameter : constructor.getParameters()) {
            Class<?> paramClass = parameter.getType();
            Component component = paramClass.getDeclaredAnnotation(Component.class);
            String beanName = component.value().length() == 0 ? parameter.getName() : component.value();
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            // 从单例池中取对象
            if(singletonObjects.containsKey(beanName)){
                parameters[index++] = singletonObjects.get(beanName);
            }
            /*
                实例化阶段循环依赖检测
             */
            else if(inCreation.contains(beanName)){
                throw new RuntimeException("实例化 " + beanName + " 发生循环依赖");
            }
            else{
                // 创建bean
                parameters[index++] = createBean(beanName, beanDefinition);
            }

        }

        constructor.setAccessible(true);
        return constructor.newInstance(parameters);
    }

    /**
     * 获取beanDefinition
     * @param beanName beanName
     * @return BeanDefinition
     */
    protected BeanDefinition getBeanDefinition(String beanName){
        return beanDefinitionMap.get(beanName);
    }

    protected Object getSingleton(String beanName){
        return singletonObjects.get(beanName);
    }
}
