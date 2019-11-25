package com.danielsharp01.taskstopwatch.model;

import com.danielsharp01.taskstopwatch.TimeUtils;
import com.danielsharp01.taskstopwatch.api.TagListTypeAdapter;
import com.google.gson.annotations.JsonAdapter;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime stop;
    @JsonAdapter(TagListTypeAdapter.class)
    private ArrayList<Tag> tags = new ArrayList<>();
    private boolean disabled;

    public Task() {
        id = "temporary";
    }

    public Task(String id, String name, LocalDateTime start, LocalDateTime stop, List<Tag> tags)
    {
        this.id = id;
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.tags.addAll(tags);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return tags;
    }

    public boolean hasTag(Tag tag) {
        for (Tag t: tags) {
            if (t.getName().equals(tag.getName())) return true;
        }

        return false;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void addAllTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void changeTag(Tag from, Tag to) {
        int toRemove = 0;
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getName().equals(from.getName())) {
                toRemove = i;
                break;
            }
        }
        tags.remove(toRemove);
        tags.add(toRemove, to);
    }

    public void removeTag(Tag tag) {
        Tag toRemove = null;
        for (Tag t: tags) {
            if (t.getName().equals(tag.getName())) {
                toRemove = t;
                break;
            }
        }
        tags.remove(toRemove);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Task cloneAtNow() {
        return new Task("temporary", name, LocalDateTime.now(), null, tags);
    }
}
