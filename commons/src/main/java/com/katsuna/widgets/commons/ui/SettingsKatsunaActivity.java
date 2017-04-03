package com.katsuna.widgets.commons.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.katsuna.widgets.commons.R;
import com.katsuna.widgets.commons.entities.ColorProfile;
import com.katsuna.widgets.commons.entities.PreferenceKey;
import com.katsuna.widgets.commons.entities.ProfileType;
import com.katsuna.widgets.commons.entities.SpinnerItem;
import com.katsuna.widgets.commons.ui.adapters.SpinnerItemAdapter;
import com.katsuna.widgets.commons.utils.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingsKatsunaActivity extends KatsunaActivity {

    protected CheckBox mRightHand;

    @Override
    protected void onResume() {
        super.onResume();

        if (mUserProfileChanged) {
            if (!mUserProfileContainer.hasKatsunaServices()) {
                //enable app right hand setting
                boolean isRightHanded = SettingsManager.readSetting(this, PreferenceKey.RIGHT_HAND,
                        true);
                mRightHand.setChecked(isRightHanded);
                mRightHand.setVisibility(View.VISIBLE);
            } else {
                mRightHand.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void showPopup(boolean flag) {
        // no op for settings
    }

    protected void initSizeProfiles() {
        Spinner mProfileTypes = (Spinner) findViewById(R.id.profiles);
        String profileSetting = SettingsManager.readSetting(this,
                PreferenceKey.OPTICAL_SIZE_PROFILE,
                String.valueOf(ProfileType.INTERMEDIATE.getNumVal()));
        mProfileTypes.setSelection(Integer.parseInt(profileSetting));
        mProfileTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SettingsManager.setSetting(SettingsKatsunaActivity.this,
                        PreferenceKey.OPTICAL_SIZE_PROFILE, String.valueOf(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected void initColorProfiles() {
        Spinner mProfileColor = (Spinner) findViewById(R.id.profiles_optical_color);

        List<SpinnerItem> spinnerArray = new ArrayList<>();
        for (ColorProfile colorProfile : ColorProfile.values()) {
            spinnerArray.add(new SpinnerItem(colorProfile.name(), colorProfile.getResId()));
        }
        SpinnerItemAdapter mColorProfilesAdapter = new SpinnerItemAdapter(this, spinnerArray);
        mProfileColor.setAdapter(mColorProfilesAdapter);


        String value = SettingsManager.readSetting(SettingsKatsunaActivity.this,
                PreferenceKey.COLOR_PROFILE, ColorProfile.AUTO.name());
        mProfileColor.setSelection(mColorProfilesAdapter.getPosition(new SpinnerItem(value)));

        mProfileColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = ((SpinnerItem) adapterView.getItemAtPosition(i)).getValue();
                SettingsManager.setSetting(SettingsKatsunaActivity.this, PreferenceKey.COLOR_PROFILE,
                        value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected void initRightHand() {
        mRightHand = (CheckBox) findViewById(R.id.right_hand);
        mRightHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.setSetting(SettingsKatsunaActivity.this, PreferenceKey.RIGHT_HAND,
                        isChecked);
            }
        });
    }
}
