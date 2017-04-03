package com.katsuna.widgets.commons.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.katsuna.widgets.commons.entities.SpinnerItem;

import java.util.List;

public class SpinnerItemAdapter extends ArrayAdapter<SpinnerItem> {

    public SpinnerItemAdapter(Context context, List<SpinnerItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getTextView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getTextView(position, convertView, parent);
    }

    private View getTextView(int position, View convertView, @NonNull ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text == null) {
            text = (CheckedTextView) LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        SpinnerItem item = getItem(position);
        if (item != null) {
            text.setText(item.getDescriptionResId());
        }

        return text;
    }
}
