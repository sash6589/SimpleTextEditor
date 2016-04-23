package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.utilities.Utilities;


import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

public class SyntaxHighlighter {

    public static AttributedString getAttributedString(int lineIndex, SimpleDocument document) {
        String text = document.getLine(lineIndex).toString();
        if ("".equals(text)) {
            text = " ";
        }
        AttributedString coloredString = new AttributedString(text, Utilities.attributesMap);

        Color chars[] = new Color[text.length()];

        fillBounds(chars, document.getIdentifiersBounds(lineIndex), Utilities.IDENTIFIERS_COLOR);
        fillBounds(chars, document.getKeywordsBounds(lineIndex), Utilities.KEYWORD_COLOR);
        fillBounds(chars, document.getLiteralsBounds(lineIndex), Utilities.LITERAL_COLOR);
        fillBounds(chars, document.getBracketsBounds(lineIndex), Utilities.BRACKET_COLOR);
        fillBounds(chars, document.getCommentsBounds(lineIndex), Utilities.COMMENT_COLOR);

        paintBounds(coloredString, createBounds(chars));
        return coloredString;
    }

    public static TextLayout getTextLayout(int lineIndex, SimpleDocument document) {
        return new TextLayout(getAttributedString(lineIndex, document).getIterator(), Utilities.DEFAULT_FRC);
    }

    private static void fillBounds(Color chars[], List<Pair<Integer, Integer>> bounds, Color color) {
        for (Pair<Integer, Integer> bound : bounds) {
            for (int i = bound.getFirst(); i < bound.getSecond(); ++i) {
                chars[i] = color;
            }
        }
    }

    private static List<Pair<Pair<Integer, Integer>, Color>> createBounds(Color chars[]) {
        List<Pair<Pair<Integer, Integer>, Color>> ans = new ArrayList<>();
        int start = 0;
        for (int i = 1; i < chars.length - 1; ++i) {
            if (chars[start] != chars[i]) {
                if (chars[start] != null) {
                    ans.add(new Pair<>(new Pair<>(start, i), chars[start]));
                }
                start = i;
            }
        }
        if (chars[start] != null) {
            ans.add(new Pair<>(new Pair<>(start, chars.length), chars[start]));
        }
        return ans;
    }

    private static void paintBounds(AttributedString coloredString, List<Pair<Pair<Integer, Integer>, Color>> bounds) {
        for (Pair<Pair<Integer, Integer>, Color> bound : bounds) {
            coloredString.addAttribute(TextAttribute.FOREGROUND, bound.getSecond(), bound.getFirst().getFirst(),
                    bound.getFirst().getSecond());
        }
    }

}