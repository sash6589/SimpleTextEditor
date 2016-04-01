package com.aomatveev.texteditor.model;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import com.aomatveev.texteditor.syntax.AbstractSyntax;
import com.aomatveev.texteditor.syntax.NoneSyntax;
import com.aomatveev.texteditor.utilities.Utilities;
import javafx.util.Pair;

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
    private List<Integer> matchingBracket;
    private List<Integer> lineCommentIndex;
    private SimpleCaret currentCaret;
    private SimpleCaret startSelectCaret;
    private AbstractSyntax syntax;
    private int length;
    private boolean isSelected;
    private boolean insertMode;
    private int matchingBracketLine;

    public SimpleDocument(SimpleTextComponent viewModel) {
        this.viewModel = viewModel;
        newDocument();
    }

    public SimpleDocument(SimpleTextComponent viewModel, String textData) {
        this.viewModel = viewModel;
        length = textData.length();
        init();
    }

    public void newDocument() {
        length = 0;
        init();
    }


    public List<StringBuilder> getLines() {
        return lines;
    }

    public StringBuilder getLine(int index) {
        return lines.get(index);
    }

    public AbstractSyntax getSyntax() {
        return syntax;
    }

    public List<Integer> getMatchingBracket() {
        return matchingBracket;
    }

    public List<Integer> getLineCommentIndex() {
        return lineCommentIndex;
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
        resetSyntaxObjects();
        this.syntax = syntax;
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
        resetMatchingBracket();
        resetComment();
        if ((insertMode) && (!currentCaret.atEndLine())) {
            lines.get(currentCaret.lineIndex).setCharAt(currentCaret.charIndex, c);
        } else {
            lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, c);
            length += 1;
        }
        currentCaret.updateAfterInsertChar();
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }

    public void insertNewLine() {
        resetMatchingBracket();
        resetComment();
        StringBuilder rest = new StringBuilder("");
        rest.append(getLine(currentCaret.lineIndex).substring(currentCaret.charIndex));
        getLine(currentCaret.lineIndex).delete(currentCaret.charIndex, lineLength(currentCaret.lineIndex));
        lines.add(currentCaret.lineIndex + 1, rest);
        matchingBracket.add(-1);
        lineCommentIndex.add(-1);
        length += 1;
        currentCaret.updateAfterInsertNewline();
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }

    public void append(String text) {
        currentCaret.moveToEndFile();
        insertText(text);
    }

    public void backspaceChar() {
        if (!currentCaret.atBeginningFile()) {
            resetMatchingBracket();
            resetComment();
            if (currentCaret.atBeginningLine()) {
                backspaceLine();
            } else {
                lines.get(currentCaret.lineIndex).deleteCharAt(currentCaret.charIndex - 1);
                length -= 1;
                currentCaret.updateAfterDeleteChar();
            }
            checkIfBracket();
            checkIfComment();
            viewModel.updateView();
        }
    }

    public void deleteChar() {
        if (!currentCaret.atEndFile()) {
            resetMatchingBracket();
            resetComment();
            if (currentCaret.atEndLine()) {
                currentCaret.moveToNextLine();
                backspaceLine();
            } else {
                lines.get(currentCaret.lineIndex).deleteCharAt(currentCaret.charIndex);
                length -= 1;
            }
            checkIfBracket();
            checkIfComment();
            viewModel.updateView();
        }
    }

    public void paste() {
        if (isSelected) {
            cut();
        }
        resetMatchingBracket();
        resetComment();
        try {
            String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            insertText(text);
            checkIfBracket();
            checkIfComment();
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
        resetMatchingBracket();
        resetComment();
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getKey();
        SimpleCaret second = bounds.getValue();
        while (first.lineIndex != second.lineIndex) {
            removeUntilCaret(second);
        }
        lines.get(first.lineIndex).delete(first.charIndex, second.charIndex);
        length -= second.charIndex - first.charIndex;
        currentCaret.setPosition(first);
        if (matchingBracket.get(first.lineIndex) >= first.charIndex) {
            matchingBracket.set(first.lineIndex, -1);
        }
        if (lineCommentIndex.get(first.lineIndex) >= first.charIndex) {
            lineCommentIndex.set(first.lineIndex, -1);
        }
        cancelSelect();
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }

    public void selectAll() {
        isSelected = true;
        startSelectCaret = new SimpleCaret(this, 0, 0);
        currentCaret.moveToEndFile();

        viewModel.updateView();
    }

    public SimpleCaret getCurrentCaret() {
        return currentCaret;
    }

    public Pair<Integer, Integer> getSelectedBounds(int index) {
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getKey();
        SimpleCaret second = bounds.getValue();
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
        resetMatchingBracket();
        resetComment();
        if (e.isControlDown()) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                currentCaret.moveToPrevWord();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                currentCaret.moveToNextWord();
            }
            viewModel.updateView();
            return;
        }
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
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }

    public void moveCaret(int lineIndex, int charIndex) {
        resetMatchingBracket();
        resetComment();
        if (lineIndex == linesSize()) {
            if (lineLength(lineIndex - 1) > 0) {
                lines.add(new StringBuilder(""));
                matchingBracket.add(-1);
                lineCommentIndex.add(-1);
                length += 1;
            } else {
                lineIndex -= 1;
            }
        }
        charIndex = Math.min(lineLength(lineIndex), charIndex);
        currentCaret.setPosition(lineIndex, charIndex);
        checkIfBracket();
        checkIfComment();
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
            startSelectCaret = new SimpleCaret(this, lineIndex, charIndex);
        }
        moveCaret(lineIndex, charIndex);
    }

    public void cancelSelect() {
        isSelected = false;
        startSelectCaret = null;
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
        matchingBracketLine = -1;
        syntax = new NoneSyntax();
        initLines();
        initCaret();
    }

    private void initLines() {
        lines = new ArrayList<>();
        matchingBracket = new ArrayList<>();
        lineCommentIndex = new ArrayList<>();
        lines.add(new StringBuilder(""));
        matchingBracket.add(-1);
        lineCommentIndex.add(-1);
    }

    private void initLines(String textData) {
        String[] items = textData.split("\n");
        lines = new ArrayList<>();
        matchingBracket = new ArrayList<>();
        lineCommentIndex = new ArrayList<>();
        for (String item : items) {
            lines.add(new StringBuilder(item));
            matchingBracket.add(-1);
            lineCommentIndex.add(-1);
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
        matchingBracket.remove(caret.lineIndex);
        lineCommentIndex.remove(caret.lineIndex);
        caret.setPosition(caret.lineIndex - 1, newCharIndex);
    }

    private String getSelectedText() {
        StringBuilder res = new StringBuilder();
        Pair<SimpleCaret, SimpleCaret> bounds = findSelectedBounds();
        SimpleCaret first = bounds.getKey();
        SimpleCaret second = bounds.getValue();
        while (first.lineIndex != second.lineIndex) {
            res.append(lines.get(first.lineIndex).substring(first.charIndex)).append("\n");
            first.lineIndex += 1;
            first.charIndex = 0;
        }
        res.append(lines.get(first.lineIndex).substring(first.charIndex, second.charIndex));
        return res.toString();
    }

    private void insertText(String text) {
        resetMatchingBracket();
        resetComment();
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
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }


    private void insertLine(String text) {
        resetMatchingBracket();
        resetComment();
        lines.get(currentCaret.lineIndex).insert(currentCaret.charIndex, text);
        length += text.length();
        currentCaret.updateAfterInsertText(text);
        checkIfBracket();
        checkIfComment();
        viewModel.updateView();
    }

    private void backspaceLine() {
        resetMatchingBracket();
        resetComment();
        StringBuilder line = lines.get(currentCaret.lineIndex);
        int lineLength = lineLength(currentCaret.lineIndex - 1);
        lines.get(currentCaret.lineIndex - 1).append(line);
        lines.remove(currentCaret.lineIndex);
        matchingBracket.remove(currentCaret.lineIndex);
        lineCommentIndex.remove(currentCaret.lineIndex);
        currentCaret.updateAfterDeleteLine(lineLength);
        checkIfBracket();
        checkIfComment();
        length -= 1;
    }

    private void resetSyntaxObjects() {
        for (int i = 0; i < linesSize(); ++i) {
            matchingBracket.set(i, -1);
            lineCommentIndex.set(i, -1);
            checkIfComment(i);
        }
    }

    private void checkIfBracket() {
        Character c = currentCaret.getSymbol();
        if (isCharCommented(currentCaret.lineIndex, currentCaret.charIndex)) return;
        if (c != null) {
            if (Utilities.isBracket(c)) {
                findMatchingBracket();
            }
        }
    }

    private void findMatchingBracket() {
        char bracketChar = currentCaret.getSymbol();
        char matchingBracketChar = Utilities.matchingBracket(bracketChar);
        if (Utilities.isOpenBracket(bracketChar)) {
            findCloseBracket(bracketChar, matchingBracketChar);
        } else {
            findOpenBracket(bracketChar, matchingBracketChar);
        }
    }

    private void findCloseBracket(char bracketChar, char matchingBracketChar) {
        int lineIndex = currentCaret.lineIndex;
        int charIndex = currentCaret.charIndex;
        int balance = 1;
        for (int i = charIndex + 1; i < lineLength(lineIndex); ++i) {
            if (isCharCommented(lineIndex, i)) continue;
            if (getLine(lineIndex).charAt(i) == bracketChar) balance += 1;
            if (getLine(lineIndex).charAt(i) == matchingBracketChar) balance -= 1;
            if (balance == 0) {
                matchingBracket.set(lineIndex, i);
                matchingBracketLine = lineIndex;
                return;
            }
        }
        for (int i = lineIndex + 1; i < linesSize(); ++i) {
            for (int j = 0; j < lineLength(i); ++j) {
                if (isCharCommented(i, j)) continue;
                if (getLine(i).charAt(j) == bracketChar) balance += 1;
                if (getLine(i).charAt(j) == matchingBracketChar) balance -= 1;
                if (balance == 0) {
                    matchingBracket.set(i, j);
                    matchingBracketLine = i;
                    return;
                }
            }
        }
    }

    private void findOpenBracket(char bracketChar, char matchingBracketChar) {
        int lineIndex = currentCaret.lineIndex;
        int charIndex = currentCaret.charIndex;
        int balance = 1;
        for (int i = charIndex - 1; i >= 0; i--) {
            if (isCharCommented(lineIndex, i)) continue;
            if (getLine(lineIndex).charAt(i) == bracketChar) balance += 1;
            if (getLine(lineIndex).charAt(i) == matchingBracketChar) balance -= 1;
            if (balance == 0) {
                matchingBracket.set(lineIndex, i);
                matchingBracketLine = lineIndex;
                return;
            }
        }
        for (int i = lineIndex - 1; i >= 0; --i) {
            for (int j = lineLength(i) - 1; j >= 0; --j) {
                if (isCharCommented(i, j)) continue;
                if (getLine(i).charAt(j) == bracketChar) balance += 1;
                if (getLine(i).charAt(j) == matchingBracketChar) balance -= 1;
                if (balance == 0) {
                    matchingBracket.set(i, j);
                    matchingBracketLine = i;
                    return;
                }
            }
        }
    }

    private void resetMatchingBracket() {
        matchingBracket.set(currentCaret.lineIndex, -1);
        if (matchingBracketLine != -1) {
            matchingBracket.set(matchingBracketLine, -1);
        }
        matchingBracketLine = -1;
    }

    private void checkIfComment() {
        String commentString = syntax.LINE_COMMENT_IDENTIFIER;
        if (commentString == null) return;
        int index = getLine(currentCaret.lineIndex).indexOf(commentString);
        if (index != -1) {
            setLineComment(index);
        }
    }

    private void checkIfComment(int lineIndex) {
        String commentString = syntax.LINE_COMMENT_IDENTIFIER;
        if (commentString == null) return;
        int index = getLine(lineIndex).indexOf(commentString);
        if (index != -1) {
            setLineComment(lineIndex, index);
        }
    }

    private void setLineComment(int index) {
        lineCommentIndex.set(currentCaret.lineIndex, index);
    }

    private void setLineComment(int lineIndex, int index) {
        lineCommentIndex.set(lineIndex, index);
    }

    private void resetComment() {
        resetLineComment();
    }

    private void resetLineComment() {
        String commentString = syntax.LINE_COMMENT_IDENTIFIER;
        if (commentString == null) return;
        int index = getLine(currentCaret.lineIndex).indexOf(commentString);
        if (index == -1) {
            lineCommentIndex.set(currentCaret.lineIndex, -1);
        }
    }

    private boolean isCharCommented(int lineIndex, int charIndex) {
        int index = getLineCommentIndex().get(lineIndex);
        return index != -1 && charIndex >= index;
    }
}