package com.aomatveev.texteditor.model;

import com.aomatveev.texteditor.gui.MockSimpleTextComponent;
import com.aomatveev.texteditor.utilities.TestUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleDocumentTest extends Assert {

    private SimpleDocument document;

    @Before
    public void setUp() {
        document = new SimpleDocument(new MockSimpleTextComponent());
    }

    @Test
    public void insertCharToEmptyDocument() {
        document.insertText('a');
        assertEquals("a", document.toString());
    }


    @Test
    public void insertTextToEmptyDocument() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);
        assertEquals(pasteString, document.toString());
    }

    @Test
    public void insertNewLineToEmptyDocument() {
        document.insertNewLine();
        assertEquals("\n", document.toString());
    }

    @Test
    public void insertManyLinesToEmptyDocument() {
        String pasteString = "\n\npaste\n string\n\n";
        TestUtilities.initDocument(document, pasteString);
        assertEquals(pasteString, document.toString());
    }

    @Test
    public void insertCharToEndDocument() {
        String pasteString = "\n\npaste\n string\n\n";
        TestUtilities.initDocument(document, pasteString);

        document.insertText('a');
        assertEquals(pasteString + 'a', document.toString());
    }

    @Test
    public void insertNewLineToEndDocument() {
        String pasteString = "\n\npaste\n string\n\n";
        TestUtilities.initDocument(document, pasteString);

        document.insertText('\n');
        assertEquals(pasteString + '\n', document.toString());
    }

    @Test
    public void insertCharToAnyPlace() {
        String part1 = "\n\npaste\n";
        String part2 = " string\n\n";
        TestUtilities.initDocument(document, part1 + part2);

        document.moveCaret(3, 0);

        document.insertText('a');
        assertEquals(part1 + "a" + part2, document.toString());
    }

    @Test
    public void insertNewLineToAnyPlace() {
        String part1 = "\n\npaste\n";
        String part2 = " string\n\n";
        TestUtilities.initDocument(document, part1 + part2);

        document.moveCaret(3, 0);

        document.insertNewLine();
        assertEquals(part1 + "\n" + part2, document.toString());
    }

    @Test
    public void insertCharToEndDocumentInInsertMode() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);

        document.setInsertMode();
        document.insertText('a');
        assertEquals(pasteString + 'a', document.toString());
    }

    @Test
    public void insertCharToAnyPlaceInInsertMode() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(0, 4);

        document.setInsertMode();
        document.insertText('a');
        assertEquals("pasta string", document.toString());
    }

    @Test
    public void insertCharToEndLineInInsertMode() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(0, 5);

        document.setInsertMode();
        document.insertText('a');
        assertEquals("pastea\nstring", document.toString());
    }

    @Test
    public void backspaceCharAtBeginningDocument() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(0, 0);

        document.backspaceChar();
        assertEquals(pasteString, document.toString());
    }


    @Test
    public void backspaceCharAtBeginningLine() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(1, 0);

        document.backspaceChar();
        assertEquals("pastestring", document.toString());
    }

    @Test
    public void backspaceCharAtAnyPlace() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(1, 2);

        document.backspaceChar();
        assertEquals("paste\nsring", document.toString());
    }

    @Test
    public void deleteCharAtEndDocument() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(0, pasteString.length());

        document.deleteChar();
        assertEquals(pasteString, document.toString());
    }

    @Test
    public void deleteCharAtEndLine() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(0, 5);

        document.deleteChar();
        assertEquals("pastestring", document.toString());
    }

    @Test
    public void deleteCharAtAnyPlace() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveCaret(1, 2);

        document.deleteChar();
        assertEquals("paste\nsting", document.toString());
    }

    @Test
    public void selectAll() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.selectAll();
        assertEquals(pasteString, getSelectedText());
    }

    @Test
    public void selectPartOfLine() {
        String pasteString = "paste string";
        TestUtilities.initDocument(document, pasteString);

        document.moveSelectedCaret(0, 10);
        document.moveCaret(0, 12);

        assertEquals("ng", getSelectedText());
    }

    @Test
    public void selectManyLines() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveSelectedCaret(0, 2);
        document.moveCaret(1, 3);

        assertEquals("ste\nstr", getSelectedText());
    }

    @Test
    public void cutFromAnyPlace() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveSelectedCaret(0, 2);
        document.moveCaret(1, 3);

        document.cut();
        assertEquals("paing", document.toString());
    }

    @Test
    public void pasteFromAnyPlace() {
        String part1 = "\n\npaste\n";
        String part2 = " string\n\n";
        TestUtilities.initDocument(document, part1 + part2);

        document.moveCaret(3, 0);

        String text = "te\nxt";
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        document.paste();
        assertEquals(part1 + text + part2, document.toString());
    }

    @Test
    public void copyFromAnyPlace() {
        String pasteString = "paste\nstring";
        TestUtilities.initDocument(document, pasteString);

        document.moveSelectedCaret(0, 2);
        document.moveCaret(1, 3);

        document.copy();
        try {
            assertEquals("ste\nstr", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getSelectedText() {
        try {
            Method method = document.getClass().getDeclaredMethod("getSelectedText");
            method.setAccessible(true);
            return (String) method.invoke(document);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
