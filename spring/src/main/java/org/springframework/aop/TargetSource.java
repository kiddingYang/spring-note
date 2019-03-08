/*<
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
 * A {@code TargetSource} is used to obtain the current "target" of
 * an AOP invocation, which will be invoked via reflection if no around
 * advice chooses to end the interceptor chain itself.
 *
 * 一个TargetSource对象用来获取当前目标对象的一个AOP调用，
 * 将会通过反射的方式被调用，如果没有环绕通知则终止拦截连
 *
 *
 * <p>If a {@code TargetSource} is "static", it will always return
 * the same target, allowing optimizations in the AOP framework. Dynamic
 * target sources can support pooling, hot swapping, etc.
 *
 * 如果TargetSource是“静态的”，会返回相同的目标对象，使AOP框架拥有最佳的性能。
 * 动态的目标源，可以支持池话，热交换等
 *
 *
 * <p>Application developers don't usually need to work with
 * {@code TargetSources} directly: this is an AOP framework interface.

 *
 * @author Rod Johnson
 */
public interface TargetSource extends TargetClassAware {

    /**
     * Return the type of targets returned by this {@link TargetSource}.
     * <p>Can return {@code null}, although certain usages of a
     * {@code TargetSource} might just work with a predetermined
     * target class.
     *
     * 返回TargetSource的目标类，可以返回null，尽管可以预定一个目标类
     *
     * @return the type of targets returned by this {@link TargetSource}
     */
    Class<?> getTargetClass();

    /**
     * Will all calls to {@link #getTarget()} return the same object?
     * <p>In that case, there will be no need to invoke
     * {@link #releaseTarget(Object)}, and the AOP framework can cache
     * the return value of {@link #getTarget()}.
     *
     * 判断是否{@link #getTarget()}返回相同的对象，
     * 如果是相同对象，不需要调用{@link #releaseTarget(Object)}，aop框架会缓存getTarget()返回值
     *
     * @return {@code true} if the target is immutable
     * @see #getTarget
     */
    boolean isStatic();

    /**
     * Return a target instance. Invoked immediately before the
     * AOP framework calls the "target" of an AOP method invocation.
     * @return the target object, which contains the joinpoint
     *
     * 返回目标实例，在AOP框调用AOP方法目标前被立刻调用
     * 返回的目标方法包含连接点
     *
     * @throws Exception if the target object can't be resolved
     */
    Object getTarget() throws Exception;

    /**
     * Release the given target object obtained from the
     *
     * 释放对象
     *
     * {@link #getTarget()} method.
     * @param target object obtained from a call to {@link #getTarget()}
     * @throws Exception if the object can't be released
     */
    void releaseTarget(Object target) throws Exception;

}
