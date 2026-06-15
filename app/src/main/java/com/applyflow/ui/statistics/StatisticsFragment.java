package com.applyflow.ui.statistics;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.applyflow.R;
import com.applyflow.util.Constants;
import com.applyflow.util.StatsCalculator;
import com.applyflow.util.StatusUtils;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel viewModel;

    private View content;
    private TextView textEmpty;
    private TextView textTotal;
    private TextView textResponseRate;
    private TextView textInterviewRate;
    private TextView textOfferRate;
    private TextView textAvgDays;
    private LinearLayout containerBreakdown;
    private LinearLayout containerOverTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.statistics_title);
        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        content = view.findViewById(R.id.content);
        textEmpty = view.findViewById(R.id.text_empty);
        textTotal = view.findViewById(R.id.text_total);
        textResponseRate = view.findViewById(R.id.text_response_rate);
        textInterviewRate = view.findViewById(R.id.text_interview_rate);
        textOfferRate = view.findViewById(R.id.text_offer_rate);
        textAvgDays = view.findViewById(R.id.text_avg_days);
        containerBreakdown = view.findViewById(R.id.container_breakdown);
        containerOverTime = view.findViewById(R.id.container_over_time);

        viewModel.getStats().observe(getViewLifecycleOwner(), this::bind);
    }

    private void bind(StatsCalculator.Stats stats) {
        boolean empty = stats == null || stats.total == 0;
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        content.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) {
            return;
        }

        textTotal.setText(String.valueOf(stats.total));
        textResponseRate.setText(percent(stats.responseRate));
        textInterviewRate.setText(percent(stats.interviewRate));
        textOfferRate.setText(percent(stats.offerRate));
        textAvgDays.setText(stats.avgDaysSinceApplied < 0
                ? getString(R.string.not_set)
                : String.valueOf(stats.avgDaysSinceApplied));

        buildBreakdown(stats);
        buildOverTime(stats);
    }

    private void buildBreakdown(StatsCalculator.Stats stats) {
        containerBreakdown.removeAllViews();
        for (String status : Constants.STATUSES) {
            Integer countObj = stats.countByStatus.get(status);
            int count = countObj == null ? 0 : countObj;
            int pct = stats.total == 0 ? 0 : Math.round(100f * count / stats.total);
            int color = StatusUtils.statusColor(requireContext(), status);
            String value = count + " (" + pct + "%)";
            addRow(containerBreakdown, StatusUtils.statusLabel(requireContext(), status),
                    value, pct, color, true);
        }
    }

    private void buildOverTime(StatsCalculator.Stats stats) {
        containerOverTime.removeAllViews();
        int max = 0;
        for (StatsCalculator.MonthCount mc : stats.overTime) {
            max = Math.max(max, mc.count);
        }
        int primary = MaterialColors.getColor(containerOverTime,
                com.google.android.material.R.attr.colorPrimary);
        for (StatsCalculator.MonthCount mc : stats.overTime) {
            int pct = max == 0 ? 0 : Math.round(100f * mc.count / max);
            addRow(containerOverTime, mc.label, String.valueOf(mc.count), pct, primary, false);
        }
    }

    private void addRow(LinearLayout container, String label, String value,
                        int progress, int color, boolean showDot) {
        View row = getLayoutInflater().inflate(R.layout.item_status_breakdown, container, false);
        View dot = row.findViewById(R.id.dot);
        dot.setVisibility(showDot ? View.VISIBLE : View.GONE);
        if (showDot) {
            dot.setBackgroundTintList(ColorStateList.valueOf(color));
        }
        ((TextView) row.findViewById(R.id.text_label)).setText(label);
        ((TextView) row.findViewById(R.id.text_value)).setText(value);
        LinearProgressIndicator bar = row.findViewById(R.id.progress);
        bar.setIndicatorColor(color);
        bar.setProgressCompat(progress, false);
        container.addView(row);
    }

    private String percent(int value) {
        return String.format(Locale.getDefault(), "%d%%", value);
    }
}
