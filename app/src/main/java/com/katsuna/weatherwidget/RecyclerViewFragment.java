package com.katsuna.weatherwidget;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.katsuna.R;


public class RecyclerViewFragment extends Fragment {


    public RecyclerViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RemoteViews updateViews = new RemoteViews(getContext().getPackageName(),
                R.layout.extended_widget_view);
        AppWidgetManager.getInstance(getContext()).updateAppWidget(
                new ComponentName(getContext(), ExtendedWeatherWidget.class), updateViews);

//        ExtendedWeatherWidget widget = (ExtendedWeatherWidget) getIntent();
//        recyclerView.setAdapter(mainActivity.getAdapter(bundle.getInt("day")));
        System.out.println("Here it is------");
        return view;
    }

}