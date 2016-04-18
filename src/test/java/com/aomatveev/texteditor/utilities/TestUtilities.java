package com.aomatveev.texteditor.utilities;

import com.aomatveev.texteditor.model.SimpleDocument;
import com.aomatveev.texteditor.primitives.SimpleCaret;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestUtilities {
    public static void initDocument(SimpleDocument document, String text) {
        try {
            Method method = document.getClass().getDeclaredMethod("insertText", String.class);
            method.setAccessible(true);
            method.invoke(document, text);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
