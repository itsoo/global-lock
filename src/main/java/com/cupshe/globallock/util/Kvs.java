package com.cupshe.globallock.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

import static com.cupshe.globallock.util.BeggarsLexicalAnalyzer.SimpleFiniteState;
import static com.cupshe.globallock.util.Kvs.Kv;

/**
 * Kvs
 *
 * @author zxy
 */
class Kvs implements Iterable<Kv> {

    private final Kv head;

    private Kv curr;

    /**
     * Empty kvs
     */
    private static final Kvs EMPTY = new Kvs() {

        final Iterator<Kv> EMPTY_ITR = new Itr() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Kv next() {
                throw new NoSuchElementException("Nothing in the world");
            }
        };

        @Override
        void add(Kv kv) {
            throw new IllegalStateException("Utility class");
        }

        @Override
        public Iterator<Kv> iterator() {
            return EMPTY_ITR;
        }
    };

    Kvs() {
        head = new Kv(null, null);
        curr = head;
    }

    static Kvs emptyKvs() {
        return EMPTY;
    }

    void add(Kv kv) {
        curr.next = kv;
        curr = curr.next;
    }

    @Override
    public Iterator<Kv> iterator() {
        return new Itr();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        this.forEach(t -> joiner.add(t.toString()));
        return "Kvs[" + joiner + ']';
    }

    /**
     * Iterator
     */
    final class Itr implements Iterator<Kv> {

        private Kv itrCurr;

        Itr() {
            itrCurr = head;
        }

        @Override
        public boolean hasNext() {
            return Objects.nonNull(itrCurr.next);
        }

        @Override
        public Kv next() {
            return itrCurr = itrCurr.next;
        }
    }

    /**
     * SimpleFiniteState -> String
     */
    static final class Kv {

        final SimpleFiniteState state;

        final String value;

        Kv next;

        Kv(SimpleFiniteState state, String value) {
            this.state = state;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" +
                    "state=" + state +
                    ", value=" + value +
                    '}';
        }
    }
}
