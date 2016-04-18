package com.aomatveev.texteditor.primitives;

import com.aomatveev.texteditor.gui.MockSimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.utilities.TestUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleCaretTest extends Assert {
    private SimpleDocument document;
    private SimpleCaret caret;

    @Before
    public void setUp() {
        document = new SimpleDocument(new MockSimpleTextComponent());
        String text = "\nsimple \n\n text with\n many lines\n";
        TestUtilities.initDocument(document, text);
        caret = document.getCurrentCaret();
    }

    @Test
    public void moveNextWordFromEndDocument() {
        caret.moveToNextWord(document);
        assertTrue(new SimpleCaret(5, 0).equals(caret));
    }

    @Test
    public void moveNextWordFromEndLine() {
        setCaret(1, 7);

        caret.moveToNextWord(document);
        assertTrue(new SimpleCaret(2, 0).equals(caret));
    }

    @Test
    public void moveNextWordFromAnyPlace() {
        setCaret(3, 3);

        caret.moveToNextWord(document);
        assertTrue(new SimpleCaret(3, 6).equals(caret));
    }

    @Test
    public void movePrevWordFromBeginningFile() {
        setCaret(0, 0);

        caret.moveToPrevWord(document);
        assertTrue(new SimpleCaret(0, 0).equals(caret));
    }

    @Test
    public void movePrevWordFromBeginningLine() {
        setCaret(4, 0);

        caret.moveToPrevWord(document);
        assertTrue(new SimpleCaret(3, 10).equals(caret));
    }

    @Test
    public void movePrevWordFromAnyPlace() {
        setCaret(3, 4);

        caret.moveToPrevWord(document);
        assertTrue(new SimpleCaret(3, 1).equals(caret));
    }

    private void setCaret(int lineIndex, int charIndex) {
        caret.lineIndex = lineIndex;
        caret.charIndex = charIndex;
    }
}
