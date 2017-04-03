package com.katsuna.widgets.commons.ui.adapters.models;

import com.katsuna.widgets.commons.domain.Contact;
import com.katsuna.widgets.commons.utils.Separator;

public class ContactListItemModel {

    private Contact contact;
    private Separator separator = Separator.NONE;
    private boolean premium;
    private boolean selected;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Separator getSeparator() {
        return separator;
    }

    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
