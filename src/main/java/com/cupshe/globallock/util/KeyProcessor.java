package com.cupshe.globallock.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * KeyProcessor
 *
 * @author zxy
 */
@Slf4j
public class KeyProcessor {

    private static final Map<String, String> KEYS_CACHE = new HashMap<>();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParserContext PARSER_CONTEXT = ParserContext.TEMPLATE_EXPRESSION;

    public static final String EXPRESSION_DELIMITER_PREFIX = PARSER_CONTEXT.getExpressionPrefix();

    public static final String EXPRESSION_DELIMITER_SUFFIX = PARSER_CONTEXT.getExpressionSuffix();

    public static String getLockKey(String namespace, String key, Map<String, Object> params) {
        return getStandardLockKey(namespace, getLockKey(key, params));
    }

    public static String getLockKey(String key, Map<String, Object> params) {
        String parsedKey = getLockKey(key);
        log.info("Source lock key: [{}] ===> Parsed lock key: [{}]", key, parsedKey);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(params);
        return PARSER.parseExpression(parsedKey, PARSER_CONTEXT).getValue(context, String.class);
    }

    private static String getLockKey(String key) {
        StringBuilder result = new StringBuilder();
        int i = 0, j = i;
        while ((i = key.indexOf(EXPRESSION_DELIMITER_PREFIX, i)) != -1) {
            result.append(key, j, i); // no expression template delimiter
            j = key.indexOf(EXPRESSION_DELIMITER_SUFFIX, i);
            result.append(getExpressionLockKey(key.substring(i, j)));
            i = j;
        }

        return result.append(key.substring(j)).toString();
    }

    private static String getStandardLockKey(String namespace, String key) {
        return getSampleNamespace(namespace) + getSampleKey(key);
    }

    private static String getSampleNamespace(String namespace) {
        return "".equals(namespace) || ":".equals(namespace)
                ? ""
                : namespace.endsWith(":") ? namespace : namespace + ':';
    }

    private static String getSampleKey(String key) {
        return key.charAt(0) == ':' ? key.substring(1) : key;
    }

    private static String getExpressionLockKey(String key) {
        if (!KEYS_CACHE.containsKey(key)) {
            synchronized (KEYS_CACHE) {
                if (!KEYS_CACHE.containsKey(key)) {
                    KEYS_CACHE.put(key, getExpressionVarLockKey(key));
                }
            }
        }

        return KEYS_CACHE.get(key);
    }

    private static String getExpressionVarLockKey(String key) {
        StringBuilder result = new StringBuilder(key.length() + 8);
        for (Kv kv : BeggarsLexicalAnalyzer.getResult(key)) {
            if (kv.state.isVariable()) {
                result.append('#');
            }

            result.append(kv.value);
        }

        return result.toString();
    }
}
