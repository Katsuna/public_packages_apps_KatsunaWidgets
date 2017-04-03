package com.katsuna.widgets.commons.profile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.katsuna.widgets.commons.R;
import com.katsuna.widgets.commons.entities.ColorProfileKey;
import com.katsuna.widgets.commons.entities.UserProfile;
import com.katsuna.widgets.commons.utils.ColorCalc;
import com.katsuna.widgets.commons.utils.Shape;

public class Adjuster {

    private Context mContext;
    private UserProfile mUserProfile;

    public Adjuster(Context context) {
        mContext = context;
    }

    public Adjuster(Context context, UserProfile userProfile) {
        this(context);
        mUserProfile = userProfile;
    }

    public void adjustFabColors(FloatingActionButton fab1) {
        adjustFabColors(fab1, null);
    }

    public void adjustFabColors(FloatingActionButton fab1, FloatingActionButton fab2) {
        if (fab1 != null) {
            int color1 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT1_COLOR,
                    mUserProfile.colorProfile);
            setFabBackgroundColor(fab1, color1);
        }
        if (fab2 != null) {
            int color2 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT2_COLOR,
                    mUserProfile.colorProfile);
            setFabBackgroundColor(fab2, color2);
        }
    }

    public void setFabBackgroundColor(FloatingActionButton fab, int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void tintFabs(FloatingActionButton fab1, boolean flag) {
        tintFabs(fab1, null, flag);
    }

    public void tintFabs(FloatingActionButton fab1, FloatingActionButton fab2, boolean flag) {
        int color1;
        int color2;
        if (flag) {
            color1 = ContextCompat.getColor(mContext, R.color.common_pink_tinted);
            color2 = ContextCompat.getColor(mContext, R.color.common_blue_tinted);
        } else {
            color1 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT1_COLOR,
                    mUserProfile.colorProfile);
            color2 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT2_COLOR,
                    mUserProfile.colorProfile);
        }

        if (fab1 != null) {
            fab1.setBackgroundTintList(ColorStateList.valueOf(color1));
        }
        if (fab2 != null) {
            fab2.setBackgroundTintList(ColorStateList.valueOf(color2));
        }
    }

    public void adjustPopupButtons(Button popupButton1) {
        adjustPopupButtons(popupButton1, null);
    }

    public void adjustPopupButtons(Button popupButton1, Button popupButton2) {
        if (popupButton1 != null) {
            int color1 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT1_COLOR,
                    mUserProfile.colorProfile);
            Shape.setRoundedBackground(popupButton1, color1);
        }

        if (popupButton2 != null) {
            int color2 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT2_COLOR,
                    mUserProfile.colorProfile);
            Shape.setRoundedBackground(popupButton2, color2);
        }
    }

    public void adjustFabPosition(LinearLayout fabContainer, boolean verticalCenter) {
        int verticalCenterGravity = verticalCenter ? Gravity.CENTER : Gravity.BOTTOM;
        if (mUserProfile.isRightHanded) {
            fabContainer.setGravity(verticalCenterGravity | Gravity.END);
        } else {
            fabContainer.setGravity(verticalCenterGravity | Gravity.START);
        }
    }

    public void adjustSearchBar(View container) {
        if (container != null) {
            int accentColor1 = ColorCalc.getColor(mContext, ColorProfileKey.ACCENT1_COLOR,
                    mUserProfile.colorProfile);
            container.setBackgroundColor(accentColor1);
        }
    }

    public void adjustRightHand(LinearLayout fabContainer, FloatingActionButton fab1,
                                Button popupButton1) {
        int horizontalGravity = mUserProfile.isRightHanded ? Gravity.END : Gravity.START;
        if (fabContainer != null) {
            fabContainer.setGravity(horizontalGravity | Gravity.CENTER);
            fabContainer.removeAllViews();
            fabContainer.addView(mUserProfile.isRightHanded ? popupButton1 : fab1);
            fabContainer.addView(mUserProfile.isRightHanded ? fab1 : popupButton1);
        }
    }

}
