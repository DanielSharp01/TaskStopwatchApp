package com.danielsharp01.taskstopwatch.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java9.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Map<String, Tag> tags = new HashMap<>();

    public TaskStopwatchService(Context context) {
        sharedPreferences = context.getSharedPreferences("TaskStopwatch", 0);
        bearerToken = sharedPreferences.getString("bearerToken", null);
        username = sharedPreferences.getString("username", null);
    }

    public boolean isLoggedIn() {
        return bearerToken != null;
    }

    public String getUsername() {
        return username;
    }

    public Tag getTagByName(String name) {
        return tags.get(name);
    }

    public void tryLogin(String username, String password, Consumer<LoginResult> resultCallback) {
        MainActivity.getInstance().getAPI().login(new LoginCredentials(username, password)).enqueue(new Callback<LoginResponse>() {
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
    }

    public void queryTasks(LocalDate date, String type, Consumer<List<Task>> resultCallback) {
        queryTags(tags -> {
            this.tags.clear();
            for (Tag tag: tags) {
                this.tags.put(tag.getName(), tag);
            }
        });
        Callback<TasksResponse> callback = new Callback<TasksResponse>() {
            @Override
            public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                if (response.code() == 200) resultCallback.accept(response.body().result);
                else resultCallback.accept(new ArrayList<>());

                // TODO: Do error handling
            }

            @Override
            public void onFailure(Call<TasksResponse> call, Throwable t) {
                resultCallback.accept(new ArrayList<>());

                // TODO: Do error handling
            }
        };
        TaskStopwatchAPI api = MainActivity.getInstance().getAPI();
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

    public void queryTags(Consumer<List<Tag>> resultCallback) {
        Callback<TagsResponse> callback = new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.code() == 200) resultCallback.accept(response.body().result);
                else resultCallback.accept(new ArrayList<>());
                // TODO: Do error handling
            }

            @Override
            public void onFailure(Call<TagsResponse> call, Throwable t) {
                resultCallback.accept(new ArrayList<>());
            }
        };
        MainActivity.getInstance().getAPI().getTags("Bearer " + bearerToken).enqueue(callback);
    }
}
