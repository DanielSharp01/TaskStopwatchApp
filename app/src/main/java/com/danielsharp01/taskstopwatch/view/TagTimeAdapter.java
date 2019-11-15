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
import com.danielsharp01.taskstopwatch.model.TagTime;

import org.threeten.bp.Duration;

import java.util.ArrayList;

public class TagTimeAdapter extends RecyclerView.Adapter<TagTimeAdapter.TagTimeViewHolder>
{
    private ArrayList<TagTime> data = new ArrayList<>();
    private Context context;

    public TagTimeAdapter(Context context)
    {
        this.data.add(new TagTime(new Tag("Job", "red"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("School", "blue"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Project", "green"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Gaming", "yellow"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Job", "red"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("School", "blue"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Project", "green"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Gaming", "yellow"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Job", "red"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("School", "blue"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Project", "green"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Gaming", "yellow"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Job", "red"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("School", "blue"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Project", "green"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Gaming", "yellow"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Job", "red"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("School", "blue"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Project", "green"), Duration.ofHours(2)));
        this.data.add(new TagTime(new Tag("Gaming", "yellow"), Duration.ofHours(2)));
        this.context = context;
    }

    @NonNull
    @Override
    public TagTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tag_time, parent, false);
        return new TagTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagTimeViewHolder viewHolder, int i)
    {
        viewHolder.bind(data.get(i));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class TagTimeViewHolder extends RecyclerView.ViewHolder
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
    }
}