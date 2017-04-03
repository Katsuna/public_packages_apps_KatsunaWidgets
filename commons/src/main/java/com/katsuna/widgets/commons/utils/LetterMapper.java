package com.katsuna.widgets.commons.utils;

import java.util.LinkedHashMap;

public class LetterMapper {

    // LetterMapper: all available groups
    // Mapping result letters in latin - capital - non accented.
    private static final String LETTER_A = "A";
    private static final String LETTER_B = "B";
    private static final String LETTER_E = "E";
    private static final String LETTER_H = "H";
    private static final String LETTER_I = "I";
    private static final String LETTER_K = "K";
    private static final String LETTER_M = "M";
    private static final String LETTER_N = "N";
    private static final String LETTER_O = "O";
    private static final String LETTER_P = "P";
    private static final String LETTER_S = "S";
    private static final String LETTER_T = "T";
    private static final String LETTER_X = "X";
    private static final String LETTER_Y = "Y";
    private static final String LETTER_Z = "Z";

    private static LetterMapper ourInstance = new LetterMapper();
    private LinkedHashMap<String, String> map = new LinkedHashMap<>();
    private LetterMapper() {
        // For each language put the corresponding letter to the map in capital - non accented.

        // Greek mappings
        map.put("Α", LETTER_A);
        map.put("Β", LETTER_B);
        map.put("Ε", LETTER_E);
        map.put("Η", LETTER_H);
        map.put("Ι", LETTER_I);
        map.put("Κ", LETTER_K);
        map.put("Μ", LETTER_M);
        map.put("Ν", LETTER_N);
        map.put("Ο", LETTER_O);
        map.put("Ρ", LETTER_P);
        map.put("Σ", LETTER_S);
        map.put("Τ", LETTER_T);
        map.put("Χ", LETTER_X);
        map.put("Υ", LETTER_Y);
        map.put("Ζ", LETTER_Z);

        // Cyrillic mappings (these characters are Cyrillic, even though they look like latin!)
        map.put("А", LETTER_A);
        map.put("В", LETTER_B);
        map.put("Е", LETTER_E);
        map.put("Н", LETTER_H);
        map.put("К", LETTER_K);
        map.put("М", LETTER_M);
        map.put("О", LETTER_O);
        map.put("Р", LETTER_P);
        map.put("Τ", LETTER_T);
        map.put("Х", LETTER_X);
        map.put("У", LETTER_Y);
    }

    public static LetterMapper getInstance() {
        return ourInstance;
    }

    public String getLetter(String input) {
        String output = map.get(input);
        if (output == null) {
            output = input;
        }
        return output;
    }
}
