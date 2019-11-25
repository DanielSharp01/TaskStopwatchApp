package com.danielsharp01.taskstopwatch.view.adapter;

import android.content.Context;
import android.util.Log;
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
    private RecyclerView view;

    public TagTimeAdapter(Context context, @LayoutRes int layout, RecyclerView view)
    {
        this.context = context;
        this.layout = layout;
        this.view = view;
    }

    public void bindStorage(@NonNull TagTimeStorage storage) {
        unbindStorage();

        this.storage = storage;
        this.storage.bindTagTimeAdapter(this);
        notifyDataSetChanged();
    }

    public void unbindStorage() {
        if (this.storage != null) {
            this.storage.unbindTagTimeAdapter(this);
            this.storage.unbindSelf();
        }
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

    public void requestNotifyItemChanged(int position) {
        view.post(() -> this.notifyItemChanged(position));
    }

    public void requestNotifyItemInserted(int position) {
        view.post(() -> this.notifyItemInserted(position));
    }

    public void requestNotifyItemRemoved(int position) {
        view.post(() -> this.notifyItemRemoved(position));
    }

    public void requestNotifyItemMoved(int oldPosition, int newPosition) {
        view.post(() -> this.notifyItemMoved(oldPosition, newPosition));
    }

    public void requestNotifyDataSetChanged() {
        view.post(this::notifyDataSetChanged);
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