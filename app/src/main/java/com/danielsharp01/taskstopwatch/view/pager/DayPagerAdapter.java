package com.danielsharp01.taskstopwatch.view.pager;

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

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.Tickable;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.TagTime;
import com.danielsharp01.taskstopwatch.model.Task;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TaskAdapter;
import com.danielsharp01.taskstopwatch.view.ViewPagerListener;

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

    public class DayViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvDate;
        private TextView tvDow;
        private TaskAdapter taskAdapter;
        private TagTimeAdapter tagTimeAdapter;


        public DayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            RecyclerView recyclerViewTasks = itemView.findViewById(R.id.recyclerViewTasks);
            recyclerViewTasks.setLayoutManager(new LinearLayoutManager(context));
            taskAdapter = new TaskAdapter(context);
            recyclerViewTasks.setAdapter(taskAdapter);
            recyclerViewTasks.setItemAnimator(new DefaultItemAnimator());

            RecyclerView recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            tagTimeAdapter = new TagTimeAdapter(context, R.layout.tag_time);
            recyclerViewTags.setAdapter(tagTimeAdapter);
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            tvDate = itemView.findViewById(R.id.tvDate);
            tvDow = itemView.findViewById(R.id.tvDow);
            Button btnPrev = itemView.findViewById(R.id.btnPrev);
            btnPrev.setOnClickListener((v) -> viewPagerListener.previous());
            Button btnNext = itemView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener((v) -> viewPagerListener.next());
        }

        public void bind(LocalDate date)
        {
            tvDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy MMMM d", Locale.ENGLISH)));
            tvDow.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

            taskAdapter.bindStorage(DI.getStorage().getTaskStorage(date));
            tagTimeAdapter.bindStorage(DI.getStorage().getTaskStorage(date));
            DI.getTaskStopwatchService().queryTasks(date, "day");
            // TODO: New tasks should be tied to strict mode otherwise add you can't add tasks
        }
    }
}
