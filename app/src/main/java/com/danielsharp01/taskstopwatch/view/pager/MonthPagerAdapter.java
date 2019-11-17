package com.danielsharp01.taskstopwatch.view.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.view.TouchDisableListener;
import com.danielsharp01.taskstopwatch.view.ViewPagerListener;
import com.danielsharp01.taskstopwatch.view.adapter.DaySummaryAdapter;
import com.danielsharp01.taskstopwatch.view.adapter.TagTimeAdapter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

import static org.threeten.bp.temporal.ChronoUnit.MONTHS;

public class MonthPagerAdapter extends RecyclerView.Adapter<MonthPagerAdapter.MonthViewHolder>
{
    private Context context;
    private ViewPagerListener viewPagerListener;
    private LocalDate now;

    public MonthPagerAdapter(ViewPagerListener viewPagerListener, Context context) {
        this.viewPagerListener = viewPagerListener;
        this.context = context;
        now = LocalDate.now().withDayOfMonth(1);
    }

    @Override
    public int getItemCount() {
        return (int) ((Integer.MAX_VALUE / 2) + MONTHS.between(now, LocalDate.now().withDayOfMonth(1))) + 1;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.month, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder viewHolder, int i)
    {
        viewHolder.bind(now.plusMonths(i - Integer.MAX_VALUE / 2));
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder implements TouchDisableListener {
        private TagTimeAdapter tagTimeAdapter;
        private RecyclerView recyclerViewMonth;

        private TextView tvMonth;
        private TextView tvYear;


        public MonthViewHolder(@NonNull View itemView)
        {
            super(itemView);
            RecyclerView recyclerViewTags = itemView.findViewById(R.id.recyclerViewTags);
            recyclerViewTags.setLayoutManager(new LinearLayoutManager(context));
            tagTimeAdapter = new TagTimeAdapter(context, R.layout.tag_time_small);
            recyclerViewTags.setAdapter(tagTimeAdapter);
            recyclerViewTags.setItemAnimator(new DefaultItemAnimator());

            recyclerViewMonth = itemView.findViewById(R.id.recyclerViewMonth);
            recyclerViewMonth.setLayoutManager(new GridLayoutManager(context, 7));
            recyclerViewMonth.setItemAnimator(new DefaultItemAnimator());

            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvYear = itemView.findViewById(R.id.tvYear);
            Button btnPrev = itemView.findViewById(R.id.btnPrev);
            btnPrev.setOnClickListener((v) -> viewPagerListener.previous());
            Button btnNext = itemView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener((v) -> viewPagerListener.next());
        }

        public void bind(LocalDate date)
        {
            tvMonth.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            tvYear.setText(String.valueOf(date.getYear()));

            recyclerViewMonth.setAdapter(new DaySummaryAdapter(context, "month", date, this));
            tagTimeAdapter.bindStorage(DI.getStorage().getAggregrateTaskStorageForMonth(date));
            DI.getTaskStopwatchService().queryTasks(date, "month");
        }

        @Override
        public void requestTouchShouldDisable() {
            recyclerViewMonth.requestDisallowInterceptTouchEvent(true);
        }
    }
}
