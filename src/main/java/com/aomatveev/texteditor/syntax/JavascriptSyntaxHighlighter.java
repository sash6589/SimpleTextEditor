package com.aomatveev.texteditor.syntax;

import java.util.ArrayList;
import java.util.Arrays;

public class JavascriptSyntaxHighlighter extends SyntaxHighlighter {

    @Override
    protected void initKeywords() {
        keywords = new ArrayList<>(Arrays.asList(
                "abstract", "arguments", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue",	"debugger",	"default", "delete", "do", "double", "else", "enum", "eval", "export",
                "extends*", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements",
                "import", "in",	"instanceof", "int", "interface", "let", "long", "native", "new", "null", "package",
                "private",	"protected", "public",	"return", "short", "static", "super", "switch",	"synchronized",
                "this",	"throw", "throws", "transient",	"true", "try",	"typeof", "var", "void", "volatile", "while",
                "with",	"yield"));
    }
}
