package com.aomatveev.texteditor.model;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.primitives.SimpleCaret;

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

    public int charCount(int lineIndex) {
        return lines.get(lineIndex).length();
    }

    public int linesCount() {
        return lines.size();
    }

    public int length() {
        return length;
    }

    public void insertText(char c) {
        lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, c);
        length += 1;
        updateCaretAfterChar();
        viewModel.updateView();
    }

    public void insertNewLine() {
        lines.add(new StringBuilder(""));
        length += 1;
        updateCaretAfterNewline();
        viewModel.updateView();
    }

    public SimpleCaret getCurrentCaret() {
        return currentCaret;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        for (StringBuilder line : lines) {
            ans.append(line);
        }
        return ans.toString();
    }

    private void initLines() {
        lines = new ArrayList<>();
        lines.add(new StringBuilder(""));
    }

    private void initLines(String textData) {
        String[] items = textData.split("\n");
        lines = new ArrayList<>();
        for (int i = 0; i < items.length; ++i) {
            lines.add(new StringBuilder(items[i]));
        }
    }

    private void initCaret() {
        int linesCount = Math.max(linesCount() - 1, 0);
        int charCount = 0;
        if (linesCount() > 0) {
            charCount = charCount(linesCount);
        }
        currentCaret = new SimpleCaret(linesCount, charCount, length());
    }

    private void updateCaretAfterChar() {
        currentCaret.textIndex += 1;
        currentCaret.charIndex += 1;
    }

    private void updateCaretAfterNewline() {
        currentCaret.textIndex += 1;
        currentCaret.lineIndex = linesCount() - 1;
        currentCaret.charIndex = 0;
    }
}
