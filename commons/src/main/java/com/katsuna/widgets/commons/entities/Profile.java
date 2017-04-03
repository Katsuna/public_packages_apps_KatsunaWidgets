package com.katsuna.widgets.commons.entities;

public class Profile extends BaseObject {

    public static final int OPTICAL_SIZE_PROFILE = 1;
    public static final int OPTICAL_CONTRAST_PROFILE = 2;
    public static final int OPTICAL_COLOR_PROFILE = 3;
    public static final int COGNITIVE_PROFILE = 4;
    public static final int MEMORY_PROFILE = 5;

    public static final String TABLE_NAME = "profile";
    public static final String COL_TYPE = "type";

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Profile[type=%s]", getType());
    }
}

