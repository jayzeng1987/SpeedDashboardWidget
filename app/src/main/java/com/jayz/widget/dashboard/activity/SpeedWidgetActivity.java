package com.jayz.widget.dashboard.activity;

import android.os.Bundle;

import com.jayz.widget.dashboard.R;
import com.jayz.widget.dashboard.widget.SpeedDashboardWidget;

import androidx.appcompat.app.AppCompatActivity;

public class SpeedWidgetActivity extends AppCompatActivity {
    private SpeedDashboardWidget speedDashboardWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_widget);

        speedDashboardWidget = findViewById(R.id.speedDashboardWidget);

        new Thread(() -> {
            int i = 0;
            while(true) {
                if (i == 260) {
                    i = 0;
                }
                speedDashboardWidget.updateSpeed(i++);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}