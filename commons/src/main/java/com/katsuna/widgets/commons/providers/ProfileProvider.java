package com.katsuna.widgets.commons.providers;

import android.net.Uri;

public class ProfileProvider {

    /**
     *  @deprecated use Profile.OPTICAL_SIZE_PROFILE instead.
     */
    @Deprecated
    public static final long MAIN_PROFILE_ID = 1;

    public static final String AUTHORITY = "com.katsuna.datastore.providers.ProfileProvider";
    public static final String SCHEME = "content://";

    public static final String PROFILES = SCHEME + AUTHORITY + "/profile";
    public static final Uri URI_PROFILES = Uri.parse(PROFILES);
    public static final String PROFILE_BASE = PROFILES + "/";
}
