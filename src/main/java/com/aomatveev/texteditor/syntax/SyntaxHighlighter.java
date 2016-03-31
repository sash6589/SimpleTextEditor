package com.aomatveev.texteditor.syntax;

import com.aomatveev.texteditor.primitives.Constants;

import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

public abstract class SyntaxHighlighter {

    protected List<String> keywords;

    public SyntaxHighlighter() {
        initKeywords();
    }

    protected void initKeywords() {
        keywords = new ArrayList<>();
    }

    protected AttributedString paintKeywords(String text) {
        if ("".equals(text)) {
            text = " ";
        }
        String specialText = "! " + text;
        String[] words = specialText.split(" ");
        int start = 0;
        AttributedString coloredString = new AttributedString(text, Constants.attributesMap);

        for (int i = 1; i < words.length; ++i) {
            if (keywords.contains(words[i])) {
                coloredString.addAttribute(TextAttribute.FOREGROUND, Constants.KEYWORD_COLOR, start,
                        start + words[i].length());
            }
            start += words[i].length() + 1;
        }
        return coloredString;
    }

    public TextLayout highlightSyntax(String text) {
        return new TextLayout(paintKeywords(text).getIterator(), Constants.DEFAULT_FRC);
    }
}
