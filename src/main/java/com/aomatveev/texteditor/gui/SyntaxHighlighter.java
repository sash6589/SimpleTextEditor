package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.utilities.Utilities;


import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.List;

public class SyntaxHighlighter {

    public static TextLayout highlightSyntax(int lineIndex, SimpleDocument document) {
        String text = document.getLine(lineIndex).toString();
        if ("".equals(text)) {
            text = " ";
        }
        AttributedString coloredString = new AttributedString(text, Utilities.attributesMap);
        paintBounds(coloredString, document.getIdentifiersBounds(lineIndex), Utilities.IDENTIFIERS_COLOR);
        paintBounds(coloredString, document.getKeywordsBounds(lineIndex), Utilities.KEYWORD_COLOR);
        paintBounds(coloredString, document.getLiteralsBounds(lineIndex), Utilities.LITERAL_COLOR);
        paintBounds(coloredString, document.getBracketsBounds(lineIndex), Utilities.BRACKET_COLOR);
        paintBounds(coloredString, document.getCommentsBounds(lineIndex), Utilities.COMMENT_COLOR);
        return new TextLayout(coloredString.getIterator(), Utilities.DEFAULT_FRC);
    }

    private static void paintBounds(AttributedString coloredString, List<Pair<Integer, Integer>> bounds, Color color) {
        for (Pair<Integer, Integer> bound : bounds) {
            coloredString.addAttribute(TextAttribute.FOREGROUND, color, bound.getFirst(),
                    bound.getSecond());
        }
    }

}