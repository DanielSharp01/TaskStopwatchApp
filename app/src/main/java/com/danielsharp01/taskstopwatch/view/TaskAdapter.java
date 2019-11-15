package com.danielsharp01.taskstopwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;
import com.google.android.flexbox.FlexboxLayout;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
{
    private ArrayList<Task> data = new ArrayList<>();
    private Context context;

    public TaskAdapter(Context context)
    {
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task", LocalTime.of(12, 0, 0), LocalTime.of(13,0,0), new Tag[] { new Tag("Work", "red") }));
        this.data.add(new Task("New task2", LocalTime.of(13, 0, 0), null, new Tag[] { }));
        this.context = context;
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
        viewHolder.bind(data.get(i));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
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
            MainActivity.getInstance().subscribeTickable(this);
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
            this.tvTimeEnd.setText(task.getEndString());
            this.tvTime.setText(task.getDurationString());
            this.btnAction.setBackground(context.getResources().getDrawable(task.isRunning() ? R.drawable.ic_stop_24dp : R.drawable.ic_refresh_24dp));

        }

        @Override
        public void tick() {
            tvTimeEnd.setText(task.getEndString());
            tvTime.setText(task.getDurationString());
        }
    }
}