package com.example.nuradadmin.Utilities;

public class TrimUtil {
    public static String trimRight(String text) {
        if (text == null) {
            return null;
        }
        int len = text.length();
        while (len > 0 && text.charAt(len - 1) == ' ') {
            len--;
        }
        return text.substring(0, len);
    }
}
