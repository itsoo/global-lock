package com.cupshe.globallock.util;

import org.springframework.lang.NonNull;

import java.util.Iterator;

import static com.cupshe.globallock.util.BeggarsLexicalAnalyzer.SimpleFiniteState;

/**
 * Kvs
 *
 * @author zxy
 */
class Kvs implements Iterable<Kv> {

    static final Kvs EMPTY = new Kvs();

    private final Kv head;
    private Kv curr;

    Kvs() {
        head = Kv.EMPTY;
        curr = head;
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
}

/**
 * Kv
 * <p>state is {@code SimpleFiniteState.class}
 * <p>value is {@code String.class}
 *
 * @author zxy
 */
final class Kv {

    static final Kv EMPTY = new Kv();

    Kv next;

    final SimpleFiniteState state;
    final String value;

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
