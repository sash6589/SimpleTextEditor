package com.aomatveev.texteditor;

import com.aomatveev.texteditor.gui.SimpleTextComponent;

import javax.swing.*;
import java.awt.*;

public class MainActivity {

    private static JFrame frame;

    private static void createAndShowGUI() {
        frame = new JFrame("Text editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        SimpleTextComponent simpleTextComponent = new SimpleTextComponent();
        JScrollPane scrollPane = new JScrollPane(simpleTextComponent);
        scrollPane.setPreferredSize(new Dimension(1024, 768));
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


