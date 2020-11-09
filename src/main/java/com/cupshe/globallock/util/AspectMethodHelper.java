package com.cupshe.globallock.util;

import com.cupshe.globallock.AnnotationAttribute;
import com.cupshe.globallock.GlobalLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AspectMethodHelper
 *
 * @author zxy
 */
public class AspectMethodHelper {

    public static String[] getMethodParameterNames(ProceedingJoinPoint point) {
        return getMethodSignature(point).getParameterNames();
    }

    public static Map<String, Object> getMappingOfParameters(ProceedingJoinPoint point) {
        return getMappingOfParameters(getMethodParameterNames(point), point.getArgs());
    }

    public static Map<String, Object> getMappingOfParameters(String[] params, Object[] args) {
        Assert.isTrue(params.length == args.length, "The params does not match the values.");
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < params.length; i++) {
            result.put(params[i], args[i]);
        }

        return result;
    }

    public static AnnotationAttribute getAnnotationAttribute(ProceedingJoinPoint point) {
        GlobalLock gl = getMethodSignature(point).getMethod().getAnnotation(GlobalLock.class);
        return AnnotationAttribute.of(gl.key(), gl.namespace(), gl.leaseTime(), gl.waitTime(),
                gl.timeUnit(), gl.policy());
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint point) {
        return (MethodSignature) point.getSignature();
    }
}
