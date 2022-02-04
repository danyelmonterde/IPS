package com.monterdev.util;

import java.util.IllegalFormatException;

public class DataUtil {

    public static double formatDouble(String text){
        try{
            return Double.parseDouble(String.format("%.2f", Double.parseDouble(text)));
        }catch (IllegalFormatException exception){
            return 0.0;
        }catch (Exception e){
            return 0.0;
        }

    }

    public static String formatDouble(Double text){
        return String.format("%.2f", text);
    }

    public static Double formatToDouble(Double text){
        return DataUtil.formatDouble(String.format("%.2f", text));
    }
}
