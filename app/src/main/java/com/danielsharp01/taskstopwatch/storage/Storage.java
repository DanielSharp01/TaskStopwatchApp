package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    private Map<String, Tag> tags = new LinkedHashMap<>();
    private TagStorage tagStorage = new TagStorage();

    private Map<LocalDate, TaskStorage> taskStorages = new HashMap<>();

    private AggregateTaskStorage currentAggregrate = null;

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
        if (this.currentAggregrate != null) currentAggregrate.unbindSelf();
        List<TaskStorage> storages = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            storages.add(getTaskStorage(date.with(DayOfWeek.MONDAY).plusDays(i)));
        }
        return this.currentAggregrate = new AggregateTaskStorage(storages);
    }

    public AggregateTaskStorage getAggregrateTaskStorageForMonth(LocalDate date) {
        if (this.currentAggregrate != null) currentAggregrate.unbindSelf();
        List<TaskStorage> storages = new ArrayList<>();
        for (int i = 0; i < date.lengthOfMonth(); i++) {
            storages.add(getTaskStorage(date.withDayOfMonth(1).plusDays(i)));
        }
        return this.currentAggregrate = new AggregateTaskStorage(storages);
    }

    public void stopTask(Task task) {
        stopTask(task, null);
    }

    public void stopTask(Task task, Runnable after) {
        getTaskStorage(task.getStart().toLocalDate()).stopTask(task);

        DI.getTaskStopwatchService().stopTask(task, taskResponse -> {
            if (taskResponse == null) {
                getTaskStorage(task.getStart().toLocalDate()).restartTask(task);
            }
            else if (after != null) after.run();
        });
    }

    public void startTask(Task task) {
        for (TaskStorage storage: taskStorages.values()) {
            for (Task t: storage.getTaskList()) {
                if (t.isRunning()) {
                    stopTask(t, () -> {
                        getTaskStorage(task.getStart().toLocalDate()).startTask(task);

                        DI.getTaskStopwatchService().startTask(task, taskResponse -> {
                            if (taskResponse != null) {
                                task.setId(taskResponse.getId());
                                getTaskStorage(task.getStart().toLocalDate()).changedTask(task);
                            }
                            else {
                                removeTask(task);
                            }
                        });
                    });
                    return;
                }
            }
        }

        getTaskStorage(task.getStart().toLocalDate()).startTask(task);

        DI.getTaskStopwatchService().startTask(task, taskResponse -> {
            if (taskResponse != null) {
                task.setId(taskResponse.getId());
                getTaskStorage(task.getStart().toLocalDate()).changedTask(task);
            }
            else {
                removeTask(task);
            }
        });
    }

    public void createTagOnTask(Task task, Tag tag) {
        task.addTag(tag);
        getTaskStorage(task.getStart().toLocalDate()).tagAdded(task, tag);

        DI.getTaskStopwatchService().createTagOnTask(task, tag, tagResponse -> {
            if (tagResponse == null) {
                task.removeTag(tag);
                getTaskStorage(task.getStart().toLocalDate()).tagRemoved(task, tag);
            }
        });
    }

    public void changeTagOnTask(Task task, Tag from, Tag to) {
        task.changeTag(from, to);
        getTaskStorage(task.getStart().toLocalDate()).tagRemoved(task, from);
        getTaskStorage(task.getStart().toLocalDate()).tagAdded(task, to);

        DI.getTaskStopwatchService().changeTagOnTask(task, from, to, tagResponse -> {
            if (tagResponse == null) {
                task.changeTag(to, from);
                getTaskStorage(task.getStart().toLocalDate()).tagRemoved(task, to);
                getTaskStorage(task.getStart().toLocalDate()).tagAdded(task, from);
            }
        });
    }

    public void deleteTagOnTask(Task task, Tag tag) {
        task.removeTag(tag);
        getTaskStorage(task.getStart().toLocalDate()).tagRemoved(task, tag);

        DI.getTaskStopwatchService().deleteTagOnTask(task, tag, tagResponse -> {
            if (tagResponse == null) {
                task.addTag(tag);
                getTaskStorage(task.getStart().toLocalDate()).tagAdded(task, tag);
            }
        });
    }

    public void addTask(Task task) {
        getTaskStorage(task.getStart().toLocalDate()).addTask(task);
    }

    public void removeTask(Task task) {
        getTaskStorage(task.getStart().toLocalDate()).removeTask(task);
    }

    public void changeTaskTimes(Task task, LocalTime start, LocalTime stop) {
        if (task.getStop() == null || stop == null) throw new RuntimeException("This should definetely not happen!");

        task.setStart(start.atDate(task.getStart().toLocalDate()));
        task.setStop(stop.atDate(task.getStop().toLocalDate()));
        getTaskStorage(task.getStart().toLocalDate()).changeTaskTimes(task);
    }

    public void renameTask(Task task, String name) {
        String original = task.getName();
        task.setName(name);

        DI.getTaskStopwatchService().renameTask(task, taskResponse -> {
            if (taskResponse == null) {
                task.setName(original);
                getTaskStorage(task.getStart().toLocalDate()).changedTask(task);
            }
        });
    }

    public void addTag(Tag tag) {
        tags.put(tag.getName(), tag);
        tagStorage.addedTag(tag);
    }

    public void recolorTag(Tag tag, String color) {
        String originalColor = tag.getColor();
        tag.setColor(color);
        tagStorage.changedTag(tag);
        DI.getTaskStopwatchService().changeTag(tag, (tagResponse) -> {
            if (tagResponse == null) {
                tag.setColor(originalColor);
                tagStorage.changedTag(tag);
            }
        });
    }

    public void removeTag(Tag tag) {
        tagStorage.removedTag(tag);
    }
}
