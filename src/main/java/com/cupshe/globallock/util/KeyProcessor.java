package com.cupshe.globallock.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KeyProcessor
 *
 * @author zxy
 */
@Slf4j
public class KeyProcessor {

    private static final Pattern VARIABLE_RULES = Pattern.compile("(?i)(?:\"[^\"]*\"|'[^']*')|[^#.\\[\\w\\s]\\s*\\b[a-z_$][\\w$]*\\b");

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParserContext PARSER_CONTEXT = ParserContext.TEMPLATE_EXPRESSION;

    private static final String EXPRESSION_PREFIX = PARSER_CONTEXT.getExpressionPrefix();

    private static final String EXPRESSION_SUFFIX = PARSER_CONTEXT.getExpressionSuffix();

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
        while ((i = key.indexOf(EXPRESSION_PREFIX, i)) != -1) {
            result.append(key, j, i); // no expression template string
            j = key.indexOf(EXPRESSION_SUFFIX, i);
            Assert.isTrue(j != -1, "Expression format error.");
            result.append(getSubLockKey(key.substring(i, j)));
            i = j;
        }

        return result.append(key.substring(j)).toString();
    }

    private static String getSubLockKey(String key) {
        StringBuilder result = new StringBuilder();
        Matcher m = VARIABLE_RULES.matcher(key);
        int i = 0;
        while (m.find()) {
            result.append(key, i, i = m.start());
            i += getSubLockKeyAndGetLength(result, m.group());
        }

        return result.append(key.substring(i)).toString();
    }

    private static String getStandardLockKey(String namespace, String key) {
        return getSampleNamespace(namespace) + getSampleKey(key);
    }

    private static String getSampleNamespace(String namespace) {
        return "".equals(namespace) || ":".equals(namespace)
                ? ""
                : (namespace.endsWith(":") ? namespace : namespace + ':');
    }

    private static String getSampleKey(String key) {
        return key.charAt(0) == ':' ? key.substring(1) : key;
    }

    private static int getSubLockKeyAndGetLength(StringBuilder sbr, String group) {
        return getExpressionSubLockKey(sbr, group);
    }

    private static int getExpressionSubLockKey(StringBuilder sbr, String group) {
        if (group.startsWith("\"") || group.startsWith("'")) {
            sbr.append(group);
            return group.length();
        }

        return getExpressionSubVarLockKey(sbr, group);
    }

    private static int getExpressionSubVarLockKey(StringBuilder sbr, String group) {
        int i = 0, length = group.length();
        for (char c; i < length; i++) {
            c = group.charAt(i);
            if (Character.isLetter(c)) {
                break;
            }

            sbr.append(c);
        }

        sbr.append('#').append(group.substring(i));
        return length;
    }
}
