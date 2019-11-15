package com.danielsharp01.taskstopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.danielsharp01.taskstopwatch.view.DayNavigationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Timer, DayNavigationListener {

    private Handler timeHandler;
    private Runnable clock;

    private static MainActivity instance;
    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_week, R.id.navigation_day, R.id.navigation_month, R.id.navigation_settings)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_login) navView.setVisibility(View.GONE);
            else navView.setVisibility(View.VISIBLE);
        });
        NavigationUI.setupWithNavController(navView, navController);
        navView.setVisibility(View.GONE);
        getSupportActionBar().hide();

        // Setup one second/tick clock
        timeHandler = new Handler();
        clock = () -> {
            for (Tickable tickable : tickables) {
                tickable.tick();
            }
            timeHandler.postDelayed(clock, 1000);
        };
        timeHandler.postAtTime(clock, LocalTime.now().withNano(0).plusSeconds(1).getNano() / 1000000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(clock);
    }

    private ArrayList<Tickable> tickables = new ArrayList<>();

    @Override
    public void subscribeTickable(Tickable tickable) {
        tickables.add(tickable);
    }

    @Override
    public void unsubscribeTickable(Tickable tickable) {
        tickables.remove(tickable);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void navigate(LocalDate date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", date);
        bundle.putString("type", "day");
        navController.navigate(R.id.navigation_day, bundle);
        navView.getMenu().findItem(R.id.navigation_day).setChecked(true);
    }
}
