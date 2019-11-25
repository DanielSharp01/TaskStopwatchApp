package com.danielsharp01.taskstopwatch.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.EditableTextView;
import com.danielsharp01.taskstopwatch.MainActivity;
import com.danielsharp01.taskstopwatch.R;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.danielsharp01.taskstopwatch.storage.TagStorage;
import com.danielsharp01.taskstopwatch.storage.TaskStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>
{
    private Context context;
    private TagStorage storage;

    public TagAdapter(Context context)
    {
        this.context = context;
    }

    public void bindStorage(@NonNull TagStorage storage) {
        if (this.storage != null) {
            this.storage.unbindAdapter(this);
        }

        this.storage = storage;
        this.storage.bindAdapter(this);
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder viewHolder, int i)
    {
        viewHolder.bind(storage.getTagList().get(i));
    }

    @Override
    public int getItemCount()
    {
        return storage != null ? storage.getTagList().size() : 0;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvTag;
        private EditText etTag;
        private View rectColor;
        private Button btnTrackTag;
        private Tag tag;
        private boolean tracked = false;


        public TagViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);

            rectColor = itemView.findViewById(R.id.rectColor);
            btnTrackTag = itemView.findViewById(R.id.btnTrackTag);

            rectColor.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertLayout = inflater.inflate(R.layout.color_picker_layout, null);
                builder.setView(alertLayout);
                AlertDialog dialog = builder.create();

                GridLayout layout = alertLayout.findViewById(R.id.gridLayout);

                for (int i = 0; i < layout.getChildCount(); i++) {
                    View rect = layout.getChildAt(i);
                    rect.setOnClickListener(r -> {
                        DI.getStorage().recolorTag(tag, r.getResources().getResourceName(r.getId()).split("_")[1]);
                        dialog.cancel();
                    });
                }

                TextView tvTitle = alertLayout.findViewById(R.id.tvTitle);
                tvTitle.setText(tag.getName() + "'s color");
                Button btnCancel = alertLayout.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(b -> {
                    dialog.cancel();
                });

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            });

            btnTrackTag.setOnClickListener(v -> {
                DI.getTaskStopwatchService().setTagTracking(tag, !tracked);
                setTracked(!tracked);
            });
        }

        public void bind(Tag tag)
        {
            this.tag = tag;
            setTracked(DI.getTaskStopwatchService().isTagTracked(tag));
            tvTag.setText(tag.getName());
            tvTag.setBackgroundColor(tag.getColorResource(context.getResources()));
            rectColor.setBackgroundColor(tag.getColorResource(context.getResources()));
        }

        public void setTracked(boolean tracked) {
            this.tracked = tracked;
            btnTrackTag.setBackground(context.getResources().getDrawable(tracked ? R.drawable.ic_tracked_24dp : R.drawable.ic_untracked_24dp));
        }
    }
}