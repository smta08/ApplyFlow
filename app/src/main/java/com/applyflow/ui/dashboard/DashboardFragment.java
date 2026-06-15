package com.applyflow.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.applyflow.util.Constants;
import com.applyflow.util.StatusUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private UpcomingEventAdapter adapter;
    private final Map<String, TextView> countViews = new LinkedHashMap<>();

    private RecyclerView recyclerUpcoming;
    private TextView textNoUpcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        bindStatusCards(view);

        recyclerUpcoming = view.findViewById(R.id.recycler_upcoming);
        textNoUpcoming = view.findViewById(R.id.text_no_upcoming);

        NavController navController = NavHostFragment.findNavController(this);

        adapter = new UpcomingEventAdapter(applicationId -> {
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
            navController.navigate(R.id.action_dashboard_to_applicationDetail, args);
        });
        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerUpcoming.setAdapter(adapter);

        view.findViewById(R.id.button_view_all).setOnClickListener(v ->
                navController.navigate(R.id.action_dashboard_to_applicationList));

        viewModel.getStatusCounts().observe(getViewLifecycleOwner(), counts -> {
            if (counts == null) {
                return;
            }
            for (Map.Entry<String, TextView> entry : countViews.entrySet()) {
                Integer c = counts.get(entry.getKey());
                entry.getValue().setText(String.valueOf(c == null ? 0 : c));
            }
        });

        viewModel.getUpcomingEvents().observe(getViewLifecycleOwner(), events -> {
            adapter.submitList(events);
            boolean empty = events == null || events.isEmpty();
            textNoUpcoming.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerUpcoming.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
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
            label.setText(StatusUtils.statusLabel(requireContext(), status));
            countViews.put(status, count);
        }
    }
}
