package com.cupshe.globallock.util;

import org.junit.Test;

/**
 * BeggarsLexicalAnalyzerTests
 *
 * @author zxy
 */
public class BeggarsLexicalAnalyzerTests {

    @Test
    public void test() {
        System.out.println(BeggarsLexicalAnalyzer.getResult(""));

        System.out.println("===============================");

        System.out.println(BeggarsLexicalAnalyzer.getResult("port\\''abc\\'\\''names"));

        System.out.println("===============================");

        System.out.println(BeggarsLexicalAnalyzer.getResult("port + '\\':abc\\'\"\"' + names.get(0.0) + 'ABC'"));

        System.out.println("===============================");

        System.out.println(BeggarsLexicalAnalyzer.getResult("port'\\':abc\\'\"\"' + names.get(100_000.00)'ABC'"));
    }
}
