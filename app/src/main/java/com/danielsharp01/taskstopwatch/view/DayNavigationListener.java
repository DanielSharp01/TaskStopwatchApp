package com.danielsharp01.taskstopwatch.view;

import org.threeten.bp.LocalDate;

public interface DayNavigationListener {
    void navigate(LocalDate date);
}
