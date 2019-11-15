package com.danielsharp01.taskstopwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.model.Tag;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>
{
    private ArrayList<Tag> data = new ArrayList<>();
    private Context context;

    public TagAdapter(Context context)
    {
        this.data.add(new Tag("Job", "red"));
        this.data.add(new Tag("School", "blue"));
        this.data.add(new Tag("Project", "green"));
        this.data.add(new Tag("Gaming", "yellow"));
        this.context = context;
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
        viewHolder.bind(data.get(i));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
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