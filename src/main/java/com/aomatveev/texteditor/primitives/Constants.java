package com.aomatveev.texteditor.primitives;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
    public static final Map<TextAttribute, Object> attributesMap = new HashMap<>();

    public static final int FONT_SIZE = 14;
    public static final int CHARACTER_WIDTH = 9;

    public static final Color SELECT_COLOR = Color.orange;
    public static final Color TEXT_COLOR = Color.black;
    public static final Color KEYWORD_COLOR = Color.blue;
    public static final Color INSERT_MODE_CARET = Color.red;


    public static final TextLayout defaultTextLayout;

    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, FONT_SIZE);
        defaultTextLayout = new TextLayout(" ", attributesMap, DEFAULT_FRC);
    }

    public static final int TOP_OFFSET = 12;
    public static final int LEFT_OFFSET = 3;
}
