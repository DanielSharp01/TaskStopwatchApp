package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TaskAdapter;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskStorage implements TagTimeStorage, Tickable {

    private LocalDate date;
    private List<TaskAdapter> taskAdapters = new ArrayList<>();
    private List<TagTimeAdapter> tagTimeAdapters = new ArrayList<>();
    private List<AggregateTaskStorage> aggregateStorages = new ArrayList<>();
    private ArrayList<Task> cachedList = null;
    private Map<String, TagTime> tagTimeMap = null;
    private ArrayList<TagTime> cachedTagTimes = null;
    private Task activeTask = null;

    public TaskStorage(LocalDate date) {
        this.date = date;
    }

    public void recieveTasks(ArrayList<Task> tasks) {
        cachedList = tasks;
        calculateTagTimes();
        for (TaskAdapter adapter: taskAdapters)
        {
            adapter.notifyDataSetChanged();
        }

        for (TagTimeAdapter adapter: tagTimeAdapters)
        {
            adapter.notifyDataSetChanged();
        }

        for (AggregateTaskStorage storage: aggregateStorages) {
            storage.receiveTagTimes(date, cachedTagTimes, activeTask);
        }
    }

    public void bindTaskAdapter(TaskAdapter adapter) {
        taskAdapters.add(adapter);
    }

    public void unbindTaskAdapter(TaskAdapter adapter) {
        taskAdapters.remove(adapter);
    }

    public void bindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.add(adapter);
    }

    public void unbindTagTimeAdapter(TagTimeAdapter adapter) {
        tagTimeAdapters.remove(adapter);
    }

    public void bindTagAggregateStorage(AggregateTaskStorage storage) {
        aggregateStorages.add(storage);
    }

    public void unbindAggregateStorage(AggregateTaskStorage storage) {
        aggregateStorages.remove(storage);
    }

    public void calculateTagTimes() {
        tagTimeMap = new HashMap<>();
        cachedTagTimes = new ArrayList<>();
        activeTask = null;

        for (Task task: cachedList) {
            setTagTimesForTask(task);
        }

        cachedTagTimes = new ArrayList<>(tagTimeMap.values());
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
        cachedTagTimes = new ArrayList<>(tagTimeMap.values());
    }

    public ArrayList<Task> getTaskList() {
        return cachedList != null ? cachedList : new ArrayList<>();
    }

    public ArrayList<TagTime> getTagTimeList() {
        return cachedTagTimes != null ? cachedTagTimes : new ArrayList<>();
    }

    @Override
    public void unbindSelf() {
        // Purposefully empty
    }

    @Override
    public void tick() {
        for (TaskAdapter adapter: taskAdapters)
        {
            adapter.notifyItemTick(cachedList.indexOf(activeTask));
        }
        setTagTimesForTask(activeTask);

        // TODO: Check for active task still running or new active task

        for (AggregateTaskStorage storage: aggregateStorages) {
            storage.tick();
        }
    }
}
