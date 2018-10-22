/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

/**
 * Interface that defines a registry for shared bean instances.
 * Can be implemented by {@link org.springframework.beans.factory.BeanFactory}
 * implementations in order to expose their singleton management facility
 * in a uniform manner.
 *
 * 为共享bean的实例定义了注册接口.
 * 可以通过{@link org.springframework.beans.factory.BeanFactory}接口的实现来实现,
 * 以便以统一的方式管理他们暴露的单例的功能
 *
 * <p>The {@link ConfigurableBeanFactory} interface extends this interface.
 *  {@link ConfigurableBeanFactory} 接口扩展了该接口
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableBeanFactory
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
 * @see org.springframework.beans.factory.support.AbstractBeanFactory
 */
public interface SingletonBeanRegistry {

    /**
     * Register the given existing object as singleton in the bean registry,
     * under the given bean name.
     * <p>The given instance is supposed to be fully initialized; the registry
     * will not perform any initialization callbacks (in particular, it won't
     * call InitializingBean's {@code afterPropertiesSet} method).
     * The given instance will not receive any destruction callbacks
     * (like DisposableBean's {@code destroy} method) either.
     * <p>When running within a full BeanFactory: <b>Register a bean definition
     * instead of an existing instance if your bean is supposed to receive
     * initialization and/or destruction callbacks.</b>
     * <p>Typically invoked during registry configuration, but can also be used
     * for runtime registration of singletons. As a consequence, a registry
     * implementation should synchronize singleton access; it will have to do
     * this anyway if it supports a BeanFactory's lazy initialization of singletons.
     *
     * 在bean的注册中心中,已给定的名称和对象注册为一个单例对象.
     * 假定给定的实例是被完全实例化,注册中心将不会执行任何初始化的回调(特别是,它不会调用InitializingBean的
     * {@code afterPropertiesSet})方法.
     * 给定的实例将不会收到任何销毁bean的回调方法(就像DisposableBean's {@code destroy} 方法)
     * 当运行在一个完整的BeanFactory时:如果你的bean需要接受初始化或者销毁的回调,应该注册一个beanDefinition
     * 而不是一个现有的实例.
     * 通常在注册配置期间,可以运行时注册单例.因此,如果它支持BeanFactory的单例延迟初始化,
     * 那么注册中心需要实现同步访问.
     *
     *
     *
     * @param beanName the name of the bean
     * @param singletonObject the existing singleton object
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
     * @see org.springframework.beans.factory.DisposableBean#destroy
     * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#registerBeanDefinition
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Only checks already instantiated singletons; does not return an Object
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to access manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to access a singleton
     * defined by a bean definition that already been created, in a raw fashion.
     * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
     * You need to resolve the canonical bean name first before obtaining the singleton instance.
     *
     * 返回在给定名称下注册的(原始)单例对象.仅检查已经实例化的单例,对于尚未实例化的beanDefinitions
     * 不返回对象.
     * 此方法的目的是手动访问已注册的单例,(见 {@link #registerSingleton}),也可以使用原始的方式
     * 访问由beanDefinitions创建的单例对象.
     * 注:此方法不知道FactoryBean的前缀或者别名,在获取单例时,需要实现解析bean的名称.
     *
     *
     * @param beanName the name of the bean to look for
     * @return the registered singleton object, or {@code null} if none found
     * @see ConfigurableListableBeanFactory#getBeanDefinition
     */
    Object getSingleton(String beanName);

    /**
     * Check if this registry contains a singleton instance with the given name.
     * <p>Only checks already instantiated singletons; does not return {@code true}
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to check whether a
     * singleton defined by a bean definition has already been created.
     * <p>To check whether a bean factory contains a bean definition with a given name,
     * use ListableBeanFactory's {@code containsBeanDefinition}. Calling both
     * {@code containsBeanDefinition} and {@code containsSingleton} answers
     * whether a specific bean factory contains a local bean instance with the given name.
     * <p>Use BeanFactory's {@code containsBean} for general checks whether the
     * factory knows about a bean with a given name (whether manually registered singleton
     * instance or created by bean definition), also checking ancestor factories.
     * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
     * You need to resolve the canonical bean name first before checking the singleton status.
     *
     * 检查这个注册中心是否包含一个给定名称的单例实例.
     * 只检查已经初始化的单例,对于尚未实例化的单例BeanDefinition,不返回true.
     * 此方法的目的是手动访问已注册的单例,(见 {@link #registerSingleton}).
     * 还可以检查BeanDefinition的单例对象是否已经创建.
     * 检查一个bean工厂是否包含给定名称的BeanDefinition可以使用 ListableBeanFactory's {@code containsBeanDefinition}.
     * 调用{@code containsBeanDefinition} 和 {@code containsSingleton} 可以知道特定bean工厂是否包含
     * 给定名称的本地bean实例.
     * 使用BeanFactory的{@code containsBean}进行常规的检查可以知道工厂是否有一个给定名称的bean(
     * 无论是手动注册的单例实例还是通过bean定义创建的),还会检查父工厂.
     * 注:此方法不知道FactoryBean的前缀或者别名,在获取单例时,需要实现解析bean的名称.
     *
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a singleton instance with the given name
     * @see #registerSingleton
     * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition
     * @see org.springframework.beans.factory.BeanFactory#containsBean
     */
    boolean containsSingleton(String beanName);

    /**
     * Return the names of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not return names
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to check which singletons
     * defined by a bean definition have already been created.
     *
     * 返回在此注册中心中注册的单例bean的名称
     * 只检查已经初始化的单例,对于尚未实例化的单例BeanDefinition,不返回名称.
     * 这个方法的主要目的是检查手动注册的单例对象(见{@link #registerSingleton}.
     * 还可以用来检查已经通过BeanDefinition所创建的单例对象.
     *
     *
     * @return the list of names as a String array (never {@code null})
     * @see #registerSingleton
     * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
     * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionNames
     */
    String[] getSingletonNames();

    /**
     * Return the number of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not count
     * singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to count the number of
     * singletons defined by a bean definition that have already been created.
     *
     * 返回在此注册中心中注册的单例bean的数量.
     * 只检查已经初始化的单例,对于尚未实例化的单例BeanDefinition,不统计.
     * 这个方法的主要目的是检查手动注册的单例对象(见{@link #registerSingleton}.
     * 还可以用来检查已经通过BeanDefinition所创建的单例数.
     *
     *
     * @return the number of singleton beans
     * @see #registerSingleton
     * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionCount
     * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionCount
     */
    int getSingletonCount();

}
