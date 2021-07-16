package com.cupshe.globallock.util;

import com.cupshe.globallock.AnnotationAttribute;
import com.cupshe.globallock.GlobalLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AspectMethodHelper
 *
 * @author zxy
 */
public class AspectMethodHelper {

    private AspectMethodHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Object> getMappingOfParameters(ProceedingJoinPoint point) {
        return getMappingOfParameters(getMethodParameterNames(point), point.getArgs());
    }

    public static Map<String, Object> getMappingOfParameters(String[] keys, Object[] values) {
        Assert.isTrue(keys.length == values.length, "The parameters does not match the values.");
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }

        return result;
    }

    public static AnnotationAttribute getAnnotationAttribute(ProceedingJoinPoint point) {
        Method method = getMethod(point);
        GlobalLock ann = AnnotationUtils.findAnnotation(method, GlobalLock.class);
        Assert.notNull(ann, method.toGenericString() + ": cannot found annotation @GlobalLock.");
        return AnnotationAttribute.annotationAttributeBuilder()
                .setKey(ann.key())
                .setNamespace(ann.namespace())
                .setLeaseTime(ann.leaseTime())
                .setWaitTime(ann.waitTime())
                .setTimeUnit(ann.timeUnit())
                .setPolicy(ann.policy())
                .build();
    }

    private static String[] getMethodParameterNames(ProceedingJoinPoint point) {
        return getMethodSignature(point).getParameterNames();
    }

    private static Method getMethod(ProceedingJoinPoint point) {
        return getMethodSignature(point).getMethod();
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint point) {
        return (MethodSignature) point.getSignature();
    }
}
