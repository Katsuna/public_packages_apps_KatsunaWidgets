package com.katsuna.widgets.commons.entities;

import com.katsuna.widgets.commons.R;

public enum ColorProfile {
    AUTO(R.string.common_profile_auto),
    MAIN(R.string.common_profile_color_main),
    CONTRAST(R.string.common_profile_color_contrast),
    COLOR_IMPAIREMENT(R.string.common_profile_color_impairement),
    COLOR_IMPAIRMENT_AND_CONTRAST(R.string.common_profile_color_impairement_contrast);

    private final int resId;

    ColorProfile(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}