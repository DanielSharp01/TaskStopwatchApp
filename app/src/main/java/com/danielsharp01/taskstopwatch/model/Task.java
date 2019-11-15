package com.danielsharp01.taskstopwatch.model;

import com.danielsharp01.taskstopwatch.TimeUtils;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Collections;

public class Task {
    private String name;
    private LocalTime start;
    private LocalTime end;
    private ArrayList<Tag> tags = new ArrayList<>();

    public Task(String name, LocalTime start, LocalTime end, Tag[] tags)
    {
        this.name = name;
        this.start = start;
        this.end = end;
        Collections.addAll(this.tags, tags);
    }

    public Duration getDuration() {
        return Duration.between(start, end == null ? LocalTime.now() : end);
    }

    public String getDurationString()
    {
        return TimeUtils.durationAsLongString(getDuration());
    }

    public boolean isRunning() {
        return end == null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getStart() {
        return start;
    }

    public String getStartString()
    {
        return TimeUtils.timeAsString(getStart());
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public LocalTime getEnd() {
        return end != null ? end : LocalTime.now();
    }

    public String getEndString() {
        return TimeUtils.timeAsString(getEnd());
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

}
