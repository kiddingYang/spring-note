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

package org.springframework.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic registry for shared bean instances, implementing the
 * {@link org.springframework.beans.factory.config.SingletonBeanRegistry}.
 * Allows for registering singleton instances that should be shared
 * for all callers of the registry, to be obtained via bean name.
 *
 * 共享bean实例的通用注册中心,实现{@link org.springframework.beans.factory.config.SingletonBeanRegistry}.
 * 允许注册表中心注册的单例可以通过bean名称被所有调用者共享
 *
 * <p>Also supports registration of
 * {@link org.springframework.beans.factory.DisposableBean} instances,
 * (which might or might not correspond to registered singletons),
 * to be destroyed on shutdown of the registry. Dependencies between
 * beans can be registered to enforce an appropriate shutdown order.
 *
 * 还支持注册{@link org.springframework.beans.factory.DisposableBean}的实例.
 * (可能对应也可能不对应已注册的单例),在注册中心时destroyed.
 * 可以注册bean的依赖关系,已适当的顺序关闭.
 *
 *
 * <p>This class mainly serves as base class for
 * {@link org.springframework.beans.factory.BeanFactory} implementations,
 * factoring out the common management of singleton bean instances. Note that
 * the {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * interface extends the {@link SingletonBeanRegistry} interface.
 *
 * 这个类的主要作为{@link org.springframework.beans.factory.BeanFactory}实现的基类,
 * 分解出单例bean的通用管理.注意{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}接口
 * 扩展了该接口
 *
 *
 * <p>Note that this class assumes neither a bean definition concept
 * nor a specific creation process for bean instances, in contrast to
 * {@link AbstractBeanFactory} and {@link DefaultListableBeanFactory}
 * (which inherit from it). Can alternatively also be used as a nested
 * helper to delegate to.
 *
 * 这个类与{@link AbstractBeanFactory} 和 {@link DefaultListableBeanFactory}不同.
 * 这个类既不假设beanDefinition概念,也不假定bean实例创建的过程.可以作为一个委托的帮助类.
 *
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #registerSingleton
 * @see #registerDisposableBean
 * @see org.springframework.beans.factory.DisposableBean
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

    /**
     * Internal marker for a null singleton object:
     * used as marker value for concurrent Maps (which don't support null values).
     *
     * 内部标记为一个空的单例对象:用作并发maps(不支持空值)的标记值
     * 由于Map不能存放null，因此用一个特殊的对象表示null
     *
     */
    protected static final Object NULL_OBJECT = new Object();


    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());


    /** Cache of singleton objects: bean name --> bean instance */
    // 单例对象的缓存: bean的名称和bean的实例
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(64);

    /** Cache of singleton factories: bean name --> ObjectFactory */
    // 单例工厂的缓存:bean的名称和ObjectFactory(ObjectFactory.getObject 是用来获取bean的)
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>(16);

    /** Cache of early singleton objects: bean name --> bean instance */
    // 提前生成的bean实例的缓存: bean的名称和bean的实例 (用于解决循环依赖)
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    /** Set of registered singletons, containing the bean names in registration order */
    // 已经注册的单例名称(包含注册顺序)
    private final Set<String> registeredSingletons = new LinkedHashSet<String>(64);

    /** Names of beans that are currently in creation (using a ConcurrentHashMap as a Set) */
    // 正在创建bean的名称(使用ConcurrentHashMap代替Set) guava的Sets.newConcurrentHashSet()也是类似的实现主要是用来实现线程安全
    private final Map<String, Boolean> singletonsCurrentlyInCreation = new ConcurrentHashMap<String, Boolean>(16);

    /** Names of beans currently excluded from in creation checks (using a ConcurrentHashMap as a Set) */
    // 缓存当前不加载的bean
    private final Map<String, Boolean> inCreationCheckExclusions = new ConcurrentHashMap<String, Boolean>(16);

    /** List of suppressed Exceptions, available for associating related causes */
    // 异常
    private Set<Exception> suppressedExceptions;

    /** Flag that indicates whether we're currently within destroySingletons */
    // 标志是否当前处于单例被销毁
    private boolean singletonsCurrentlyInDestruction = false;

    /** Disposable bean instances: bean name --> disposable instance */
    // Disposable的bean实例: bean名称和Disposable实例(即实现DisposableBean接口的bean,需要做后置处理)
    private final Map<String, Object> disposableBeans = new LinkedHashMap<String, Object>();

    /** Map between containing bean names: bean name --> Set of bean names that the bean contains */
    // 包含bean名称和bean名称映射:bean名称 对应 该bean包含的所有bean的名称(例如,bean A 中注入的bean B,C 那么就是 A -> B,C)
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<String, Set<String>>(16);

    /** Map between dependent bean names: bean name --> Set of dependent bean names */
    // bean依赖关系的映射: bean名称对应 依赖于该bean的所有bean的名称(value的bean先于key表示的bean被销毁)
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<String, Set<String>>(64);

    /** Map between depending bean names: bean name --> Set of bean names for the bean's dependencies */
    // bean依赖关系的缓存,bean名称对应该bean依赖所有的bean的名称(就是说如果创建key表示bean,需要创建value中的bean)
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<String, Set<String>>(64);


    /**
     *  注册一个单例的bean
     */
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            // 如果该bean名称已经注册,抛出异常
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    /**
     * Add the given singleton object to the singleton cache of this factory.
     * 添加给定单例对象到该工厂的单例缓存中
     * <p>To be called for eager registration of singletons.
     * 为了给提前注册的单例调用
     * @param beanName the name of the bean
     * @param singletonObject the singleton object
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            // 如果为NULL_OBJECT则不加入,添加到已注册的集合中,移除相应的ObjectFactory和earlySingleton
            this.singletonObjects.put(beanName, (singletonObject != null ? singletonObject : NULL_OBJECT));
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /**
     * Add the given singleton factory for building the specified singleton
     * if necessary.
     * <p>To be called for eager registration of singletons, e.g. to be able to
     * resolve circular references.
     * 添加给定的单例工厂,以便在必要时用来构建指定的单例.
     * 为了给提前注册的单例调用,例如,解析循环引用
     *
     * @param beanName the name of the bean
     * @param singletonFactory the factory for the singleton object
     */
    protected void addSingletonFactory(String beanName, ObjectFactory singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            // 如果当前singletonObjects不包含该bean,添加到singletonFactory中,添加到已注册的名称集合中,移除earlySingleton
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }

    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Checks already instantiated singletons and also allows for an early
     * reference to a currently created singleton (resolving a circular reference).
     *
     * 返回指定名称注册的(原始)单例对象.
     * 检查已经初始化的单例和允许对当前创建的单例对象提前引用(解决循环引用)
     *
     * @param beanName the name of the bean to look for
     * @param allowEarlyReference whether early references should be created or not
     * @return the registered singleton object, or {@code null} if none found
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 在已注册的单例对象中获取
        Object singletonObject = this.singletonObjects.get(beanName);
        // 如果返回为空,并且bean在创建中
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                // 从提前引用的工厂中获取
                singletonObject = this.earlySingletonObjects.get(beanName);
                // 如果提前引用为空并且允许提前引用
                if (singletonObject == null && allowEarlyReference) {
                    // 查询该bean的ObjectFactory是否存在
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    // 如果ObjectFactory存在,把该bean创建出来并且放入提前引用的集合earlySingletonObjects中,并且在singletonFactories移除
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return (singletonObject != NULL_OBJECT ? singletonObject : null);
    }

    /**
     * Return the (raw) singleton object registered under the given name,
     * creating and registering a new one if none registered yet.
     *
     * 返回指定名称注册的(原始)单例对象,如果还没有注册,就创建注册一个新的
     *
     * @param beanName the name of the bean
     * @param singletonFactory the ObjectFactory to lazily create the singleton
     * with, if necessary
     * @return the registered singleton object
     */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            // 从singletonObjects获取bean实例
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                // 如果没有获取到,判断是否正在销毁中,如果在销毁中,抛出异常
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName,
                            "Singleton bean creation not allowed while the singletons of this factory are in destruction " +
                                    "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                // 放入正在创建的集合中
                beforeSingletonCreation(beanName);
                boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<Exception>();
                }
                try {
                    // 使用ObjectFactory创建实例
                    singletonObject = singletonFactory.getObject();
                }
                catch (BeanCreationException ex) {
                    // 创建失败,记录异常
                    if (recordSuppressedExceptions) {
                        for (Exception suppressedException : this.suppressedExceptions) {
                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                }
                finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    // 从正在创建的集合中移除
                    afterSingletonCreation(beanName);
                }
                // 添加到缓存中
                addSingleton(beanName, singletonObject);
            }
            return (singletonObject != NULL_OBJECT ? singletonObject : null);
        }
    }

    /**
     * Register an Exception that happened to get suppressed during the creation of a
     * singleton bean instance, e.g. a temporary circular reference resolution problem.
     * @param ex the Exception to register
     */
    protected void onSuppressedException(Exception ex) {
        synchronized (this.singletonObjects) {
            if (this.suppressedExceptions != null) {
                this.suppressedExceptions.add(ex);
            }
        }
    }

    /**
     * Remove the bean with the given name from the singleton cache of this factory,
     * to be able to clean up eager registration of a singleton if creation failed.
     *
     * 移除工厂中指定bean名称的单例缓存,以便在创建失败时能够清理提前注册的单例
     *
     * @param beanName the name of the bean
     * @see #getSingletonMutex()
     */
    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    // 检查是否包含该bean的实例
    public boolean containsSingleton(String beanName) {
        return (this.singletonObjects.containsKey(beanName));
    }

    // 返回已注册的单例bean名称
    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }

    // 返回已注册单例的数量
    public int getSingletonCount() {
        synchronized (this.singletonObjects) {
            return this.registeredSingletons.size();
        }
    }


    // 设置bean正在被创建,如果不在创建中放入inCreationCheckExclusions中,否则从inCreationCheckExclusions移除
    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        Assert.notNull(beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.put(beanName, Boolean.TRUE);
        }
        else {
            this.inCreationCheckExclusions.remove(beanName);
        }
    }

    // 判断bean是否正在创建中,并且不再不可创建的集合中
    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return (!this.inCreationCheckExclusions.containsKey(beanName) && isActuallyInCreation(beanName));
    }

    // 判断bean是否在创建中
    protected boolean isActuallyInCreation(String beanName) {
        return isSingletonCurrentlyInCreation(beanName);
    }

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     *
     * 返回指定的单例bean是否当前正在创建中(在整个工厂内)
     *
     * @param beanName the name of the bean
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.containsKey(beanName);
    }

    /**
     * Callback before singleton creation.
     * <p>Default implementation register the singleton as currently in creation.
     * 在创建单例之前回调,默认实现是注册当前注册的实例在创建中.
     *
     * @param beanName the name of the singleton about to be created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void beforeSingletonCreation(String beanName) {
        // 如果bean不在不能加载bean的集合中并且放入正在创建中集合失败,抛出异常
        if (!this.inCreationCheckExclusions.containsKey(beanName) &&
                this.singletonsCurrentlyInCreation.put(beanName, Boolean.TRUE) != null) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    /**
     * Callback after singleton creation.
     * <p>The default implementation marks the singleton as not in creation anymore.
     *
     * 在创建单例之后回调,默认实现是将单例对象标记为不在创建
     *
     * @param beanName the name of the singleton that has been created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void afterSingletonCreation(String beanName) {
        // 如果bean不在不能加载bean的集合中并且从创建中集合移除失败,抛出异常
        if (!this.inCreationCheckExclusions.containsKey(beanName) &&
                !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }


    /**
     * Add the given bean to the list of disposable beans in this registry.
     * <p>Disposable beans usually correspond to registered singletons,
     * matching the bean name but potentially being a different instance
     * (for example, a DisposableBean adapter for a singleton that does not
     * naturally implement Spring's DisposableBean interface).
     *
     * 将给定的disposable bean添加到此注册中心集合中,
     * Disposable的bean通常是已经注册的bean,与bean的名称匹配但是可能是不能的实例.
     * (例如,对于单例对象的DisposableBean适配器,该适配器不会天然实现spring的DisposableBean接口)
     *
     *
     * @param beanName the name of the bean
     * @param bean the bean instance
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        synchronized (this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }

    /**
     * Register a containment relationship between two beans,
     * e.g. between an inner bean and its containing outer bean.
     * <p>Also registers the containing bean as dependent on the contained bean
     * in terms of destruction order.
     *
     * 注册两个bean之间的关系,例如内部bean和外部bean.
     * 还根据销毁顺序将注册的bean依赖于内部bean。
     *
     * @param containedBeanName the name of the contained (inner) bean
     * @param containingBeanName the name of the containing (outer) bean
     * @see #registerDependentBean
     */
    // 如bean A中注入了B和C 那么需要A -> B,C
    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized (this.containedBeanMap) {
            // 根据inner 查询对于outer的集合,如果为空则创建一个空集合
            Set<String> containedBeans = this.containedBeanMap.get(containingBeanName);
            if (containedBeans == null) {
                containedBeans = new LinkedHashSet<String>(8);
                this.containedBeanMap.put(containingBeanName, containedBeans);
            }
            // 把inner添加到集合中
            containedBeans.add(containedBeanName);
        }
        // 注册bean的依赖关系
        registerDependentBean(containedBeanName, containingBeanName);
    }

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     *
     * 注册指定bean依赖的bean,需要在指定的bean销毁之前被销毁
     *
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     */
    public void registerDependentBean(String beanName, String dependentBeanName) {
        // 解析bean的名称
        String canonicalName = canonicalName(beanName);
        synchronized (this.dependentBeanMap) {
            // 如果依赖指定bean的集合为空,创建一个空集合
            Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
            if (dependentBeans == null) {
                dependentBeans = new LinkedHashSet<String>(8);
                this.dependentBeanMap.put(canonicalName, dependentBeans);
            }
            // 将依赖指定bean的bean添加到集合中
            dependentBeans.add(dependentBeanName);
        }
        // 注册dependentBeanName的依赖关系
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet<String>(8);
                this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
            }
            dependenciesForBean.add(canonicalName);
        }
    }

    /**
     * Determine whether a dependent bean has been registered for the given name.
     *
     * 判断是否给指定bean名称注册了依赖的bean
     *
     * @param beanName the name of the bean to check
     */
    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    /**
     * Return the names of all beans which depend on the specified bean, if any.
     *
     * 返回所有依赖于指定bean(如果有的话)的bean名称.
     *
     * @param beanName the name of the bean
     * @return the array of dependent bean names, or an empty array if none
     */
    public String[] getDependentBeans(String beanName) {
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        return StringUtils.toStringArray(dependentBeans);
    }

    /**
     * Return the names of all beans that the specified bean depends on, if any.
     *
     * 返回指定bean所依赖的所有bean的名称(如果有的话).
     *
     * @param beanName the name of the bean
     * @return the array of names of beans which the bean depends on,
     * or an empty array if none
     */
    public String[] getDependenciesForBean(String beanName) {
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        return dependenciesForBean.toArray(new String[dependenciesForBean.size()]);
    }

    // 销毁单例
    public void destroySingletons() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroying singletons in " + this);
        }
        // 置位销毁状态
        synchronized (this.singletonObjects) {
            this.singletonsCurrentlyInDestruction = true;
        }

        String[] disposableBeanNames;
        synchronized (this.disposableBeans) {
            disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
        }
        // 销毁bean
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            destroySingleton(disposableBeanNames[i]);
        }

        // 清理缓存
        this.containedBeanMap.clear();
        this.dependentBeanMap.clear();
        this.dependenciesForBeanMap.clear();

        synchronized (this.singletonObjects) {
            this.singletonObjects.clear();
            this.singletonFactories.clear();
            this.earlySingletonObjects.clear();
            this.registeredSingletons.clear();
            this.singletonsCurrentlyInDestruction = false;
        }
    }

    /**
     * Destroy the given bean. Delegates to {@code destroyBean}
     * if a corresponding disposable bean instance is found.
     *
     * 销毁指定的bean,如果找到了destroyBean实例,委托给{@code destroyBean}处理
     *
     * @param beanName the name of the bean
     * @see #destroyBean
     */
    public void destroySingleton(String beanName) {
        // Remove a registered singleton of the given name, if any.
        // 移除已注册的单例
        removeSingleton(beanName);

        // Destroy the corresponding DisposableBean instance.
        DisposableBean disposableBean;
        synchronized (this.disposableBeans) {
            // 移除DisposableBean
            disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
        }
        // 销毁bean
        destroyBean(beanName, disposableBean);
    }

    /**
     * Destroy the given bean. Must destroy beans that depend on the given
     * bean before the bean itself. Should not throw any exceptions.
     *
     * 销毁给定的bean.必须在bean本身之前销毁依赖的bean,不应该抛出任何异常。
     *
     * @param beanName the name of the bean
     * @param bean the bean instance to destroy
     */
    protected void destroyBean(String beanName, DisposableBean bean) {
        // Trigger destruction of dependent beans first...
        // 查找依赖的bean
        Set<String> dependencies = this.dependentBeanMap.remove(beanName);
        if (dependencies != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }
            for (String dependentBeanName : dependencies) {
                // 递归销毁依赖的bean
                destroySingleton(dependentBeanName);
            }
        }

        // Actually destroy the bean now...
        // 如果是DisposableBean调用destroy方法
        if (bean != null) {
            try {
                bean.destroy();
            }
            catch (Throwable ex) {
                logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", ex);
            }
        }

        // Trigger destruction of contained beans...
        // 销毁包含的bean
        Set<String> containedBeans = this.containedBeanMap.remove(beanName);
        if (containedBeans != null) {
            for (String containedBeanName : containedBeans) {
                // 递归销毁bean
                destroySingleton(containedBeanName);
            }
        }

        // Remove destroyed bean from other beans' dependencies.
        // 从其他bean的依赖项中删除被销毁的bean
        synchronized (this.dependentBeanMap) {
            for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Set<String>> entry = it.next();
                Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }

        // Remove destroyed bean's prepared dependency information.
        // 移除被销毁bean准备的依赖消息
        this.dependenciesForBeanMap.remove(beanName);
    }

    /**
     * Expose the singleton mutex to subclasses.
     * <p>Subclasses should synchronize on the given Object if they perform
     * any sort of extended singleton creation phase. In particular, subclasses
     * should <i>not</i> have their own mutexes involved in singleton creation,
     * to avoid the potential for deadlocks in lazy-init situations.
     *
     *
     * 将单例互斥对象暴露给子类.
     * 如果子类执行任何类型的扩展单例对象的创建节点,需要同步在指定的对象像,
     * 特别是,子类不应该在单例创建中包含自己互斥的对象,避免在懒加载情况下死锁
     *
     */
    protected final Object getSingletonMutex() {
        return this.singletonObjects;
    }

}
