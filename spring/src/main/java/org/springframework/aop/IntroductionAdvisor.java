/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop;

/**
 * Superinterface for advisors that perform one or more AOP <b>introductions</b>.
 *
 * 执行一个或者多个AOP advisor的顶层接口
 *
 * <p>This interface cannot be implemented directly; subinterfaces must
 * provide the advice type implementing the introduction.
 *
 * 这个接口不能直接被实现，子类接口必须提供一个实现引入的advice
 *
 *
 * <p>Introduction is the implementation of additional interfaces
 * (not implemented by a target) via AOP advice.
 *
 * 引入是附加接口的实现通过AOP advice（不是通过目标对象实现）
 *
 * @author Rod Johnson
 * @since 04.04.2003
 * @see IntroductionInterceptor
 */
public interface IntroductionAdvisor extends Advisor, IntroductionInfo {

    /**
     * Return the filter determining which target classes this introduction
     * should apply to.
     * <p>This represents the class part of a pointcut. Note that method
     * matching doesn't make sense to introductions.
     *
     *
     * 返回一个过滤器，决定目标类是否需要使用引入。
     * 这代表切入点的类部分。方法匹配不会对引入感知。
     *
     *
     * @return the class filter
     */
    ClassFilter getClassFilter();

    /**
     * Can the advised interfaces be implemented by the introduction advice?
     * Invoked before adding an IntroductionAdvisor.
     * @throws IllegalArgumentException if the advised interfaces can't be
     * implemented by the introduction advice
     */
    void validateInterfaces() throws IllegalArgumentException;

}
