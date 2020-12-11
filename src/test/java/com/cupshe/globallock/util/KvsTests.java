package com.cupshe.globallock.util;

import org.junit.Test;

import static com.cupshe.globallock.util.BeggarsLexicalAnalyzer.SimpleFiniteState;
import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * KvsTests
 *
 * @author zxy
 */
public class KvsTests {

    @Test
    public void test() {
        Kvs kvs = new Kvs();
        kvs.add(new Kv(SimpleFiniteState.VARCHAR, "names"));
        kvs.add(new Kv(SimpleFiniteState.OTHER, ".get(0)"));
        kvs.add(new Kv(SimpleFiniteState.OTHER, " "));
        kvs.add(new Kv(SimpleFiniteState.OTHER, "+"));
        kvs.add(new Kv(SimpleFiniteState.OTHER, " "));
        kvs.add(new Kv(SimpleFiniteState.DIGIT, "1"));

        for (Kv kv : kvs) {
            System.out.println(kv);
        }
    }
}
