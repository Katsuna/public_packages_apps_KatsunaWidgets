package com.katsuna.widgets.commons.providers;

import android.net.Uri;

public class LauncherProvider {
    public static final String AUTHORITY = "com.katsuna.services.datastore.providers.LauncherProvider";
    public static final String SCHEME = "content://";

    public static final String LAUNCHER_ACCESS = SCHEME + AUTHORITY + "/launcher_access";
    public static final String LAUNCHER_ACCESS_BASE = LAUNCHER_ACCESS + "/";
    public static final Uri URI_LAUNCHER_ACCESS = Uri.parse(LAUNCHER_ACCESS);
}
