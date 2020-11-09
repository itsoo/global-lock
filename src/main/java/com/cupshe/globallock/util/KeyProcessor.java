package com.cupshe.globallock.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KeyProcessor
 *
 * @author zxy
 */
public class KeyProcessor {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public static void checkKeyValidity(String key) {
        Assert.isTrue(!key.contains("${"), "Wrong parameter defined by key.");
    }

    public static String getLockKey(String namespace, String key, Map<String, Object> params) {
        return processStandardLockKey(namespace, getLockKey(key, params));
    }

    public static String getLockKey(String key, Map<String, Object> params) {
        String result = key;
        Matcher m = PATTERN.matcher(key);
        while (m.find()) {
            result = getVariableKey(result, m.group(1), params);
        }

        return result;
    }

    private static String processStandardLockKey(String namespace, String key) {
        return getSampleNamespace(namespace) + getSampleKey(key);
    }

    private static String getSampleNamespace(String namespace) {
        return "".equals(namespace) || ":".equals(namespace) ? "" :
                (namespace.endsWith(":") ? namespace : namespace + ':');
    }

    private static String getSampleKey(String key) {
        return key.charAt(0) == ':' ? key.substring(1) : key;
    }

    private static String getVariableKey(String str, String key, Map<String, Object> params) {
        return getVariableKey(str, key, params.get(key));
    }

    private static String getVariableKey(String str, String key, Object value) {
        return StringUtils.replace(str, "${" + key + '}', valueOf(value));
    }

    private static String valueOf(Object obj) {
        return obj == null ? null : obj.toString();
    }
}
