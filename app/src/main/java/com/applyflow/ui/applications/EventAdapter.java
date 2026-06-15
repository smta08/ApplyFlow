package com.applyflow.ui.applications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.data.db.EventEntity;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;

public class EventAdapter extends ListAdapter<EventEntity, EventAdapter.ViewHolder> {

    public interface OnEventActionListener {
        void onEditEvent(EventEntity event);

        void onDeleteEvent(EventEntity event);
    }

    private final OnEventActionListener listener;

    public EventAdapter(OnEventActionListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<EventEntity> DIFF =
            new DiffUtil.ItemCallback<EventEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull EventEntity a, @NonNull EventEntity b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull EventEntity a, @NonNull EventEntity b) {
                    return a.type.equals(b.type)
                            && a.dateTime.equals(b.dateTime)
                            && equalsNullable(a.description, b.description);
                }

                private boolean equalsNullable(String x, String y) {
                    return x == null ? y == null : x.equals(y);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView type;
        private final TextView dateTime;
        private final TextView description;
        private final ImageButton edit;
        private final ImageButton delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.text_event_type);
            dateTime = itemView.findViewById(R.id.text_event_datetime);
            description = itemView.findViewById(R.id.text_event_description);
            edit = itemView.findViewById(R.id.button_edit_event);
            delete = itemView.findViewById(R.id.button_delete_event);

            edit.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onEditEvent(getItem(pos));
                }
            });
            delete.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDeleteEvent(getItem(pos));
                }
            });
        }

        void bind(EventEntity item) {
            type.setText(StatusUtils.eventTypeLabel(itemView.getContext(), item.type));
            String when = DateUtils.displayDateTime(item.dateTime);
            dateTime.setText(when != null ? when : item.dateTime);
            if (item.description == null || item.description.trim().isEmpty()) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(item.description);
            }
        }
    }
}
