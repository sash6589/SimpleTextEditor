package com.aomatveev.texteditor.primitives;

public class SimpleCaret {
    public int lineIndex;
    public int charIndex;
    public int textIndex;

    public SimpleCaret() {
        lineIndex = 0;
        charIndex = 0;
        textIndex = 0;
    }

    public SimpleCaret(int lineIndex, int charIndex, int textIndex) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
        this.textIndex = textIndex;
    }
}
