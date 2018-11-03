package com.application.beans.defaultSingletonBeanRegistry;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2018/10/24.
 */
@Data
@ToString
public class Student {

    private String name;

    private int age;

    private int score;

    private Teacher teacher;

}
