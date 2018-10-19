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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 * <p>
 *
 * 由大多数可列表的bean工厂实现配置接口,除了 {@link ConfigurableBeanFactory}接口外,
 * 该接口还听过了分析和修改bean定义以及预实例化单例的工具.
 * 该接口是{@link org.springframework.beans.factory.BeanFactory}子接口,不应该在应用程序代码
 * 中使用,应该使用{@link org.springframework.beans.factory.BeanFactory}或者
 * {@link org.springframework.beans.factory.ListableBeanFactory},
 * 这个扩展接口仅仅是允许框架内部对bean工厂配置方法的特殊使用.
 *
 *
 *
 * @author Juergen Hoeller
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 * @since 03.11.2003
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * Ignore the given dependency type for autowiring:
     * for example, String. Default is none.
     *
     * 忽略用于自动注入的指定类型,例如,String,默认是空
     *
     * @param type the dependency type to ignore
     */
    void ignoreDependencyType(Class<?> type);

    /**
     * Ignore the given dependency interface for autowiring.
     * <p>This will typically be used by application contexts to register
     * dependencies that are resolved in other ways, like BeanFactory through
     * BeanFactoryAware or ApplicationContext through ApplicationContextAware.
     * <p>By default, only the BeanFactoryAware interface is ignored.
     * For further types to ignore, invoke this method for each type.
     *
     * 忽略用于自动注入的依赖的接口.
     * 这通常会被应用上下文中来注册以及其他方式解决依赖关系,
     * 比如BeanFactory通过BeanFactoryAware或
     * ApplicationContext通过ApplicationContextAware解析的ApplicationContext
     * 默认情况下,只有BeanFactoryAware接口会被忽略,如果需要其他的类型也被忽略,需要为
     * 这些类型调用此方法
     *
     *
     * @param ifc the dependency interface to ignore
     * @see org.springframework.beans.factory.BeanFactoryAware
     * @see org.springframework.context.ApplicationContextAware
     */
    void ignoreDependencyInterface(Class<?> ifc);

    /**
     * Register a special dependency type with corresponding autowired value.
     *
     * 使用相应注入的值注册带指定的依赖类型
     *
     * <p>This is intended for factory/context references that are supposed
     * to be autowirable but are not defined as beans in the factory:
     * e.g. a dependency of type ApplicationContext resolved to the
     * ApplicationContext instance that the bean is living in.
     * <p>Note: There are no such default types registered in a plain BeanFactory,
     * not even for the BeanFactory interface itself.
     *
     * 这是为工厂/上下文引用而设计的,这些引用应该是要自动注入的,但是没有在工厂中定义为bean
     * 例如,将ApplicationContext类型的依赖关系解析为bean所在的ApplicationContext实例.
     * 注意:在普通的BeanFactory中没有这样的默认类型注册,甚至BeanFactory接口本身也没有。
     *
     *
     * @param dependencyType the dependency type to register. This will typically
     *                       be a base interface such as BeanFactory, with extensions of it resolved
     *                       as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
     *                       as long as the given value actually implements the extended interface.
     * @param autowiredValue the corresponding autowired value. This may also be an
     *                       implementation of the {@link org.springframework.beans.factory.ObjectFactory}
     *                       interface, which allows for lazy resolution of the actual target value.
     */
    void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue);

    /**
     * Determine whether the specified bean qualifies as an autowire candidate,
     * to be injected into other beans which declare a dependency of matching type.
     * <p>This method checks ancestor factories as well.
     *
     * 确定指定的bean是否是一个合格的候选对象,将其注入到其他声明依赖注入他的bean中.
     * 这个方法也会检查父工厂.
     *
     *
     * @param beanName   the name of the bean to check
     * @param descriptor the descriptor of the dependency to resolve
     * @return whether the bean should be considered as autowire candidate
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     */
    boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException;

    /**
     * Return the registered BeanDefinition for the specified bean, allowing access
     * to its property values and constructor argument value (which can be
     * modified during bean factory post-processing).
     *
     * 返回指定bean的已注册bean的定义,允许访问他的属性中和构造函数参数值(可以是在bean工厂后期修改的)
     *
     * <p>A returned BeanDefinition object should not be a copy but the original
     * definition object as registered in the factory. This means that it should
     * be castable to a more specific implementation type, if necessary.
     *
     * 返回的BeanDefinition对象不应该是副本，而应该是原始对象
     * 定义在工厂中注册的对象。这意味着它应该如果需要，可以将其转换为更特定的实现类型。
     *
     * <p><b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
     * It is only meant for accessing local bean definitions of this factory.
     *
     * 这个方法不考虑父工厂,仅仅用于此工厂bean的定义
     *
     * @param beanName the name of the bean
     * @return the registered BeanDefinition
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     *                                       defined in this factory
     */
    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Freeze all bean definitions, signalling that the registered bean definitions
     * will not be modified or post-processed any further.
     * <p>This allows the factory to aggressively cache bean definition metadata.
     *
     * 冻结所有bean的定义,表示注册的bean定义不会被进一步修改或者加工.
     * 允许工厂缓存beanDefinition的元数据
     *
     */
    void freezeConfiguration();

    /**
     * Return whether this factory's bean definitions are frozen,
     * i.e. are not supposed to be modified or post-processed any further.
     *
     * 返回工厂的bean定义是否被冻结，不应被进一步修改或处理
     *
     * @return {@code true} if the factory's configuration is considered frozen
     */
    boolean isConfigurationFrozen();

    /**
     * Ensure that all non-lazy-init singletons are instantiated, also considering
     * {@link org.springframework.beans.factory.FactoryBean FactoryBeans}.
     * Typically invoked at the end of factory setup, if desired.
     *
     * 确保实例化所有非延时初始化的单例,同事需要考虑到{@link org.springframework.beans.factory.FactoryBean FactoryBeans}
     * 如果需要，通常在工厂设置结束时调用
     *
     * @throws BeansException if one of the singleton beans could not be created.
     *                        Note: This may have left the factory with some beans already initialized!
     *                        Call {@link #destroySingletons()} for full cleanup in this case.
     * @see #destroySingletons()
     */
    void preInstantiateSingletons() throws BeansException;

}
