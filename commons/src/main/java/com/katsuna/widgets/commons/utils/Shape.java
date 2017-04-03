package com.katsuna.widgets.commons.utils;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.katsuna.widgets.commons.R;

public class Shape {

    public static void setRoundedBackground(View v, int backgroundColor) {
        Resources r = v.getResources();
        float radius = r.getDimension(R.dimen.common_corner_radius);

        setRoundedBackground(v, backgroundColor, radius);
    }

    public static void setRoundedBackground(View v, int backgroundColor, float radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius,
                radius});
        shape.setColor(backgroundColor);
        v.setBackground(shape);
    }
}
