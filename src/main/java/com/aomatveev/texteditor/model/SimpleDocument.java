package com.aomatveev.texteditor.model;

public class SimpleDocument {
    private StringBuilder textData;
    private String[] lines;

    public SimpleDocument() {
        textData = new StringBuilder();
        lines = new String[0];
    }

    public SimpleDocument(String textData) {
        this.textData = new StringBuilder(textData);
        this.lines = textData.split("\n");
    }

    public String getLine(int index) {
        return lines[index];
    }

    public int charCount(int lineIndex) {
        return lines[lineIndex].length();
    }

    public int linesCount() {
        return lines.length;
    }

    public int length() {
        return textData.length();
    }

    @Override
    public String toString() {
        return textData.toString();
    }

}
