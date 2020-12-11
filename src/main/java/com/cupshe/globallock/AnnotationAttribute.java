package com.cupshe.globallock;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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

    private AnnotationAttribute(
            String key, String namespace, long leaseTime, long waitTime, TimeUnit timeUnit, LockedPolicy policy) {

        this.key = key;
        this.namespace = namespace;
        this.leaseTime = leaseTime;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
        this.policy = policy;
    }

    public static AnnotationAttributeBuilder annotationAttributeBuilder() {
        return new AnnotationAttributeBuilder();
    }

    /**
     * AnnotationAttributeBuilder
     */
    @Setter
    @Accessors(chain = true)
    public static class AnnotationAttributeBuilder {
        private String key;
        private String namespace;
        private long leaseTime;
        private long waitTime;
        private TimeUnit timeUnit;
        private LockedPolicy policy;

        private AnnotationAttributeBuilder() {}

        public AnnotationAttribute build() {
            return new AnnotationAttribute(key, namespace, leaseTime, waitTime, timeUnit, policy);
        }
    }
}
