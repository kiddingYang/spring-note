### BEAN加载总结

#### 单例bean的加载步骤
    1.转换为对应的bean名称
        (1).如果是factoryBean需要把名称前的&去掉
        (2).如果是别名需要找到最终的bean名称
    
    2.尝试从缓存中获取单例bean.
        单例在spring中智慧被创建一次,如果后续在获取bean,就直接从缓存中获取.这里是尝试获取,如果不成功  
        在从singletonFactory中加载.
    
    3.bean的实例化.
        如果从缓存中获取到了bean的原始状态,需要对bean进行实例化.在缓存中记录的只是bean的原始状态,并不  
        一定是我们最终想要的bean.例如我们需要对bean进行处理,而getObjectForBeanFactory就是完成这个工作.
    
    4.依赖检查.
        如果是原型模式,并且有循环依赖的情况抛出异常
     
    5.检查父工厂
        如果在当前工厂中查询不到beanDefinition并且有父工厂的情况下,递归到在父工厂中getBean
    
    6.将配置文件的GernericBeanDefinition转为RootBeanDefinition.
        因为后续处理bean都是针对RootBeanDefinition  
    
    7.寻找依赖
        bean初始化的过程中可能会遇到某些属性,那么就需要先加载这些依赖的bean.
        
    8.针对不同的scope进行创建bean
    
    9.类型转换.
        可能会有这样的情况:如创建的bean可能是个string类型,但是接受requireType是个Integer类型,所有需要  
        转换,spring提供了一些默认的转换器,用户也可以自定义扩展转换器
        
        中文注释:
   [org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean](../../main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java)