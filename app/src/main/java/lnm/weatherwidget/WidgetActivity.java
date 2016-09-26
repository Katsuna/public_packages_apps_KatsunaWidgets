package lnm.weatherwidget;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nikos on 23-Sep-16.
 */
public class WidgetActivity extends Activity {

    private LinearLayout mGraphLayout;
//    private GraphicalView mGraphicalView;
//    private XYSeries mXYSeries;
    private TextView mStatusView;
    private TextView mPlugView;
    private TextView mLevelView;
    private TextView mScaleView;
    private TextView mVoltageView;
    private TextView mTemperatureView;
    private TextView mTechnologyView;
    private TextView mHealthView;

    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Start!!!3");

            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
              //  BatteryInfo batteryInfo = new BatteryInfo(intent);
            //    updateView(batteryInfo);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Start!!!4");

        setContentView(R.layout.activity_widget);
//        mGraphLayout = (LinearLayout) findViewById(R.id.chart);
//        mStatusView = (TextView) findViewById(R.id.state);
//        mPlugView = (TextView) findViewById(R.id.plug);
//        mLevelView = (TextView) findViewById(R.id.level);
//        mScaleView = (TextView) findViewById(R.id.scale);
//        mVoltageView = (TextView) findViewById(R.id.voltage);
//        mTemperatureView = (TextView) findViewById(R.id.temperature);
//        mTechnologyView = (TextView) findViewById(R.id.technology);
//        mHealthView = (TextView) findViewById(R.id.health);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
//        BatteryInfo batteryInfo = new BatteryInfo(sharedPreferences);
//        updateView(batteryInfo);

        // ensure service is running
        startService(new Intent(this, WeatherWidgetUpdateService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGraphLayout != null) {
            mGraphLayout.removeAllViews();
        }
//        if (BatteryWidget.getNumberOfWidgets(this) == 0) {
//            // stop monitoring if there are no more widgets on screen
//            stopService(new Intent(this, MonitorService.class));
//        }
    }

    private synchronized void updateView(ApplicationErrorReport.BatteryInfo batteryInfo) {
//        mStatusView.setText(getStatus(batteryInfo));
//        mPlugView.setText(getPlug(batteryInfo));
//        mTemperatureView.setText(getTemperature(batteryInfo));
//        mHealthView.setText(getHealth(batteryInfo));
//        mLevelView.setText(getLevel(batteryInfo));
//        mScaleView.setText(getScale(batteryInfo));
//        mVoltageView.setText(getVoltage(batteryInfo));
//        mTechnologyView.setText(getTechnology(batteryInfo));
        drawGraph();
    }

    private void initGraphicalView() {

    }

    private void drawGraph() {
        initGraphicalView();
//        Database database = null;
//        Cursor cursor = null;
//        try {
//            database = new Database(this);
//            cursor = database.openRead().getEntries();
//            if (cursor.moveToFirst()) {
//                do {
//                    long time = cursor.getLong(Database.TIME);
//                    int level = cursor.getInt(Database.LEVEL);
//                    mXYSeries.add(time, level);
//                } while (cursor.moveToNext());
//            }
//            mGraphicalView.repaint();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//            if (cursor != null)
//                cursor.close();
//        }
    }


}