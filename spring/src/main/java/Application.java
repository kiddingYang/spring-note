import com.application.beans.Person;
import com.application.beans.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;


/**
 * Created by Administrator on 2018/10/4.
 */
public class Application {


    public static void main(String[] args) {

        ListableBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean.xml"));
//        Person person = beanFactory.getBean(Person.class);
//        Object personAlias = beanFactory.getBean("personAlias");
//        System.out.println(personAlias);

        Test person = beanFactory.findAnnotationOnBean("person", Test.class);
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(Test.class);
        Map<String, Person> beansOfType = beanFactory.getBeansOfType(Person.class, true, true);
        System.out.println(beansOfType);
        System.out.println(person == null);
        System.out.println(beansWithAnnotation);
    }

}
