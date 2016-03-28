package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.handlers.SimpleKeyListener;
import com.aomatveev.texteditor.handlers.SimpleMouseListener;
import com.aomatveev.texteditor.handlers.SimpleMouseMotionListener;
import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
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

    private static final Color SELECT_COLOR = Color.orange;
    private static final Color TEXT_COLOR = Color.black;

    private int lineSpacing;

    private Dimension preferredScrollableViewportSize;

    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, FONT_SIZE);
    }

    private SimpleDocument document;

    private SimpleCaret currentCaret;

    private List<TextLayout> textLayouts;

    public SimpleTextComponent() {
        document = new SimpleDocument(this);
        currentCaret = document.getCurrentCaret();
        preferredScrollableViewportSize = new Dimension();
        initLineSpacing();
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(TOP_OFFSET, LEFT_OFFSET, 0, 0));
        addKeyListener(new SimpleKeyListener(document));
        addMouseListener(new SimpleMouseListener(this, document));
        addMouseMotionListener(new SimpleMouseMotionListener(this, document));
        setFocusable(true);
    }

    public void newFile() {
        document.newDocument();
        currentCaret = document.getCurrentCaret();
        updateView();
    }

    public void append(String text) {
        document.append(text);
    }

    public void updateView() {
        repaint();
    }

    public List<TextLayout> getTextLayouts() {
        return textLayouts;
    }

    public String getText() {
        return document.toString();
    }

    private Point2D.Float computeLayoutOrigin() {
        Point2D.Float origin = new Point2D.Float();

        origin.x = ((EmptyBorder) getBorder()).getBorderInsets().left;
        origin.y = ((EmptyBorder) getBorder()).getBorderInsets().top;

        return origin;
    }

    @Override
    protected void paintComponent(Graphics g) {

        g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        Point2D.Float origin = computeLayoutOrigin();

        generateTextLayouts();
        for (int i = 0; i < textLayouts.size(); ++i) {
            if (document.isSelected()) {
                Pair<Integer, Integer> bounds = document.getSelectedBounds(i);
                if (bounds != null) {
                    Shape base = textLayouts.get(i).getLogicalHighlightShape(bounds.getKey(), bounds.getValue());
                    AffineTransform at = AffineTransform.getTranslateInstance(origin.getX(), origin.getY());
                    Shape highlight = at.createTransformedShape(base);
                    graphics2D.setColor(SELECT_COLOR);
                    graphics2D.fill(highlight);
                }
            }
            graphics2D.setColor(TEXT_COLOR);
            textLayouts.get(i).draw(graphics2D, (float) origin.getX(), (float) origin.getY());
            origin.y += textLayouts.get(i).getAscent() + textLayouts.get(i).getDescent();
        }

        origin = computeCaretOrigin(textLayouts.get(currentCaret.lineIndex));
        graphics2D.translate(origin.getX(), origin.getY());
        Shape[] carets = textLayouts.get(currentCaret.lineIndex).getCaretShapes(currentCaret.charIndex);
        graphics2D.draw(carets[0]);
    }

    private void generateTextLayouts() {
        List<TextLayout> res = new ArrayList<>();

        List<StringBuilder> lines = document.getLines();

        for (StringBuilder line : lines) {
            if ("".equals(line.toString())) {
                res.add(new TextLayout(" ", attributesMap, DEFAULT_FRC));
            } else {
                res.add(new TextLayout(line.toString(), attributesMap, DEFAULT_FRC));
            }
        }
        textLayouts = res;
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

        int height = TOP_OFFSET + (lineSpacing * document.linesSize());

        int maxLen = 0;
        for (int i = 0; i < document.linesSize(); ++i) {
            if (maxLen < document.getLine(i).length()) {
                maxLen = document.getLine(i).length();
            }
        }

        int width = LEFT_OFFSET + (maxLen * CHARACTER_WIDTH);


        preferredScrollableViewportSize.setSize(Math.max(1024, width), Math.max(768, height));

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
}
