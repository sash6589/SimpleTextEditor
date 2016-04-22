package com.aomatveev.texteditor.handlers;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.primitives.Pair;

import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;

public class ListenerUtils {
    public static Pair<Integer, Integer> findPosition(MouseEvent e, SimpleTextComponent viewModel) {
        int clickX = e.getX();
        int clickY = e.getY();

        int lineIndex = findLine(clickY, viewModel);
        int charIndex = 0;
        TextLayout layout = viewModel.getTextLayout(lineIndex);
        if (lineIndex < viewModel.linesSize()) {
            TextHitInfo currentHit = layout.hitTestChar(clickX, 0);
            charIndex = currentHit.getInsertionIndex();
        }
        return new Pair<>(lineIndex, charIndex);
    }

    private static int findLine(int clickY, SimpleTextComponent viewModel) {
        int lineSpacing = viewModel.getLineSpacing();
        int yValue = 0;

        for (int i = 0; i < viewModel.linesSize(); ++i) {
            if (yValue + lineSpacing >= clickY) {
                return i;
            }
            yValue += lineSpacing;
        }
        return viewModel.linesSize();
    }
}