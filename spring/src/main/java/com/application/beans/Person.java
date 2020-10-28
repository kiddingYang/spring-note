package com.application.beans;


import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/10/4.
 */
@Data
@ToString
@Test
@Component
public class Person /*implements FactoryBean*/ {

    private String name;

    private int age;

    @Autowired
    private Book book;


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

}
