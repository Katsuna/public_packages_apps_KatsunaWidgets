package com.katsuna.widgets.commons.domain;

import com.katsuna.widgets.commons.utils.DataAction;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = -6330427348994502843L;

    private String id;
    private String formattedAddress;
    private DataAction dataAction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public DataAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(DataAction dataAction) {
        this.dataAction = dataAction;
    }
}
