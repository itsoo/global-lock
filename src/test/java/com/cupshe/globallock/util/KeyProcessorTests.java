package com.cupshe.globallock.util;

import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * KeyProcessorTests
 *
 * @author zxy
 */
public class KeyProcessorTests {

    @Test
    public void test() {
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("id", 1);
        System.out.println(KeyProcessor.getLockKey("#{  id  }", arg1));

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("port", 3306);
        arg2.put("names", Collections.singletonList("ZhangSan"));
        arg2.put("map", new HashMap<>());
        System.out.println(KeyProcessor.getLockKey("#{ port + ':\"abc\"' + names.get(0)  .  length()+map['name'] }", arg2));

        Map<String, Object> arg3 = new HashMap<>();
        arg3.put("port", 1521);
        arg3.put("names", Collections.singletonList("LiSir"));
        System.out.println(KeyProcessor.getLockKey("#{port + ':' + names.get(0) + 'ABC'}", arg3));

        // 验证表达式中出现关键字的场景
        Map<String, Object> arg4 = new HashMap<>();
        arg4.put("if", 1);
        arg4.put("new", 1);
        arg4.put("void", 1);
        arg4.put("while", 1);
        System.out.println(KeyProcessor.getLockKey("#{  if + ':' + new + ':' + void + ':' + while  }", arg4));
    }

    @Test
    public void testOnlyParseKeyFormat() {
        System.out.println(KeyProcessor.getLockKey("#{ port\\\\''abc\\'\\''names }"));
        System.out.println(KeyProcessor.getLockKey("#{ port\\\\\\\\'''abc\\'\\''names }"));
        System.out.println(KeyProcessor.getLockKey("#{ port + '\\':\"abc\" + names.get(0)   .  length()+map[name] }"));
        System.out.println(KeyProcessor.getLockKey("#{ port + '\\':\"abc\" + names.get(0)   .  length()+map['name'] }"));
        System.out.println(KeyProcessor.getLockKey("#{ port + ':\"abc\"' + names.get(0)   .  length()+map[name] }"));
        System.out.println(KeyProcessor.getLockKey("#{ port + ':\"abc\\'\"' + names.get(0) . length()+map[name]}: #{  id  }"));
        System.out.println(KeyProcessor.getLockKey("#{ port + ':\"abc\"' + names.get(0)  .  length()+map[name] } : #{  id  }#{}::#{age}"));
    }

    @Test
    public void testSpelParser() {
        ExpressionParser parser = new SpelExpressionParser();
        ParserContext parserContext = ParserContext.TEMPLATE_EXPRESSION;

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("port", 3306);
        params.put("names", Collections.singletonList("zhangsan"));
        params.put("map", new HashMap<String, String>() {{
            put("name", "lisi");
        }});

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(params);
        String value = parser.parseExpression(
                "#{ #port + ':\"abc\"' + #names.get(0)  .  length()+#map[name] }",
                parserContext)
                .getValue(context, String.class);
        System.out.println(value);
    }
}
