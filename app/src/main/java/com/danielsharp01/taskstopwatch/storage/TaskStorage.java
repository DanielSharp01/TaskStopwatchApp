package com.danielsharp01.taskstopwatch.storage;

import android.util.Log;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TaskAdapter;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskStorage implements TagTimeStorage, Tickable {

private LocalDate date;
    private List<TaskAdapter> taskAdapters = new ArrayList<>();
    private List<TagTimeAdapter> tagTimeAdapters = new ArrayList<>();
    private List<AggregateTaskStorage> aggregateStorages = new ArrayList<>();
    private ArrayList<Task> cachedList = new ArrayList<>();
    private Map<String, TagTime> tagTimeMap = new HashMap<>();
    private ArrayList<TagTime> cachedTagTimes = new ArrayList<>();
    private Task activeTask = null;

    public TaskStorage(LocalDate date) {
        this.date = date;
    }

    public void recieveTasks(ArrayList<Task> tasks) {
        cachedList.clear();
        cachedList.addAll(tasks);
        calculateTagTimes();
        for (TaskAdapter adapter: taskAdapters)
        {
            adapter.requestNotifyDataSetChanged();
        }

        for (TagTimeAdapter adapter: tagTimeAdapters)
        {
            adapter.requestNotifyDataSetChanged();
        }

        for (AggregateTaskStorage storage: aggregateStorages) {
            if (activeTask != null) storage.setActiveTask(activeTask);
            storage.receiveTagTimes(date, cachedTagTimes);
        }

        if (activeTask != null) DI.getTimer().subscribeTickable(this);
        if (activeTask == null)  DI.getTimer().unsubscribeTickable(this);
    }

    public void stopTask(Task task) {
        if (task != this.activeTask) return;
        task.setStop(LocalDateTime.now());
        this.activeTask = null;
        DI.getTimer().unsubscribeTickable(this);
        setTagTimesForTask(task);
        for (Tag tag: task.getTags()) {
            if (tagTimeMap.containsKey(tag.getName())) {
                tagTimeMap.get(tag.getName()).setActiveDuration(Duration.ofNanos(0));
            }
        }

        for (TaskAdapter adapter : taskAdapters) {
            adapter.notifyItemTick(this.cachedList.indexOf(task));
        }
    }

    public void restartTask(Task task) {
        task.setStop(null);
        this.activeTask = task;
        setTagTimesForTask(task);

        for (TaskAdapter adapter : taskAdapters) {
            adapter.notifyItemTick(this.cachedList.indexOf(task));
        }
    }

    public void startTask(Task task) {
        activeTask = task;
        DI.getTimer().subscribeTickable(this);
        setTagTimesForTask(task);
        for (TagTimeAdapter adapter : tagTimeAdapters) {
            adapter.notifyDataSetChanged();
        }

        this.cachedList.add(task);

        for (TaskAdapter adapter : taskAdapters) {
            adapter.requestNotifyItemInserted(this.cachedList.size() - 1);
        }
    }

    public void tagRemoved(Task task, Tag tag) {
        if (task == activeTask) {
            tagTimeMap.get(tag.getName()).setActiveDuration(null);
            for (TagTimeAdapter adapter : tagTimeAdapters) {
                adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
            }
        } else {
            tagTimeMap.get(tag.getName()).subDuration(task.getDuration());
            for (TagTimeAdapter adapter : tagTimeAdapters) {
                adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
            }
        }

        if (tagTimeMap.get(tag.getName()).getDuration().getNano() == 0) {
            cachedTagTimes.remove(tagTimeMap.get(tag.getName()));
            tagTimeMap.remove(tag.getName());
        }

        for (TagTimeAdapter adapter : tagTimeAdapters) {
            adapter.requestNotifyDataSetChanged();
        }


        changedTask(task);
    }

    public void tagAdded(Task task, Tag tag) {
        boolean added = false;
        if (!tagTimeMap.containsKey(tag.getName())) {
            tagTimeMap.put(tag.getName(), new TagTime(tag, Duration.ofNanos(0)));
            cachedTagTimes.add(tagTimeMap.get(tag.getName()));
            added = true;
        }
        if (task == activeTask) {
            tagTimeMap.get(tag.getName()).setActiveDuration(task.getDuration());
        } else {
            tagTimeMap.get(tag.getName()).addDuration(task.getDuration());
        }


        for (TagTimeAdapter adapter : tagTimeAdapters) {
            if (!added) {
                adapter.notifyItemTick(cachedTagTimes.indexOf(tagTimeMap.get(tag.getName())));
            }
            else {
                adapter.requestNotifyDataSetChanged();
            }
        }

        changedTask(task);
    }

    public void addTask(Task task) {
        // TODO: Insertion sort
        setTagTimesForTask(task);
        cachedList.add(task);
        for (TaskAdapter adapter : taskAdapters) {
            adapter.requestNotifyItemInserted(this.cachedList.indexOf(task));
        }
    }

    public void removeTask(Task task) {
        stopTask(task);
        for (Tag tag: task.getTags()) {
            tagRemoved(task, tag);
        }
        for (TaskAdapter adapter : taskAdapters) {
            adapter.requestNotifyItemRemoved(this.cachedList.indexOf(task));
        }
        cachedList.remove(task);
    }

    public void changeTaskTimes(Task task) {
        int originalIndex = cachedList.indexOf(task);
        cachedList.remove(task);
        // TODO: Insertion sort
        cachedList.add(task);
        int newIndex = cachedList.indexOf(task);
        for (TaskAdapter adapter : taskAdapters) {
            adapter.requestNotifyItemMoved(originalIndex, newIndex);
        }
    }

    public void changedTask(Task task) {
        for (TaskAdapter adapter : taskAdapters) {
            adapter.requestNotifyItemChanged(this.cachedList.indexOf(task));
        }
    }

    public void bindTaskAdapter(TaskAdapter adapter) {
        taskAdapters.add(adapter);
    }

    public void unbindTaskAdapter(TaskAdapter adapter) {
        taskAdapters.remove(adapter);
    }

    public LocalDate getDate() {
        return date;
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
        tagTimeMap.clear();
        cachedTagTimes.clear();
        activeTask = null;

        for (Task task: cachedList) {
            setTagTimesForTask(task);
        }
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
            else {
                tagTimeMap.get(tag.getName()).addDuration(task.getDuration());
            }
        }
        cachedTagTimes.clear();
        cachedTagTimes.addAll(tagTimeMap.values());
    }

    public ArrayList<Task> getTaskList() {
        return cachedList;
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
    public void unbindSelf() {
        // Purposefully empty
    }

    @Override
    public void tick() {
        if (activeTask != null) {
            setTagTimesForTask(activeTask);
            for (TaskAdapter adapter : taskAdapters) {
                adapter.notifyItemTick(cachedList.indexOf(activeTask));
            }
        }

        for (AggregateTaskStorage storage: aggregateStorages) {
            storage.tick();
        }
    }
}
