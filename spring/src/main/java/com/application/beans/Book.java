package com.application.beans;

/**
 * Created by Administrator on 2018/10/19.
 */
public class Book {

    private Person Person;
    private String name;

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
