package com.danielsharp01.taskstopwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.TagTime;

import java.util.ArrayList;
import java.util.Collection;

public class TagTimeAdapter extends RecyclerView.Adapter<TagTimeAdapter.TagTimeViewHolder>
{
    private ArrayList<TagTime> data = new ArrayList<>();
    private Context context;
    private @LayoutRes int layout;

    public TagTimeAdapter(Context context, Collection<TagTime> data, @LayoutRes int layout)
    {
        this.context = context;
        this.data.addAll(data);
        this.layout = layout;
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
        viewHolder.bind(data.get(i));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
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
            MainActivity.getInstance().subscribeTickable(this);
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