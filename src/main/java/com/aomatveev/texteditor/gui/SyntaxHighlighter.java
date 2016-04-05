package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.utilities.Utilities;


import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.List;

public class SyntaxHighlighter {

    private SimpleDocument document;

    public SyntaxHighlighter(SimpleDocument document) {
        this.document = document;
    }

    public TextLayout highlightSyntax(String text, int lineIndex) {
        if ("".equals(text)) {
            text = " ";
        }
        AttributedString coloredString = new AttributedString(text, Utilities.attributesMap);
        paintIdentifier(text, coloredString);
        paintKeywords(text, coloredString);
        paintLiterals(text, coloredString);
        paintBrackets(coloredString, lineIndex);
        paintComments(coloredString, lineIndex);
        return new TextLayout(coloredString.getIterator(), Utilities.DEFAULT_FRC);
    }

    private void paintIdentifier(String text, AttributedString coloredString) {
        boolean start = false;
        for (int i = 0; i < text.length(); ++i) {

            if (document.getSyntax().isValidIdentifier(start, text.charAt(i))) {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.IDENTIFIERS_COLOR, i, i + 1);
                start = true;
            } else {
                start = false;
            }
        }
    }

    private void paintKeywords(String text, AttributedString coloredString) {

        List<String> keywords = document.getSyntax().getKeywords();
        String specialText = "! " + text;
        String[] words = specialText.split(" ");
        int start = 0;

        for (int i = 1; i < words.length; ++i) {
            if (keywords.contains(words[i])) {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.KEYWORD_COLOR, start,
                        start + words[i].length());
            }
            start += words[i].length() + 1;
        }

    }

    private void paintLiterals(String text, AttributedString coloredString) {
        boolean start = false;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '"') {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.LITERAL_COLOR, i, i + 1);
                start ^= true;
                continue;
            }
            if (start) {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.LITERAL_COLOR, i, i + 1);
            }
        }
    }

    private void paintBrackets(AttributedString coloredString, int lineIndex) {
        Integer index = document.getMatchingBracket().get(lineIndex);
        if (index != -1) {
            coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.BRACKET_COLOR, index, index + 1);
        }
        SimpleCaret caret = document.getCurrentCaret();
        if ((Utilities.isBracket(caret.getSymbol())) && (caret.lineIndex == lineIndex)) {
            coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.BRACKET_COLOR, caret.charIndex,
                    caret.charIndex + 1);
        }
    }

    private void paintComments(AttributedString coloredString, int lineIndex) {
        Integer index = document.getLineCommentIndex().get(lineIndex);
        if (index != -1) {
            coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.COMMENT_COLOR, index,
                    document.lineLength(lineIndex));
        }
        List<Pair<Integer, Integer>> pairs = document.getTextCommentIndex().get(lineIndex);
        for (Pair<Integer, Integer> pair : pairs) {
            if (pair.getFirst() < pair.getSecond()) {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Utilities.COMMENT_COLOR, pair.getFirst(),
                        pair.getSecond());
            }
        }
    }
}
