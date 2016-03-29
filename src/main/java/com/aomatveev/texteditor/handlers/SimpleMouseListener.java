package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;
import javafx.util.Pair;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleMouseListener extends MouseAdapter {

    private SimpleTextComponent viewModel;
    private SimpleDocument document;

    public SimpleMouseListener(SimpleTextComponent viewModel, SimpleDocument document) {
        this.viewModel = viewModel;
        this.document = document;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Pair<Integer, Integer> position = ListenerUtils.findPosition(e, viewModel);
        document.cancelSelect();
        document.moveCaret(position.getKey(), position.getValue());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Pair<Integer, Integer> position = ListenerUtils.findPosition(e, viewModel);
        document.moveSelectedCaret(position.getKey(), position.getValue());
    }

}
