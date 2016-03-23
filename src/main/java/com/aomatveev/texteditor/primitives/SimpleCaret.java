package com.aomatveev.texteditor.primitives;

public class SimpleCaret {
    public int lineCount;
    public int charCount;

    public SimpleCaret() {
        lineCount = 0;
        charCount = 0;
    }

    public SimpleCaret(int lineCount, int charCount) {
        this.lineCount = lineCount;
        this.charCount = charCount;
    }
}
