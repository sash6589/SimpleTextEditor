package com.aomatveev.texteditor.primitives;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.utilities.Utilities;

public class SimpleCaret implements Comparable<SimpleCaret> {
    public int lineIndex;
    public int charIndex;

    private SimpleDocument document;

    public SimpleCaret(SimpleDocument document) {
        this.document = document;
        lineIndex = 0;
        charIndex = 0;
    }

    public SimpleCaret(SimpleDocument document, int lineIndex, int charIndex) {
        this.document = document;
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
    }

    public SimpleCaret(SimpleCaret other) {
        this.document = other.getDocument();
        lineIndex = other.lineIndex;
        charIndex = other.charIndex;
    }

    public SimpleDocument getDocument() {
        return document;
    }

    public boolean atBeginningFile() {
        return (lineIndex == 0) && (charIndex == 0);
    }

    public boolean atBeginningLine() {
        return charIndex == 0;
    }

    public boolean atEndLine() {
        return charIndex == document.lineLength(lineIndex);
    }

    public boolean atEndFile() {
        return atEndLine() && (lineIndex == document.linesSize() - 1);
    }

    public boolean atFirstLine() {
        return lineIndex == 0;
    }

    public boolean atLastLine() {
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

    public void moveLeft() {
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

    public void moveRight() {
        if (atEndFile()) {
            return;
        }
        if (atEndLine()) {
            lineIndex += 1;
            charIndex = 0;
            return;
        }
        charIndex += 1;
    }

    public void moveUp() {
        if (!atFirstLine()) {
            lineIndex -= 1;
            charIndex = Math.min(charIndex, document.lineLength(lineIndex));
        }
    }

    public void moveDown() {
        if (!atLastLine()) {
            lineIndex += 1;
            charIndex = Math.min(charIndex, document.lineLength(lineIndex));
        }
    }

    public void moveEndLine() {
        charIndex = document.lineLength(lineIndex);
    }

    public void moveStartLine() {
        charIndex = 0;
    }

    public void moveToPrevWord() {
        if (atBeginningLine()) {
            moveLeft();
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

    public void moveToNextWord() {
        if (atEndLine()) {
            moveRight();
            return;
        }
        StringBuilder line = document.getLine(lineIndex);
        for (int i = charIndex; i < line.length(); ++i) {
            if ((line.charAt(i) == ' ') && (line.charAt(i + 1) != ' ')) {
                charIndex = i + 1;
                return;
            }
        }
        charIndex = line.length();
    }

    public void moveToEndFile() {
        lineIndex = document.linesSize() - 1;
        charIndex = document.lineLength(lineIndex);
    }

    public void moveToNextLine() {
        if (atEndFile()) {
            return;
        }
        lineIndex += 1;
        charIndex = 0;
    }

    public Character getSymbol() {
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
}