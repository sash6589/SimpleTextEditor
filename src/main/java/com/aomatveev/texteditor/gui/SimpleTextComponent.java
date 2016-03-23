package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleTextComponent extends JPanel {

    private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
    private static final Map<TextAttribute, Object> attributesMap = new HashMap<>();
    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, 14);
    }

    private SimpleDocument simpleDocument;

    private SimpleCaret currentCaret;

    public SimpleTextComponent() {
        simpleDocument = new SimpleDocument("It's a test!\n\nAnd new line!");
        currentCaret = initCaret();
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 3, 0, 0));
    }

    private SimpleCaret initCaret() {
        return new SimpleCaret(simpleDocument.linesCount()-1, simpleDocument.charCount(simpleDocument.linesCount()-1));
    }

    private List<TextLayout> generateTextLayouts() {
        List<TextLayout> res = new ArrayList<>();
        if (simpleDocument.length() == 0) {
            res.add(new TextLayout(" ", attributesMap, DEFAULT_FRC));
        }

        String text = simpleDocument.toString();

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            if ("".equals(lines[i])) {
                res.add(new TextLayout(" ", attributesMap, DEFAULT_FRC));
            }
            else {
                res.add(new TextLayout(lines[i], attributesMap, DEFAULT_FRC));
            }
        }
        return res;
    }


    private Point2D.Float computeLayoutOrigin() {
        Point2D.Float origin = new Point2D.Float();

        origin.x = ((EmptyBorder) getBorder()).getBorderInsets().left;
        origin.y = ((EmptyBorder) getBorder()).getBorderInsets().top;

        return origin;
    }

    private Point2D.Float computeCaretOrigin(TextLayout layout) {
        Point2D.Float origin = computeLayoutOrigin();
        origin.y += (layout.getAscent() + layout.getDescent()) * currentCaret.lineCount;
        return origin;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        Point2D.Float origin = computeLayoutOrigin();

        List <TextLayout> textLayouts = generateTextLayouts();
        for (TextLayout layout : textLayouts) {
            layout.draw(graphics2D, (float) origin.getX(), (float) origin.getY());
            origin.y += layout.getAscent() + layout.getDescent();
        }

        origin = computeCaretOrigin(textLayouts.get(0));
        graphics2D.translate(origin.getX(), origin.getY());
        Shape[] carets = textLayouts.get(currentCaret.lineCount).getCaretShapes(currentCaret.charCount);
        graphics2D.draw(carets[0]);
    }

}
