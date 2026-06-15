package com.applyflow.ui.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.util.Constants;
import com.applyflow.util.StatusUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class ApplicationListFragment extends Fragment {

    private ApplicationViewModel viewModel;
    private ApplicationAdapter adapter;
    private View emptyState;

    private final Map<Integer, String> chipStatus = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_application_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);

        NavController navController = NavHostFragment.findNavController(this);

        RecyclerView recycler = view.findViewById(R.id.recycler_applications);
        emptyState = view.findViewById(R.id.empty_state);

        adapter = new ApplicationAdapter(applicationId -> {
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
            navController.navigate(R.id.action_applicationList_to_applicationDetail, args);
        });
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab_add_application);
        fab.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_APPLICATION_ID, Constants.NEW_ID);
            navController.navigate(R.id.action_applicationList_to_addEditApplication, args);
        });

        setupFilterChips(view);
        setupMenu();

        viewModel.getApplications().observe(getViewLifecycleOwner(), applications -> {
            adapter.submitList(applications);
            boolean empty = applications == null || applications.isEmpty();
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_application_list, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();
                if (searchView != null) {
                    searchView.setQueryHint(getString(R.string.search_hint));
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            viewModel.setQuery(newText);
                            return true;
                        }
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_sort) {
                    showSortDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showSortDialog() {
        String[] options = {
                getString(R.string.sort_recent),
                getString(R.string.sort_date_applied),
                getString(R.string.sort_company),
                getString(R.string.sort_status)
        };
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sort_title)
                .setSingleChoiceItems(options, viewModel.getSortMode(), (dialog, which) -> {
                    viewModel.setSortMode(which);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void setupFilterChips(View root) {
        ChipGroup group = root.findViewById(R.id.chip_group_filter);

        addChip(group, getString(R.string.filter_all), null, true);
        for (String status : Constants.STATUSES) {
            addChip(group, StatusUtils.statusLabel(requireContext(), status), status, false);
        }

        group.setOnCheckedStateChangeListener((g, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                viewModel.setFilter(chipStatus.get(checkedIds.get(0)));
            }
        });
    }

    private void addChip(ChipGroup group, String label, @Nullable String status, boolean checked) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_filter_chip, group, false);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setChecked(checked);
        chipStatus.put(chip.getId(), status);
        group.addView(chip);
    }
}
