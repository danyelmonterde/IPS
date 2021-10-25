package com.monterdev.util;

public class DataUtil {

    public static double formatDouble(String text){
        return Double.parseDouble(String.format("%.2f", Double.parseDouble(text)));
    }

    public static String formatDouble(Double text){
        return String.format("%.2f", text);
    }

    public static Double formatToDouble(Double text){
        return Double.parseDouble(String.format("%.2f", text));
    }
}
