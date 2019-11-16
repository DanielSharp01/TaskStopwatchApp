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

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.threeten.bp.temporal.ChronoUnit.WEEKS;

public class WeekPagerAdapter extends RecyclerView.Adapter<WeekPagerAdapter.WeekViewHolder>
{
    private Context context;
    private ViewPagerListener viewPagerListener;
    private LocalDate now;

    public WeekPagerAdapter(ViewPagerListener viewPagerListener, Context context) {
        this.viewPagerListener = viewPagerListener;
        this.context = context;
        now = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    @Override
    public int getItemCount() {
        return (int) ((Integer.MAX_VALUE / 2) + WEEKS.between(now, LocalDate.now().with(DayOfWeek.MONDAY))) + 1;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.week, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder viewHolder, int i)
    {
        viewHolder.bind(now.plusWeeks(i - Integer.MAX_VALUE / 2));
    }

    public class WeekViewHolder extends RecyclerView.ViewHolder implements Tickable
    {
        private RecyclerView recyclerViewTags;
        private RecyclerView recyclerViewWeek;

        private TextView tvDate;
        private TextView tvYear;

        private Map<String, TagTime> tagTimes = new HashMap<>();
        private Task activeTask = null;


        public WeekViewHolder(@NonNull View itemView)
        {
            super(itemView);
            recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewTags.setAdapter(new TagTimeAdapter(context, new ArrayList<>()));
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            recyclerViewWeek = itemView.findViewById(R.id.recyclerViewWeek);
            recyclerViewWeek.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewWeek.setItemAnimator(new DefaultItemAnimator());

            tvDate = itemView.findViewById(R.id.tvDate);
            tvYear = itemView.findViewById(R.id.tvYear);
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
            tvDate.setText(String.format("%s - %s",
                    date.format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)),
                    date.with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH))));
            tvYear.setText(String.valueOf(date.getYear()));
            MainActivity.getInstance().getService().queryTasks(date, "week", data -> {
                recyclerViewWeek.setAdapter(new DaySummaryAdapter(context, "week", date, data));
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
