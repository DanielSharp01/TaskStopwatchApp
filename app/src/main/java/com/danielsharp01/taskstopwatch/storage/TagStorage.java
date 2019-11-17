package com.danielsharp01.taskstopwatch.storage;

import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.view.adapter.TagAdapter;

import java.util.ArrayList;
import java.util.List;

public class TagStorage {

    private List<TagAdapter> adapters = new ArrayList<>();
    private ArrayList<Tag> cachedList = null;

    public void recieveTags(List<Tag> tags) {
        cachedList = new ArrayList<>(tags);
        for (TagAdapter adapter: adapters)
        {
            adapter.notifyDataSetChanged();
        }
    }

    public void bindAdapter(TagAdapter adapter) {
        adapters.add(adapter);
    }

    public void unbindAdapter(TagAdapter adapter) {
        adapters.remove(adapter);
    }

    public ArrayList<Tag> getTagList() {
        return cachedList != null ? cachedList : new ArrayList<>();
    }
}
