package com.aomatveev.texteditor;

import com.aomatveev.texteditor.gui.SimpleTextComponent;

import javax.swing.*;
import java.awt.*;

public class MainActivity {

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Text editor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        SimpleTextComponent simpleTextComponent = new SimpleTextComponent();

        JScrollPane scrollPane = new JScrollPane(simpleTextComponent);
        InputMap im = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke("UP"), "none");
        im.put(KeyStroke.getKeyStroke("DOWN"), "none");
        im.put(KeyStroke.getKeyStroke("LEFT"), "none");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);
        frame.setBackground(Color.WHITE);
        frame.pack();
        frame.setSize(new Dimension(1024, 768));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }
}


