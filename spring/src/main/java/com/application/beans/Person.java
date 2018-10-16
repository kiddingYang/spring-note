package com.application.beans;


import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 2018/10/4.
 */
@Data
@ToString
@Test
public class Person implements FactoryBean {

    private String name;

    private int age;

    @Override
    public Object getObject() throws Exception {
        return new Person();
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
