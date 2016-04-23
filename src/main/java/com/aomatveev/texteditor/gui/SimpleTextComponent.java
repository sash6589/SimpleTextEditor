package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.handlers.SimpleKeyListener;
import com.aomatveev.texteditor.handlers.SimpleMouseListener;
import com.aomatveev.texteditor.handlers.SimpleMouseMotionListener;
import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.utilities.Utilities;
import com.aomatveev.texteditor.primitives.Pair;
import com.aomatveev.texteditor.syntax.AbstractSyntax;
import com.aomatveev.texteditor.syntax.NoneSyntax;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.AttributedString;

public class SimpleTextComponent extends JPanel implements Scrollable {

    private SimpleDocument document;
    private int lineSpacing;
    private Dimension preferredScrollableViewportSize;

    public SimpleTextComponent() {
        document = new SimpleDocument(this);
        setSyntax(new NoneSyntax());
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
            return Utilities.defaultTextLayout;
        }
        return SyntaxHighlighter.getTextLayout(lineIndex, document);
    }

    public void paste() {
        document.paste();
    }

    public void copy() {
        document.copy();
    }

    public void cut() {
        document.copy();
        document.cut();
    }

    public void selectAll() {
        document.selectAll();
    }

    public void setSyntax(AbstractSyntax syntax) {
        document.setSyntax(syntax);
        updateView();
    }

    @Override
    protected void paintComponent(Graphics g) {

        g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        Rectangle visibleRect = getVisibleRect();
        TextLayout caretLayout = null;
        Pair<Integer, Integer> bound = getDrawBound(visibleRect);
        Point2D.Double origin = computeLayoutOrigin(bound.getFirst());
        for (int i = bound.getFirst(); i < bound.getSecond(); ++i) {
            if (needDraw(visibleRect, origin)) {
                if (document.isSelected()) {
                    TextLayout layout = getTextLayout(i);
                    Pair<Integer, Integer> bounds = document.getSelectedBounds(i);
                    if (bounds != null) {
                        Shape base = layout.getLogicalHighlightShape(bounds.getFirst(), bounds.getSecond());
                        AffineTransform at = AffineTransform.getTranslateInstance(origin.getX(), origin.getY());
                        Shape highlight = at.createTransformedShape(base);
                        graphics2D.setColor(Utilities.SELECT_COLOR);
                        graphics2D.fill(highlight);
                    }
                    graphics2D.setColor(Utilities.TEXT_COLOR);
                    layout.draw(graphics2D, (int) origin.getX(), (int) origin.getY());
                } else {
                    AttributedString drawString = SyntaxHighlighter.getAttributedString(i, document);
                    graphics2D.setColor(Utilities.TEXT_COLOR);
                    graphics2D.drawString(drawString.getIterator(), (int) origin.getX(), (int) origin.getY());
                }
                
                if (i == document.getCaretLineIndex()) {
                    caretLayout = getTextLayout(i);
                }
            }
            origin.y += lineSpacing;
        }
        if (caretLayout != null) {
            origin = computeCaretOrigin();
            graphics2D.translate(origin.getX(), origin.getY());
            if (document.isInsertMode()) {
                graphics2D.setColor(Utilities.INSERT_MODE_CARET);
            } else {
                graphics2D.setColor(Utilities.TEXT_COLOR);
            }
            Shape[] carets = caretLayout.getCaretShapes(document.getCaretCharIndex());
            graphics2D.draw(carets[0]);
        }
    }

    private Point2D.Double computeLayoutOrigin() {
        Point2D.Double origin = new Point2D.Double();

        origin.x = ((EmptyBorder) getBorder()).getBorderInsets().left;
        origin.y = ((EmptyBorder) getBorder()).getBorderInsets().top;

        return origin;
    }

    private Point2D.Double computeLayoutOrigin(int y) {
        Point2D.Double origin = new Point2D.Double();

        origin.x = ((EmptyBorder) getBorder()).getBorderInsets().left;
        origin.y = ((EmptyBorder) getBorder()).getBorderInsets().top;
        origin.y += y * lineSpacing;

        return origin;
    }

    private Point2D.Double computeCaretOrigin() {
        Point2D.Double origin = computeLayoutOrigin();
        origin.y += lineSpacing * document.getCaretLineIndex();
        return origin;
    }

    private void initLineSpacing() {
        lineSpacing = ((int) (Utilities.defaultTextLayout.getAscent() + Utilities.defaultTextLayout.getDescent())) + 1;
    }

    private boolean needDraw(Rectangle visibleRect, Point2D origin) {
        return visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY(), getWidth(), lineSpacing))
                || visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY() - lineSpacing,
                getWidth(), lineSpacing));
    }

    private Pair<Integer, Integer> getDrawBound(Rectangle visibleRect) {
        int start = (int) (visibleRect.getY() / lineSpacing);
        int finish = (int) (((visibleRect.getY() + visibleRect.getHeight()) / lineSpacing) + 1);
        return new Pair<>(Math.max(start - 1, 0), Math.min(finish + 1, linesSize()));
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
            return Utilities.CHARACTER_WIDTH;
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

    private Dimension computeDimension() {
        int top = ((EmptyBorder) getBorder()).getBorderInsets().top;
        int height = top + (lineSpacing * document.linesSize());
        int maxLen = document.getMaxLength();

        int left = ((EmptyBorder) getBorder()).getBorderInsets().left;
        int width = left + (maxLen * Utilities.CHARACTER_WIDTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        preferredScrollableViewportSize.setSize(Math.max(screenSize.getWidth(), width), Math.max(screenSize.getHeight(), height));

        return preferredScrollableViewportSize;
    }
}