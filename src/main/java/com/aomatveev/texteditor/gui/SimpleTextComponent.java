package com.aomatveev.texteditor.gui;

import com.aomatveev.texteditor.handlers.SimpleKeyListener;
import com.aomatveev.texteditor.handlers.SimpleMouseListener;
import com.aomatveev.texteditor.handlers.SimpleMouseMotionListener;
import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.Constants;
import com.aomatveev.texteditor.primitives.SimpleCaret;
import com.aomatveev.texteditor.syntax.DefaultSyntaxHighlighter;
import com.aomatveev.texteditor.syntax.SyntaxHighlighter;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class SimpleTextComponent extends JPanel implements Scrollable {

    private SimpleDocument document;
    private SimpleCaret currentCaret;
    private SyntaxHighlighter syntaxHighlighter;
    private int lineSpacing;
    private Dimension preferredScrollableViewportSize;

    public SimpleTextComponent() {
        document = new SimpleDocument(this);
        currentCaret = document.getCurrentCaret();
        syntaxHighlighter = new DefaultSyntaxHighlighter();
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
            return Constants.defaultTextLayout;
        }
        StringBuilder line = document.getLine(lineIndex);
        return syntaxHighlighter.highlightSyntax(line.toString());
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

    public void setSyntaxHighlighter(SyntaxHighlighter h) {
        syntaxHighlighter = h;
        updateView();
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
                TextLayout layout = getTextLayout(i);
                if (document.isSelected()) {
                    Pair<Integer, Integer> bounds = document.getSelectedBounds(i);
                    if (bounds != null) {
                        Shape base = layout.getLogicalHighlightShape(bounds.getKey(), bounds.getValue());
                        AffineTransform at = AffineTransform.getTranslateInstance(origin.getX(), origin.getY());
                        Shape highlight = at.createTransformedShape(base);
                        graphics2D.setColor(Constants.SELECT_COLOR);
                        graphics2D.fill(highlight);
                    }
                }
                graphics2D.setColor(Constants.TEXT_COLOR);
                layout.draw(graphics2D, (float) origin.getX(), (float) origin.getY());
                if (i == currentCaret.lineIndex) {
                    caretLayout = layout;
                }
            }
            origin.y += lineSpacing;
        }

        if (caretLayout != null) {
            origin = computeCaretOrigin();
            graphics2D.translate(origin.getX(), origin.getY());
            if (document.isInsertMode()) {
                graphics2D.setColor(Constants.INSERT_MODE_CARET);
            } else {
                graphics2D.setColor(Constants.TEXT_COLOR);
            }
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


    private Point2D.Float computeCaretOrigin() {
        Point2D.Float origin = computeLayoutOrigin();
        origin.y += lineSpacing * currentCaret.lineIndex;
        return origin;
    }

    private void initLineSpacing() {
        lineSpacing = ((int) (Constants.defaultTextLayout.getAscent() + Constants.defaultTextLayout.getDescent())) + 1;
    }

    private Dimension computeDimension() {
        int top = ((EmptyBorder) getBorder()).getBorderInsets().top;
        int height = top + (lineSpacing * document.linesSize());

        int maxLen = 0;
        for (int i = 0; i < document.linesSize(); ++i) {
            if (maxLen < document.getLine(i).length()) {
                maxLen = document.getLine(i).length();
            }
        }

        int left = ((EmptyBorder) getBorder()).getBorderInsets().left;
        int width = left + (maxLen * Constants.CHARACTER_WIDTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        preferredScrollableViewportSize.setSize(Math.max(screenSize.getWidth(), width), Math.max(screenSize.getHeight(), height));

        return preferredScrollableViewportSize;
    }

    private boolean needDraw(Rectangle visibleRect, Point2D origin) {
        return visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY(), getWidth(), lineSpacing))
                || visibleRect.intersects(new Rectangle((int) origin.getX(), (int) origin.getY() - lineSpacing,
                getWidth(), lineSpacing));
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
            return Constants.CHARACTER_WIDTH;
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