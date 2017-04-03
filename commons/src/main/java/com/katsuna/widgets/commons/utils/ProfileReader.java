package com.katsuna.widgets.commons.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.katsuna.widgets.commons.entities.ColorProfile;
import com.katsuna.widgets.commons.entities.Preference;
import com.katsuna.widgets.commons.entities.PreferenceKey;
import com.katsuna.widgets.commons.entities.Profile;
import com.katsuna.widgets.commons.entities.ProfileType;
import com.katsuna.widgets.commons.entities.UserProfile;
import com.katsuna.widgets.commons.entities.UserProfileContainer;
import com.katsuna.widgets.commons.providers.PreferenceProvider;
import com.katsuna.widgets.commons.providers.ProfileProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to retrieve Profile info given a context.
 */

public class ProfileReader {

    public static Profile getProfile(Context context) {
        return getProfileById(context, Profile.OPTICAL_SIZE_PROFILE);
    }

    public static Profile getProfileById(Context context, long profileId) {
        Profile profile = null;
        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(ProfileProvider.URI_PROFILES, String.valueOf(profileId)),
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                profile = new Profile();
                profile.setId(cursor.getLong(cursor.getColumnIndex(Profile.COL_ID)));
                profile.setType(cursor.getInt(cursor.getColumnIndex(Profile.COL_TYPE)));
                Log.d(context, "Error for content provider: " + profile);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return profile;
    }

    public static UserProfileContainer getKatsunaUserProfile(Context context) {
        UserProfile userProfileFromKatsunaServices = getUserProfileFromKatsunaServices(context);
        UserProfile userProfileFromAppSettings = getUserProfileFromAppSettings(context);

        return new UserProfileContainer(userProfileFromKatsunaServices, userProfileFromAppSettings);
    }

    public static UserProfile getUserProfileFromKatsunaServices(Context context) {
        UserProfile userProfile = null;

        try {
            Cursor cursor = context.getContentResolver().query(PreferenceProvider.URI_PREFERENCE, null,
                    null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        ArrayList<Preference> preferences = new ArrayList<>();
                        do {
                            Preference pref = new Preference();
                            pref.setId(cursor.getLong(Preference.COL_ID_INDEX));
                            pref.setKey(cursor.getString(Preference.COL_KEY_INDEX));
                            pref.setValue(cursor.getString(Preference.COL_VALUE_INDEX));
                            preferences.add(pref);
                        } while (cursor.moveToNext());

                        userProfile = getUserProfileFromPreferences(preferences);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (SecurityException se) {
            Log.d(context, "SecurityException! The app doesn't have the required permissions"
                    + " to access the Katsuna ContentProvider!");
        }

        return userProfile;
    }

    public static UserProfile getUserProfileFromAppSettings(Context context) {

        List<Preference> preferences = new ArrayList<>();

        Preference opticalSize = new Preference(PreferenceKey.OPTICAL_SIZE_PROFILE,
                SettingsManager.readSetting(context, PreferenceKey.OPTICAL_SIZE_PROFILE,
                        String.valueOf(ProfileType.INTERMEDIATE.getNumVal())));
        preferences.add(opticalSize);

        Preference opticalContrast = new Preference(PreferenceKey.OPTICAL_CONTRAST_PROFILE,
                SettingsManager.readSetting(context, PreferenceKey.OPTICAL_CONTRAST_PROFILE,
                        String.valueOf(ProfileType.INTERMEDIATE.getNumVal())));
        preferences.add(opticalContrast);

        String value = SettingsManager.readSetting(context, PreferenceKey.COLOR_PROFILE,
                ColorProfile.AUTO.name());
        Preference opticalColor = new Preference(PreferenceKey.COLOR_PROFILE, value);
        preferences.add(opticalColor);

        Preference cognitive = new Preference(PreferenceKey.COGNITIVE_PROFILE,
                SettingsManager.readSetting(context, PreferenceKey.COGNITIVE_PROFILE,
                        String.valueOf(ProfileType.INTERMEDIATE.getNumVal())));
        preferences.add(cognitive);

        Preference memory = new Preference(PreferenceKey.MEMORY_PROFILE,
                SettingsManager.readSetting(context, PreferenceKey.MEMORY_PROFILE,
                        String.valueOf(ProfileType.INTERMEDIATE.getNumVal())));
        preferences.add(memory);

        Preference rightHand = new Preference(PreferenceKey.RIGHT_HAND,
                String.valueOf(SettingsManager.readSetting(context, PreferenceKey.RIGHT_HAND, true)));
        preferences.add(rightHand);

        return getUserProfileFromPreferences(preferences);
    }

    private static UserProfile getUserProfileFromPreferences(List<Preference> preferences) {
        UserProfile userProfile = new UserProfile();
        for (Preference pref : preferences) {
            switch (pref.getKey()) {
                case PreferenceKey.OPTICAL_SIZE_PROFILE: {
                    int prefValue = Integer.parseInt(pref.getValue());
                    userProfile.opticalSizeProfile = ProfileType.values()[prefValue];
                    break;
                }
                case PreferenceKey.OPTICAL_CONTRAST_PROFILE: {
                    int prefValue = Integer.parseInt(pref.getValue());
                    userProfile.opticalContrastProfile =
                            ProfileType.values()[prefValue];
                    break;
                }
                case PreferenceKey.COLOR_PROFILE: {
                    userProfile.colorProfile = ColorProfile.valueOf(pref.getValue());
                    break;
                }
                case PreferenceKey.COGNITIVE_PROFILE: {
                    int prefValue = Integer.parseInt(pref.getValue());
                    userProfile.cognityProfile =
                            ProfileType.values()[prefValue];
                    break;
                }
                case PreferenceKey.MEMORY_PROFILE: {
                    int prefValue = Integer.parseInt(pref.getValue());
                    userProfile.memoryProfile =
                            ProfileType.values()[prefValue];
                    break;
                }
                case PreferenceKey.RIGHT_HAND:
                    userProfile.isRightHanded = Boolean.parseBoolean(pref.getValue());
                    break;
            }
        }
        return userProfile;
    }

}
