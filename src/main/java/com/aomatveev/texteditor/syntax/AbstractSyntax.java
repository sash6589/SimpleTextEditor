package com.aomatveev.texteditor.syntax;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import com.aomatveev.texteditor.utilities.Utilities;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSyntax {

    public static String LINE_COMMENT_IDENTIFIER;
    public static String OPEN_TEXT_COMMENT_IDENTIFIER;
    public static String CLOSE_TEXT_COMMENT_IDENTIFIER;

    private SimpleDocument document;

    protected List<String> keywords;

    private List<Integer> matchingBracket;
    private List<Integer> lineCommentIndex;
    private List<List<Pair<Integer, Integer>>> textCommentIndex;

    private int matchingBracketLine;

    public AbstractSyntax() {
        initCommentIdentifiers();
        initKeywords();
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public boolean isValidIdentifier(boolean start, char c) {
        return false;
    }

    public void setDocument(SimpleDocument document) {
        this.document = document;
        initLists();
    }

    public List<Integer> getMatchingBracket() {
        return matchingBracket;
    }

    public void addMatchingBracket() {
        matchingBracket.add(-1);
    }

    public void removeMatchingBracket(int lineIndex) {
        matchingBracket.remove(lineIndex);
    }

    public List<Integer> getLineCommentIndex() {
        return lineCommentIndex;
    }

    public void addLineCommentIndex() {
        lineCommentIndex.add(-1);
    }

    public void removeLineCommentIndex(int lineIndex) {
        lineCommentIndex.remove(lineIndex);
    }

    public List<List<Pair<Integer, Integer>>> getTextCommentIndex() {
        return textCommentIndex;
    }

    public void addTextCommentIndex() {
        textCommentIndex.add(new ArrayList<>());
    }

    public void removeTextCommentIndex(int index) {
        textCommentIndex.remove(index);
    }

    public void checkIfBracket() {
        SimpleCaret currentCaret = document.getCurrentCaret();
        Character c = currentCaret.getSymbol();
        if (isCharCommented(currentCaret.lineIndex, currentCaret.charIndex)) return;
        if (c != null) {
            if (Utilities.isBracket(c)) {
                findMatchingBracket();
            }
        }
    }

    public void resetMatchingBracket() {
        SimpleCaret currentCaret = document.getCurrentCaret();
        matchingBracket.set(currentCaret.lineIndex, -1);
        if (matchingBracketLine != -1) {
            matchingBracket.set(matchingBracketLine, -1);
        }
        matchingBracketLine = -1;
    }

    public void checkIfComment() {
        SimpleCaret currentCaret = document.getCurrentCaret();
        checkIfComment(currentCaret.lineIndex);
    }

    public void resetComment() {
        resetLineComment();
    }

    protected void initCommentIdentifiers() {
        LINE_COMMENT_IDENTIFIER = null;
        OPEN_TEXT_COMMENT_IDENTIFIER = null;
        CLOSE_TEXT_COMMENT_IDENTIFIER = null;
    }

    protected void initKeywords() {
        keywords = new ArrayList<>();
    }

    protected void checkIfComment(int lineIndex) {
        checkIfLineComment(lineIndex);
        updateTextCommentedAfterInsert(lineIndex);
    }

    private void initLists() {
        matchingBracket = new ArrayList<>();
        lineCommentIndex = new ArrayList<>();
        textCommentIndex = new ArrayList<>();
        for (int i = 0; i < document.linesSize(); ++i) {
            matchingBracket.add(-1);
            lineCommentIndex.add(-1);
            textCommentIndex.add(new ArrayList<>());
            checkIfComment(i);
        }
        matchingBracketLine = -1;
    }

    private void findMatchingBracket() {
        SimpleCaret currentCaret = document.getCurrentCaret();
        char bracketChar = currentCaret.getSymbol();
        char matchingBracketChar = Utilities.matchingBracket(bracketChar);
        if (Utilities.isOpenBracket(bracketChar)) {
            findCloseBracket(bracketChar, matchingBracketChar);
        } else {
            findOpenBracket(bracketChar, matchingBracketChar);
        }
    }

    private void findCloseBracket(char bracketChar, char matchingBracketChar) {
        SimpleCaret currentCaret = document.getCurrentCaret();
        int lineIndex = currentCaret.lineIndex;
        int charIndex = currentCaret.charIndex;
        int balance = 1;
        for (int i = charIndex + 1; i < document.lineLength(lineIndex); ++i) {
            if (isCharCommented(lineIndex, i)) continue;
            if (document.getLine(lineIndex).charAt(i) == bracketChar) balance += 1;
            if (document.getLine(lineIndex).charAt(i) == matchingBracketChar) balance -= 1;
            if (balance == 0) {
                matchingBracket.set(lineIndex, i);
                matchingBracketLine = lineIndex;
                return;
            }
        }
        for (int i = lineIndex + 1; i < document.linesSize(); ++i) {
            for (int j = 0; j < document.lineLength(i); ++j) {
                if (isCharCommented(i, j)) continue;
                if (document.getLine(i).charAt(j) == bracketChar) balance += 1;
                if (document.getLine(i).charAt(j) == matchingBracketChar) balance -= 1;
                if (balance == 0) {
                    matchingBracket.set(i, j);
                    matchingBracketLine = i;
                    return;
                }
            }
        }
    }

    private void findOpenBracket(char bracketChar, char matchingBracketChar) {
        SimpleCaret currentCaret = document.getCurrentCaret();
        int lineIndex = currentCaret.lineIndex;
        int charIndex = currentCaret.charIndex;
        int balance = 1;
        for (int i = charIndex - 1; i >= 0; i--) {
            if (isCharCommented(lineIndex, i)) continue;
            if (document.getLine(lineIndex).charAt(i) == bracketChar) balance += 1;
            if (document.getLine(lineIndex).charAt(i) == matchingBracketChar) balance -= 1;
            if (balance == 0) {
                matchingBracket.set(lineIndex, i);
                matchingBracketLine = lineIndex;
                return;
            }
        }
        for (int i = lineIndex - 1; i >= 0; --i) {
            for (int j = document.lineLength(i) - 1; j >= 0; --j) {
                if (isCharCommented(i, j)) continue;
                if (document.getLine(i).charAt(j) == bracketChar) balance += 1;
                if (document.getLine(i).charAt(j) == matchingBracketChar) balance -= 1;
                if (balance == 0) {
                    matchingBracket.set(i, j);
                    matchingBracketLine = i;
                    return;
                }
            }
        }
    }

    private boolean isCharCommented(int lineIndex, int charIndex) {
        List<Pair<Integer, Integer>> pairs = textCommentIndex.get(lineIndex);
        for (Pair<Integer, Integer> pair : pairs) {
            if ((charIndex >= pair.getKey()) && (charIndex < pair.getValue())) {
                return true;
            }
        }
        int index = lineCommentIndex.get(lineIndex);
        return index != -1 && charIndex >= index;
    }

    private boolean isCharTextCommented(int lineIndex, int charIndex) {
        List<Pair<Integer, Integer>> pairs = textCommentIndex.get(lineIndex);
        for (Pair<Integer, Integer> pair : pairs) {
            if ((charIndex >= pair.getKey()) && (charIndex < pair.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void checkIfLineComment(int lineIndex) {
        String commentString = LINE_COMMENT_IDENTIFIER;
        int index = document.getLine(lineIndex).indexOf(commentString);
        if (index != -1) {
            setLineComment(lineIndex, index);
        }
    }

    private void setLineComment(int lineIndex, int index) {
        lineCommentIndex.set(lineIndex, index);
    }

    private void resetLineComment() {
        SimpleCaret currentCaret = document.getCurrentCaret();
        String commentString = LINE_COMMENT_IDENTIFIER;
        int index = document.getLine(currentCaret.lineIndex).indexOf(commentString);
        if (index == -1) {
            lineCommentIndex.set(currentCaret.lineIndex, -1);
        }
    }

    private boolean isPrevLineCommented(int lineIndex) {
        while ((lineIndex > 0) && (document.lineLength(lineIndex - 1) == 0)) {
            lineIndex -= 1;
        }
        if (lineIndex == 0) return false;
        if (isCharTextCommented(lineIndex - 1, document.lineLength(lineIndex - 1) - 1)) {
            StringBuilder line = document.getLine(lineIndex - 1);
            if (line.length() < CLOSE_TEXT_COMMENT_IDENTIFIER.length()) return true;
            if ((line.length() >= CLOSE_TEXT_COMMENT_IDENTIFIER.length()) && (!CLOSE_TEXT_COMMENT_IDENTIFIER.equals(
                    line.substring(line.length() - CLOSE_TEXT_COMMENT_IDENTIFIER.length())))) {
                return true;
            }
        }
        return false;
    }

    private boolean isNextLineCommented(int lineIndex) {
        while ((lineIndex + 1 < textCommentIndex.size()) && (document.lineLength(lineIndex + 1) == 0)) {
            lineIndex += 1;
        }
        if (lineIndex + 1 == textCommentIndex.size()) return false;
        if (isCharTextCommented(lineIndex + 1, 0)) {
            StringBuilder line = document.getLine(lineIndex + 1);
            if (line.length() < OPEN_TEXT_COMMENT_IDENTIFIER.length()) return true;
            if ((line.length() >= OPEN_TEXT_COMMENT_IDENTIFIER.length()) &&
                    (!OPEN_TEXT_COMMENT_IDENTIFIER.equals(line.substring(0, OPEN_TEXT_COMMENT_IDENTIFIER.length())))) {
                return true;
            }
        }
        return false;
    }

    private void updateTextCommentedAfterInsert(int lineIndex) {
        if (lineIndex == textCommentIndex.size()) return;
        if (document.lineLength(lineIndex) == 0) {
            updateTextCommentedAfterInsert(lineIndex + 1);
        }
        boolean commentedMode = isPrevLineCommented(lineIndex);
        StringBuilder line = document.getLine(lineIndex);
        List<Pair<Integer, Integer>> res = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < line.length(); ++i) {
            if ((i + OPEN_TEXT_COMMENT_IDENTIFIER.length() <= line.length()) &&
                    (OPEN_TEXT_COMMENT_IDENTIFIER.equals(line.substring(i, i + OPEN_TEXT_COMMENT_IDENTIFIER.length())))) {
                if (!commentedMode) {
                    commentedMode = true;
                    start = i;
                }
            }
            if ((i + CLOSE_TEXT_COMMENT_IDENTIFIER.length() <= line.length()) &&
                    (CLOSE_TEXT_COMMENT_IDENTIFIER.equals(line.substring(i, i + CLOSE_TEXT_COMMENT_IDENTIFIER.length())))) {
                if (commentedMode) {
                    res.add(new Pair<>(start, i + CLOSE_TEXT_COMMENT_IDENTIFIER.length()));
                    commentedMode = false;
                }
            }
        }
        if (commentedMode) {
            res.add(new Pair<>(start, line.length()));
        }
        textCommentIndex.set(lineIndex, res);
        if (((commentedMode) && (!isNextLineCommented(lineIndex))) ||
                ((!commentedMode) && (isNextLineCommented(lineIndex)))) {
            updateTextCommentedAfterInsert(lineIndex + 1);
        }
    }

}
