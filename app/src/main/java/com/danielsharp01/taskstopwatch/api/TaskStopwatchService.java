package com.danielsharp01.taskstopwatch.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.model.Task;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public TaskStopwatchAPI getAPI() {
        return api;
    }

    public TaskStopwatchService(Context context) {
        sharedPreferences = context.getSharedPreferences("TaskStopwatch", 0);
        bearerToken = sharedPreferences.getString("bearerToken", null);
        username = sharedPreferences.getString("username", null);

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setLenient();
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
        queryTags(() -> {
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
        });
    }

    public void queryTags() {
        queryTags(null);
    }

    public void queryTags(Runnable after) {
        Callback<TagsResponse> callback = new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.code() == 200) DI.getStorage().onRecieveTags(response.body().result);
                // TODO: Do error handling
                if (after != null) {
                    after.run();
                }
            }

            @Override
            public void onFailure(Call<TagsResponse> call, Throwable t) {
                // TODO: Do error handling
            }
        };
        api.getTags("Bearer " + bearerToken).enqueue(callback);
    }

    public void startTask(Task task, Consumer<Task> resultCallback) {
        api.startTask("Bearer " + bearerToken, task).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void stopTask(Task task, Consumer<Task> resultCallback) {
        api.stopTask("Bearer " + bearerToken, task.getId()).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void createTask(Task task, Consumer<Task> resultCallback) {
        api.createTask("Bearer " + bearerToken, task).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void renameTask(Task task, Consumer<Task> resultCallback) {
        api.renameTask("Bearer " + bearerToken, task.getId(), new RenameTask(task)).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void deleteTask(Task task, Consumer<Task> resultCallback) {
        api.deleteTask("Bearer " + bearerToken, task.getId()).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void createTagOnTask(Task task, Tag tag, Consumer<Tag> resultCallback) {
        api.createTagOnTask("Bearer " + bearerToken, task.getId(), tag).enqueue(new Callback<TagResponse>() {
            @Override
            public void onResponse(Call<TagResponse> call, Response<TagResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TagResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void changeTagOnTask(Task task, Tag from, Tag to, Consumer<Tag> resultCallback) {
        api.changeTagOnTask("Bearer " + bearerToken, task.getId(), from.getName(), to).enqueue(new Callback<TagResponse>() {
            @Override
            public void onResponse(Call<TagResponse> call, Response<TagResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TagResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void deleteTagOnTask(Task task, Tag tag, Consumer<Tag> resultCallback) {
        api.deleteTagOnTask("Bearer " + bearerToken, task.getId(), tag.getName()).enqueue(new Callback<TagResponse>() {
            @Override
            public void onResponse(Call<TagResponse> call, Response<TagResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TagResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void changeTag(Tag tag, Consumer<Tag> resultCallback) {
        api.changeTag("Bearer " + bearerToken, tag.getName(), tag).enqueue(new Callback<TagResponse>() {
            @Override
            public void onResponse(Call<TagResponse> call, Response<TagResponse> response) {
                if (response.code() == 200) {
                    resultCallback.accept(response.body().result);
                }
                else {
                    resultCallback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<TagResponse> call, Throwable t) {
                resultCallback.accept(null);
            }
        });
    }

    public void persistAddedTags(List<Tag> tags) {
        StringBuilder sb = new StringBuilder();
        for (Tag tag: tags) {
            sb.append(tag.getName()).append(",");
        }
        sharedPreferences.edit().putString("addedTags", sb.toString()).apply();
    }

    public List<Tag> getAddedTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        for (String name: sharedPreferences.getString("addedTags", "").split(",")) {
            Tag tag = DI.getStorage().getTagByName(name);
            if (tag != null) tags.add(tag);
        }

        return tags;
    }

    public void setTagTracking(Tag tag, boolean tracked) {
        sharedPreferences.edit().putBoolean("tag:" + tag.getName(), tracked).apply();
    }

    public boolean isTagTracked(Tag tag) {
        return sharedPreferences.getBoolean("tag:" + tag.getName(), false);
    }
}
