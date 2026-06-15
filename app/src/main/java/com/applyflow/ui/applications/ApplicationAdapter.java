package com.applyflow.ui.applications;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.color.MaterialColors;

import java.util.Locale;

public class ApplicationAdapter extends ListAdapter<ApplicationEntity, ApplicationAdapter.ViewHolder> {

    public interface OnApplicationClickListener {
        void onApplicationClick(int applicationId);
    }

    private final OnApplicationClickListener listener;

    public ApplicationAdapter(OnApplicationClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ApplicationEntity> DIFF =
            new DiffUtil.ItemCallback<ApplicationEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ApplicationEntity a, @NonNull ApplicationEntity b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull ApplicationEntity a, @NonNull ApplicationEntity b) {
                    return a.company.equals(b.company)
                            && a.role.equals(b.role)
                            && a.status.equals(b.status)
                            && equalsNullable(a.dateApplied, b.dateApplied);
                }

                private boolean equalsNullable(String x, String y) {
                    return x == null ? y == null : x.equals(y);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView monogram;
        private final TextView company;
        private final TextView role;
        private final TextView date;
        private final Chip status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            monogram = itemView.findViewById(R.id.text_monogram);
            company = itemView.findViewById(R.id.text_company);
            role = itemView.findViewById(R.id.text_role);
            date = itemView.findViewById(R.id.text_date);
            status = itemView.findViewById(R.id.chip_status);
            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onApplicationClick(getItem(pos).id);
                }
            });
        }

        void bind(ApplicationEntity item) {
            company.setText(item.company);
            role.setText(item.role);

            monogram.setText(monogramOf(item.company));
            monogram.setBackgroundTintList(ColorStateList.valueOf(MaterialColors.getColor(
                    monogram, com.google.android.material.R.attr.colorPrimaryContainer)));
            monogram.setTextColor(MaterialColors.getColor(
                    monogram, com.google.android.material.R.attr.colorOnPrimaryContainer));

            status.setText(StatusUtils.statusLabel(itemView.getContext(), item.status));
            status.setChipBackgroundColor(ColorStateList.valueOf(
                    StatusUtils.statusColor(itemView.getContext(), item.status)));
            status.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_on_chip));

            String when = DateUtils.displayDate(item.dateApplied);
            date.setText(when != null ? when : "");
        }

        private String monogramOf(String company) {
            if (company == null || company.trim().isEmpty()) {
                return "?";
            }
            return company.trim().substring(0, 1).toUpperCase(Locale.getDefault());
        }
    }
}
