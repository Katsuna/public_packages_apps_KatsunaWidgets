package com.katsuna.widgets.commons.utils;

import java.text.Normalizer;

public class LetterNormalizer {

    public static String normalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String firstLetter = input.substring(0, 1);
        String firstLetterCapital = firstLetter.toUpperCase();
        String firstLetterCapitalNoAccents = stripAccents(firstLetterCapital);
        return LetterMapper.getInstance().getLetter(firstLetterCapitalNoAccents);
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }


}