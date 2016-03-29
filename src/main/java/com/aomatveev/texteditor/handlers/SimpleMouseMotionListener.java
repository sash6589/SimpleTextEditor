package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;
import javafx.util.Pair;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SimpleMouseMotionListener extends MouseMotionAdapter {
    private SimpleTextComponent viewModel;
    private SimpleDocument document;

    public SimpleMouseMotionListener(SimpleTextComponent viewModel, SimpleDocument document) {
        this.viewModel = viewModel;
        this.document = document;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Pair<Integer, Integer> position = ListenerUtils.findPosition(e, viewModel);
        document.moveSelectedCaret(position.getKey(), position.getValue());
    }
}
