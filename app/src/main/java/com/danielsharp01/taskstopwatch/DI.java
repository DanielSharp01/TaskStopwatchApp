package com.danielsharp01.taskstopwatch;

import androidx.appcompat.app.AppCompatActivity;

import com.danielsharp01.taskstopwatch.api.TaskStopwatchService;
import com.danielsharp01.taskstopwatch.navigation.NavigationService;
import com.danielsharp01.taskstopwatch.storage.Storage;

import java.util.Random;

public class DI {
    private static Storage storage;
    private static Random random;

    public static Storage getStorage() {
        return storage;
    }

    private static NavigationService navigationService;
    public static NavigationService getNavigationService() {
        return navigationService;
    }

    private static TaskStopwatchService taskStopwatchService;
    public static TaskStopwatchService getTaskStopwatchService() {
        return taskStopwatchService;
    }

    private static Timer timer;
    public static Timer getTimer() {
        return timer;
    }

    public static void init(AppCompatActivity activity) {
        storage = new Storage();
        random = new Random();
        navigationService = new NavigationService(activity);
        taskStopwatchService = new TaskStopwatchService(activity.getApplicationContext());
        timer = new Timer();
    }

    public static Random getRandom() {
        return random;
    }
}
