package com.applyflow.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.data.dao.EventDao;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;

public class UpcomingEventAdapter
        extends ListAdapter<EventDao.UpcomingEvent, UpcomingEventAdapter.ViewHolder> {

    public interface OnUpcomingClickListener {
        void onUpcomingClick(int applicationId);
    }

    private final OnUpcomingClickListener listener;

    public UpcomingEventAdapter(OnUpcomingClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<EventDao.UpcomingEvent> DIFF =
            new DiffUtil.ItemCallback<EventDao.UpcomingEvent>() {
                @Override
                public boolean areItemsTheSame(@NonNull EventDao.UpcomingEvent a,
                                               @NonNull EventDao.UpcomingEvent b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull EventDao.UpcomingEvent a,
                                                  @NonNull EventDao.UpcomingEvent b) {
                    return a.company.equals(b.company)
                            && a.type.equals(b.type)
                            && a.dateTime.equals(b.dateTime);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView company;
        private final TextView detail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            company = itemView.findViewById(R.id.text_upcoming_company);
            detail = itemView.findViewById(R.id.text_upcoming_detail);
            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onUpcomingClick(getItem(pos).applicationId);
                }
            });
        }

        void bind(EventDao.UpcomingEvent item) {
            company.setText(item.company);
            String typeLabel = StatusUtils.eventTypeLabel(itemView.getContext(), item.type);
            String when = DateUtils.displayDateTime(item.dateTime);
            detail.setText(typeLabel + " • " + (when != null ? when : item.dateTime));
        }
    }
}
