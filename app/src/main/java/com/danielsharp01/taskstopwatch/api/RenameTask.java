package com.danielsharp01.taskstopwatch.api;

import com.danielsharp01.taskstopwatch.model.Task;

public class RenameTask {
    private String id;
    private String name;

    public RenameTask(Task task) {
        id = task.getId();
        name = task.getName();
    }
}
