package com.aomatveev.texteditor;

import com.aomatveev.texteditor.gui.SimpleTextComponent;
import com.aomatveev.texteditor.utilities.Loader;
import com.aomatveev.texteditor.utilities.Saver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainActivity {

    private static final int TOP_OFFSET = 12;
    private static final int LEFT_OFFSET = 3;

    private static SimpleTextComponent simpleTextComponent;

    private static JMenuItem newItem;
    private static JMenuItem openItem;
    private static JMenuItem saveItem;
    private static JMenuItem exitItem;

    private static JMenuItem pasteItem;
    private static JMenuItem copyItem;
    private static JMenuItem cutItem;
    private static JMenuItem selectAll;

    private static void initTextPanel(JFrame frame) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        simpleTextComponent = new SimpleTextComponent();
        JScrollPane scrollPane = new JScrollPane(simpleTextComponent);
        int top = TOP_OFFSET;
        int left = LEFT_OFFSET;
        int bottom = scrollPane.getHorizontalScrollBar().getHeight();
        int right = scrollPane.getVerticalScrollBar().getWidth();
        simpleTextComponent.setBorder(new EmptyBorder(top, left, bottom, right));

        InputMap im = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke("UP"), "none");
        im.put(KeyStroke.getKeyStroke("DOWN"), "none");
        im.put(KeyStroke.getKeyStroke("LEFT"), "none");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);

    }

    private static void initMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        JMenu editMenu = new JMenu("Edit");

        pasteItem = new JMenuItem("Paste");
        copyItem = new JMenuItem("Copy");
        cutItem = new JMenuItem("Cut");
        selectAll = new JMenuItem("Select All");

        frame.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        editMenu.add(pasteItem);
        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(selectAll);

        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        ActionListener listener = new SimpleActionListener();
        newItem.addActionListener(listener);
        openItem.addActionListener(listener);
        saveItem.addActionListener(listener);
        exitItem.addActionListener(listener);
        pasteItem.addActionListener(listener);
        copyItem.addActionListener(listener);
        cutItem.addActionListener(listener);
        selectAll.addActionListener(listener);
    }

    private static void newFile() {
        simpleTextComponent.newFile();
    }

    private static void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            newFile();
            new Loader(fileChooser.getSelectedFile(), simpleTextComponent).execute();
        }
    }

    private static void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            new Saver(fileChooser.getSelectedFile(), simpleTextComponent.getText()).execute();
        }
    }

    private static void exitProgram() {
        System.exit(0);
    }

    private static void paste() {
        simpleTextComponent.paste();
    }

    private static void copy() {
        simpleTextComponent.copy();
    }

    private static void cut() {
        simpleTextComponent.cut();
    }

    private static void selectAll() {
        simpleTextComponent.selectAll();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Text editor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        initTextPanel(frame);
        initMenuBar(frame);

        frame.setBackground(Color.WHITE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }

    private static class SimpleActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem choice = (JMenuItem) e.getSource();
            if (choice == newItem) {
                newFile();
            }
            if (choice == saveItem) {
                saveFile();
                return;
            }
            if (choice == openItem) {
                openFile();
                return;
            }
            if (choice == exitItem) {
                exitProgram();
                return;
            }
            if (choice == pasteItem) {
                paste();
                return;
            }
            if (choice == copyItem) {
                copy();
                return;
            }
            if (choice == cutItem) {
                cut();
                return;
            }
            if (choice == selectAll) {
                selectAll();
                return;
            }
        }
    }
}
