package com.cupshe.globallock.util;

import static com.cupshe.globallock.util.BeggarsLexicalAnalyzer.SimpleFiniteState.*;
import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * BeggarsLexicalAnalyzer
 * <p>It is only used for analysis variables, not handling keywords, complex character types.
 *
 * @author zxy
 */
class BeggarsLexicalAnalyzer {

    static Kvs getResult(String key) {
        if (key == null) {
            return Kvs.emptyKvs();
        }

        Kvs result = new Kvs();
        for (int i = 0, length = key.length(); i < length; i++) {
            char c = key.charAt(i);
            if (c == '\\' && !isEscapeBefore(key, i)) {
                continue;
            }

            if (isLetter(c)) {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                    sbr.append(c);
                }

                result.add(new Kv(VARIABLE, sbr.toString()));
                i--;
            } else if (isEscapeBefore(key, i)) {
                result.add(new Kv(VARCHAR, String.valueOf('\\') + c));
            } else if (c == '\'') {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                while (++i < length && (
                        (c = key.charAt(i)) != '\'' || isEscapeBefore(key, i))) {

                    sbr.append(c);
                }
                if (i < length) {
                    sbr.append(c);
                }

                result.add(new Kv(VARCHAR, sbr.toString()));
            } else if (c == '"') {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                while (++i < length && (
                        (c = key.charAt(i)) != '"' || isEscapeBefore(key, i))) {

                    sbr.append(c);
                }
                if (i < length) {
                    sbr.append(c);
                }

                result.add(new Kv(VARCHAR, sbr.toString()));
            } else if (Character.isDigit(c)) {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                short dotCount = 0;
                while (++i < length && isFullDigit(c = key.charAt(i), key, i)) {
                    if (c == '.') {
                        dotCount++;
                    }
                    if (dotCount > 1) {
                        break;
                    }

                    sbr.append(c);
                }

                result.add(new Kv(DIGIT, sbr.toString()));
                i--;
            } else if (c == '.') {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                while (++i < length && Character.isWhitespace(c = key.charAt(i))) {
                    sbr.append(c);
                }
                if (Character.isDigit(c)) {
                    result.add(new Kv(OTHER, sbr.toString()));
                    i--;
                    continue;
                }

                sbr.append(c);
                if (++i < length && isLetter(c = key.charAt(i))) {
                    sbr.append(c);
                    while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                        sbr.append(c);
                    }
                }

                result.add(new Kv(OTHER, sbr.toString()));
                i--;
            } else if (c == '(' || c == '[' || c == '{') {
                StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                if (c == '(') {
                    i = append(i, length, key, sbr, ')');
                } else if (c == '[') {
                    i = append(i, length, key, sbr, ']');
                } else {
                    i = append(i, length, key, sbr, '}');
                }

                result.add(new Kv(OTHER, sbr.append(key.charAt(i)).toString()));
            } else if (Character.isWhitespace(c)) {
                if (c == ' ') {
                    StringBuilder sbr = new StringBuilder(1 << 3).append(c);
                    while (++i < length && (c = key.charAt(i)) == ' ') {
                        sbr.append(c);
                    }

                    result.add(new Kv(OTHER, sbr.toString()));
                    i--;
                } else {
                    result.add(new Kv(OTHER, String.valueOf(c)));
                }
            } else {
                result.add(new Kv(OTHER, String.valueOf(c)));
            }
        }

        return result;
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c)
                || c == '_'
                || c == '$';
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || Character.isDigit(c);
    }

    private static boolean isFullDigit(char c, String key, int i) {
        return Character.isDigit(c)
                || (('_' == c || '.' == c)
                && Character.isDigit(key.charAt(++i)));
    }

    private static boolean isEscapeBefore(String key, int i) {
        short count = 0;
        while (i > 0 && key.charAt(--i) == '\\') {
            count++;
        }

        return count % 2 != 0;
    }

    private static int append(
            int i, int length, String key, StringBuilder sbr, char sp) {

        char c;
        while (++i < length && (c = key.charAt(i)) != sp) {
            sbr.append(c);
        }

        return i;
    }

    /**
     * SimpleFiniteState
     */
    enum SimpleFiniteState {

        VARIABLE, // variable  e.g. foo
        VARCHAR,  // varchar   e.g. 'foo' or "foo"
        DIGIT,    // numbers   e.g. 1 or 1.0 or 1_000 or 1_000_000.000000
        OTHER;    // others    e.g. empty/clause/delimiter/operator and...

        boolean isVariable() {
            return VARIABLE.equals(this);
        }
    }
}
