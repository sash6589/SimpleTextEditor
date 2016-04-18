package com.aomatveev.texteditor.primitives;

import com.aomatveev.texteditor.model.SimpleDocument;

public class SimpleCaret implements Comparable<SimpleCaret> {
    public int lineIndex;
    public int charIndex;

    public SimpleCaret(int lineIndex, int charIndex) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
    }

    public SimpleCaret(SimpleCaret other) {
        lineIndex = other.lineIndex;
        charIndex = other.charIndex;
    }

    public boolean atBeginningFile() {
        return (lineIndex == 0) && (charIndex == 0);
    }

    public boolean atBeginningLine() {
        return charIndex == 0;
    }

    public boolean atEndLine(SimpleDocument document) {
        return charIndex == document.lineLength(lineIndex);
    }

    public boolean atEndFile(SimpleDocument document) {
        return atEndLine(document) && (lineIndex == document.linesSize() - 1);
    }

    public boolean atFirstLine() {
        return lineIndex == 0;
    }

    public boolean atLastLine(SimpleDocument document) {
        return lineIndex == document.linesSize() - 1;
    }

    public void setPosition(int lineIndex, int charIndex) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
    }

    public void setPosition(SimpleCaret other) {
        lineIndex = other.lineIndex;
        charIndex = other.charIndex;
    }

    public void updateAfterInsertChar() {
        charIndex += 1;
    }

    public void updateAfterInsertNewline() {
        lineIndex += 1;
        charIndex = 0;
    }

    public void updateAfterDeleteLine(int lineLength) {
        lineIndex -= 1;
        charIndex = lineLength;
    }

    public void updateAfterDeleteChar() {
        charIndex -= 1;
    }

    public void updateAfterInsertText(String text) {
        charIndex += text.length();
    }

    public void moveLeft(SimpleDocument document) {
        if (atBeginningFile()) {
            return;
        }
        if (atBeginningLine()) {
            lineIndex -= 1;
            charIndex = document.lineLength(lineIndex);
            return;
        }
        charIndex -= 1;
    }

    public void moveRight(SimpleDocument document) {
        if (atEndFile(document)) {
            return;
        }
        if (atEndLine(document)) {
            lineIndex += 1;
            charIndex = 0;
            return;
        }
        charIndex += 1;
    }

    public void moveUp(SimpleDocument document) {
        if (!atFirstLine()) {
            lineIndex -= 1;
            charIndex = Math.min(charIndex, document.lineLength(lineIndex));
        }
    }

    public void moveDown(SimpleDocument document) {
        if (!atLastLine(document)) {
            lineIndex += 1;
            charIndex = Math.min(charIndex, document.lineLength(lineIndex));
        }
    }

    public void moveEndLine(SimpleDocument document) {
        charIndex = document.lineLength(lineIndex);
    }

    public void moveStartLine() {
        charIndex = 0;
    }

    public void moveToPrevWord(SimpleDocument document) {
        if (atBeginningLine()) {
            moveLeft(document);
            return;
        }
        StringBuilder line = document.getLine(lineIndex);
        for (int i = charIndex - 2; i >= 0; --i) {
            if ((line.charAt(i) == ' ') && (line.charAt(i + 1) != ' ')) {
                charIndex = i + 1;
                return;
            }
        }
        charIndex = 0;
    }

    public void moveToNextWord(SimpleDocument document) {
        if (atEndLine(document)) {
            moveRight(document);
            return;
        }
        StringBuilder line = document.getLine(lineIndex);
        for (int i = charIndex; i < line.length() - 1; ++i) {
            if ((line.charAt(i) == ' ') && (line.charAt(i + 1) != ' ')) {
                charIndex = i + 1;
                return;
            }
        }
        charIndex = line.length();
    }

    public void moveToEndFile(SimpleDocument document) {
        lineIndex = document.linesSize() - 1;
        charIndex = document.lineLength(lineIndex);
    }

    public void moveToNextLine(SimpleDocument document) {
        if (atEndFile(document)) {
            return;
        }
        lineIndex += 1;
        charIndex = 0;
    }

    public Character getSymbol(SimpleDocument document) {
        if (charIndex == document.lineLength(lineIndex)) {
            return null;
        }
        return document.getLine(lineIndex).charAt(charIndex);
    }

    @Override
    public int compareTo(SimpleCaret o) {
        if (lineIndex == o.lineIndex) {
            return new Integer(charIndex).compareTo(o.charIndex);
        }
        return new Integer(lineIndex).compareTo(o.lineIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleCaret)) return false;
        SimpleCaret caret = (SimpleCaret) o;
        return lineIndex == caret.lineIndex && charIndex == caret.charIndex;
    }
}