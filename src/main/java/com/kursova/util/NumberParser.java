package com.kursova.util;

public class NumberParser {
    public static Integer parseIntOrNull(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Double parseDoubleOrNull(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
