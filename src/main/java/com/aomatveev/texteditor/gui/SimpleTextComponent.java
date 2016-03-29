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
import java.util.HashMap;
import java.util.Map;

public class SimpleTextComponent extends JPanel implements Scrollable {

    private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
    private static final Map<TextAttribute, Object> attributesMap = new HashMap<>();

    private static final int FONT_SIZE = 14;
    private static final int TOP_OFFSET = 12;
    private static final int LEFT_OFFSET = 3;
    private static final int CHARACTER_WIDTH = 9;

    private static final Color SELECT_COLOR = Color.orange;
    private static final Color TEXT_COLOR = Color.black;

    private int lineSpacing;

    private Dimension preferredScrollableViewportSize;
    private static final TextLayout defaultTextLayout;

    static {
        attributesMap.put(TextAttribute.FAMILY, "Serif");
        attributesMap.put(TextAttribute.SIZE, FONT_SIZE);
        defaultTextLayout = new TextLayout(" ", attributesMap, DEFAULT_FRC);
    }

    private SimpleDocument document;
    private SimpleCaret currentCaret;

    public SimpleTextComponent() {
        document = new SimpleDocument(this);
        currentCaret = document.getCurrentCaret();
        preferredScrollableViewportSize = new Dimension();
        initLineSpacing();
        setBackground(Color.WHITE);
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

    public String getText() {
        return document.toString();
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public int linesSize() {
        return document.linesSize();
    }

    public TextLayout getTextLayout(int lineIndex) {
        if (lineIndex >= document.linesSize()) {
            return defaultTextLayout;
        }
        StringBuilder line = document.getLine(lineIndex);
        if ("".equals(line.toString())) {
            return new TextLayout(" ", attributesMap, DEFAULT_FRC);
        } else {
            return new TextLayout(line.toString(), attributesMap, DEFAULT_FRC);
        }
    }

    public void paste() {
        document.paste();
    }

    public void copy() {
        document.copy();
    }

    public void cut() {
        document.cut();
    }

    public void selectAll() {
        document.selectAll();
    }

    @Override
    protected void paintComponent(Graphics g) {

        g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        Point2D.Float origin = computeLayoutOrigin();
        Rectangle visibleRect = getVisibleRect();
        TextLayout caretLayout = null;

        for (int i = 0; i < document.linesSize(); ++i) {
            if (needDraw(visibleRect, origin)) {
                StringBuilder line = document.getLine(i);
                TextLayout layout;
                if ("".equals(line.toString())) {
                    layout = new TextLayout(" ", attributesMap, DEFAULT_FRC);
                } else {
                    layout = new TextLayout(line.toString(), attributesMap, DEFAULT_FRC);
                }
                if (document.isSelected()) {
                    Pair<Integer, Integer> bounds = document.getSelectedBounds(i);
                    if (bounds != null) {
                        Shape base = layout.getLogicalHighlightShape(bounds.getKey(), bounds.getValue());
                        AffineTransform at = AffineTransform.getTranslateInstance(origin.getX(), origin.getY());
                        Shape highlight = at.createTransformedShape(base);
                        graphics2D.setColor(SELECT_COLOR);
                        graphics2D.fill(highlight);
                    }
                }
                graphics2D.setColor(TEXT_COLOR);
                layout.draw(graphics2D, (float) origin.getX(), (float) origin.getY());
                if (i == currentCaret.lineIndex) {
                    caretLayout = layout;
                }
            }
            origin.y += lineSpacing;
        }

        if (caretLayout != null) {
            origin = computeCaretOrigin(caretLayout);
            graphics2D.translate(origin.getX(), origin.getY());
            Shape[] carets = caretLayout.getCaretShapes(currentCaret.charIndex);
            graphics2D.draw(carets[0]);
        }
    }

    private Point2D.Float computeLayoutOrigin() {
        Point2D.Float origin = new Point2D.Float();

        origin.x = ((EmptyBorder) getBorder()).getBorderInsets().left;
        origin.y = ((EmptyBorder) getBorder()).getBorderInsets().top;

        return origin;
    }


    private Point2D.Float computeCaretOrigin(TextLayout layout) {
        Point2D.Float origin = computeLayoutOrigin();
        origin.y += lineSpacing * currentCaret.lineIndex;
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        preferredScrollableViewportSize.setSize(Math.max(screenSize.getWidth(), width), Math.max(screenSize.getHeight(), height));

        return preferredScrollableViewportSize;
    }

    private boolean needDraw(Rectangle visibleRect, Point2D origin) {
        return visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY(),
                getWidth(), (int) (defaultTextLayout.getAscent() + defaultTextLayout.getDescent()))) ||
                visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY() -
                        (int) (defaultTextLayout.getAscent() + defaultTextLayout.getDescent()),
                        getWidth(), (int) (defaultTextLayout.getAscent() + defaultTextLayout.getDescent())));
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