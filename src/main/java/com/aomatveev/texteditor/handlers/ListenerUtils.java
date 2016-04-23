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

        int lineIndex = clickY / viewModel.getLineSpacing();
        int charIndex = 0;
        TextLayout layout = viewModel.getTextLayout(lineIndex);
        if (lineIndex < viewModel.linesSize()) {
            TextHitInfo currentHit = layout.hitTestChar(clickX, 0);
            charIndex = currentHit.getInsertionIndex();
        }
        return new Pair<>(lineIndex, charIndex);
    }
}