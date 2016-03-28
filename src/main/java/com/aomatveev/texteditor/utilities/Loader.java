package com.aomatveev.texteditor.utilities;

import com.aomatveev.texteditor.gui.SimpleTextComponent;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class Loader extends SwingWorker<Void, String> {

    private static final int ONE_MB = 1024 * 1024;

    private File loadFile;
    private SimpleTextComponent simpleTextComponent;

    public Loader(File loadFile, SimpleTextComponent simpleTextComponent) {
        this.loadFile = loadFile;
        this.simpleTextComponent = simpleTextComponent;
    }

    @Override
    protected Void doInBackground() throws Exception {
        char[] buf = new char[ONE_MB];
        try (BufferedReader reader = new BufferedReader(new FileReader(loadFile))) {
            int read;
            while ((read = reader.read(buf)) != -1) {
                publish(new String(buf, 0, read));
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            simpleTextComponent.append(chunk);
        }
    }
}
