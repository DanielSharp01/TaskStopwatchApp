package com.danielsharp01.taskstopwatch;

import android.os.Handler;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;

public class Timer {
    private ArrayList<Tickable> tickables = new ArrayList<>();
    private Handler timeHandler;

    public Timer() {
        timeHandler = new Handler();
    }

    public void start() {
        timeHandler.postAtTime(this::tick, LocalTime.now().withNano(0).plusSeconds(1).getNano() / 1000000);
    }

    public void subscribeTickable(Tickable tickable) {
        tickables.add(tickable);
    }

    public void unsubscribeTickable(Tickable tickable) {
        tickables.remove(tickable);
    }

    void tick() {
        for (Tickable tickable: tickables) {
            tickable.tick();
        }
        timeHandler.postDelayed(this::tick, 100);
    }

    public void stop() {
        timeHandler.removeCallbacks(this::tick);
    }
}
