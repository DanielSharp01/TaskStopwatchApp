package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateTaskStorage implements TagTimeStorage, Tickable {
    private List<TagTimeAdapter> tagTimeAdapters = new ArrayList<>();
    private Map<LocalDate, List<TagTime>> tagTimeListMap = new HashMap<>();
    private Map<String, TagTime> tagTimeMap = new HashMap<>();
    private ArrayList<TagTime> cachedTagTimes = null;
    private Task activeTask;
    private List<TaskStorage> taskStorages;

    public AggregateTaskStorage(List<TaskStorage> taskStorages) {
        this.taskStorages = taskStorages;
        for (TaskStorage storage: taskStorages) {
            storage.bindTagAggregateStorage(this);
        }
        sumTagTimes();
    }

    public void receiveTagTimes(LocalDate date, List<TagTime> tagTimes, Task activeTask) {
        tagTimeListMap.put(date, tagTimes);
        this.activeTask = activeTask;
        tagTimeMap.clear();
        sumTagTimes();
    }

    private void sumTagTimes() {
        for (List<TagTime> tagTimeList: tagTimeListMap.values()) {
            for (TagTime tagTime : tagTimeList) {
                if (!tagTimeMap.containsKey(tagTime.getTag().getName())) tagTimeMap.put(tagTime.getTag().getName(), new TagTime(tagTime.getTag(), tagTime.getDuration()));
                else tagTimeMap.get(tagTime.getTag().getName()).addDuration(tagTime.getDuration());
                if (tagTime.getActiveDuration() != null) tagTimeMap.get(tagTime.getTag().getName()).setActiveDuration(tagTime.getActiveDuration());
            }
        }
        cachedTagTimes = new ArrayList<>(tagTimeMap.values());
        for (TagTimeAdapter adapter: tagTimeAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public void bindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.add(adapter);
    }

    public void unbindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.remove(adapter);
    }

    private void setTagTimesForTask(Task task) {
        // TODO: Logical calculate towards based on date and type defined above
        for (Tag tag: task.getTags()) {
            if (!tagTimeMap.containsKey(tag.getName())) tagTimeMap.put(tag.getName(), new TagTime(tag, Duration.ofNanos(0)));
            if (task.isRunning()) {
                activeTask = task;
                tagTimeMap.get(tag.getName()).setActiveDuration(task.getDuration());
                for (TagTimeAdapter adapter: tagTimeAdapters)
                {
                    adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
                }
            }
            else tagTimeMap.get(tag.getName()).addDuration(task.getDuration());
        }
    }

    public ArrayList<TagTime> getTagTimeList() {
        return cachedTagTimes != null ? cachedTagTimes : new ArrayList<>();
    }

    @Override
    public void tick() {
        setTagTimesForTask(activeTask);
        // TODO: Check for active task still running or new active task
    }

    public void unbindSelf() {
        for (TaskStorage storage: taskStorages) {
            storage.unbindAggregateStorage(this);
        }
    }
}
