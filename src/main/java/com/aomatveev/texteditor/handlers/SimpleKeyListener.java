package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.model.SimpleDocument;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleKeyListener extends KeyAdapter {


    private SimpleDocument document;

    public SimpleKeyListener(SimpleDocument document) {
        this.document = document;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_INSERT) {
            document.setInsertMode();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (document.isSelected()) {
                document.cut();
            }
            document.insertNewLine();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (document.isSelected()) {
                document.cut();
            } else {
                document.backspaceChar();
            }
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (document.isSelected()) {
                document.cut();
            } else {
                document.deleteChar();
            }
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_END || e.getKeyCode() == KeyEvent.VK_HOME) {
            if (e.isShiftDown()) {
                document.moveSelectedCaret(e);
                return;
            } else {
                document.cancelSelect();
                document.moveCaret(e);
                return;
            }
        }
        if (!specialKey(e)) {
            if (document.isSelected()) {
                document.cut();
            }
            if (e.isShiftDown()) {
                document.insertText(Character.toUpperCase(e.getKeyChar()));
            } else {
                document.insertText(e.getKeyChar());
            }
        }
    }

    private boolean specialKey(KeyEvent e) {
        return (e.getKeyCode() == KeyEvent.CHAR_UNDEFINED) || (e.isActionKey())
                || (e.getKeyCode() == KeyEvent.VK_CONTROL) || (e.getKeyCode() == KeyEvent.VK_SHIFT)
                || (e.getKeyCode() == KeyEvent.VK_ALT) || (e.getKeyCode() == KeyEvent.VK_META) || (e.isControlDown())
                || (e.isMetaDown()) || (e.getKeyCode() == 0);
    }
}