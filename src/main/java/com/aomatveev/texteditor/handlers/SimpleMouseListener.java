package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.util.List;

public class SimpleMouseListener extends MouseAdapter {

    private SimpleTextComponent viewModel;
    private SimpleDocument document;

    public SimpleMouseListener(SimpleTextComponent viewModel, SimpleDocument document) {
        this.viewModel = viewModel;
        this.document = document;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        float clickX = e.getX();
        float clickY = e.getY();

        int lineIndex = findLine(clickY);
        int charIndex = 0;
        List<TextLayout> layouts = viewModel.getTextLayouts();
        if (lineIndex < layouts.size()) {
            TextHitInfo currentHit = layouts.get(lineIndex).hitTestChar(clickX, clickY);
            charIndex = currentHit.getInsertionIndex();
        }
        document.moveCaret(lineIndex, charIndex);
    }

    private int findLine(float clickY) {
        List<TextLayout> layouts = viewModel.getTextLayouts();
        float yValue = 0;

        for (int i = 0; i < layouts.size(); ++i) {
            if (yValue + layouts.get(i).getAscent() + layouts.get(i).getDescent() >= clickY) {
                return i;
            }
            yValue += layouts.get(i).getAscent() + layouts.get(i).getDescent();
        }
        return layouts.size();
    }
}
