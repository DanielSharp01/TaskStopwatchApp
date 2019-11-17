package com.danielsharp01.taskstopwatch.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.view.pager.DayPagerAdapter;
import com.danielsharp01.taskstopwatch.view.pager.MonthPagerAdapter;
import com.danielsharp01.taskstopwatch.view.pager.WeekPagerAdapter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.MONTHS;
import static org.threeten.bp.temporal.ChronoUnit.WEEKS;


public class PagerFragment extends Fragment implements ViewPagerListener {

    private ViewPager2 viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(getAdapterFromArguments());

        String type = getArguments().getString("type");
        LocalDate date = (LocalDate)getArguments().getSerializable("date");

        switch (type) {
            case "day":
                viewPager.setCurrentItem(Integer.MAX_VALUE / 2  + (date != null ? (int)DAYS.between(LocalDate.now(), date) : 0), false);
                break;
            case "week":
                viewPager.setCurrentItem(Integer.MAX_VALUE / 2  + (date != null ? (int)WEEKS.between(LocalDate.now().with(DayOfWeek.MONDAY), date) : 0), false);
                break;
            case "month":
                viewPager.setCurrentItem(Integer.MAX_VALUE / 2  + (date != null ? (int)MONTHS.between(LocalDate.now().withDayOfMonth(1), date) : 0), false);
                break;
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (type) {
                    case "day":
                        DI.getNavigationService().onNavigatedTo(LocalDate.now().plusDays(position -  Integer.MAX_VALUE / 2));
                        break;
                    case "week":
                        DI.getNavigationService().onNavigatedTo(LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(position -  Integer.MAX_VALUE / 2));
                        break;
                    case "month":
                        DI.getNavigationService().onNavigatedTo(LocalDate.now().withDayOfMonth(1).plusMonths(position -  Integer.MAX_VALUE / 2));
                        break;
                }
            }
        });

        DI.getNavigationService().onNavigatedTo(date != null ? date : LocalDate.now());
    }

    private RecyclerView.Adapter getAdapterFromArguments() {
        String type = getArguments().getString("type");
        switch (type) {
            case "day":
                return new DayPagerAdapter(this, getContext());
            case "week":
                return new WeekPagerAdapter(this, getContext());
            case "month":
                return new MonthPagerAdapter(this, getContext());
        }

        return null;
    }

    @Override
    public void next() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    @Override
    public void previous() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }
}
