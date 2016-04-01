package com.aomatveev.texteditor.utilities;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.util.*;
import java.util.List;

public class Utilities {
    public static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
    public static final Map<TextAttribute, Object> attributesMap = new HashMap<>();

    public static final int FONT_SIZE = 14;
    public static final int CHARACTER_WIDTH = 9;

    public static final Color SELECT_COLOR = Color.orange;
    public static final Color TEXT_COLOR = Color.black;
    public static final Color KEYWORD_COLOR = Color.blue;
    public static final Color INSERT_MODE_CARET = Color.red;
    public static final Color BRACKET_COLOR = Color.yellow;
    public static final Color IDENTIFIERS_COLOR = new Color(165, 42, 42);
    public static final Color COMMENT_COLOR = Color.gray;
    public static final Color LITERAL_COLOR = Color.magenta;

    public static final TextLayout defaultTextLayout;

    public static final List<Character> brackets;

    public static final int TOP_OFFSET = 12;
    public static final int LEFT_OFFSET = 3;

    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, FONT_SIZE);
        defaultTextLayout = new TextLayout(" ", attributesMap, DEFAULT_FRC);
        brackets = new ArrayList<>(Arrays.asList('(', '{', '[', ']', '}', ')'));
    }

    public static boolean isBracket(Character c) {
        if (c == null) return false;
        return brackets.contains(c);
    }

    public static Character matchingBracket(char c) {
        if (c == '(') return ')';
        if (c == '{') return '}';
        if (c == '[') return ']';
        if (c == ')') return '(';
        if (c == '}') return '{';
        if (c == ']') return '[';
        return null;
    }

    public static boolean isOpenBracket(char c) {
        return c == '(' || c == '{' || c == '[';
    }
}
