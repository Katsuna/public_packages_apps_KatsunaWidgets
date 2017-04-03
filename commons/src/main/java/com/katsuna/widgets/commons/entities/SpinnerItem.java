package com.katsuna.widgets.commons.entities;

public class SpinnerItem {
    private String value;
    private int descriptionResId;

    public SpinnerItem(String value) {
        this.value = value;
    }

    public SpinnerItem(String value, int descriptionResId) {
        this.value = value;
        this.descriptionResId = descriptionResId;
    }

    public String getValue() {
        return value;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpinnerItem) {
            SpinnerItem c = (SpinnerItem) obj;
            return c.getValue().equals(value);
        }

        return false;
    }
}
