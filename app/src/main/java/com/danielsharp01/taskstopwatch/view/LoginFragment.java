package com.danielsharp01.taskstopwatch.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.api.TaskStopwatchService;


public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvError = view.findViewById(R.id.tvError);
        EditText editUsername = view.findViewById(R.id.editUsername);
        EditText editPassword = view.findViewById(R.id.editPassword);
        Button button = view.findViewById(R.id.btnLogin);
        button.setOnClickListener(v -> {
            DI.getTaskStopwatchService().tryLogin(editUsername.getText().toString(), editPassword.getText().toString(), loginResult -> {
                if (loginResult == TaskStopwatchService.LoginResult.success) {
                    tvError.setVisibility(View.INVISIBLE);
                    Navigation.findNavController(view).navigate(R.id.action_login);
                }
                else {
                    tvError.setVisibility(View.VISIBLE);
                    editPassword.setText("");
                }

                if (loginResult == TaskStopwatchService.LoginResult.invalidCredentials) tvError.setText("ERROR: Invalid credentials!");
                if (loginResult == TaskStopwatchService.LoginResult.serverFailure) tvError.setText("ERROR: Server is currently down!");
                if (loginResult == TaskStopwatchService.LoginResult.networkDown) tvError.setText("ERROR: Your network connection is down!");
            });
        });
        editPassword.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                button.performClick();
            }
            return false;
        });
    }
}
