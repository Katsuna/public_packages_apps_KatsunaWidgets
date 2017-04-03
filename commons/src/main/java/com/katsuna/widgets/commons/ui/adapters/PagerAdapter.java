package com.katsuna.widgets.commons.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> list;

    public PagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int index) {
        return list.get(index);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}