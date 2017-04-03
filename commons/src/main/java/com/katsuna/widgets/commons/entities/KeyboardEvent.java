package com.katsuna.widgets.commons.entities;

public class KeyboardEvent extends BaseObject {

    public static final String TABLE_NAME = "keyboard_event";
    public static final String COL_CODE = "code";
    public static final int COL_CODE_INDEX = 1;
    public static final String COL_CHARACTER = "character";
    public static final int COL_CHARACTER_INDEX = 2;
    public static final String COL_TIME = "time";
    public static final int COL_TIME_INDEX = 3;

    private int code;
    private String character;
    private long time;

    public KeyboardEvent() {
    }

    public KeyboardEvent(int code) {
        this(code, "");
    }

    public KeyboardEvent(int code, String character) {
        this.code = code;
        this.character = character;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return String.format("KeyboardEvent[code=%s, character=%s, time=%s]", code, character,
                time);
    }

}
