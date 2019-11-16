package com.danielsharp01.taskstopwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;

public class DayPagerAdapter extends RecyclerView.Adapter<DayPagerAdapter.DayViewHolder>
{
    private Context context;
    private ViewPagerListener viewPagerListener;
    private LocalDate now;

    public DayPagerAdapter(ViewPagerListener viewPagerListener, Context context) {
        this.viewPagerListener = viewPagerListener;
        this.context = context;
        now = LocalDate.now();
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
        viewHolder.bind(now.plusDays(i - Integer.MAX_VALUE / 2));
    }

    public class DayViewHolder extends RecyclerView.ViewHolder implements Tickable
    {
        private RecyclerView recyclerViewTasks;
        private RecyclerView recyclerViewTags;
        private TextView tvDate;
        private TextView tvDow;
        private Map<String, TagTime> tagTimes = new HashMap<>();
        private Task activeTask = null;


        public DayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            recyclerViewTasks = itemView.findViewById(R.id.recyclerViewTasks);
            recyclerViewTasks.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewTasks.setItemAnimator(new DefaultItemAnimator());

            recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            tvDate = itemView.findViewById(R.id.tvDate);
            tvDow = itemView.findViewById(R.id.tvDow);
            Button btnPrev = itemView.findViewById(R.id.btnPrev);
            btnPrev.setOnClickListener((v) -> viewPagerListener.previous());
            Button btnNext = itemView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener((v) -> viewPagerListener.next());
            MainActivity.getInstance().subscribeTickable(this);
        }

        public void bind(LocalDate date)
        {
            activeTask = null;
            tagTimes.clear();
            tvDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy MMMM d", Locale.ENGLISH)));
            tvDow.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

            MainActivity.getInstance().getService().queryTasks(date, "day", data -> {
                recyclerViewTasks.setAdapter(new TaskAdapter(context, data));
                for (Task task: data) {
                    if (task.isDisabled()) continue;

                    if (task.isRunning()) activeTask = task;

                    for (Tag tag: task.getTags()) {
                        if (task.isRunning()) {
                            if (!tagTimes.containsKey(tag.getName())) {
                                tagTimes.put(tag.getName(), new TagTime(tag, Duration.ofNanos(0)));
                                tagTimes.get(tag.getName()).setActiveDuration(task.getDuration());
                            }
                            else {
                                tagTimes.get(tag.getName()).setActiveDuration(task.getDuration());
                            }
                        }
                        else {
                            if (!tagTimes.containsKey(tag.getName())) {
                                tagTimes.put(tag.getName(), new TagTime(tag, task.getDuration()));
                            }
                            else {
                                tagTimes.get(tag.getName()).addDuration(task.getDuration());
                            }
                        }
                    }
                }
                recyclerViewTags.setAdapter(new TagTimeAdapter(context, tagTimes.values()));
            });

            // TODO: New tasks should be tied to strict mode otherwise add you can't add tasks
        }

        @Override
        public void tick() {
            if (activeTask == null) return;
            for (Tag tag: activeTask.getTags()) {
                tagTimes.get(tag.getName()).setActiveDuration(activeTask.getDuration());
            }
        }
    }
}
