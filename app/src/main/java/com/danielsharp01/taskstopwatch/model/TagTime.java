package com.danielsharp01.taskstopwatch.model;

import com.danielsharp01.taskstopwatch.TimeUtils;

import org.threeten.bp.Duration;

public class TagTime {
    private Tag tag;
    private Duration duration;

    public TagTime(Tag tag, Duration duration) {
        this.tag = tag;
        this.duration = duration;
    }

    public String getDurationString()
    {
        return TimeUtils.durationAsLongString(duration);
    }

    public Tag getTag() {
        return tag;
    }

    public Duration getDuration() {
        return duration;
    }
}
