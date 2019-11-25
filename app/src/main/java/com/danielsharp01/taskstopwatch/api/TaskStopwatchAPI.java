package com.danielsharp01.taskstopwatch.api;

import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
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

    @POST("tasks/start")
    Call<TaskResponse> startTask(@Header("authorization") String bearerToken, @Body Task task);

    @PATCH("tasks/{id}/stop")
    Call<TaskResponse> stopTask(@Header("authorization") String bearerToken, @Path("id") String id);

    @POST("tasks/")
    Call<TaskResponse> createTask(@Header("authorization") String bearerToken, @Body Task task);

    @PATCH("tasks/{id}")
    Call<TaskResponse> renameTask(@Header("authorization") String bearerToken, @Path("id") String id, @Body RenameTask task);

    @DELETE("tasks/{id}")
    Call<TaskResponse> deleteTask(@Header("authorization") String bearerToken, @Path("id") String id);

    @POST("tasks/{id}/tags")
    Call<TagResponse> createTagOnTask(@Header("authorization") String bearerToken, @Path("id") String id, @Body Tag tag);

    @PATCH("tasks/{id}/tags/{tagName}")
    Call<TagResponse> changeTagOnTask(@Header("authorization") String bearerToken, @Path("id") String id, @Path("tagName") String tagName, @Body Tag tag);

    @DELETE("tasks/{id}/tags/{tagName}")
    Call<TagResponse> deleteTagOnTask(@Header("authorization") String bearerToken, @Path("id") String id, @Path("tagName") String tagName);

    @PATCH("tags/{tagName}")
    Call<TagResponse> changeTag(@Header("authorization") String bearerToken, @Path("tagName") String tagName, @Body Tag tag);
}
