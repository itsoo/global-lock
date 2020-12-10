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

    public static AnnotationAttributeBuilder annotationAttributeBuilder() {
        return new AnnotationAttributeBuilder();
    }

    /**
     * AnnotationAttributeBuilder
     */
    public static class AnnotationAttributeBuilder {
        private String _key;
        private String _namespace;
        private long _leaseTime;
        private long _waitTime;
        private TimeUnit _timeUnit;
        private LockedPolicy _policy;

        private AnnotationAttributeBuilder() {}

        public AnnotationAttribute build() {
            return new AnnotationAttribute(_key, _namespace, _leaseTime, _waitTime, _timeUnit, _policy);
        }

        public AnnotationAttributeBuilder setKey(String key) {
            _key = key;
            return this;
        }

        public AnnotationAttributeBuilder setNamespace(String namespace) {
            _namespace = namespace;
            return this;
        }

        public AnnotationAttributeBuilder setLeaseTime(long leaseTime) {
            _leaseTime = leaseTime;
            return this;
        }

        public AnnotationAttributeBuilder setWaitTime(long waitTime) {
            _waitTime = waitTime;
            return this;
        }

        public AnnotationAttributeBuilder setTimeUnit(TimeUnit timeUnit) {
            _timeUnit = timeUnit;
            return this;
        }

        public AnnotationAttributeBuilder setPolicy(LockedPolicy policy) {
            _policy = policy;
            return this;
        }
    }
}
