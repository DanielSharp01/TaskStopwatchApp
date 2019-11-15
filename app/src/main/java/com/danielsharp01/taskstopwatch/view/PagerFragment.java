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

import com.danielsharp01.taskstopwatch.R;

import org.threeten.bp.LocalDate;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;


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

        LocalDate date = (LocalDate)getArguments().getSerializable("date");
        if (date != null && getArguments().getString("type").equals("day")) {
            viewPager.setCurrentItem(Integer.MAX_VALUE / 2  + (int)DAYS.between(LocalDate.now(), date), false);
        } else {
            viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false);
        }
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
