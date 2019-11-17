package com.danielsharp01.taskstopwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class DaySummaryAdapter extends RecyclerView.Adapter<DaySummaryAdapter.DaySummaryViewHolder> {
    private Context context;
    private String type;
    private LocalDate startDate;
    private TouchDisableListener touchDisableListener;
    private List<Task> relevantTasks = new ArrayList<>();

    public DaySummaryAdapter(Context context, String type, LocalDate date, List<Task> relevantTasks) {
        this(context, type, date, relevantTasks, null);
    }

    public DaySummaryAdapter(Context context, String type, LocalDate date, List<Task> relevantTasks, TouchDisableListener touchDisableListener) {
        this.context = context;
        this.type = type;
        this.startDate = date;
        this.relevantTasks.addAll(relevantTasks);
        this.touchDisableListener = touchDisableListener;
    }

    @NonNull
    @Override
    public DaySummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.summary_day, parent, false);
        if (type.equals("month")) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) (70 * context.getResources().getDisplayMetrics().density);
            view.setLayoutParams(params);
        }
        return new DaySummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaySummaryViewHolder viewHolder, int i)
    {
        viewHolder.bind(startDate.plusDays(i - (startDate.getDayOfWeek().getValue() - 1)));
    }

    @Override
    public int getItemCount() {
        return type.equals("week") ? 7 : 7 * 6;
    }

    public class DaySummaryViewHolder extends RecyclerView.ViewHolder implements Tickable {
        private RecyclerView recyclerView;
        private TextView tvDay;
        private LocalDate date;

        private Map<String, TagTime> tagTimes = new HashMap<>();
        private Task activeTask = null;


        public DaySummaryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new TagTimeAdapter(context, new ArrayList<>(), R.layout.tag_time_day_summary));
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (touchDisableListener != null) {
                recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        int action = e.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_MOVE:
                                if (isRecyclerViewScrollable()) touchDisableListener.requestTouchShouldDisable();
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
            }

            tvDay = itemView.findViewById(R.id.tvDay);
            tvDay.setOnClickListener(view -> MainActivity.getInstance().navigate(date));
            MainActivity.getInstance().subscribeTickable(this);
        }

        public void bind(LocalDate date)
        {
            activeTask = null;
            tagTimes.clear();
            this.date = date;
            if (!startDate.getMonth().equals(this.date.getMonth())) {
                tvDay.setTextColor(context.getResources().getColor(R.color.foregroundDark));

                MainActivity.getInstance().getService().queryTasks(date, "day", tasks -> {
                    for (Task task : tasks) {
                        if (task.isDisabled()) continue;

                        if (task.isRunning()) activeTask = task;

                        for (Tag tag : task.getTags()) {
                            if (task.isRunning()) {
                                if (!tagTimes.containsKey(tag.getName())) {
                                    tagTimes.put(tag.getName(), new TagTime(tag, Duration.ofNanos(0)));
                                    tagTimes.get(tag.getName()).setActiveDuration(task.getDuration());
                                } else {
                                    tagTimes.get(tag.getName()).setActiveDuration(task.getDuration());
                                }
                            } else {
                                if (!tagTimes.containsKey(tag.getName())) {
                                    tagTimes.put(tag.getName(), new TagTime(tag, task.getDuration()));
                                } else {
                                    tagTimes.get(tag.getName()).addDuration(task.getDuration());
                                }
                            }
                        }
                    }
                    recyclerView.setAdapter(new TagTimeAdapter(context, tagTimes.values(), R.layout.tag_time_day_summary));
                });
            }
            else {
                for (Task task: relevantTasks) {
                    if (date.isBefore(task.getStart().toLocalDate()) || date.isAfter(task.getStop().toLocalDate())) continue;
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
                recyclerView.setAdapter(new TagTimeAdapter(context, tagTimes.values(), R.layout.tag_time_day_summary));
            }
            tvDay.setText(String.format("%s %s", type.equals("month") ? String.valueOf(date.getDayOfMonth()) : "", date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)));


        }

        private boolean isRecyclerViewScrollable() {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1 || layoutManager.findFirstCompletelyVisibleItemPosition() > 0;
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
