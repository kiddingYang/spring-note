import com.application.beans.Person;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;


/**
 * Created by Administrator on 2018/10/4.
 */
public class Application {


    public static void main(String[] args) {

        BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean.xml"));
        Person person = beanFactory.getBean(Person.class);
        System.out.println(person);

    }

}
