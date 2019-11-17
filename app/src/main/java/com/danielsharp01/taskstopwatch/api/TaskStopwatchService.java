package com.danielsharp01.taskstopwatch.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.danielsharp01.taskstopwatch.DI;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import java9.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskStopwatchService {
    public enum LoginResult {
        success,
        invalidCredentials,
        serverFailure,
        networkDown
    }

    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private String username;

    private TaskStopwatchAPI api;

    public TaskStopwatchService(Context context) {
        sharedPreferences = context.getSharedPreferences("TaskStopwatch", 0);
        bearerToken = sharedPreferences.getString("bearerToken", null);
        username = sharedPreferences.getString("username", null);

        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).setLenient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://danielsharp01.com/task-stopwatch/api/")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        api = retrofit.create(TaskStopwatchAPI.class);
    }

    public boolean isLoggedIn() {
        return bearerToken != null;
    }

    public String getUsername() {
        return username;
    }

    public void tryLogin(String username, String password, Consumer<LoginResult> resultCallback) {
        api.login(new LoginCredentials(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) {
                    bearerToken = response.body().getToken();
                    TaskStopwatchService.this.username = username;
                    sharedPreferences.edit().putString("bearerToken", bearerToken).putString("username", username).apply();
                    resultCallback.accept(LoginResult.success);
                }
                else if (response.code() < 500) {
                    resultCallback.accept(LoginResult.invalidCredentials);
                }
                else {
                    resultCallback.accept(LoginResult.serverFailure);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                resultCallback.accept(LoginResult.networkDown);
            }
        });
    }

    public void logout() {
        bearerToken = null;
        username = null;
        sharedPreferences.edit().remove("bearerToken").remove("username").apply();
        DI.getNavigationService().logout();
    }

    public void queryTasks(LocalDate date, String type) {
        queryTags();
        Callback<TasksResponse> callback = new Callback<TasksResponse>() {
            @Override
            public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                if (response.code() == 200) DI.getStorage().onRecieveTasks(type, date, response.body().result);

                // TODO: Do error handling
            }

            @Override
            public void onFailure(Call<TasksResponse> call, Throwable t) {
                // TODO: Do error handling
            }
        };
        switch (type) {
            case "day":
                api.getTasksForDay(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "Bearer " + bearerToken).enqueue(callback);
                break;
            case "week":
                api.getTasksForWeek(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "Bearer " + bearerToken).enqueue(callback);
                break;
            case "month":
                api.getTasksForMonth(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "Bearer " + bearerToken).enqueue(callback);
                break;
        }
    }

    public void queryTags() {
        Callback<TagsResponse> callback = new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.code() == 200) DI.getStorage().onRecieveTags(response.body().result);
                // TODO: Do error handling
            }

            @Override
            public void onFailure(Call<TagsResponse> call, Throwable t) {
                // TODO: Do error handling
            }
        };
        api.getTags("Bearer " + bearerToken).enqueue(callback);
    }
}
