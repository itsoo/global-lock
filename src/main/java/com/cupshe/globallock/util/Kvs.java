package com.cupshe.globallock.util;

import org.springframework.lang.NonNull;

import java.util.Iterator;

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

    private static final Kvs EMPTY = new Kvs();

    Kvs() {
        head = Kv.EMPTY;
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
    @NonNull
    public Iterator<Kv> iterator() {
        return new Itr();
    }

    /**
     * Iterator
     */
    final class Itr implements Iterator<Kv> {
        private Kv _curr;

        Itr() {
            _curr = head;
        }

        @Override
        public boolean hasNext() {
            return _curr.next != null;
        }

        @Override
        public Kv next() {
            return _curr = _curr.next;
        }
    }

    /**
     * SimpleFiniteState -> String
     */
    static final class Kv {
        final SimpleFiniteState state;
        final String value;

        private static final Kv EMPTY = new Kv();

        Kv next;

        private Kv() {
            this.state = null;
            this.value = null;
        }

        Kv(SimpleFiniteState state, String value) {
            this.state = state;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Kv(" +
                    "state=" + state +
                    ", value=" + value +
                    ')';
        }
    }
}
