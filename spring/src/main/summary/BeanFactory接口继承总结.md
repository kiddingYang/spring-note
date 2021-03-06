### spring BeanFactory 接口总结

#### BeanFactory是所有bean容器接口的跟接口,定义了bean容器的最基本的功能.接下来扩展BeanFactory有几条线.
    1.从BeanFactory到HierarchicalBeanFactory在到ConfigurableBeanFactory.
    2.从BeanFactory到AutowireCapableBeanFactory.
    3.从BeanFactory到ListableBeanFactory.
    
#### 下面详细说明这三条路线
    1.从BeanFactory到HierarchicalBeanFactory主要扩展了bean工厂提供getParentBeanFactory()方法,获取父容器的功能.  
    既然有获取父功能的方法,那么一定是有设置父工厂的方法,spring把设置父功能的扩展放在了扩展接口ConfigurableBeanFactory中,  
    提供能setParentBeanFactory(BeanFactory parentBeanFactory)方法来设置一个bean工厂的父工厂.ConfigurableBeanFactory  
    接口除了提供设置工厂的方法还提供了一些关于'配置'工厂功能的扩展.如设置bean的加载器,设置bean的后置处理器,设置bean的转换器  
    注册bean的别名等等,有关工厂本身配置的功能都放在了这个接口.
    
    2.从BeanFactory到AutowireCapableBeanFactory主要扩展了创建bean,自动注入,初始化已经应用bean的后置处理器.
    
    3.从从BeanFactory到ListableBeanFactory主要扩展了一些查询bean的配置清单的功能,比如根据类型获取bean的名称,判断是否包含  
    一个bean名称的BeanDefinition,获取所有BeanDefinition的名称等等.

在上述的几条扩展路线中有一个汇聚点那就是ConfigurableListableBeanFactory,从名称中可以看出,这个bean工厂包括了上述所有bean
工厂的功能,并且还扩展了一些方法,例如根据bean名称获取BeanDefinition,忽略某些指定的类型和接口,冻结definitions的定义等等.在spring
中大部分的实现类都是实现ConfigurableListableBeanFactory接口.

##### BeanFactory 接口继承图 
 ![接口继承图](BeanFactory.png)

