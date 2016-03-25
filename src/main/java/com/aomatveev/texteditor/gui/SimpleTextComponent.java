package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleTextComponent extends JPanel implements Scrollable {

    private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
    private static final Map<TextAttribute, Object> attributesMap = new HashMap<>();

    private static final int FONT_SIZE = 14;
    private static final int TOP_OFFSET = 12;
    private static final int LEFT_OFFSET = 3;
    private static final int CHARACTER_WIDTH = 8;

    private int lineSpacing;

    private FontMetrics fontMetrics;

    private Dimension preferredScrollableViewportSize;
    private boolean preferredScrollableViewportSizeChanged = false;

    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, FONT_SIZE);
    }

    private SimpleDocument simpleDocument;

    private SimpleCaret currentCaret;

    public SimpleTextComponent() {
        simpleDocument = new SimpleDocument(this);
        currentCaret = simpleDocument.getCurrentCaret();
        initLineSpacing();
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(TOP_OFFSET, LEFT_OFFSET, 0, 0));
        addKeyListener(new SimpleKeyListener());
        setFocusable(true);
    }

    public void updateView() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics();
        }

        g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        Point2D.Float origin = computeLayoutOrigin();

        List<TextLayout> textLayouts = generateTextLayouts();
        for (TextLayout layout : textLayouts) {
            layout.draw(graphics2D, (float) origin.getX(), (float) origin.getY());
            origin.y += layout.getAscent() + layout.getDescent();
        }

        origin = computeCaretOrigin(textLayouts.get(currentCaret.lineIndex));
        graphics2D.translate(origin.getX(), origin.getY());
        Shape[] carets = textLayouts.get(currentCaret.lineIndex).getCaretShapes(currentCaret.charIndex);
        graphics2D.draw(carets[0]);
    }

    private List<TextLayout> generateTextLayouts() {
        List<TextLayout> res = new ArrayList<>();
        if (simpleDocument.length() == 0) {
            res.add(new TextLayout(" ", attributesMap, DEFAULT_FRC));
        }

        List<StringBuilder> lines = simpleDocument.getLines();

        for (StringBuilder line : lines) {
            if ("".equals(line.toString())) {
                res.add(new TextLayout(" ", attributesMap, DEFAULT_FRC));
            } else {
                res.add(new TextLayout(line.toString(), attributesMap, DEFAULT_FRC));
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
        origin.y += (layout.getAscent() + layout.getDescent()) * currentCaret.lineIndex;
        return origin;
    }

    private void initLineSpacing() {
        TextLayout layout = new TextLayout(" ", attributesMap, DEFAULT_FRC);
        lineSpacing = ((int) (layout.getAscent() + layout.getDescent())) + 1;
    }

    private Dimension computeDimension() {
        if (preferredScrollableViewportSize == null) {
            preferredScrollableViewportSize = new Dimension();
            preferredScrollableViewportSizeChanged = true;
        }

        if (preferredScrollableViewportSizeChanged) {
            int height = TOP_OFFSET + (lineSpacing * simpleDocument.linesCount());

            int maxLen = 0;
            for (int i = 0; i < simpleDocument.linesCount(); ++i) {
                if (maxLen < simpleDocument.getLine(i).length()) {
                    maxLen = simpleDocument.getLine(i).length();
                }
            }
            int width = 0;
            if (fontMetrics != null) {
                width = LEFT_OFFSET + (maxLen * CHARACTER_WIDTH);
            }

            preferredScrollableViewportSize.setSize(Math.max(1024, width), Math.max(768, height));
            preferredScrollableViewportSizeChanged = false;
        }

        return preferredScrollableViewportSize;
    }

    // --- implements Scrollable ---------------------------------

    @Override
    public Dimension getPreferredSize() {
        return computeDimension();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return computeDimension();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return CHARACTER_WIDTH;
        } else {
            return lineSpacing;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width;
        } else {
            return visibleRect.height - (visibleRect.height % lineSpacing);
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    // --- Key listener ---------------------------------

    private class SimpleKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == '\n') {
                return;
            }
            simpleDocument.insertText(e.getKeyChar());
            preferredScrollableViewportSizeChanged = true;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                simpleDocument.insertNewLine();
                preferredScrollableViewportSizeChanged = true;
            }
        }
    }
}
