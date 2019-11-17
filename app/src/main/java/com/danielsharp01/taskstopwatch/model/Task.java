package com.danielsharp01.taskstopwatch.model;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.TimeUtils;
import com.danielsharp01.taskstopwatch.api.TaskStopwatchService;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;

public class Task {
    private String id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime stop;
    private ArrayList<String> tags = new ArrayList<>();
    private boolean disabled;

    public Task(String id, String name, LocalDateTime start, LocalDateTime end, String[] tags)
    {
        this.id = id;
        this.name = name;
        this.start = start;
        this.stop = end;
        Collections.addAll(this.tags, tags);
    }

    public String getId() {
        return id;
    }

    public Duration getDuration() {
        return Duration.between(start, stop == null ? LocalDateTime.now() : stop);
    }

    public String getDurationString()
    {
        return TimeUtils.durationAsLongString(getDuration());
    }

    public boolean isRunning() {
        return stop == null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public String getStartString()
    {
        return TimeUtils.timeAsString(getStart());
    }

    public void setStop(LocalDateTime stop) {
        this.stop = stop;
    }

    public LocalDateTime getStop() {
        return stop != null ? stop : LocalDateTime.now();
    }

    public String getStopString() {
        return TimeUtils.timeAsString(getStop());
    }

    public ArrayList<Tag> getTags() {
        ArrayList<Tag> ret = new ArrayList<>();
        for (String tagName: tags) {
            ret.add(DI.getStorage().getTagByName(tagName));
        }
        return ret;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
