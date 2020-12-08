package com.cupshe.globallock;

import com.cupshe.globallock.util.KeyProcessor;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println(KeyProcessor.getLockKey("#{  if  }", arg4));
    }
}
