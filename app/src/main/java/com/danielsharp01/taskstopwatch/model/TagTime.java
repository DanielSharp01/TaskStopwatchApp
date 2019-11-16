package com.danielsharp01.taskstopwatch.model;

import com.danielsharp01.taskstopwatch.TimeUtils;

import org.threeten.bp.Duration;

public class TagTime {
    private Tag tag;
    private Duration duration;
    private Duration activeDuration;

    public TagTime(Tag tag, Duration duration) {
        this.tag = tag;
        this.duration = duration;
    }

    public void addDuration(Duration duration) {
        this.duration = Duration.ofNanos(this.duration.toNanos() + duration.toNanos());
    }

    public void setActiveDuration(Duration duration) {
        activeDuration = duration;
    }

    public String getDurationString()
    {
        return TimeUtils.durationAsLongString(getDuration());
    }

    public Tag getTag() {
        return tag;
    }

    public Duration getDuration() {
        return Duration.ofNanos(duration.toNanos() + (activeDuration != null ? activeDuration.toNanos() : 0));
    }
}
