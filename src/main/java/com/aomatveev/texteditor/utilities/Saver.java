package com.aomatveev.texteditor.utilities;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Saver extends SwingWorker<Void, Void> {
    private File saveFile;
    private String data;

    public Saver(File saveFile, String data) {
        this.saveFile = saveFile;
        this.data = data;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(saveFile))) {
            pw.print(data);
        }
        return null;
    }
}
