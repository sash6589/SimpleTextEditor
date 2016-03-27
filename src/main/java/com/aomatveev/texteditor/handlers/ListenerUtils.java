package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.model.SimpleDocument;
import javafx.util.Pair;

import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.util.List;

public class ListenerUtils {
    public static Pair<Integer, Integer> findPosition(MouseEvent e, SimpleTextComponent viewModel,
                                                      SimpleDocument document) {
        float clickX = e.getX();
        float clickY = e.getY();

        int lineIndex = findLine(clickY, viewModel, document);
        int charIndex = 0;
        List<TextLayout> layouts = viewModel.getTextLayouts();
        if (lineIndex < layouts.size()) {
            TextHitInfo currentHit = layouts.get(lineIndex).hitTestChar(clickX, clickY);
            charIndex = currentHit.getInsertionIndex();
        }
        return new Pair<>(lineIndex, charIndex);
    }

    private static int findLine(float clickY, SimpleTextComponent viewModel, SimpleDocument document) {
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
