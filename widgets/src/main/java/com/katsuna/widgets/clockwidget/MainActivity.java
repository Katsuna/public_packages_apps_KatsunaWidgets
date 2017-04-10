package com.katsuna.widgets.clockwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.Shape;
import com.katsuna.widgets.R;

public class MainActivity extends Activity {

    private int mTheme;
    UserProfileContainer userProfileContainer;
    ColorProfile colorProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main3);
//        setupTheme(this);
      //  adjustColorProfile(this);
    }

    private void setupTheme(Context context) {
        userProfileContainer = ProfileReader.getKatsunaUserProfile(context);
        colorProfile = userProfileContainer.getColorProfile();

    }

//    private void adjustColorProfile(Context context) {
//
//
//        // set action buttons background color
//        int color1 = ColorCalc.getColor(context,
//                ColorProfileKey.ACCENT1_COLOR, colorProfile);
//        Button calendarBtn =  (Button) findViewById(R.id.calendar_btn);
//        System.out.println("the calendar button is"+calendarBtn);
//        Button  forcastBtn =(Button) findViewById( R.id.forecast_btn);
//        Button  energyModeBtn = (Button) findViewById(R.id.energy_mode_btn);
//        Button backBtn = (Button) findViewById(R.id.back);
//        Button closeBtn = (Button) findViewById(R.id.clock_close_btn);
//
//        Shape.setRoundedBackground(calendarBtn, color1);
//        Shape.setRoundedBackground(forcastBtn, color1);
//        Shape.setRoundedBackground(energyModeBtn, color1);
//
//        int color2 = ColorCalc.getColor(context, ColorProfileKey.ACCENT2_COLOR,
//                colorProfile);
//        Shape.setRoundedBackground(backBtn, color2);
//        Shape.setRoundedBackground(closeBtn, color2);
//
//        // set background color
//
//
//    }
}
