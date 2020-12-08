package com.cupshe.globallock.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BeggarsLexicalAnalyzer
 * <p>It is only used for analysis variables, not handling keywords, complex character types.
 *
 * @author zxy
 */
class BeggarsLexicalAnalyzer {

    static List<Kv> resultList(String key) {
        if (key == null) {
            return Collections.emptyList();
        }

        List<Kv> result = new ArrayList<>();
        char c;

        for (int i = 0, length = key.length(); i < length; i++) {
            c = key.charAt(i);
            if (isLetter(c)) {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARIABLE, sbr.toString()));
                i--;
            } else if (c == '\'') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && (c = key.charAt(i)) != '\'') {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARCHAR, sbr.append(c).toString()));
            } else if (c == '"') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && (c = key.charAt(i)) != '"') {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARCHAR, sbr.append(c).toString()));
            } else if (Character.isDigit(c)) {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && Character.isDigit(c = key.charAt(i))) {
                    sbr.append(c);
                }

                // Number separation and decimals are not handled
                result.add(new Kv(SimpleFiniteState.DIGIT, sbr.toString()));
                i--;
            } else if (c == '.') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && Character.isWhitespace(c = key.charAt(i))) {
                    sbr.append(c);
                }

                sbr.append(c);
                if (c == '(') {
                    i = append(i, length, key, sbr, ')');
                } else if (c == '[') {
                    i = append(i, length, key, sbr, ']');
                } else if (c == '{') {
                    i = append(i, length, key, sbr, '}');
                } else {
                    if (++i < length && isLetter(c = key.charAt(i))) {
                        sbr.append(c);
                        while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                            sbr.append(c);
                        }
                    }
                }

                result.add(new Kv(SimpleFiniteState.OTHER, sbr.append(c).toString()));
            } else if (Character.isWhitespace(c)) {
                result.add(new Kv(SimpleFiniteState.OTHER, String.valueOf(c)));
            } else {
                result.add(new Kv(SimpleFiniteState.OTHER, String.valueOf(c)));
            }
        }

        return result;
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || Character.isDigit(c);
    }

    private static int append(int i, int length, String key, StringBuilder sbr, char sp) {
        char c;
        while (++i < length && (c = key.charAt(i)) != sp) {
            sbr.append(c);
        }

        return i;
    }

    enum SimpleFiniteState {
        VARIABLE, // variable  e.g. name
        VARCHAR,  // varchar   e.g. 'name' or "name"
        DIGIT,    // numbers   e.g. 10
        OTHER     // others    e.g. empty/clause/delimiter/operator...
    }

    @Data
    @AllArgsConstructor
    static final class Kv {
        final SimpleFiniteState state;
        final String value;
    }
}
