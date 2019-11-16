package com.danielsharp01.taskstopwatch.model;

import androidx.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.util.Objects;

public class TaskQuery {
    private LocalDate queryDate;
    private String queryType;

    public TaskQuery(LocalDate queryDate, String queryType) {
        this.queryDate = queryDate;
        this.queryType = queryType;
    }

    public LocalDate getQueryDate() {
        return queryDate;
    }

    public String getQueryType() {
        return queryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryDate, queryType);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TaskQuery)) return false;
        TaskQuery other = (TaskQuery)obj;
        return other.queryDate.equals(queryDate) && other.queryType.equals(queryType);
    }
}
