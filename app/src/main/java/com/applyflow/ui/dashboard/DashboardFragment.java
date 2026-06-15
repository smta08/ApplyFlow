package com.applyflow.ui.dashboard;

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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.ui.applications.ApplicationAdapter;
import com.applyflow.util.Constants;
import com.applyflow.util.StatusUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private UpcomingEventAdapter upcomingAdapter;
    private ApplicationAdapter needsAttentionAdapter;
    private NavController navController;

    private final Map<String, TextView> countViews = new LinkedHashMap<>();

    private TextView textTotal;
    private LinearLayout pipelineBar;
    private RecyclerView recyclerUpcoming;
    private TextView textNoUpcoming;
    private View cardNeedsAttention;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.dashboard_title);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        navController = NavHostFragment.findNavController(this);

        textTotal = view.findViewById(R.id.text_total);
        pipelineBar = view.findViewById(R.id.pipeline_bar);
        recyclerUpcoming = view.findViewById(R.id.recycler_upcoming);
        textNoUpcoming = view.findViewById(R.id.text_no_upcoming);
        cardNeedsAttention = view.findViewById(R.id.card_needs_attention);

        bindStatusCards(view);

        upcomingAdapter = new UpcomingEventAdapter(this::openApplication);
        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerUpcoming.setAdapter(upcomingAdapter);

        RecyclerView recyclerNeedsAttention = view.findViewById(R.id.recycler_needs_attention);
        needsAttentionAdapter = new ApplicationAdapter(this::openApplication);
        recyclerNeedsAttention.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerNeedsAttention.setAdapter(needsAttentionAdapter);

        view.findViewById(R.id.button_view_all).setOnClickListener(v ->
                navController.navigate(R.id.action_dashboard_to_applicationList));

        observe();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }

    private void openApplication(int applicationId) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
        navController.navigate(R.id.action_dashboard_to_applicationDetail, args);
    }

    private void bindStatusCards(View root) {
        int[] cardIds = {
                R.id.card_applied, R.id.card_phone_screen, R.id.card_interview,
                R.id.card_final_round, R.id.card_offer_received, R.id.card_rejected
        };
        for (int i = 0; i < Constants.STATUSES.length; i++) {
            String status = Constants.STATUSES[i];
            View card = root.findViewById(cardIds[i]);
            TextView count = card.findViewById(R.id.text_count);
            TextView label = card.findViewById(R.id.text_label);
            View accent = card.findViewById(R.id.accent);
            label.setText(StatusUtils.statusLabel(requireContext(), status));
            accent.setBackgroundTintList(ColorStateList.valueOf(
                    StatusUtils.statusColor(requireContext(), status)));
            countViews.put(status, count);
        }
    }

    private void observe() {
        viewModel.getStatusCounts().observe(getViewLifecycleOwner(), counts -> {
            if (counts == null) {
                return;
            }
            int total = 0;
            for (Map.Entry<String, TextView> entry : countViews.entrySet()) {
                Integer c = counts.get(entry.getKey());
                int value = c == null ? 0 : c;
                entry.getValue().setText(String.valueOf(value));
                total += value;
            }
            textTotal.setText(String.valueOf(total));
            buildPipeline(counts, total);
        });

        viewModel.getUpcomingEvents().observe(getViewLifecycleOwner(), events -> {
            upcomingAdapter.submitList(events);
            boolean empty = events == null || events.isEmpty();
            textNoUpcoming.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerUpcoming.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.getNeedsAttention().observe(getViewLifecycleOwner(), applications -> {
            needsAttentionAdapter.submitList(applications);
            boolean empty = applications == null || applications.isEmpty();
            cardNeedsAttention.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void buildPipeline(Map<String, Integer> counts, int total) {
        pipelineBar.removeAllViews();
        if (total == 0) {
            return;
        }
        for (String status : Constants.STATUSES) {
            Integer c = counts.get(status);
            int value = c == null ? 0 : c;
            if (value == 0) {
                continue;
            }
            View segment = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, value);
            segment.setLayoutParams(params);
            segment.setBackgroundColor(StatusUtils.statusColor(requireContext(), status));
            pipelineBar.addView(segment);
        }
    }
}
