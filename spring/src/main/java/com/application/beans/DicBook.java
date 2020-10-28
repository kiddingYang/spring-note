package com.application.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DicBook extends Book {

    @Autowired
    private Person Person;
    private String name;


    @Override
    public com.application.beans.Person getPerson() {
        return Person;
    }

    @Override
    public void setPerson(com.application.beans.Person person) {
        Person = person;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
