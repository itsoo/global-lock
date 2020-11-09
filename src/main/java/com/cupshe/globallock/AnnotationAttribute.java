package com.cupshe.globallock;

import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * AnnotationAttribute
 *
 * @author zxy
 */
@ToString
public class AnnotationAttribute {

    final String key;
    final String namespace;
    final long leaseTime;
    final long waitTime;
    final TimeUnit timeUnit;
    final LockedPolicy policy;

    private AnnotationAttribute(String key, String namespace, long leaseTime, long waitTime,
                                TimeUnit timeUnit, LockedPolicy policy) {
        this.key = key;
        this.namespace = namespace;
        this.leaseTime = leaseTime;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
        this.policy = policy;
    }

    public static AnnotationAttribute of(String key, String namespace, long leaseTime, long waitTime,
                                         TimeUnit timeUnit, LockedPolicy policy) {
        return new AnnotationAttribute(key, namespace, leaseTime, waitTime, timeUnit, policy);
    }
}
