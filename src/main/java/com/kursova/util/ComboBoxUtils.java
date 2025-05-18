package com.kursova.util;

import javafx.scene.control.ComboBox;

public class ComboBoxUtils {
    public static String getSelectedValue(ComboBox<String> box) {
        var value = box.getValue();
        return (value != null && !"Усі".equals(value)) ? value : null;
    }
}
