package com.danielsharp01.taskstopwatch.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.EditableTextView;
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
    private RecyclerView view;

    public TaskAdapter(Context context, RecyclerView view) {
        this.context = context;
        this.view = view;
    }

    public void bindStorage(@NonNull TaskStorage storage) {
        unbindStorage();

        this.storage = storage;
        this.storage.bindTaskAdapter(this);
    }

    public void unbindStorage() {
        if (this.storage != null) {
            this.storage.unbindTaskAdapter(this);
        }
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

    public void requestNotifyItemChanged(int position) {
        view.post(() -> this.notifyItemChanged(position));
    }

    public void requestNotifyItemInserted(int position) {
        /*view.post(() -> {
            // this.notifyItemInserted(position); it just magically knows that it should update
        });*/
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

    public void notifyItemTick(int position) {
        if (viewHolders.containsKey(position))
            viewHolders.get(position).tick();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements Tickable
    {
        private FlexboxLayout tagLayout;
        private TextView tvName;
        private EditText etName;
        private TextView tvTimeStart;
        private TextView tvTimeEnd;
        private TextView tvTime;
        private Button btnAddTag;
        private Button btnAction;
        private Task task;


        public TaskViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tagLayout = itemView.findViewById(R.id.tagLayout);
            tvName = itemView.findViewById(R.id.tvName);
            etName = itemView.findViewById(R.id.etName);
            tvTimeStart = itemView.findViewById(R.id.tvTimeStart);
            tvTimeEnd = itemView.findViewById(R.id.tvTimeEnd);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnAddTag = itemView.findViewById(R.id.btnAddTag);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Task task)
        {
            this.task = task;
            this.tagLayout.removeViews(0, this.tagLayout.getChildCount() - 1);

            for (Tag tag: this.task.getTags())
            {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.tag_on_task, this.tagLayout, false);
                TextView tvTag = view.findViewById(R.id.tvTag);
                tvTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                EditText etTag = view.findViewById(R.id.etTag);
                etTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                this.tagLayout.addView(view, this.tagLayout.getChildCount() - 1);

                EditableTextView.setup(context, false, task.getId().equals("temporary"), tvTag, etTag, tag.getName(), name -> {
                    if (tag.getName().equals(name)) return;

                    if (name.length() > 0) {
                        Tag addableTag =  DI.getStorage().getTagByName(name);
                        if (addableTag != null) {
                            if (this.task.hasTag(addableTag)) {
                                DI.getStorage().deleteTagOnTask(task, tag);
                            }
                            else {
                                DI.getStorage().createTagOnTask(task, addableTag);
                            }
                        } else {
                            addableTag = new Tag(name, tag.getColor());
                            DI.getStorage().addTag(addableTag);
                            DI.getStorage().changeTagOnTask(task, tag, addableTag);
                        }
                    } else {
                        DI.getStorage().deleteTagOnTask(task, tag);
                    }
                });
            }

            if (!task.getId().equals("temporary")) btnAddTag.setOnClickListener(v -> {
                Tag tag = new Tag("");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.tag_on_task, this.tagLayout, false);
                TextView tvTag = view.findViewById(R.id.tvTag);
                tvTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                EditText etTag = view.findViewById(R.id.etTag);
                etTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                this.tagLayout.addView(view, this.tagLayout.getChildCount() - 1);
                EditableTextView.setup(context, true, false, tvTag, etTag, tag.getName(), name -> {
                    if (name.length() > 0) {
                        Tag addableTag =  DI.getStorage().getTagByName(name);
                        if (addableTag != null) {
                            if (this.task.hasTag(addableTag)) {
                                this.tagLayout.removeViews(this.tagLayout.getChildCount() - 2, 1);
                            }
                            else {
                                DI.getStorage().createTagOnTask(task, addableTag);
                            }
                        } else {
                            tag.setName(name);
                            DI.getStorage().addTag(tag);
                            DI.getStorage().createTagOnTask(task, tag);
                        }
                    }
                    else {
                        this.tagLayout.removeViews(this.tagLayout.getChildCount() - 2, 1);
                    }

                });
                etTag.requestFocus();
            });

            EditableTextView.setup(context, false, task.getId().equals("temporary"), this.tvName, this.etName, task.getName(), name -> {
                DI.getStorage().renameTask(task, etName.getText().toString());
            });

            this.tvTimeStart.setText(task.getStartString());
            this.tvTimeEnd.setText(task.getStopString());
            this.tvTime.setText(task.getDurationString());
            this.btnAction.setBackground(context.getResources().getDrawable(task.isRunning() ? R.drawable.ic_stop_24dp : R.drawable.ic_refresh_24dp));
            this.btnAction.setEnabled(!task.getId().equals("temporary"));

            if (!task.getId().equals("temporary")) this.btnAction.setOnClickListener(v -> {
                if (task.isRunning()) {
                    DI.getStorage().stopTask(task);
                }
                else {
                    DI.getStorage().startTask(task.cloneAtNow());
                }
            });

            if (task.getId().equals("temporary")) {
                this.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public void tick() {
            btnAction.setBackground(context.getResources().getDrawable(task.isRunning() ? R.drawable.ic_stop_24dp : R.drawable.ic_refresh_24dp));
            tvTimeEnd.setText(task.getStopString());
            tvTime.setText(task.getDurationString());
        }
    }
}