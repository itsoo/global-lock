package com.cupshe.globallock.util;

import org.junit.Test;

import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * BeggarsLexicalAnalyzerTests
 *
 * @author zxy
 */
public class BeggarsLexicalAnalyzerTests {

    @Test
    public void test() {
        System.out.println(getKvs(""));

        System.out.println("===============================");

        for (Kv kv : getKvs("port\\''abc\\'\\''names")) {
            System.out.println(kv);
        }

        System.out.println("===============================");

        for (Kv kv : getKvs("port\\\\''abc\\'\\''names")) {
            System.out.println(kv);
        }

        System.out.println("===============================");

        for (Kv kv : getKvs("port + '\\':abc\\'\"\" + names[0.0.0] + 'ABC'.len2()")) {
            System.out.println(kv);
        }

        System.out.println("===============================");

        for (Kv kv : getKvs("port'\\':abc\\'\"\"' + 1_000_000.00.value() + names.get(0)'ABC'")) {
            System.out.println(kv);
        }

        System.out.println("===============================");

        for (Kv kv : getKvs(" port + ':\"abc\"' + names.get(0)   .  length()+map[name] ")) {
            System.out.println(kv);
        }
    }

    private Kvs getKvs(String key) {
        return BeggarsLexicalAnalyzer.parseKey(key);
    }
}
