package com.application.beans.defaultSingletonBeanRegistry;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2018/10/24.
 */
@Data
@ToString
public class Teacher {

    private String name;

    private Student student;

    private String course;

}
