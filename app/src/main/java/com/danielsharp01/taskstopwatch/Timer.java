package com.danielsharp01.taskstopwatch;

public interface Timer {
    void subscribeTickable(Tickable tickable);
    void unsubscribeTickable(Tickable tickable);
}
