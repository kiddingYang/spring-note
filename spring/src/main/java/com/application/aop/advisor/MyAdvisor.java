package com.application.aop.advisor;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MyAdvisor {

    @Pointcut("execution(* *.*(..))")
    public void test() {

    }

    @Before("test()")
    public void beforeTest() {
        System.out.println("beforeTest");
    }


    @After("test()")
    public void afterTest() {
        System.out.println("afterTest");
    }

}
