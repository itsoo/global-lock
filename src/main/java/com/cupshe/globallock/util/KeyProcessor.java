package com.cupshe.globallock.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * KeyProcessor
 *
 * @author zxy
 */
@Slf4j
public class KeyProcessor {

    private static final Map<String, String> KEYS_CACHE = new ConcurrentHashMap<>(32);

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParserContext PARSER_CONTEXT = ParserContext.TEMPLATE_EXPRESSION;

    public static final String EXPRESSION_DELIMITER_PREFIX = PARSER_CONTEXT.getExpressionPrefix();

    public static final String EXPRESSION_DELIMITER_SUFFIX = PARSER_CONTEXT.getExpressionSuffix();

    private KeyProcessor() {
        throw new IllegalStateException("Utility class");
    }

    public static String getLockKey(String namespace, String key, Map<String, Object> params) {
        return getStandardLockKey(namespace, getLockKey(key, params));
    }

    public static String getLockKey(String key, Map<String, Object> params) {
        String parsedKey = getLockKey(key);
        log.info("Source lock-key: [{}] ===> Parsed lock-key: [{}]", key, parsedKey);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(params);
        return PARSER.parseExpression(parsedKey, PARSER_CONTEXT).getValue(context, String.class);
    }

    static String getLockKey(String key) {
        StringBuilder result = new StringBuilder();
        int i = 0, j = i;
        while ((i = key.indexOf(EXPRESSION_DELIMITER_PREFIX, i)) != -1) {
            i += EXPRESSION_DELIMITER_PREFIX.length();
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
        return (!"".equals(namespace) && !":".equals(namespace))
                ? (namespace.endsWith(":") ? namespace : namespace + ':')
                : "";
    }

    private static String getSampleKey(String key) {
        return (key.startsWith(":") && key.length() > 1) ? key.substring(1) : key;
    }

    private static String getExpressionLockKey(String key) {
        return KEYS_CACHE.computeIfAbsent(key, KeyProcessor::getExpressionVarLockKey);
    }

    private static String getExpressionVarLockKey(String key) {
        StringBuilder result = new StringBuilder(key.length() + 8);
        for (Kv kv : BeggarsLexicalAnalyzer.parseKey(key)) {
            if (kv.state.isVariable()) {
                result.append('#');
            }

            result.append(kv.value);
        }

        return result.toString();
    }
}
