package com.aomatveev.texteditor.primitives;

public class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return getFirst() == pair.getFirst() && getSecond() == pair.getSecond();
    }
}