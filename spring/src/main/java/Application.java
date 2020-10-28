import com.application.beans.Book;
import com.application.beans.DicBook;
import com.application.beans.Person;
import com.application.beans.defaultSingletonBeanRegistry.Student;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;

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
        Person bean1 = beanFactory.getBean(Person.class);
        DicBook bean = beanFactory.getBean(DicBook.class);
        Book bean2 = (Book) beanFactory.getBean("book");
        System.out.println(bean + bean1.toString());
//        Teacher person = (Teacher) beanFactory.getBean("teacher");
//        System.out.println(person);

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new Student());

        proxyFactory.addAdvice(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("------------------");
                method.invoke(target, args);
            }
        });

        Student proxy = (Student) proxyFactory.getProxy();
        System.out.println(proxy.getName());

//        proxyFactory.addAdvisor();

    }

}
