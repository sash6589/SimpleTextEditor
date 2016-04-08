package com.aomatveev.texteditor.syntax;

import java.util.Arrays;

public class JavascriptSyntax extends AbstractSyntax {

    @Override
    public boolean isValidIdentifier(boolean start, char c) {
        if (start) {
            return Character.isJavaIdentifierPart(c);
        } else {
            return Character.isJavaIdentifierStart(c);
        }
    }

    @Override
    protected void initCommentIdentifiers() {
        LINE_COMMENT_IDENTIFIER = "//";
        OPEN_TEXT_COMMENT_IDENTIFIER = "/*";
        CLOSE_TEXT_COMMENT_IDENTIFIER = "*/";
    }

    @Override
    protected void initKeywords() {
        keywords = Arrays.asList(
                "abstract", "arguments", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval", "export",
                "extends*", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements",
                "import", "in", "instanceof", "int", "interface", "let", "long", "native", "new", "null", "package",
                "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized",
                "this", "throw", "throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while",
                "with", "yield");
    }
}
