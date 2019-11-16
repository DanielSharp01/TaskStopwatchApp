package com.danielsharp01.taskstopwatch.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskStopwatchAPI {
    @POST("https://danielsharp01.com/login/api")
    Call<LoginResponse> login(@Body LoginCredentials credentials);

    @GET("tasks/day/{date}")
    Call<TasksResponse> getTasksForDay(@Path("date") String date, @Header("authorization") String bearerToken);

    @GET("tasks/week/{date}")
    Call<TasksResponse> getTasksForWeek(@Path("date") String date, @Header("authorization") String bearerToken);

    @GET("tasks/month/{date}")
    Call<TasksResponse> getTasksForMonth(@Path("date") String date, @Header("authorization") String bearerToken);

    @GET("tags")
    Call<TagsResponse> getTags(@Header("authorization") String bearerToken);
}
