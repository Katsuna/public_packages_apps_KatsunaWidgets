package com.katsuna.widgets.commons.entities;

public class KatsounaError extends BaseObject {

    public static final String TABLE_NAME = "katsouna_error";
    public static final String COL_TIME = "time";
    public static final String COL_APPLICATION = "application";
    public static final String COL_MESSAGE = "message";

    private String time;
    private String application;
    private String message;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("KatsounaError[application=%s, message=%s, time=%s]", application, message, time);
    }
}

