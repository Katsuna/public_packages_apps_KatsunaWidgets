package com.katsuna.widgets.commons.entities;

public class BaseObject {

    public static final String COL_ID = "_id";
    public static final int COL_ID_INDEX = 0;

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
