package com.danielsharp01.taskstopwatch.view.adapter;

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

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.view.TouchDisableListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Locale;

public class DaySummaryAdapter extends RecyclerView.Adapter<DaySummaryAdapter.DaySummaryViewHolder> {
    private Context context;
    private String type;
    private LocalDate startDate;
    private TouchDisableListener touchDisableListener;
    private ArrayList<TagTimeAdapter> adapters = new ArrayList<>();

    public DaySummaryAdapter(Context context) {
        this(context, null);
    }

    public DaySummaryAdapter(Context context, TouchDisableListener touchDisableListener) {
        this.context = context;
        this.touchDisableListener = touchDisableListener;
    }

    public void bind(String type, LocalDate date) {
        this.type = type;
        this.startDate = date;
        notifyDataSetChanged();
    }

    public void unbind() {
        for (TagTimeAdapter adapter: adapters) {
            adapter.unbindStorage();
        }
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

    public class DaySummaryViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TagTimeAdapter adapter;
        private TextView tvDay;
        private LocalDate date;


        public DaySummaryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new TagTimeAdapter(context, R.layout.tag_time_day_summary, recyclerView);
            adapters.add(adapter);
            recyclerView.setAdapter(adapter);
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
            tvDay.setOnClickListener(view -> DI.getNavigationService().navigateTo("day", date));
        }

        public void bind(LocalDate date)
        {
            this.date = date;
            if (!startDate.getMonth().equals(this.date.getMonth())) {
                tvDay.setTextColor(context.getResources().getColor(R.color.foregroundDark));
                DI.getTaskStopwatchService().queryTasks(date, "day");
            }

            tvDay.setText(String.format("%s %s", type.equals("month") ? String.valueOf(date.getDayOfMonth()) : "", date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)));
            adapter.bindStorage(DI.getStorage().getTaskStorage(date));
        }

        private boolean isRecyclerViewScrollable() {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1 || layoutManager.findFirstCompletelyVisibleItemPosition() > 0;
        }
    }
}
