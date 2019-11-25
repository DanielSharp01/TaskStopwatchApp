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
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.view.ViewPagerListener;
import com.danielsharp01.taskstopwatch.view.adapter.DaySummaryAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static org.threeten.bp.temporal.ChronoUnit.WEEKS;

public class WeekPagerAdapter extends RecyclerView.Adapter<WeekPagerAdapter.WeekViewHolder>
{
    private Context context;
    private ViewPagerListener viewPagerListener;
    private LocalDate now;
    private BiMap<Integer, WeekViewHolder> viewHolders = HashBiMap.create();

    public WeekPagerAdapter(ViewPagerListener viewPagerListener, Context context) {
        this.viewPagerListener = viewPagerListener;
        this.context = context;
        now = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    public void unbind() {
        for (WeekViewHolder holder: viewHolders.values()) {
            holder.unbind();
        }
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
        viewHolders.put(i, viewHolder);
        viewHolder.bind(now.plusWeeks(i - Integer.MAX_VALUE / 2));
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
        viewHolders.inverse().remove(holder);
    }

    public class WeekViewHolder extends RecyclerView.ViewHolder
    {
        private DaySummaryAdapter daySummaryAdapter;
        private TagTimeAdapter tagTimeAdapter;

        private TextView tvDate;
        private TextView tvYear;

        public WeekViewHolder(@NonNull View itemView)
        {
            super(itemView);
            RecyclerView recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            tagTimeAdapter = new TagTimeAdapter(context, R.layout.tag_time_small, recyclerViewTags);
            recyclerViewTags.setAdapter(tagTimeAdapter);
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            RecyclerView recyclerViewWeek = itemView.findViewById(R.id.recyclerViewWeek);
            recyclerViewWeek.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            daySummaryAdapter = new DaySummaryAdapter(context);
            recyclerViewWeek.setAdapter(daySummaryAdapter);
            recyclerViewWeek.setItemAnimator(new DefaultItemAnimator());

            tvDate = itemView.findViewById(R.id.tvDate);
            tvYear = itemView.findViewById(R.id.tvYear);
            Button btnPrev = itemView.findViewById(R.id.btnPrev);
            btnPrev.setOnClickListener((v) -> viewPagerListener.previous());
            Button btnNext = itemView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener((v) -> viewPagerListener.next());
        }

        public void bind(LocalDate date)
        {
            unbind();
            tvDate.setText(String.format("%s - %s",
                    date.format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)),
                    date.with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH))));
            tvYear.setText(String.valueOf(date.getYear()));

            daySummaryAdapter.bind( "week", date);
            tagTimeAdapter.bindStorage(DI.getStorage().getAggregrateTaskStorageForWeek(date));
            DI.getTaskStopwatchService().queryTasks(date, "week");
        }

        public void unbind() {
            daySummaryAdapter.unbind();
            tagTimeAdapter.unbindStorage();
        }
    }
}
