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

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * 绝大部分bean工厂都会实现可配置的接口(也就是说绝大部分bean工厂都会实现该接口).
 * 提供了除{@link org.springframework.beans.factory.BeanFactory}接口提供的方法外,
 * 还提供了配置一个bean工厂
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * 这个bean工厂的接口不该在正常的应用代码中使用,在通常情况下应该使用{@link org.springframework.beans.factory.BeanFactory}
 * 或者{@link org.springframework.beans.factory.ListableBeanFactory}工厂.
 * 这个扩展接口仅仅是允许框架内部对bean工厂配置方法的特殊使用.
 *
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * Scope identifier for the standard singleton scope: "singleton".
     * Custom scopes can be added via {@code registerScope}.
     *
     * 标准的单例的标识符.
     * 可以通过{@code registerScope}来定制bean的范围
     *
     * @see #registerScope
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: "prototype".
     * Custom scopes can be added via {@code registerScope}.
     *
     * 标准的原型标识符
     * 可以通过{@code registerScope}方法来定制bean的范围
     *
     * @see #registerScope
     */
    String SCOPE_PROTOTYPE = "prototype";


    /**
     * Set the parent of this bean factory.
     * <p>Note that the parent cannot be changed: It should only be set outside
     * a constructor if it isn't available at the time of factory instantiation.
     *
     * 设置当前工厂的父工厂
     * 父工厂不能被改变,如果工厂实例化的时候没有可以的构造函数(即没有传入父工厂的构造函数)
     * 应该使用外部的set方法设置.
     *
     * 如果当前工厂已经有一个父工厂,将会有IllegalStateException异常
     *
     * @param parentBeanFactory the parent BeanFactory
     * @throws IllegalStateException if this factory is already associated with
     * a parent BeanFactory
     * @see #getParentBeanFactory()
     */
    void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

    /**
     * Set the class loader to use for loading bean classes.
     * Default is the thread context class loader.
     *
     * 设置class加载器去加载bean的class
     * 默认是当前线程上下文的class加载器
     *
     * <p>Note that this class loader will only apply to bean definitions
     * that do not carry a resolved bean class yet. This is the case as of
     * Spring 2.0 by default: Bean definitions only carry bean class names,
     * to be resolved once the factory processes the bean definition.
     *
     * 需要知道的是类加载仅仅是对beanDefinitions适用,并且不包含已经解析的bean类.
     * 默认情况下spring2.0的bean定义仅仅包含bean类名,一旦bean工厂处理了bean定义,就会解析
     * 这个类
     *
     * @param beanClassLoader the class loader to use,
     * or {@code null} to suggest the default class loader
     */
    void setBeanClassLoader(ClassLoader beanClassLoader);

    /**
     * Return this factory's class loader for loading bean classes.
     *
     * 返回这个bean工厂加载bean class的类加载器
     *
     */
    ClassLoader getBeanClassLoader();

    /**
     * Specify a temporary ClassLoader to use for type matching purposes.
     * Default is none, simply using the standard bean ClassLoader.
     *
     * 指定一个临时的类加载器用来类型匹配。
     * 默认是没有的，使用的是标准的bean类加载器。
     *
     * <p>A temporary ClassLoader is usually just specified if
     * <i>load-time weaving</i> is involved, to make sure that actual bean
     * classes are loaded as lazily as possible. The temporary loader is
     * then removed once the BeanFactory completes its bootstrap phase.
     *
     * 一个临时类加载器通常被指定在加载时织入被使用,用来确保真正bean的class被加载尽可能的慢(用来加载被织入的类).
     * 临时的类加载器将会在bean工厂完成启动阶段后被移除
     *
     *
     * @since 2.5
     */
    void setTempClassLoader(ClassLoader tempClassLoader);

    /**
     * Return the temporary ClassLoader to use for type matching purposes,
     * if any.
     *
     * 如果需要的话，返回临时加载器,用来类型匹配
     *
     * @since 2.5
     */
    ClassLoader getTempClassLoader();

    /**
     * Set whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes. Default is on.
     * <p>Turn this flag off to enable hot-refreshing of bean definition objects
     * and in particular bean classes. If this flag is off, any creation of a bean
     * instance will re-query the bean class loader for newly resolved classes.
     *
     * 设置是否缓存bean的元数据,例如给定的bean定义(已合并的方式)和被解析的bean class.
     * 默认是开启的,关闭这个标签,可以开启beanDefinition和部分bean class的热刷新。
     * 如果这个标志关闭，创建任何bean的实例都会重新查询bean class
     *
     */
    void setCacheBeanMetadata(boolean cacheBeanMetadata);

    /**
     * Return whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes.
     *
     * 返回是否缓存bean的元数据,例如给定的bean定义(已合并的方式)和被解析的bean class.
     *
     */
    boolean isCacheBeanMetadata();

    /**
     * Specify the resolution strategy for expressions in bean definition values.
     * <p>There is no expression support active in a BeanFactory by default.
     * An ApplicationContext will typically set a standard expression strategy
     * here, supporting "#{...}" expressions in a Unified EL compatible style.
     *
     * 在bean定义值中指定表达式的解析策略.
     * 默认情况下,beanFactory是没有支持表达式的.
     * 一个ApplicationContext通常或设置一个标准的表达式解析策略,支持#{...}统一的兼容样式的表达式
     *
     *
     * @since 3.0
     */
    void setBeanExpressionResolver(BeanExpressionResolver resolver);

    /**
     * Return the resolution strategy for expressions in bean definition values.
     *
     * 返回一个bean定义中的表达式的解析策略
     *
     * @since 3.0
     */
    BeanExpressionResolver getBeanExpressionResolver();

    /**
     * Specify a Spring 3.0 ConversionService to use for converting
     * property values, as an alternative to JavaBeans PropertyEditors.
     *
     * 指定用于spring3.0的ConversionService转换属性值,作为JavaBeans的PropertyEditors替代
     *
     * @since 3.0
     */
    void setConversionService(ConversionService conversionService);

    /**
     * Return the associated ConversionService, if any.
     *
     * 如果有的话返回关联的ConversionService
     *
     * @since 3.0
     */
    ConversionService getConversionService();

    /**
     * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
     * <p>Such a registrar creates new PropertyEditor instances and registers them
     * on the given registry, fresh for each bean creation attempt. This avoids
     * the need for synchronization on custom editors; hence, it is generally
     * preferable to use this method instead of {@link #registerCustomEditor}.
     *
     * 在所有bean的创建过程中添加一个PropertyEditorRegistrar
     * 注册器为每个尝试创建的bean创建新的PropertyEditor实例并且注册他们在指定的注册表上,
     * 这样既避免了对定制编辑器的同步需求,因此,通常最好使用这个方法,而不是{@link #registerCustomEditor}.
     *
     * @param registrar the PropertyEditorRegistrar to register
     */
    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    /**
     * Register the given custom property editor for all properties of the
     * given type. To be invoked during factory configuration.
     * <p>Note that this method will register a shared custom editor instance;
     * access to that instance will be synchronized for thread-safety. It is
     * generally preferable to use {@link #addPropertyEditorRegistrar} instead
     * of this method, to avoid for the need for synchronization on custom editors.
     *
     * 对给定类型的所有属性注册给定的自定义的属性编辑器.将会在工厂配置的时候调用.
     * 注意,此方法将注册一个共享的自定义编辑器实例,为了线程安全,需要对实例的方法同步.
     * 通常最好使用{@link #addPropertyEditorRegistrar}替代该方法,以避免在自定义编辑器上同步
     *
     *
     *
     * @param requiredType type of the property
     * @param propertyEditorClass the {@link PropertyEditor} class to register
     */
    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

    /**
     * Initialize the given PropertyEditorRegistry with the custom editors
     * that have been registered with this BeanFactory.
     *
     * 使用以及被bean工厂注册的自定义属性编辑器,初始化给定的PropertyEditorRegistry
     *
     *
     * @param registry the PropertyEditorRegistry to initialize
     */
    void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

    /**
     * Set a custom type converter that this BeanFactory should use for converting
     * bean property values, constructor argument values, etc.
     * <p>This will override the default PropertyEditor mechanism and hence make
     * any custom editors or custom editor registrars irrelevant.
     *
     * 设置此bean工厂使用自定义的类型转换器转换bean属性,构造器参数等
     * 这个会覆盖默认PropertyEditor机制,从而使任何自定义编辑器或者自定义编辑器注册不相关
     *
     * @see #addPropertyEditorRegistrar
     * @see #registerCustomEditor
     * @since 2.5
     */
    void setTypeConverter(TypeConverter typeConverter);

    /**
     * Obtain a type converter as used by this BeanFactory. This may be a fresh
     * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
     * <p>If the default PropertyEditor mechanism is active, the returned
     * TypeConverter will be aware of all custom editors that have been registered.
     *
     * 获得此bean工厂的类型转换器,可能每一个都是新的实例,以为类型转换器通常都是线程不安全的.
     * 如果默认的PropertyEditor机制是激活的,则返回TypeConverter所有注册的自定义编辑器
     *
     * @since 2.5
     */
    TypeConverter getTypeConverter();

    /**
     * Add a String resolver for embedded values such as annotation attributes.
     * 为内嵌的值(如注释属性)添加字符串解析器
     *
     * @param valueResolver the String resolver to apply to embedded values
     * @since 3.0
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * Resolve the given embedded value, e.g. an annotation attribute.
     *
     * 解析给定的内嵌的值,例如注解属性
     *
     * @param value the value to resolve
     * @return the resolved value (may be the original value as-is)
     * @since 3.0
     */
    String resolveEmbeddedValue(String value);

    /**
     * Add a new BeanPostProcessor that will get applied to beans created
     * by this factory. To be invoked during factory configuration.
     * <p>Note: Post-processors submitted here will be applied in the order of
     * registration; any ordering semantics expressed through implementing the
     * {@link org.springframework.core.Ordered} interface will be ignored. Note
     * that autodetected post-processors (e.g. as beans in an ApplicationContext)
     * will always be applied after programmatically registered ones.
     *
     * 添加一个新的bean后置处理器,它将由这个工厂用于bean的创建.在工厂配置期间.
     * 后置处理器将按照注册的顺序应用,任何表示排序语义例如{@link org.springframework.core.Ordered}接口
     * 将会被忽略.注意,自动检测的后置处理器(例如,在ApplicationContext中的bean)将总是在以编程方式注册后应用
     *
     * @param beanPostProcessor the post-processor to register
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * Return the current number of registered BeanPostProcessors, if any.
     *
     * 返回已注册bean后置处理器的当前数量
     *
     */
    int getBeanPostProcessorCount();

    /**
     * Register the given scope, backed by the given Scope implementation.
     *
     * 注册给定的范围,由给的范围实现支持
     *
     * @param scopeName the scope identifier
     * @param scope the backing Scope implementation
     */
    void registerScope(String scopeName, Scope scope);

    /**
     * Return the names of all currently registered scopes.
     * <p>This will only return the names of explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     *
     * 返回所有当前注册范围的名称.
     * 只返回显示注册作用域的名称,内置的如"singleton" and "prototype"将不会暴露
     *
     * @return the array of scope names, or an empty array if none
     * @see #registerScope
     */
    String[] getRegisteredScopeNames();

    /**
     * Return the Scope implementation for the given scope name, if any.
     * <p>This will only return explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     *
     * 返回指定scope名称的实现
     * 只返回显示注册作用域的实现,内置的如"singleton" and "prototype"将不会暴露
     *
     * @param scopeName the name of the scope
     * @return the registered Scope implementation, or {@code null} if none
     * @see #registerScope
     */
    Scope getRegisteredScope(String scopeName);

    /**
     * Provides a security access control context relevant to this factory.
     *
     * 提供与此工厂相关的安全访问控制上下文。
     *
     * @return the applicable AccessControlContext (never {@code null})
     * @since 3.0
     */
    AccessControlContext getAccessControlContext();

    /**
     * Copy all relevant configuration from the given other factory.
     * <p>Should include all standard configuration settings as well as
     * BeanPostProcessors, Scopes, and factory-specific internal settings.
     * Should not include any metadata of actual bean definitions,
     * such as BeanDefinition objects and bean name aliases.
     *
     * 从给定的其他工厂拷贝所有相关的配置.
     * 应该包括,标准配置以及BeanPostProcessors,Scopes和特定由于工厂的内部配置.
     * 不应该包含任何与实际bean定于相关的元数据.例如BeanDefinition和bean的别名
     *
     *
     * @param otherFactory the other BeanFactory to copy from
     */
    void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

    /**
     * Given a bean name, create an alias. We typically use this method to
     * support names that are illegal within XML ids (used for bean names).
     * <p>Typically invoked during factory configuration, but can also be
     * used for runtime registration of aliases. Therefore, a factory
     * implementation should synchronize alias access.
     *
     * 给定一个bean名称,创建一个别名.我们通常是由这种方法来支持XMLid中不合法的名称(用于bean的名称)
     * 通常在工厂配置期间调用,但是也可以用于别名运行时注册.因此,一个工厂实现应该是同步的.
     *
     * @param beanName the canonical name of the target bean
     * @param alias the alias to be registered for the bean
     * @throws BeanDefinitionStoreException if the alias is already in use
     */
    void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

    /**
     * Resolve all alias target names and aliases registered in this
     * factory, applying the given StringValueResolver to them.
     * <p>The value resolver may for example resolve placeholders
     * in target bean names and even in alias names.
     *
     * 解析在此工厂注册的所有别名,目标名称,将给指定的StringValueResolver应用他们.
     * 例如,值解析器可以在目标bean中甚至在别名中解析占位符
     *
     *
     * @param valueResolver the StringValueResolver to apply
     * @since 2.5
     */
    void resolveAliases(StringValueResolver valueResolver);

    /**
     * Return a merged BeanDefinition for the given bean name,
     * merging a child bean definition with its parent if necessary.
     * Considers bean definitions in ancestor factories as well.
     *
     * 返回给定bean名称的合并bean的定义.
     * 必要时将子bean的定义和父bean的定义合并.
     * 还需要考虑父工厂中bean的定义
     *
     *
     * @param beanName the name of the bean to retrieve the merged definition for
     * @return a (potentially merged) BeanDefinition for the given bean
     * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
     * @since 2.5
     */
    BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Determine whether the bean with the given name is a FactoryBean.
     *
     * 判定给定名称的bean是否是一个FactoryBean
     *
     * @param name the name of the bean to check
     * @return whether the bean is a FactoryBean
     * ({@code false} means the bean exists but is not a FactoryBean)
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.5
     */
    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

    /**
     * Explicitly control the current in-creation status of the specified bean.
     * For container-internal use only.
     *
     * 显式地控制指定bean的当前创建状态,只供内部容器使用。
     *
     * @param beanName the name of the bean
     * @param inCreation whether the bean is currently in creation
     * @since 3.1
     */
    void setCurrentlyInCreation(String beanName, boolean inCreation);

    /**
     * Determine whether the specified bean is currently in creation.
     *
     * 确定指定的bean是否正在创建中
     *
     * @param beanName the name of the bean
     * @return whether the bean is currently in creation
     * @since 2.5
     */
    boolean isCurrentlyInCreation(String beanName);

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     *
     * 为给定的bean注册依赖的bean,
     * 需要在指定的bean被销毁前销毁
     *
     *
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     * @since 2.5
     */
    void registerDependentBean(String beanName, String dependentBeanName);

    /**
     * Return the names of all beans which depend on the specified bean, if any.
     *
     * 返回所有依赖于指定bean(如果有的话)的bean的名称。
     *
     * @param beanName the name of the bean
     * @return the array of dependent bean names, or an empty array if none
     * @since 2.5
     */
    String[] getDependentBeans(String beanName);

    /**
     * Return the names of all beans that the specified bean depends on, if any.
     *
     * 返回指定bean所依赖的所有bean的名称(如果有的话)。
     *
     * @param beanName the name of the bean
     * @return the array of names of beans which the bean depends on,
     * or an empty array if none
     * @since 2.5
     */
    String[] getDependenciesForBean(String beanName);

    /**
     * Destroy the given bean instance (usually a prototype instance
     * obtained from this factory) according to its bean definition.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * 根据bean定义销毁给定的bean实例(通常是从该工厂获得的原型实例)。
     * 销毁期间出现的任何异常都应该被捕获并且记录而不是抛出到方法的调用者
     *
     *
     * @param beanName the name of the bean definition
     * @param beanInstance the bean instance to destroy
     */
    void destroyBean(String beanName, Object beanInstance);

    /**
     * Destroy the specified scoped bean in the current target scope, if any.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * 销毁当前目标范围(如果有)中指定的作用域bean
     * 销毁期间出现的任何异常都应该被捕获并且记录而不是抛出到方法的调用者
     *
     * @param beanName the name of the scoped bean
     */
    void destroyScopedBean(String beanName);

    /**
     * Destroy all singleton beans in this factory, including inner beans that have
     * been registered as disposable. To be called on shutdown of a factory.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * 被要求关闭bean工厂时,销毁工厂中的所有单例bean，包括注册为一次性的内部bean.
     * 销毁期间出现的任何异常都应该被捕获并且记录而不是抛出到方法的调用者
     *
     */
    void destroySingletons();

}
