package com.danielsharp01.taskstopwatch.navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

public class NavigationService {
    private BottomNavigationView navView;
    private NavController navController;
    private LocalDate currentDate;

    public NavigationService(AppCompatActivity activity) {
        navView = activity.findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_week, R.id.navigation_day, R.id.navigation_month, R.id.navigation_settings)
                .build();
        navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(activity, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_login) loggedOut();
            else loggedIn();

            switch (destination.getId()) {
                case R.id.navigation_tags:
                    navView.getMenu().findItem(R.id.navigation_tags).setChecked(true);
                    break;
                case R.id.navigation_settings:
                    navView.getMenu().findItem(R.id.navigation_settings).setChecked(true);
                    break;
                case R.id.navigation_day:
                    navView.getMenu().findItem(R.id.navigation_day).setChecked(true);
                    break;
                case R.id.navigation_week:
                    navView.getMenu().findItem(R.id.navigation_week).setChecked(true);
                    break;
                case R.id.navigation_month:
                    navView.getMenu().findItem(R.id.navigation_month).setChecked(true);
                    break;
            }


        });

        navView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_day:
                    navigateTo("day", false);
                    return true;
                case R.id.navigation_week:
                    navigateTo("week", false);
                    return true;
                case R.id.navigation_month:
                    navigateTo("month", false);
                    return true;
                case R.id.navigation_tags:
                    navController.navigate(R.id.navigation_tags);
                    return true;
                case R.id.navigation_settings:
                    navController.navigate(R.id.navigation_settings);
                    return true;
            }

            return false;
        });

        navView.setOnNavigationItemReselectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_day:
                    navigateTo("day", true);
                    return;
                case R.id.navigation_week:
                    navigateTo("week", true);
                    return;
                case R.id.navigation_month:
                    navigateTo("month", true);
                    return;
                case R.id.navigation_tags:
                    navController.navigate(R.id.navigation_tags);
                    return;
                case R.id.navigation_settings:
                    navController.navigate(R.id.navigation_settings);
                    return;
            }
        });
    }

    public void checkLoginAndRedirect() {
        if (DI.getTaskStopwatchService().isLoggedIn()) {
            navController.navigate(R.id.action_login);
            loggedIn();
        }
        else {
            loggedOut();
        }
    }

    public void logout() {
        navController.navigate(R.id.action_logout);
    }

    public void loggedOut() {
        navView.setVisibility(View.GONE);
    }

    public void loggedIn() {
        navView.setVisibility(View.VISIBLE);
    }

    public void onNavigatedTo(LocalDate date) {
        currentDate = date;
    }

    public void navigateTo(String type, LocalDate date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", date);
        bundle.putString("type", type);
        switch (type) {
            case "day":
                navController.navigate(R.id.navigation_day, bundle);
                break;
            case "week":
                navController.navigate(R.id.navigation_week, bundle);
                break;
            case "month":
                navController.navigate(R.id.navigation_month, bundle);
                break;
        }
    }

    public void navigateTo(String type, boolean disregardCurrentDate) {
        if (!disregardCurrentDate) {
            switch (type) {
                case "day":
                    navigateTo(type, currentDate);
                    break;
                case "week":
                    navigateTo(type, currentDate.with(DayOfWeek.MONDAY));
                    break;
                case "month":
                    navigateTo(type, currentDate.withDayOfMonth(1));
                    break;
            }
        }
        else {
            navigateTo(type, null);
        }
    }
}
