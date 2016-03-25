package com.aomatveev.texteditor.primitives;

public class SimpleCaret {
    public int lineIndex;
    public int charIndex;
    public int textIndex;

    public SimpleCaret() {
        lineIndex = 0;
        charIndex = 0;
    }

    public SimpleCaret(int lineIndex, int charIndex) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
    }

    public boolean atFileBeginning() {
        return (lineIndex == 0) && (charIndex == 0);
    }

    public boolean atLineBeginning() {
        return charIndex == 0;
    }
}
