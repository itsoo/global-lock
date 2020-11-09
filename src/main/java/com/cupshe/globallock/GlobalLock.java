package com.cupshe.globallock;

import com.cupshe.globallock.exception.KeyExpressionException;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * GlobalLock
 *
 * @author zxy
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalLock {

    /**
     * Redis Key 作为全局锁
     * <p>支持对 ${} 变量的解析（需从方法入参提供），若不匹配将抛出异常 {@link KeyExpressionException}
     *
     * @return String
     */
    String key();

    /**
     * 命名空间，即组成 key 的前缀部分如：{@code GLOBAL:LOCK:}
     * <p>用于和 key 共同组成完整的 redis key，不支持变量解析
     *
     * @return String
     */
    String namespace() default "";

    /**
     * 持有锁的时间（以 {@link TimeUnit} 单位为准，-1L 为不超时）
     *
     * @return long
     */
    long leaseTime();

    /**
     * 获取锁的超时时间（以 {@link TimeUnit} 单位为准）
     *
     * @return long
     */
    long waitTime() default 0;

    /**
     * 时间单位
     *
     * @return {@link TimeUnit}
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 上锁策略
     * <ul>
     *   <li>{@link LockedPolicy#TRY_WAIT} (default)</li>
     *   <li>{@link LockedPolicy#BLOCKING}</li>
     * </ul>
     *
     * @return {@link LockedPolicy}
     */
    LockedPolicy policy() default LockedPolicy.TRY_WAIT;
}
