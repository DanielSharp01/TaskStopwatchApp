package com.danielsharp01.taskstopwatch;

import android.os.Bundle;
import android.os.Handler;

import com.danielsharp01.taskstopwatch.api.TaskStopwatchService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        DI.init(this);
        DI.getNavigationService().checkLoginAndRedirect();
        getSupportActionBar().hide();

    }

    @Override
    protected void onPause() {
        super.onPause();
        DI.getTimer().stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DI.getTimer().start();
    }
}
