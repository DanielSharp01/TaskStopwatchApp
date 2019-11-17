package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;

import java.util.ArrayList;

public interface TagTimeStorage {
    void bindTagTimeAdapter(TagTimeAdapter adapter);
    void unbindTagTimeAdapter(TagTimeAdapter adapter);
    ArrayList<TagTime> getTagTimeList();
    void unbindSelf();
}
