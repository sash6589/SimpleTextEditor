package com.aomatveev.texteditor.model;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import com.aomatveev.texteditor.syntax.AbstractSyntax;
import com.aomatveev.texteditor.syntax.NoneSyntax;
import com.aomatveev.texteditor.utilities.Utilities;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleDocument {
    private SimpleTextComponent viewModel;
    private List<StringBuilder> lines;

    private SimpleCaret currentCaret;
    private SimpleCaret startSelectCaret;
    private AbstractSyntax syntax;
    private int length;
    private boolean isSelected;
    private boolean insertMode;


    public SimpleDocument(SimpleTextComponent viewModel) {
        this.viewModel = viewModel;
        newDocument();
    }

    public void newDocument() {
        length = 0;
        init();
        setSyntax(new NoneSyntax());
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

    public void setSyntax(AbstractSyntax syntax) {
        this.syntax = syntax;
        syntax.setDocument(this);
    }

    public void setInsertMode() {
        insertMode ^= true;
        viewModel.updateView();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isInsertMode() {
        return insertMode;
    }

    public void insertText(char c) {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        if ((insertMode) && (!currentCaret.atEndLine(this))) {
            lines.get(currentCaret.lineIndex).setCharAt(currentCaret.charIndex, c);
        } else {
            lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, c);
            length += 1;
        }

        syntax.checkIfComment();
        currentCaret.updateAfterInsertChar();
        syntax.checkIfBracket();
        viewModel.updateView();
    }

    public void insertNewLine() {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        StringBuilder rest = new StringBuilder("");
        rest.append(getLine(currentCaret.lineIndex).substring(currentCaret.charIndex));
        getLine(currentCaret.lineIndex).delete(currentCaret.charIndex, lineLength(currentCaret.lineIndex));
        lines.add(currentCaret.lineIndex + 1, rest);
        syntax.getMatchingBracket().add(currentCaret.lineIndex + 1, -1);
        syntax.getLineCommentIndex().add(currentCaret.lineIndex + 1, -1);
        syntax.getTextCommentIndex().add(currentCaret.lineIndex + 1, new ArrayList<>());
        length += 1;
        syntax.checkIfComment();
        currentCaret.updateAfterInsertNewline();
        syntax.checkIfBracket();
        viewModel.updateView();
    }

    public void append(String text) {
        currentCaret.moveToEndFile(this);
        insertText(text);
    }

    public void backspaceChar() {
        if (!currentCaret.atBeginningFile()) {
            syntax.resetMatchingBracket();
            if (currentCaret.atBeginningLine()) {
                backspaceLine();
            } else {
                lines.get(currentCaret.lineIndex).deleteCharAt(currentCaret.charIndex - 1);
                length -= 1;
                currentCaret.updateAfterDeleteChar();
            }
            syntax.resetComment();
            syntax.checkIfBracket();
            syntax.checkIfComment();
            viewModel.updateView();
        }
    }

    public void deleteChar() {
        if (!currentCaret.atEndFile(this)) {
            syntax.resetMatchingBracket();
            if (currentCaret.atEndLine(this)) {
                currentCaret.moveToNextLine(this);
                backspaceLine();
            } else {
                lines.get(currentCaret.lineIndex).deleteCharAt(currentCaret.charIndex);
                length -= 1;
            }
            syntax.resetComment();
            syntax.checkIfBracket();
            syntax.checkIfComment();
            viewModel.updateView();
        }
    }

    public void paste() {
        if (isSelected) {
            cut();
        }
        syntax.resetMatchingBracket();
        syntax.resetComment();
        try {
            String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            insertText(text);
            viewModel.updateView();
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void copy() {
        if (!isSelected) {
            return;
        }
        String text = getSelectedText();
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void cut() {
        if (!isSelected) {
            return;
        }
        syntax.resetMatchingBracket();
        syntax.resetComment();
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getFirst();
        SimpleCaret second = bounds.getSecond();
        while (first.lineIndex != second.lineIndex) {
            removeUntilCaret(second);
        }
        lines.get(first.lineIndex).delete(first.charIndex, second.charIndex);
        length -= second.charIndex - first.charIndex;
        currentCaret.setPosition(first);
        cancelSelect();
        syntax.checkIfComment();
        syntax.checkIfBracket();
        viewModel.updateView();
    }

    public void selectAll() {
        isSelected = true;
        startSelectCaret = new SimpleCaret(0, 0);
        currentCaret.moveToEndFile(this);

        viewModel.updateView();
    }

    public int getCaretLineIndex() {
        return currentCaret.lineIndex;
    }

    public int getCaretCharIndex() {
        return currentCaret.charIndex;
    }

    public SimpleCaret getCurrentCaret() {
        return currentCaret;
    }

    public Pair<Integer, Integer> getSelectedBounds(int index) {
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getFirst();
        SimpleCaret second = bounds.getSecond();
        if ((index < first.lineIndex) || (index > second.lineIndex)) {
            return null;
        }
        if ((index > first.lineIndex) && (index < second.lineIndex)) {
            return new Pair<>(0, lineLength(index));
        }
        if (first.lineIndex == second.lineIndex) {
            return new Pair<>(first.charIndex, second.charIndex);
        }
        if (index == first.lineIndex) {
            return new Pair<>(first.charIndex, lineLength(index));
        } else {
            return new Pair<>(0, second.charIndex);
        }
    }

    public void moveCaret(KeyEvent e) {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        if (e.isControlDown()) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                currentCaret.moveToPrevWord(this);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                currentCaret.moveToNextWord(this);
            }
            viewModel.updateView();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentCaret.moveLeft(this);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentCaret.moveRight(this);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentCaret.moveUp(this);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentCaret.moveDown(this);
        }
        if (e.getKeyCode() == KeyEvent.VK_END) {
            currentCaret.moveEndLine(this);
        }
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            currentCaret.moveStartLine();
        }
        syntax.checkIfBracket();
        syntax.checkIfComment();
        viewModel.updateView();
    }

    public void moveCaret(int lineIndex, int charIndex) {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        if (lineIndex == linesSize()) {
            if (lineLength(lineIndex - 1) > 0) {
                lines.add(new StringBuilder(""));
                syntax.addMatchingBracket();
                syntax.addLineCommentIndex();
                syntax.addTextCommentIndex();
                length += 1;
            } else {
                lineIndex -= 1;
            }
        }
        charIndex = Math.min(lineLength(lineIndex), charIndex);
        currentCaret.setPosition(lineIndex, charIndex);
        syntax.checkIfBracket();
        syntax.checkIfComment();
        viewModel.updateView();
    }

    public void moveSelectedCaret(KeyEvent e) {
        if (!isSelected) {
            isSelected = true;
            startSelectCaret = new SimpleCaret(currentCaret);
        }
        moveCaret(e);
    }

    public void moveSelectedCaret(int lineIndex, int charIndex) {
        if (!isSelected) {
            isSelected = true;
            startSelectCaret = new SimpleCaret(lineIndex, charIndex);
        }
        moveCaret(lineIndex, charIndex);
    }

    public void cancelSelect() {
        isSelected = false;
        startSelectCaret = null;
    }

    public List<Pair<Integer, Integer>> getIdentifiersBounds(int lineIndex) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        StringBuilder text = getLine(lineIndex);
        boolean start = false;
        int startIndex = 0;

        for (int i = 0; i < text.length(); ++i) {
            if (syntax.isValidIdentifier(start, text.charAt(i))) {
                if (!start) {
                    startIndex = i;
                }
                start = true;
            } else {
                if (startIndex < i) {
                    bounds.add(new Pair<>(startIndex, i));
                }
                start = false;
            }
        }
        if (start) {
            bounds.add(new Pair<>(startIndex, text.length()));
        }
        return bounds;
    }

    public List<Pair<Integer, Integer>> getKeywordsBounds(int lineIndex) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        StringBuilder text = getLine(lineIndex);
        List<String> keywords = syntax.getKeywords();
        String specialText = "! " + text;
        String[] words = specialText.split(" ");
        int start = 0;

        for (int i = 1; i < words.length; ++i) {
            if (keywords.contains(words[i])) {
                bounds.add(new Pair<>(start, start + words[i].length()));
            }
            start += words[i].length() + 1;
        }
        return bounds;
    }

    public List<Pair<Integer, Integer>> getLiteralsBounds(int lineIndex) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        StringBuilder text = getLine(lineIndex);
        boolean start = false;
        int startIndex = 0;

        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '"') {
                if (start) {
                    bounds.add(new Pair<>(startIndex, i + 1));
                } else {
                    startIndex = i;
                }
                start ^= true;
            }
        }
        if (start) {
            bounds.add(new Pair<>(startIndex, text.length()));
        }
        return bounds;
    }

    public List<Pair<Integer, Integer>> getBracketsBounds(int lineIndex) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        Integer index = syntax.getMatchingBracket().get(lineIndex);
        if (index != -1) {
            bounds.add(new Pair<>(index, index + 1));
        }
        if ((Utilities.isBracket(currentCaret.getSymbol(this))) && (currentCaret.lineIndex == lineIndex)) {
            bounds.add(new Pair<>(currentCaret.charIndex, currentCaret.charIndex + 1));
        }
        return bounds;
    }

    public List<Pair<Integer, Integer>> getCommentsBounds(int lineIndex) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        StringBuilder text = getLine(lineIndex);
        Integer index = syntax.getLineCommentIndex().get(lineIndex);
        if (index != -1) {
            bounds.add(new Pair<>(index, text.length()));
        }
        List<Pair<Integer, Integer>> pairs = syntax.getTextCommentIndex().get(lineIndex);
        for (Pair<Integer, Integer> pair : pairs) {
            if (pair.getFirst() < pair.getSecond()) {
                bounds.add(pair);
            }
        }
        return bounds;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < linesSize(); ++i) {
            ans.append(lines.get(i)).append("\n");
        }
        return ans.toString();
    }

    private void init() {
        isSelected = false;
        startSelectCaret = null;
        insertMode = false;
        initLines();
        initCaret();
    }

    private void initLines() {
        lines = new ArrayList<>();
        lines.add(new StringBuilder(""));
    }

    private void initCaret() {
        int linesCount = Math.max(linesSize() - 1, 0);
        int charCount = 0;
        if (linesSize() > 0) {
            charCount = lineLength(linesCount);
        }
        currentCaret = new SimpleCaret(linesCount, charCount);
    }

    private Pair<SimpleCaret, SimpleCaret> findSelectedBounds() {
        if (currentCaret.compareTo(startSelectCaret) < 0) {
            return new Pair<>(new SimpleCaret(currentCaret), new SimpleCaret(startSelectCaret));
        } else {
            return new Pair<>(new SimpleCaret(startSelectCaret), new SimpleCaret(currentCaret));
        }
    }

    private void removeUntilCaret(SimpleCaret caret) {
        length -= caret.charIndex;
        int newCharIndex = lineLength(caret.lineIndex - 1);
        StringBuilder rest = new StringBuilder(getLine(caret.lineIndex).substring(caret.charIndex));
        lines.get(caret.lineIndex - 1).append(rest);
        lines.remove(caret.lineIndex);
        syntax.removeMatchingBracket(caret.lineIndex);
        syntax.removeLineCommentIndex(caret.lineIndex);
        syntax.removeTextCommentIndex(caret.lineIndex);
        caret.setPosition(caret.lineIndex - 1, newCharIndex);
    }

    private String getSelectedText() {
        StringBuilder res = new StringBuilder();
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getFirst();
        SimpleCaret second = bounds.getSecond();
        while (first.lineIndex != second.lineIndex) {
            res.append(lines.get(first.lineIndex).substring(first.charIndex)).append("\n");
            first.lineIndex += 1;
            first.charIndex = 0;
        }
        res.append(lines.get(first.lineIndex).substring(first.charIndex, second.charIndex));
        return res.toString();
    }

    private void insertText(String text) {
        String[] parts = text.split("\n");
        for (int i = 0; i < parts.length; ++i) {
            insertLine(parts[i]);
            if (i < parts.length - 1) {
                insertNewLine();
            }
        }
        if (text.charAt(text.length() - 1) == '\n') {
            insertNewLine();
        }
        viewModel.updateView();
    }


    private void insertLine(String text) {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, text);
        length += text.length();
        syntax.checkIfComment();
        currentCaret.updateAfterInsertText(text);
        syntax.checkIfBracket();
        viewModel.updateView();
    }

    private void backspaceLine() {
        syntax.resetMatchingBracket();
        syntax.resetComment();
        StringBuilder line = lines.get(currentCaret.lineIndex);
        int lineLength = lineLength(currentCaret.lineIndex - 1);
        lines.get(currentCaret.lineIndex - 1).append(line);
        lines.remove(currentCaret.lineIndex);
        syntax.removeMatchingBracket(currentCaret.lineIndex);
        syntax.removeLineCommentIndex(currentCaret.lineIndex);
        syntax.removeTextCommentIndex(currentCaret.lineIndex);
        currentCaret.updateAfterDeleteLine(lineLength);
        length -= 1;
    }

}