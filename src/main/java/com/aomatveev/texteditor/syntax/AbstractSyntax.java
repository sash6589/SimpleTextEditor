package com.aomatveev.texteditor.syntax;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSyntax {

    public static String LINE_COMMENT_IDENTIFIER;

    protected List<String> keywords;

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

    protected void initCommentIdentifiers() {
        LINE_COMMENT_IDENTIFIER = null;
    }

    protected void initKeywords() {
        keywords = new ArrayList<>();
    }
}
