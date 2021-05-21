package com.jayz.widget.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.jayz.widget.dashboard.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnSpeedWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeedWidget = findViewById(R.id.btn_widget_speed);
        btnSpeedWidget.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpeedWidgetActivity.class);
            startActivity(intent);
        });
    }
}