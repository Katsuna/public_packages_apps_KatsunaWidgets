package com.katsuna.widgets.commons.entities;

public class LauncherAccess extends BaseObject {

    public static final String TABLE_NAME = "launcher_access";
    public static final String COL_COMPONENT = "component";
    public static final String COL_USER = "user";
    public static final String COL_TIME = "time";

    private String component;
    private String user;
    private String time;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("LauncherAccess[component=%s, user=%s, time=%s]", component, user, time);
    }

}
