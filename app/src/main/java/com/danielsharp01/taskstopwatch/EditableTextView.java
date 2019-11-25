package com.danielsharp01.taskstopwatch;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java9.util.function.Consumer;

public class EditableTextView {
    public static void setup(Context context, boolean edited, boolean disabled, TextView textView, EditText editText, String text, Consumer<String> callback) {
        textView.setVisibility(!edited ? View.VISIBLE : View.GONE);
        editText.setVisibility(edited  ? View.VISIBLE : View.GONE);
        textView.setText(text);
        editText.setText(text);

        if (!disabled) {
            textView.setOnLongClickListener(v -> {
                v.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                return true;
            });
        }

        editText.setOnKeyListener((v, keyCode, e) -> {
            if (e.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                editText.clearFocus();
                return true;
            }
            else return false;
        });
        editText.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                v.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(editText.getText());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                callback.accept(editText.getText().toString());
            }
            else {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, 0);
            }
        });
    }
}
