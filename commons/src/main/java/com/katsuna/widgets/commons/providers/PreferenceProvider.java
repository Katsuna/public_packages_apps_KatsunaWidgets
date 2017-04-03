package com.katsuna.widgets.commons.providers;

import android.net.Uri;

public class PreferenceProvider {
    public static final String AUTHORITY = "com.katsuna.services.datastore.providers.PreferenceProvider";
    public static final String SCHEME = "content://";

    public static final String PREFERENCE = SCHEME + AUTHORITY + "/preference";
    public static final Uri URI_PREFERENCE = Uri.parse(PREFERENCE);
}
