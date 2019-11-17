package com.danielsharp01.taskstopwatch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.storage.TagTimeStorage;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TagTimeAdapter extends RecyclerView.Adapter<TagTimeAdapter.TagTimeViewHolder>
{
    private Context context;
    private @LayoutRes int layout;
    private BiMap<Integer, TagTimeViewHolder> viewHolders = HashBiMap.create();
    private TagTimeStorage storage;

    public TagTimeAdapter(Context context, @LayoutRes int layout)
    {
        this.context = context;
        this.layout = layout;
    }

    public void bindStorage(@NonNull TagTimeStorage storage) {
        if (this.storage != null) {
            this.storage.unbindTagTimeAdapter(this);
            this.storage.unbindSelf();
        }

        this.storage = storage;
        this.storage.bindTagTimeAdapter(this);
    }

    @NonNull
    @Override
    public TagTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout, parent, false);
        return new TagTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagTimeViewHolder viewHolder, int i)
    {
        viewHolders.put(i, viewHolder);
        viewHolder.bind(storage.getTagTimeList().get(i));
    }

    @Override
    public void onViewRecycled(@NonNull TagTimeViewHolder holder) {
        super.onViewRecycled(holder);
        viewHolders.inverse().remove(holder);
    }

    @Override
    public int getItemCount()
    {
        return storage != null ? storage.getTagTimeList().size() : 0;
    }

    public void notifyItemTick(int position) {
        if (viewHolders.containsKey(position))
            viewHolders.get(position).tick();
    }

    public class TagTimeViewHolder extends RecyclerView.ViewHolder implements Tickable
    {
        private TextView tvTag;
        private TextView tvDuration;
        private TagTime tagTime;


        public TagTimeViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        public void bind(TagTime tagTime)
        {
            this.tagTime = tagTime;
            tvTag.setText(tagTime.getTag().getName());
            tvTag.setBackgroundColor(tagTime.getTag().getColorResource(context.getResources()));
            tvDuration.setText(tagTime.getDurationString());

        }

        @Override
        public void tick() {
            tvDuration.setText(tagTime.getDurationString());
        }
    }
}