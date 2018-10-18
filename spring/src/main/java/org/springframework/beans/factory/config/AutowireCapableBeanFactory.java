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

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;

/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * 是{@link org.springframework.beans.factory.BeanFactory}接口的扩展接口,
 * 实现该接口的工厂需要能提供自动注入功能,如果他们希望暴露现有的bean实例的功能.
 *
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link org.springframework.beans.factory.BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * 这个BeanFactory的子接口不应该一般应用程序代码中使用,在通常的使用中应该使用{@link org.springframework.beans.factory.BeanFactory}和
 *  {@link org.springframework.beans.factory.ListableBeanFactory}
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * 其他框架的集成代码可以利用这个接口,连接并且填充不被spring所管理的bean.
 * 这对于网络操作非常有效果,例如:Tapestry(一个web框架)页面对象
 *
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * 注意,这个接口不是{@link org.springframework.context.ApplicationContext} 这个实现类的门面,
 * 因为他很少被应用程序使用,也就是说,他可以通过从应用的上下文中通过ApplicationContext的
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * 方法获取.
 *
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * 你可以实现{@link org.springframework.beans.factory.BeanFactoryAware}接口,
 * 该接口暴露了内部的bean工厂,即使在运行时的ApplicationContext上下文中,可以获取AutowireCapableBeanFactory
 * 只需要将BeanFactory强制转为AutowireCapableBeanFactory
 *
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * Constant that indicates no externally defined autowiring. Note that
     * BeanFactoryAware etc and annotation-driven injection will still be applied.
     *
     * 表示没有外部定义的自动注入,需要注意的是,BeanFactoryAware和注解驱动的注入依然有效
     *
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_NO = 0;

    /**
     * Constant that indicates autowiring bean properties by name
     * (applying to all bean property setters).
     *
     * 该常量表示按名称自动注入bean属性(使用bean熟悉的set方法)。
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * Constant that indicates autowiring bean properties by type
     * (applying to all bean property setters).
     *
     * 该常量表示按类型自动注入bean属性(使用bean熟悉的set方法)。
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * Constant that indicates autowiring the greediest constructor that
     * can be satisfied (involves resolving the appropriate constructor).
     *
     * 该常量表示按构造器自动注入(涉及解析适当的构造函数)
     *
     * @see #createBean
     * @see #autowire
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * Constant that indicates determining an appropriate autowire strategy
     * through introspection of the bean class.
     *
     * 它指示通过对bean类的自省来确定适当的自动注入策略。
     *
     * @see #createBean
     * @see #autowire
     * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
     * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
     */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;


    //-------------------------------------------------------------------------
    // Typical methods for creating and populating external bean instances
    //-------------------------------------------------------------------------

    /**
     * Fully create a new bean instance of the given class.
     * <p>Performs full initialization of the bean, including all applicable
     * {@link BeanPostProcessor BeanPostProcessors}.
     * <p>Note: This is intended for creating a fresh instance, populating annotated
     * fields and methods as well as applying all standard bean initialiation callbacks.
     * It does <i>not</> imply traditional by-name or by-type autowiring of properties;
     * use {@link #createBean(Class, int, boolean)} for that purposes.
     *
     * 创建一个给定class的新的bean实例.
     * 执行bean的全部初始化流程,包括所有适用的{@link BeanPostProcessor BeanPostProcessors}.
     * 需要注意的是,这是为了创建一个新的实例,填充注解字段和方法,以及应用所有标准bean初始化的回调.
     * 他不意味着传统按照名称或者类型自动注入属性; 如果需要使用 {@link #createBean(Class, int, boolean)}方法
     *
     *
     * @param beanClass the class of the bean to create
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     */
    <T> T createBean(Class<T> beanClass) throws BeansException;

    /**
     * Populate the given bean instance through applying after-instantiation callbacks
     * and bean property post-processing (e.g. for annotation-driven injection).
     * <p>Note: This is essentially intended for (re-)populating annotated fields and
     * methods, either for new instances or for deserialized instances. It does
     * <i>not</i> imply traditional by-name or by-type autowiring of properties;
     * use {@link #autowireBeanProperties} for that purposes.
     *
     * 通过应用实例化后回调和bean属性的后置处理来设置给定bean的属性.
     * 需要注意的是,通过注解设置属性和方法这基本上是为了新的实例或者反序列化填充属性.
     * 而不是为了实现传统上通过名称和类型自动注入属性.
     * 如果需要使用 {@link #autowireBeanProperties}方法
     *
     *
     * @param existingBean the existing bean instance
     * @throws BeansException if wiring failed
     */
    void autowireBean(Object existingBean) throws BeansException;

    /**
     * Configure the given raw bean: autowiring bean properties, applying
     * bean property values, applying factory callbacks such as {@code setBeanName}
     * and {@code setBeanFactory}, and also applying all bean post processors
     * (including ones which might wrap the given raw bean).
     * <p>This is effectively a superset of what {@link #initializeBean} provides,
     * fully applying the configuration specified by the corresponding bean definition.
     * <b>Note: This method requires a bean definition for the given name!</b>
     *
     * 配置给定bean的原始bean,自动注入的bean属性,应用bean的属性,应用工程的回调方法(例如{@code setBeanName})
     * 和{@code setBeanFactory},还应用所有bean的后置处理器(包括可能包装给定的原始bean的处理器)
     *
     * 这实际上是{@link #initializeBean}提供的超集，完全应用了相应beanDefinition指定的配置.
     * 需要注意的是,这个方法需要一个给定名称的beanDefinition
     *
     *
     * @param existingBean the existing bean instance 已存在bean的实例
     * @param beanName the name of the bean, to be passed to it if necessary
     * (a bean definition of that name has to be available)
     *
     *  bean的名称,必须提供该bean名称的beanDefinition
     *
     * @return the bean instance to use, either the original or a wrapped one
     *
     * 返回原始的bean或者包装的bean
     *
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * if there is no bean definition with the given name
     * @throws BeansException if the initialization failed
     * @see #initializeBean
     */
    Object configureBean(Object existingBean, String beanName) throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     *
     * 针对bean工厂中定义的bean解决指定的依赖关系。
     *
     * @param descriptor the descriptor for the dependency
     * @param beanName the name of the bean which declares the present dependency
     * @return the resolved object, or {@code null} if none found
     * @throws BeansException in dependency resolution failed
     */
    Object resolveDependency(DependencyDescriptor descriptor, String beanName) throws BeansException;


    //-------------------------------------------------------------------------
    // Specialized methods for fine-grained control over the bean lifecycle
    //-------------------------------------------------------------------------

    /**
     * Fully create a new bean instance of the given class with the specified
     * autowire strategy. All constants defined in this interface are supported here.
     * <p>Performs full initialization of the bean, including all applicable
     * {@link BeanPostProcessor BeanPostProcessors}. This is effectively a superset
     * of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
     *
     * 在创建新的bean实例时使用指定的自动注入策略.
     * 这个接口支持所有定义的常量.
     * 执行bean的所有初始化过程,包括所有的{@link BeanPostProcessor BeanPostProcessors}bean后置处理器.
     * 这个实际上是 {@link #autowire}提供的一个超集,添加{@link #initializeBean}的行为.
     *
     *
     * @param beanClass the class of the bean to create
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     * (not applicable to autowiring a constructor, thus ignored there)
     *                        是否执行对象的依赖检查,(不适用于自动注入的构造函数,会被忽略)
     *
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     */
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Instantiate a new bean instance of the given class with the specified autowire
     * strategy. All constants defined in this interface are supported here.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * before-instantiation callbacks (e.g. for annotation-driven injection).
     *
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the construction of the instance.
     *
     * 在创建新的bean实例时使用指定的自动注入策略.
     * 这个接口支持所有定义的常量.
     * 也可以使用AUTOWIRE_NO策略调用，以便仅仅应用初始化之前的调用（例如注解驱动的注入）。
     *
     * 不会应用标准的BeanPostProcessor和BeanPostProcessors回调和更深一步执行bean的初始化。
     * 此接口提供不同的，细粒度控制的方法实现那些操作（BeanPostProcessor和BeanPostProcessors
     * 回调和更深一步执行bean的初始化），如initializeBean方法。
     * 但是，如果实例的创建过程中如果InstantiationAwareBeanPostProcessor的回调是可用的，那么将被使用。
     *
     *
     *
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     * references in the bean instance (not applicable to autowiring a constructor,
     * thus ignored there)
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_AUTODETECT
     * @see #initializeBean
     * @see #applyBeanPostProcessorsBeforeInitialization
     * @see #applyBeanPostProcessorsAfterInitialization
     */
    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Autowire the bean properties of the given bean instance by name or type.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * after-instantiation callbacks (e.g. for annotation-driven injection).
     *
     * 自动注入给定的bean实例的属性，通过by name 或者 type的方式，也可以通过 {@code AUTOWIRE_NO}以实现仅仅对实例化
     * 后回调的调用（例如注解驱动的注入）。
     *
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the configuration of the instance.
     *
     * 不会应用标准的BeanPostProcessor和BeanPostProcessors回调和更深一步执行bean的初始化。
     * 此接口提供不同的，细粒度控制的方法实现那些操作（BeanPostProcessor和BeanPostProcessors
     * 回调和更深一步执行bean的初始化），如initializeBean方法。
     * 但是，如果实例的配置过程中如果InstantiationAwareBeanPostProcessor的回调是可用的，那么将被使用。
     *
     * @param existingBean the existing bean instance
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     * references in the bean instance
     * @throws BeansException if wiring failed
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_NO
     */
    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException;

    /**
     * Apply the property values of the bean definition with the given name to
     * the given bean instance. The bean definition can either define a fully
     * self-contained bean, reusing its property values, or just property values
     * meant to be used for existing bean instances.
     *
     * 应用给定bean名字的bean定义的属性值到给定的bean实例，
     * 指定的bean定义可以定义一个完全独立的bean
     * 重要它自己的属性值，或者是仅仅属性值以用于给已存在的bean实例使用。
     *
     *
     * <p>This method does <i>not</i> autowire bean properties; it just applies
     * explicitly defined property values. Use the {@link #autowireBeanProperties}
     * method to autowire an existing bean instance.
     *
     * 此方法不自动注入bean的属性，它仅仅应用明确定义的属性值。{@link #autowireBeanProperties}方法实现了对已
     * 存在实例的属性的自动注入。
     *
     *
     * <b>Note: This method requires a bean definition for the given name!</b>
     *
     * 注：这个方法要求指定的名字存在bean定义.
     *
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the configuration of the instance.
     *
     * 不会应用标准的BeanPostProcessor和BeanPostProcessors回调和更深一步执行bean的初始化。
     * 此接口提供不同的，细粒度控制的方法实现那些操作（BeanPostProcessor和BeanPostProcessors
     * 回调和更深一步执行bean的初始化），如initializeBean方法。
     * 但是，如果实例的创建过程中如果InstantiationAwareBeanPostProcessor的回调是可用的，那么将被使用。
     *
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean definition in the bean factory
     * (a bean definition of that name has to be available)
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * if there is no bean definition with the given name
     * @throws BeansException if applying the property values failed
     * @see #autowireBeanProperties
     */
    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    /**
     * Initialize the given raw bean, applying factory callbacks
     * such as {@code setBeanName} and {@code setBeanFactory},
     * also applying all bean post processors (including ones which
     * might wrap the given raw bean).
     * <p>Note that no bean definition of the given name has to exist
     * in the bean factory. The passed-in bean name will simply be used
     * for callbacks but not checked against the registered bean definitions.
     *
     * 初始化给定的原始bean,应用工厂的回调方法例如 {@code setBeanName}和{@code setBeanFactory},
     * 还应用所有bean的后置处理器(包括哪些可能保证给定的bean).
     *
     * 注意,如果给定bean的definition不存在,办么传入的bean名称将被简单的用于回调,但是不针对已注册
     * bean的定义进行检查
     *
     *
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean, to be passed to it if necessary
     * (only passed to {@link BeanPostProcessor BeanPostProcessors})
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if the initialization failed
     */
    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    /**
     * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
     * instance, invoking their {@code postProcessBeforeInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     *
     * 将{@link BeanPostProcessor BeanPostProcessors}应用在给定现有bean的实例,调用他们的
     * {@code postProcessBeforeInitialization}方法,返回的bean实例可能是原始bean的包装类
     *
     *
     * @param existingBean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessBeforeInitialization
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
     * instance, invoking their {@code postProcessAfterInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     *
     * 将{@link BeanPostProcessor BeanPostProcessors}应用在给定现有bean的实例,调用他们的
     * {@code postProcessBeforeInitialization}方法,返回的bean实例可能是原始bean的包装类
     *
     *
     * @param existingBean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessAfterInitialization
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     *
     * 针对此工厂中定义的bean解决指定的依赖关系。
     *
     * @param descriptor the descriptor for the dependency
     * @param beanName the name of the bean which declares the present dependency
     * @param autowiredBeanNames a Set that all names of autowired beans (used for
     * resolving the present dependency) are supposed to be added to
     * @param typeConverter the TypeConverter to use for populating arrays and
     * collections
     * @return the resolved object, or {@code null} if none found
     * @throws BeansException in dependency resolution failed
     */
    Object resolveDependency(DependencyDescriptor descriptor, String beanName,
                             Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException;

}
