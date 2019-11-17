package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Storage implements Tickable {
    private Map<String, Tag> tags = new LinkedHashMap<>();

    private TagStorage tagStorage = new TagStorage();

    private Map<LocalDate, TaskStorage> taskStorages = new HashMap<>();

    public Tag getTagByName(String name) {
        return tags.get(name);
    }

    public void onRecieveTasks(String type, LocalDate date, List<Task> tasks) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task: tasks) {
            if (!task.isDisabled()) filteredTasks.add(task);
        }

        switch (type) {
            case "day":
                onRecieveTasksForDay(date, filteredTasks);
                break;
            case "week":
                for (int i = 0; i < 7; i++) {
                    onRecieveTasksForDay(date.with(DayOfWeek.MONDAY).plusDays(i), filteredTasks);
                }
                break;
            case "month":
                for (int i = 0; i < date.lengthOfMonth(); i++) {
                    onRecieveTasksForDay(date.withDayOfMonth(1).plusDays(i), filteredTasks);
                }
                break;
        }
    }

    private void onRecieveTasksForDay(LocalDate date, ArrayList<Task> tasks) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task: tasks) {
            if ((task.getStart().toLocalDate().isAfter(date) || task.getStart().toLocalDate().isEqual(date))
                    && (task.getStop().toLocalDate().isBefore(date) || task.getStop().toLocalDate().isEqual(date))) {
                filteredTasks.add(task);
            }
        }
        if (!taskStorages.containsKey(date)) taskStorages.put(date, new TaskStorage(date));
        taskStorages.get(date).recieveTasks(filteredTasks);
    }

    public void onRecieveTags(List<Tag> tags) {
        for (Tag tag: tags) {
            this.tags.put(tag.getName(), tag);
        }

        tagStorage.recieveTags(new ArrayList<>(tags));
    }

    public TagStorage getTagStorage() {
        return tagStorage;
    }

    public TaskStorage getTaskStorage(LocalDate date) {
        if (!taskStorages.containsKey(date)) taskStorages.put(date, new TaskStorage(date));
        return taskStorages.get(date);
    }

    public AggregateTaskStorage getAggregrateTaskStorageForWeek(LocalDate date) {
        List<TaskStorage> storages = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            storages.add(getTaskStorage(date.with(DayOfWeek.MONDAY).plusDays(i)));
        }
        return new AggregateTaskStorage(storages);
    }

    public AggregateTaskStorage getAggregrateTaskStorageForMonth(LocalDate date) {
        List<TaskStorage> storages = new ArrayList<>();
        for (int i = 0; i < date.lengthOfMonth(); i++) {
            storages.add(getTaskStorage(date.withDayOfMonth(1).plusDays(i)));
        }
        return new AggregateTaskStorage(storages);
    }

    @Override
    public void tick() {
        for (TaskStorage storage: taskStorages.values()) {
            storage.tick();
        }
    }
}
