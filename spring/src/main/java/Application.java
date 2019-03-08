import com.application.beans.defaultSingletonBeanRegistry.Teacher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2018/10/4.
 */
public class Application {


    public static void main(String[] args) {

//        ListableBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean.xml"));
//        Person person = beanFactory.getBean(Person.class);
//        Object personAlias = beanFactory.getBean("personAlias");
//        System.out.println(personAlias);

//        Test person = beanFactory.findAnnotationOnBean("person", Test.class);
//        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(Test.class);
//        Map<String, Person> beansOfType = beanFactory.getBeansOfType(Person.class, true, true);
//        System.out.println(beansOfType);
//        System.out.println(person == null);
//        System.out.println(beansWithAnnotation);


        // AutowireCapableBeanFactory 测试

//        AutowireCapableBeanFactory autowireCapableBeanFactory = new XmlBeanFactory(new ClassPathResource("bean.xml"));
//        Book autowire = (Book) autowireCapableBeanFactory.autowire(Book.class, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
//        Book bean = autowireCapableBeanFactory.getBean(Book.class);
//        System.out.println(autowire.getPerson());


//        DefaultSingletonBeanRegistry registry = new DefaultSingletonBeanRegistry();


//        ListableBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean.xml"));
        ApplicationContext beanFactory = new ClassPathXmlApplicationContext("bean.xml");
        Teacher person = (Teacher) beanFactory.getBean("teacher");
        System.out.println(person);

//        ProxyFactory proxyFactory = new ProxyFactory();
//        proxyFactory.setTarget(new Student());
//
//        proxyFactory.addAdvice((MethodBeforeAdvice) (method, args1, target) -> {
//            System.out.println("before invoke");
//            method.invoke(target, args1);
//        });
//
//        Student proxy = (Student) proxyFactory.getProxy();
//        System.out.println(proxy.getName());

//        proxyFactory.addAdvisor();

    }

}
