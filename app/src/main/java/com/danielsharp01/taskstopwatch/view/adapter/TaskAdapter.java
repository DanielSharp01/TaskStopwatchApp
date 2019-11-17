package com.danielsharp01.taskstopwatch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.storage.TaskStorage;
import com.google.android.flexbox.FlexboxLayout;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
{
    private Context context;
    private BiMap<Integer, TaskAdapter.TaskViewHolder> viewHolders = HashBiMap.create();
    private TaskStorage storage;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    public void bindStorage(@NonNull TaskStorage storage) {
        if (this.storage != null) {
            this.storage.unbindTaskAdapter(this);
        }

        this.storage = storage;
        this.storage.bindTaskAdapter(this);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder viewHolder, int i)
    {
        viewHolders.put(i, viewHolder);
        viewHolder.bind(storage.getTaskList().get(i));
    }

    @Override
    public void onViewRecycled(@NonNull TaskViewHolder holder) {
        super.onViewRecycled(holder);
        viewHolders.inverse().remove(holder);
    }

    @Override
    public int getItemCount()
    {
        return storage != null ? storage.getTaskList().size() : 0;
    }

    public void notifyItemTick(int position) {
        if (viewHolders.containsKey(position))
            viewHolders.get(position).tick();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements Tickable
    {
        private FlexboxLayout tagLayout;
        private TextView tvName;
        private TextView tvTimeStart;
        private TextView tvTimeEnd;
        private TextView tvTime;
        private Button btnAction;
        private Task task;


        public TaskViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tagLayout = itemView.findViewById(R.id.tagLayout);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimeStart = itemView.findViewById(R.id.tvTimeStart);
            tvTimeEnd = itemView.findViewById(R.id.tvTimeEnd);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Task task)
        {
            this.task = task;
            this.tagLayout.removeViews(0, this.tagLayout.getChildCount() - 1);

            for (Tag tag: this.task.getTags())
            {
                TextView view = new TextView(context, null, 0, R.style.tag);
                view.setText(tag.getName());
                view.setBackgroundColor(tag.getColorResource(context.getResources()));
                this.tagLayout.addView(view, this.tagLayout.getChildCount() - 1);
            }


            this.tvName.setText(task.getName());
            this.tvTimeStart.setText(task.getStartString());
            this.tvTimeEnd.setText(task.getStopString());
            this.tvTime.setText(task.getDurationString());
            this.btnAction.setBackground(context.getResources().getDrawable(task.isRunning() ? R.drawable.ic_stop_24dp : R.drawable.ic_refresh_24dp));

        }

        @Override
        public void tick() {
            tvTimeEnd.setText(task.getStopString());
            tvTime.setText(task.getDurationString());
        }
    }
}