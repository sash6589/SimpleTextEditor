package com.aomatveev.texteditor.syntax;

import java.util.ArrayList;
import java.util.Arrays;

public class JavaSyntaxHighlighter extends SyntaxHighlighter {

    @Override
    protected void initKeywords() {
        keywords = new ArrayList<>(Arrays.asList(
                "abstract", "continue", "for",	"new",	"switch", "assert", "default", "package",	"synchronized",
                "boolean", "do", "if", "private", "this",  "break", "double", "implements", "protected", "throw",
                "byte",	"else", "import", "public", "throws", "case", "enum", "instanceof",	"return", "transient",
                "catch", "extends", "int", 	"short", "try", "char",	"final", "interface", "static", "void", "class",
                "finally", "long", "strictfp", "volatile", "float", "native", "super", "while"));
    }
}
