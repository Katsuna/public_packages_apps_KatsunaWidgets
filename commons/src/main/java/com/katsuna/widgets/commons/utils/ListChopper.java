package com.katsuna.widgets.commons.utils;

import java.util.ArrayList;
import java.util.List;

public class ListChopper {

    // chops a list into non-view sublists of length L
    public static <T> List<ArrayList<T>> chopped(List<T> list, final int L) {
        List<ArrayList<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

}
