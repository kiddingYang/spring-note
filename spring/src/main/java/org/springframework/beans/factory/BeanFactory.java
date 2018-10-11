/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * The root interface for accessing a Spring bean container.
 * This is the basic client view of a bean container;
 * further interfaces such as {@link ListableBeanFactory} and
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 *
 * 该接口是访问spring bean容器的根接口
 * 这是bean容器最基本的一个客户端视图
 * 进一步的接口有{@link ListableBeanFactory} 和 {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 他们提供了特定目的的功能
 *
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * each uniquely identified by a String name. Depending on the bean definition,
 * the factory will return either an independent instance of a contained object
 * (the Prototype design pattern), or a single shared instance (a superior
 * alternative to the Singleton design pattern, in which the instance is a
 * singleton in the scope of the factory). Which type of instance will be returned
 * depends on the bean factory configuration: the API is the same. Since Spring
 * 2.0, further scopes are available depending on the concrete application
 * context (e.g. "request" and "session" scopes in a web environment).
 *
 * 这个接口的是由持有许多BeanDefinition的对象实现的,每一个BeanDefinition都有唯一的名字标识,
 * 这个工厂会返回一个独立的bean(也就是Prototype[原型]模式),或者一个共享的实例(也是就是Singleton[单例]模式)
 * 返回是Singleton或者Prototype取决于工厂的配置,但是他们的API都是相同,从spring2.0开始,可以使用更多的范围
 * (比如在web环境下可以使用request返回或者session范围)
 *
 *
 *
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 *
 * 这些方法的重心是BeanFactory是一个应用程序的注册中心以及应用程序的配置中心(每个对象不需要单独的读取配置文件)
 * 可以查看Expert One-on-One J2EE Design and Development 这本书的chapters 4 和 11讨论这个方式的好处
 *
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 *
 * 需要注意的是,通过set方法或者构造器依赖注入通常更好,而不是通过bean工厂去拉去(个人理解pull就是通过
 * BeanFactory.getBean的方式获取),spring的依赖注入是通过BeanFactory以及子类接口来实现的
 *
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * source (such as an XML document), and use the {@code org.springframework.beans}
 * package to configure the beans. However, an implementation could simply return
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 *
 * 通常情况下,BeanFactory会从一个配置源(例如一个XML文件)中加载BeanDefinition,然后
 * 使用{@code org.springframework.beans}打包配置bean,一个实现可以通过Java代码
 * 很容易的返回一个Java对象.但是并没有约束如何存储bean定义,可以通过LDAP, RDBMS, XML,properties file
 * 这些方式来存储.鼓励通过bean之间的引用(依赖注入)来实现
 *
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * 和{@link ListableBeanFactory}不同,所有实现{@link HierarchicalBeanFactory}接口的类,
 * 在使用的时候会先检查父工厂,如果bean没有在当前bean工厂中发现,那么会从直接父工厂中查询是否存在.
 * 当前工厂中定义的bean实例,会覆盖在父工厂中定义名称相同的bean.
 *
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:<br>
 * 1. BeanNameAware's {@code setBeanName}<br>
 * 2. BeanClassLoaderAware's {@code setBeanClassLoader}<br>
 * 3. BeanFactoryAware's {@code setBeanFactory}<br>
 * 4. ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)<br>
 * 5. ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)<br>
 * 6. MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)<br>
 * 7. ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)<br>
 * 8. ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)<br>
 * 9. {@code postProcessBeforeInitialization} methods of BeanPostProcessors<br>
 * 10. InitializingBean's {@code afterPropertiesSet}<br>
 * 11. a custom init-method definition<br>
 * 12. {@code postProcessAfterInitialization} methods of BeanPostProcessors
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:<br>
 * 1. DisposableBean's {@code destroy}<br>
 * 2. a custom destroy-method definition
 *
 * bean工厂的实现应该支持标准的bean生命周期接口,完整的初始化方法和顺序是:
 * 1.BeanNameAware接口的setBeanName方法
 * 2.BeanClassLoaderAware接口的setBeanClassLoader方法
 * 3.BeanFactoryAware接口的setBeanFactory方法
 * 4.ResourceLoaderAware接口的setResourceLoader方法(在应用程序上下文中运行时)
 * 5.ApplicationEventPublisherAware接口的setApplicationEventPublisher方法(在应用程序上下文中运行时)
 * 6.MessageSourceAware接口的setMessageSource方法(在应用程序上下文中运行时)
 * 7.ApplicationContextAware的setApplicationContext方法(在应用程序上下文中运行时)
 * 8.ServletContextAware接口的setServletContext方法(在web应用程序上下文中运行时)
 * 9.各种BeanPostProcessors的postProcessBeforeInitialization方法(很多BeanPostProcessor接口)
 * 10.InitializingBean的afterPropertiesSet方法
 * 11.自定义的init-method方法
 * 12.各种BeanPostProcessors的postProcessAfterInitialization方法(很多BeanPostProcessor接口)
 *
 * 当关闭bean工厂的时候,以下的生命周期方法将适用
 * 1.DisposableBean接口的destroy方法
 * 2.自定义的destroy-method方法
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

    /**
     * Used to dereference a {@link FactoryBean} instance and distinguish it from
     * beans <i>created</i> by the FactoryBean. For example, if the bean named
     * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
     * will return the factory, not the instance returned by the factory.
     *
     * &是用来取消创建FactoryBean本身实例,并且与FactoryBean创建的bean区分开来.
     * 比如,如果一个名称为myJndiObject的FactoryBean,使用&myJndiObject回返回FactoryBean,
     * 而不是工厂本身的实例.
     *
     * 个人理解:如果一个类实现了FactoryBean,如果需要获得FactoryBean本身的实例,那么就需要
     * 加上&来获取,如果不加&符号获取,得到的是org.springframework.beans.factory.FactoryBean#getObject()
     * 返回的对象
     *
     */
    String FACTORY_BEAN_PREFIX = "&";

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>This method allows a Spring BeanFactory to be used as a replacement for the
     * Singleton or Prototype design pattern. Callers may retain references to
     * returned objects in the case of Singleton beans.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     *
     * 返回一个实例,这个实例可能是单例或者原型,这个方法允许spring BeanFactory为Singleton模式或者
     * Prototype模式,调用者可以保留在单例情况下返回的实例
     * 可以通过别名转为相应的bean.
     * 如果在当前工厂中找不到当前实例,那么就会从父工厂中询问
     *
     *
     * @param name the name of the bean to retrieve
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no bean definition
     * with the specified name
     * @throws BeansException if the bean could not be obtained
     */
    Object getBean(String name) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
     * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
     * required type. This means that ClassCastException can't be thrown on casting
     * the result correctly, as can happen with {@link #getBean(String)}.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     *
     * 会返回一个指定bean的实例,该实例可能是共享的或者是独立的,和{@link #getBean(String)}是一样
     * 的功能,但是该方法提供了一个度量,如果和期望的bean类型不一样,将会抛出BeanNotOfRequiredTypeException
     * 异常.也就是说如果转换类型成功,将不会抛出异常,和{@link #getBean(String)}一样.
     * 可以通过别名转为相应的bean.
     * 如果在当前工厂中找不到当前实例,那么就会从父工厂中询问
     *
     *
     * @param name the name of the bean to retrieve
     * @param requiredType type the bean must match. Can be an interface or superclass
     * of the actual class, or {@code null} for any match. For example, if the value
     * is {@code Object.class}, this method will succeed whatever the class of the
     * returned instance.
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
     * @throws BeansException if the bean could not be created
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     *
     * 返回一个唯一匹配给定类型对象的bean实例,
     * 这个方法将会从{@link ListableBeanFactory}类中根据类型查询所有,也可以在给定的类型中根据名称进行逐个
     * 查找,对于beans的检索可以使用{@link ListableBeanFactory} 和 {@link BeanFactoryUtils}
     *
     * Return the bean instance that uniquely matches the given object type, if any.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * {@code null} is disallowed.
     * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
     * but may also be translated into a conventional by-name lookup based on the name
     * of the given type. For more extensive retrieval operations across sets of beans,
     * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
     * @return an instance of the single bean matching the required type
     * @throws NoSuchBeanDefinitionException if no bean of the given type was found
     * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
     * 如果该类型返回不止一个实例,抛出NoUniqueBeanDefinitionException
     * @since 3.0
     * @see ListableBeanFactory
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;

    /**
     *
     * // todo 需要在确认
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>Allows for specifying explicit constructor arguments / factory method arguments,
     * overriding the specified default arguments (if any) in the bean definition.
     * @param name the name of the bean to retrieve
     * @param args arguments to use if creating a prototype using explicit arguments to a
     * static factory method. It is invalid to use a non-null args value in any other case.
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     * @throws BeanDefinitionStoreException if arguments have been given but
     * the affected bean isn't a prototype
     * @throws BeansException if the bean could not be created
     * @since 2.5
     */
    Object getBean(String name, Object... args) throws BeansException;

    /**
     * Does this bean factory contain a bean definition or externally registered singleton
     * instance with the given name?
     * 对于给定的bean名称,判断当前bean工厂是否包含bean定义或者外部注册的单例对象
     *
     * <p>If the given name is an alias, it will be translated back to the corresponding
     * canonical bean name.
     * 如果给定的是一个别名,将会被转换为bean的名称
     *
     * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
     * be found in this factory instance.
     *
     * 如果当前的工厂是有父工厂的,如果在当前工厂找不到bean,就会从父工厂中查询
     *
     * <p>If a bean definition or singleton instance matching the given name is found,
     * this method will return {@code true} whether the named bean definition is concrete
     * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
     * return value from this method does not necessarily indicate that {@link #getBean}
     * will be able to obtain an instance for the same name.
     *
     * 如果找到匹配给定名称的bean定义或者实例,无论bean的定义是具体的还是抽象的
     * 懒加载或者是立刻加载,是不是在当前的上下文中,这个方法就会返回true.
     * 所以返回true并不表示一定能够获得相同名称的实例
     *
     *
     * @param name the name of the bean to query
     * @return whether a bean with the given name is present
     */
    boolean containsBean(String name);

    /**
     * Is this bean a shared singleton? That is, will {@link #getBean} always
     * return the same instance?
     * 判断该名称是bean是否是单例.
     *
     * <p>Note: This method returning {@code false} does not clearly indicate
     * independent instances. It indicates non-singleton instances, which may correspond
     * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
     * check for independent instances.
     *
     * 当这个方法返回false的时候,并不能清楚的表明是一个非单例对象,可能是对应作用于的bean是一样的
     * 如果需要清楚的检查是非单例,使用{@link #isPrototype}
     *
     *
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     *
     *  可以通过别名转为相应的bean.
     * 如果在当前工厂中找不到当前实例,那么就会从父工厂中询问
     *
     * @param name the name of the bean to query
     * @return whether this bean corresponds to a singleton instance
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @see #getBean
     * @see #isPrototype
     */
    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    /**
     * Is this bean a prototype? That is, will {@link #getBean} always return
     * independent instances?
     * 判断该名称是bean是否是非单例.
     *
     * <p>Note: This method returning {@code false} does not clearly indicate
     * a singleton object. It indicates non-independent instances, which may correspond
     * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
     * check for a shared singleton instance.
     *
     * 当这个方法返回false的时候,并不能清楚的表明是一个单例对象,可能是对应作用于的bean是一样的
     * 如果需要清楚的检查是单例,使用{@link #isSingleton}
     *
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return whether this bean will always deliver independent instances
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.3
     * @see #getBean
     * @see #isSingleton
     */
    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    /**
     * Check whether the bean with the given name matches the specified type.
     * More specifically, check whether a {@link #getBean} call for the given name
     * would return an object that is assignable to the specified target type.
     *
     * 检查给定bean的名称和类型是否是匹配的,具体说来,检查{@link #getBean}调用指定名称
     * 将会返回一个指定的目标对象
     *
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     *
     * 可以通过别名转为相应的bean.
     * 如果在当前工厂中找不到当前实例,那么就会从父工厂中询问
     *
     * @param name the name of the bean to query
     * @param targetType the type to match against
     * @return {@code true} if the bean type matches,
     * {@code false} if it doesn't match or cannot be determined yet
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.1
     * @see #getBean
     * @see #getType
     */
    boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException;

    /**
     * Determine the type of the bean with the given name. More specifically,
     * determine the type of object that {@link #getBean} would return for the given name.
     *
     * 明确给定名称的bean是什么类型的对象,具体来说,{@link #getBean}调用该名称就会返回该类型的对象
     *
     * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
     * as exposed by {@link FactoryBean#getObjectType()}.
     *
     * 对与{@link FactoryBean} 对象,将会返回{@link FactoryBean#getObjectType()}结果
     *
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     *
     * 可以通过别名转为相应的bean.
     * 如果在当前工厂中找不到当前实例,那么就会从父工厂中询问
     *
     * @param name the name of the bean to query
     * @return the type of the bean, or {@code null} if not determinable
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 1.1.2
     * @see #getBean
     * @see #isTypeMatch
     */
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    /**
     * Return the aliases for the given bean name, if any.
     * All of those aliases point to the same bean when used in a {@link #getBean} call.
     *
     * 如果有的话,将会返回给定bean名称的别名
     *
     * <p>If the given name is an alias, the corresponding original bean name
     * and other aliases (if any) will be returned, with the original bean name
     * being the first element in the array.
     *
     * 如果给定的名称是别名,则对象原始bean名称和其他别名(如果有的话)将会返回,原始bean的名称作为数组的第一个元素
     *
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the bean name to check for aliases
     * @return the aliases, or an empty array if none
     * @see #getBean
     */
    String[] getAliases(String name);

}
