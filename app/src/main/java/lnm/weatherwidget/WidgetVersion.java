package lnm.weatherwidget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;


public class WidgetVersion {

    private static boolean isStandalone;

    protected WidgetVersion() {

    }

    public static boolean isStandalone() {
        return isStandalone;
    }

    public static void setIsStandalone(boolean isStandalone) {
        WidgetVersion.isStandalone = isStandalone;
    }

    /**
     * A handle to the unique Singleton instance.
     */
    static private WidgetVersion _instance = null;

    /**
     * @return The unique instance of this class.
     */
    static public WidgetVersion instance() {
        if(null == _instance) {
            _instance = new WidgetVersion();
        }
        return _instance;
    }

}
