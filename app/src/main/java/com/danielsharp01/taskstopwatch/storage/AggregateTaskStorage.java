package com.danielsharp01.taskstopwatch.storage;

import android.util.Log;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateTaskStorage implements TagTimeStorage, Tickable {
    private List<TagTimeAdapter> tagTimeAdapters = new ArrayList<>();
    private Map<LocalDate, List<TagTime>> tagTimeListMap = new HashMap<>();
    private Map<String, TagTime> tagTimeMap = new HashMap<>();
    private ArrayList<TagTime> cachedTagTimes = new ArrayList<>();
    private Task activeTask;
    private List<TaskStorage> taskStorages;

    public AggregateTaskStorage(List<TaskStorage> taskStorages) {
        this.taskStorages = taskStorages;
        for (TaskStorage storage: taskStorages) {
            storage.bindTagAggregateStorage(this);
        }
        sumTagTimes();
    }

    public void receiveTagTimes(LocalDate date, List<TagTime> tagTimes) {
        tagTimeListMap.put(date, tagTimes);
        tagTimeMap.clear();
        sumTagTimes();
    }

    public void setActiveTask(Task activeTask) {
        this.activeTask = activeTask;
        if (this.activeTask != null && activeTask == null) {
            for (Tag tag: activeTask.getTags()) {
                tagTimeMap.get(tag.getName()).addDuration(activeTask.getDuration());
                for (TagTimeAdapter adapter: tagTimeAdapters)
                {
                    adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
                }
            }
        }
        if (activeTask != null) DI.getTimer().subscribeTickable(this);
        if (activeTask == null)  DI.getTimer().unsubscribeTickable(this);

    }

    private void sumTagTimes() {
        for (List<TagTime> tagTimeList: tagTimeListMap.values()) {
            for (TagTime tagTime : tagTimeList) {
                if (!tagTimeMap.containsKey(tagTime.getTag().getName())) tagTimeMap.put(tagTime.getTag().getName(), new TagTime(tagTime.getTag(), tagTime.getInactiveDuration()));
                else tagTimeMap.get(tagTime.getTag().getName()).addDuration(tagTime.getInactiveDuration());
                if (tagTime.getActiveDuration() != null) tagTimeMap.get(tagTime.getTag().getName()).setActiveDuration(tagTime.getActiveDuration());
            }
        }
        cachedTagTimes.clear();
        cachedTagTimes.addAll(tagTimeMap.values());
        for (TagTimeAdapter adapter: tagTimeAdapters) {
            adapter.requestNotifyDataSetChanged();
        }
    }

    public void bindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.add(adapter);
    }

    public void unbindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.remove(adapter);
    }

    public ArrayList<TagTime> getTagTimeList() {
        ArrayList<TagTime> filteredTagTimes = new ArrayList<>();
        for (TagTime tagTime: cachedTagTimes) {
            if (DI.getTaskStopwatchService().isTagTracked(tagTime.getTag())) {
                filteredTagTimes.add(tagTime);
            }
        }
        return filteredTagTimes;
    }

    @Override
    public void tick() {
        if (activeTask != null) {
            for (Tag tag: activeTask.getTags()) {
                tagTimeMap.get(tag.getName()).setActiveDuration(activeTask.getDuration());
                for (TagTimeAdapter adapter: tagTimeAdapters)
                {
                    adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
                }
            }
        }
    }

    public void unbindSelf() {
        for (TaskStorage storage: taskStorages) {
            storage.unbindAggregateStorage(this);
        }
        DI.getTimer().unsubscribeTickable(this);
    }
}
