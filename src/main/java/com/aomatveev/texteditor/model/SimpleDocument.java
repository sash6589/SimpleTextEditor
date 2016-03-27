package com.aomatveev.texteditor.model;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.primitives.SimpleCaret;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SimpleDocument {
    private SimpleTextComponent viewModel;
    private List<StringBuilder> lines;
    private SimpleCaret currentCaret;
    private int length;

    public SimpleDocument(SimpleTextComponent viewModel) {
        this.viewModel = viewModel;
        initLines();
        initCaret();
    }

    public SimpleDocument(SimpleTextComponent viewModel, String textData) {
        this.viewModel = viewModel;
        this.length = textData.length();
        initLines(textData);
        initCaret();
    }

    public List<StringBuilder> getLines() {
        return lines;
    }

    public StringBuilder getLine(int index) {
        return lines.get(index);
    }

    public int lineLength(int lineIndex) {
        return lines.get(lineIndex).length();
    }

    public int linesSize() {
        return lines.size();
    }

    public int length() {
        return length;
    }

    public void insertText(char c) {
        lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, c);
        length += 1;
        currentCaret.updateCaretAfterInsertChar();
        viewModel.updateView();
    }

    public void insertNewLine() {
        StringBuilder rest = new StringBuilder("");
        rest.append(getLine(currentCaret.lineIndex).substring(currentCaret.charIndex));
        getLine(currentCaret.lineIndex).delete(currentCaret.charIndex, lineLength(currentCaret.lineIndex));
        lines.add(currentCaret.lineIndex + 1, rest);
        length += 1;
        currentCaret.updateCaretAfterInsertNewline();
        viewModel.updateView();
    }

    public void deleteChar() {
        if (!currentCaret.atBeginningFile()) {
            if (currentCaret.atBeginningLine()) {
                StringBuilder line = lines.get(currentCaret.lineIndex);
                int lineLength = lineLength(currentCaret.lineIndex - 1);
                lines.get(currentCaret.lineIndex - 1).append(line);
                lines.remove(currentCaret.lineIndex);
                currentCaret.updateCaretAfterDeleteLine(lineLength);
            } else {
                lines.get(currentCaret.lineIndex).deleteCharAt(currentCaret.charIndex - 1);
                currentCaret.updateCaretAfterDeleteChar();
            }
            length -= 1;
            viewModel.updateView();
        }
    }

    public SimpleCaret getCurrentCaret() {
        return currentCaret;
    }

    public void moveCaret(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentCaret.moveLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentCaret.moveRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentCaret.moveUp();
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentCaret.moveDown();
        }
        if (e.getKeyCode() == KeyEvent.VK_END) {
            currentCaret.moveEndLine();
        }
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            currentCaret.moveStartLine();
        }
        viewModel.updateView();
    }

    public void moveCaret(int lineIndex, int charIndex) {
        if (lineIndex == linesSize()) {
            if (lineLength(lineIndex - 1) > 0) {
                lines.add(new StringBuilder(""));
                length += 1;
            } else {
                lineIndex -= 1;
            }
        }
        charIndex = Math.min(lineLength(lineIndex), charIndex);
        currentCaret.setPosition(lineIndex, charIndex);
        viewModel.updateView();
    }

    public void moveCaretToWord(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentCaret.moveToPrevWord();
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentCaret.moveToNextWord();
        }
        viewModel.updateView();
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        lines.forEach(ans::append);
        return ans.toString();
    }

    private void initLines() {
        lines = new ArrayList<>();
        lines.add(new StringBuilder(""));
    }

    private void initLines(String textData) {
        String[] items = textData.split("\n");
        lines = new ArrayList<>();
        for (String item : items) {
            lines.add(new StringBuilder(item));
        }
    }

    private void initCaret() {
        int linesCount = Math.max(linesSize() - 1, 0);
        int charCount = 0;
        if (linesSize() > 0) {
            charCount = lineLength(linesCount);
        }
        currentCaret = new SimpleCaret(this, linesCount, charCount);
    }
}
