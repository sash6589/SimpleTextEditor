package com.aomatveev.texteditor.primitives;

import com.aomatveev.texteditor.model.SimpleDocument;

public class SimpleCaret {
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

    public void updateCaretAfterInsertChar() {
        charIndex += 1;
    }

    public void updateCaretAfterInsertNewline() {
        lineIndex += 1;
        charIndex = 0;
    }

    public void updateCaretAfterDeleteLine(int lineLength) {
        lineIndex -= 1;
        charIndex = lineLength;
    }

    public void updateCaretAfterDeleteChar() {
        charIndex -= 1;
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
}
