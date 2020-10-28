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
 * Interface supplying the information necessary to describe an introduction.
 *
 * 接口提供了描述了一个引入需要必须的信息
 *
 * <p>{@link IntroductionAdvisor IntroductionAdvisors} must implement this
 * interface. If an {@link org.aopalliance.aop.Advice} implements this,
 * it may be used as an introduction without an {@link IntroductionAdvisor}.
 * In this case, the advice is self-describing, providing not only the
 * necessary behavior, but describing the interfaces it introduces.
 *
 * {IntroductionAdvisor} 必须实现该接口，如果{@link org.aopalliance.aop.Advice} 实现了，
 * 它可以脱离{IntroductionAdvisor}接口使用引入。
 * 在当前例子中，advice是一个描述，提供必要的行为描述，还描述引入的接口
 *
 *
 * @author Rod Johnson
 * @since 1.1.1
 */
public interface IntroductionInfo {

    /**
     * Return the additional interfaces introduced by this Advisor or Advice.
     * 返回附加的在当前advisor或者advice中引入接口，
     *
     * @return the introduced interfaces
     */
    Class[] getInterfaces();

}
