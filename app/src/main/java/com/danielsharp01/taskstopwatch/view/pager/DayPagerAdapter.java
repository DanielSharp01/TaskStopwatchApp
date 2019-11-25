package com.danielsharp01.taskstopwatch.view.pager;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.EditableTextView;
import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TaskAdapter;
import com.danielsharp01.taskstopwatch.view.ViewPagerListener;
import com.google.android.flexbox.FlexboxLayout;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;

public class DayPagerAdapter extends RecyclerView.Adapter<DayPagerAdapter.DayViewHolder>
{
    private Context context;
    private ViewPagerListener viewPagerListener;
    private LocalDate now;
    private BiMap<Integer, DayViewHolder> viewHolders = HashBiMap.create();

    public DayPagerAdapter(ViewPagerListener viewPagerListener, Context context) {
        this.viewPagerListener = viewPagerListener;
        this.context = context;
        this.now = LocalDate.now();
    }

    public void unbind() {
        for (DayViewHolder holder: viewHolders.values()) {
            holder.unbind();
        }
    }

    @Override
    public int getItemCount() {
        return (int) ((Integer.MAX_VALUE / 2) + DAYS.between(now, LocalDate.now())) + 1;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder viewHolder, int i)
    {
        viewHolders.put(i, viewHolder);
        viewHolder.bind(now.plusDays(i - Integer.MAX_VALUE / 2));
    }

    @Override
    public void onViewRecycled(@NonNull DayViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
        viewHolders.inverse().remove(holder);
    }

    public class DayViewHolder extends RecyclerView.ViewHolder
    {
        private Task newTask = new Task();

        private TextView tvDate;
        private TextView tvDow;
        private TaskAdapter taskAdapter;
        private TagTimeAdapter tagTimeAdapter;
        private FlexboxLayout tagLayout;
        private EditText etName;
        private Button btnAddTag;
        private Button btnStartTask;


        public DayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            RecyclerView recyclerViewTasks = itemView.findViewById(R.id.recyclerViewTasks);
            recyclerViewTasks.setLayoutManager(new LinearLayoutManager(context));
            taskAdapter = new TaskAdapter(context, recyclerViewTasks);
            recyclerViewTasks.setAdapter(taskAdapter);
            recyclerViewTasks.setItemAnimator(new DefaultItemAnimator());

            RecyclerView recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            tagTimeAdapter = new TagTimeAdapter(context, R.layout.tag_time, recyclerViewTags);
            recyclerViewTags.setAdapter(tagTimeAdapter);
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            tvDate = itemView.findViewById(R.id.tvDate);
            tvDow = itemView.findViewById(R.id.tvDow);
            etName = itemView.findViewById(R.id.etName);

            btnAddTag = itemView.findViewById(R.id.btnAddTag);
            tagLayout = itemView.findViewById(R.id.newTagLayout);

            btnAddTag.setOnClickListener(v -> {
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
                            if (newTask.hasTag(addableTag)) {
                                this.tagLayout.removeViews(this.tagLayout.getChildCount() - 2, 1);
                            }
                            else {
                                newTask.addTag(addableTag);
                            }
                        } else {
                            tag.setName(name);
                            DI.getStorage().addTag(tag);
                            newTask.addTag(tag);
                        }
                    }
                    else {
                        this.tagLayout.removeViews(this.tagLayout.getChildCount() - 2, 1);
                    }
                    refreshTags();
                });
                etTag.requestFocus();
            });

            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    btnStartTask.setEnabled(s.length() > 0);
                }
            });

            etName.setOnKeyListener((v, keyCode, e) -> {
                if (e.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (btnStartTask.isEnabled()) {
                        btnStartTask.performClick();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
                else return false;
            });

            btnStartTask = itemView.findViewById(R.id.btnStartTask);
            btnStartTask.setEnabled(false);

            btnStartTask.setOnClickListener(v -> {
                newTask.setStart(LocalDateTime.now());
                newTask.setName(etName.getText().toString());
                List<Tag> tags = newTask.getTags();
                DI.getStorage().startTask(newTask);
                DI.getTaskStopwatchService().persistAddedTags(tags);
                newTask = new Task();
                newTask.addAllTags(tags);
                etName.setText("");
                btnStartTask.setEnabled(false);
                refreshTags();
            });

            Button btnPrev = itemView.findViewById(R.id.btnPrev);
            btnPrev.setOnClickListener((v) -> viewPagerListener.previous());
            Button btnNext = itemView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener((v) -> viewPagerListener.next());
        }

        private void refreshTags() {
            this.tagLayout.removeViews(0, this.tagLayout.getChildCount() - 1);
            for (Tag tag: newTask.getTags()) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.tag_on_task, this.tagLayout, false);
                TextView tvTag = view.findViewById(R.id.tvTag);
                tvTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                EditText etTag = view.findViewById(R.id.etTag);
                etTag.setBackgroundColor(tag.getColorResource(context.getResources()));
                this.tagLayout.addView(view, this.tagLayout.getChildCount() - 1);
                EditableTextView.setup(context, false, false, tvTag, etTag, tag.getName(), name -> {
                    if (name.length() > 0) {
                        Tag addableTag =  DI.getStorage().getTagByName(name);
                        if (addableTag != null) {
                            if (newTask.hasTag(addableTag)) {
                                newTask.removeTag(tag);
                            }
                            else {
                                newTask.addTag(tag);
                            }
                        } else {
                            addableTag = new Tag(name, tag.getColor());
                            DI.getStorage().addTag(addableTag);
                            newTask.removeTag(tag);
                            newTask.addTag(addableTag);
                        }
                    } else {
                        newTask.removeTag(tag);
                    }

                    refreshTags();
                });
            }
        }

        public void bind(LocalDate date)
        {
            unbind();
            newTask = new Task();
            DI.getTaskStopwatchService().queryTags(() -> {
                newTask.addAllTags(DI.getTaskStopwatchService().getAddedTags());
                refreshTags();
            });

            tvDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy MMMM d", Locale.ENGLISH)));
            tvDow.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));


            taskAdapter.bindStorage(DI.getStorage().getTaskStorage(date));
            tagTimeAdapter.bindStorage(DI.getStorage().getTaskStorage(date));
            DI.getTaskStopwatchService().queryTasks(date, "day");
            // TODO: New tasks should be tied to strict mode otherwise add you can't add tasks
        }

        public void unbind() {
            taskAdapter.unbindStorage();
            tagTimeAdapter.unbindStorage();
        }
    }
}
