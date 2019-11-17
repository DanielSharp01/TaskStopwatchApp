package com.danielsharp01.taskstopwatch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.storage.TagStorage;
import com.danielsharp01.taskstopwatch.storage.TaskStorage;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>
{
    private Context context;
    private TagStorage storage;

    public TagAdapter(Context context)
    {
        this.context = context;
    }

    public void bindStorage(@NonNull TagStorage storage) {
        if (this.storage != null) {
            this.storage.unbindAdapter(this);
        }

        this.storage = storage;
        this.storage.bindAdapter(this);
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder viewHolder, int i)
    {
        viewHolder.bind(storage.getTagList().get(i));
    }

    @Override
    public int getItemCount()
    {
        return storage != null ? storage.getTagList().size() : 0;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvTag;
        private Tag tag;


        public TagViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
        }

        public void bind(Tag tag)
        {
            this.tag = tag;
            tvTag.setText(tag.getName());
            tvTag.setBackgroundColor(tag.getColorResource(context.getResources()));
        }
    }
}