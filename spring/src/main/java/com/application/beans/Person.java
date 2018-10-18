package com.application.beans;


import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2018/10/4.
 */
@Data
@ToString
@Test
public class Person /*implements FactoryBean*/ {

    private String name;

    private int age;


//    @Override
//    public Object getObject() throws Exception {
//        return new Person();
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return Person.class;
//    }
//
//    @Override
//    public boolean isSingleton() {
//        return false;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
