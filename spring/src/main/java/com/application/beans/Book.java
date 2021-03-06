package com.application.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018/10/19.
 */
@Component
public class Book {

    @Autowired
    private Person Person;
    private String name;

    @Transactional
    public com.application.beans.Person getPerson() {
        return Person;
    }

    public void setPerson(com.application.beans.Person person) {
        Person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
