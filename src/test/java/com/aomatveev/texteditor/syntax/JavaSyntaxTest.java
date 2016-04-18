package com.aomatveev.texteditor.syntax;

import com.aomatveev.texteditor.gui.MockSimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.utilities.TestUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JavaSyntaxTest extends Assert {
    private SimpleDocument document;

    @Before
    public void setUp() {
        document = new SimpleDocument(new MockSimpleTextComponent());
        TestUtilities.initDocument(document, generateText());
        document.setSyntax(new JavaSyntax());
    }

    @Test
    public void identifiers() {
        List<Pair<Integer, Integer>> bounds = document.getIdentifiersBounds(0);
        assertEquals(3, bounds.size());
        assertTrue(new Pair<>(0, 6).equals(bounds.get(0)));
        assertTrue(new Pair<>(7, 12).equals(bounds.get(1)));
        assertTrue(new Pair<>(13, 17).equals(bounds.get(2)));
    }

    @Test
    public void keywords() {
        List<Pair<Integer, Integer>> bounds = document.getKeywordsBounds(0);
        assertEquals(2, bounds.size());
        assertTrue(new Pair<>(0, 6).equals(bounds.get(0)));
        assertTrue(new Pair<>(7, 12).equals(bounds.get(1)));
    }

    @Test
    public void literalsTest() {
        List<Pair<Integer, Integer>> bounds = document.getLiteralsBounds(2);
        assertEquals(1, bounds.size());
        assertTrue(new Pair<>(31, 38).equals(bounds.get(0)));
    }

    @Test
    public void lineComments() {
        List<Pair<Integer, Integer>> bounds = document.getCommentsBounds(1);
        assertEquals(1, bounds.size());
        assertTrue(new Pair<>(0, 46).equals(bounds.get(0)));
    }

    @Test
    public void textComments() {
        List<Pair<Integer, Integer>> bounds = document.getCommentsBounds(2);
        assertEquals(1, bounds.size());
        assertTrue(new Pair<>(10, 26).equals(bounds.get(0)));
    }

    @Test
    public void oneLineMatchingOpenBracket() {
        document.moveCaret(2, 30);
        List<Pair<Integer, Integer>> bounds = document.getBracketsBounds(2);

        assertEquals(2, bounds.size());
        assertTrue(new Pair<>(30, 31).equals(bounds.get(1)));
        assertTrue(new Pair<>(38, 39).equals(bounds.get(0)));
    }

    @Test
    public void oneLineMatchingCloseBracket() {
        document.moveCaret(2, 38);
        List<Pair<Integer, Integer>> bounds = document.getBracketsBounds(2);

        assertEquals(2, bounds.size());
        assertTrue(new Pair<>(30, 31).equals(bounds.get(0)));
        assertTrue(new Pair<>(38, 39).equals(bounds.get(1)));
    }

    @Test
    public void manyLineMatchingOpenBracket() {
        document.moveCaret(0, 18);

        assertEquals(1, document.getBracketsBounds(0).size());
        assertEquals(0, document.getBracketsBounds(1).size());
        assertEquals(0, document.getBracketsBounds(2).size());
        assertEquals(1, document.getBracketsBounds(3).size());
        assertEquals(0, document.getBracketsBounds(4).size());

        assertTrue(new Pair<>(18, 19).equals(document.getBracketsBounds(0).get(0)));
        assertTrue(new Pair<>(4, 5).equals(document.getBracketsBounds(3).get(0)));
    }

    @Test
    public void manyLineMatchingCloseBracket() {
        document.moveCaret(3, 4);

        assertEquals(1, document.getBracketsBounds(0).size());
        assertEquals(0, document.getBracketsBounds(1).size());
        assertEquals(0, document.getBracketsBounds(2).size());
        assertEquals(1, document.getBracketsBounds(3).size());
        assertEquals(0, document.getBracketsBounds(4).size());

        assertTrue(new Pair<>(18, 19).equals(document.getBracketsBounds(0).get(0)));
        assertTrue(new Pair<>(4, 5).equals(document.getBracketsBounds(3).get(0)));
    }

    private String generateText() {
        return  "public class Main {\n" +
                "//    public static void main(String[] args) {\n" +
                "        Sy/*stem.out.pri*/ntln(\"Hello\");\n" +
                "    }\n" +
                "}";
    }
}
