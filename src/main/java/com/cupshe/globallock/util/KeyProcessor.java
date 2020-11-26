package com.cupshe.globallock.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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

    private static final Pattern PATTERN = Pattern.compile("#\\{\\s*[$_A-Za-z][\\w$]+|\\b[$_A-Za-z][\\w$]+\\.");

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParserContext PARSER_CONTEXT = ParserContext.TEMPLATE_EXPRESSION;

    private static final String EXPRESSION_PREFIX = PARSER_CONTEXT.getExpressionPrefix();

    public static String getLockKey(String namespace, String key, Map<String, Object> params) {
        return getStandardLockKey(namespace, getLockKey(key, params));
    }

    public static String getLockKey(String key, Map<String, Object> params) {
        String parseKey = getLockKey(key);
        log.info("Source lock key: [{}] ===> Parsed lock key: [{}]", key, parseKey);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(params);
        return PARSER.parseExpression(parseKey, PARSER_CONTEXT).getValue(context, String.class);
    }

    private static String getLockKey(String key) {
        StringBuilder result = new StringBuilder();
        Matcher m = PATTERN.matcher(key);
        int i = 0;
        while (m.find()) {
            result.append(key, i, i = m.start());
            i += processSubLockKeyAndGetLength(result, m.group());
        }

        return result.append(key.substring(i)).toString();
    }

    private static String getStandardLockKey(String ns, String k) {
        return getSampleNamespace(ns) + getSampleKey(k);
    }

    private static String getSampleNamespace(String ns) {
        return "".equals(ns) || ":".equals(ns) ? "" : (ns.endsWith(":") ? ns : ns + ':');
    }

    private static String getSampleKey(String k) {
        return k.charAt(0) == ':' ? k.substring(1) : k;
    }

    private static int processSubLockKeyAndGetLength(StringBuilder sbr, String group) {
        boolean isRoots = group.startsWith(EXPRESSION_PREFIX);
        return isRoots ? processTplExpSubLockKey(sbr, group) : processSimpleSubLockKey(sbr, group);
    }

    private static int processTplExpSubLockKey(StringBuilder sbr, String group) {
        sbr.append(EXPRESSION_PREFIX);
        processSimpleSubLockKey(sbr, group.substring(EXPRESSION_PREFIX.length()));
        return group.length();
    }

    private static int processSimpleSubLockKey(StringBuilder sbr, String group) {
        int i = 0, length = group.length();
        for (char c; i < length; i++) {
            c = group.charAt(i);
            if (!Character.isWhitespace(c)) {
                break;
            }

            sbr.append(c);
        }

        sbr.append('#').append(group.substring(i));
        return length;
    }
}
