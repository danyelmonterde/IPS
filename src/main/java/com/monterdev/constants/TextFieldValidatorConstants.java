package com.monterdev.constants;

public class TextFieldValidatorConstants {

    public static final  String FLOAT_NUMBERS_REGEX = "^\\d*\\.\\d+|\\d+\\.\\d*$";
    public static final  String WHOLE_NUMBERS_REGEX = "^\\d+$";
    public static final  String WHOLE_NUMBERS_REGEX_EXCLUDE = "[^\\d+$]";
    public static final  String WHITE_SPACE_REGEX = "\\s";
    public static final  String FLOAT_NUMBERS_REGEX_EXCLUDE = "[^\\d*\\.\\d+|\\d+\\.\\d*$]|[+$*|]";
    public static final  String PLUS_DOLLAR_REGEX_EXCLUDE ="[?!^+$]";
}
