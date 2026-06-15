package com.applyflow.ui.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applyflow.R;
import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.data.db.EventEntity;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class ApplicationDetailFragment extends Fragment {

    private ApplicationViewModel viewModel;
    private EventAdapter eventAdapter;
    private NavController navController;

    private int applicationId;
    private ApplicationEntity currentApp;
    private List<EventEntity> currentEvents;

    private TextView textCompany;
    private TextView textRole;
    private TextView textLink;
    private TextView textDateApplied;
    private TextView textNotes;
    private Spinner spinnerStatus;
    private TextView textNoEvents;
    private RecyclerView recyclerEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_application_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);
        navController = NavHostFragment.findNavController(this);
        applicationId = getArguments() != null
                ? getArguments().getInt(Constants.ARG_APPLICATION_ID, Constants.NEW_ID)
                : Constants.NEW_ID;

        textCompany = view.findViewById(R.id.text_company);
        textRole = view.findViewById(R.id.text_role);
        textLink = view.findViewById(R.id.text_link);
        textDateApplied = view.findViewById(R.id.text_date_applied);
        textNotes = view.findViewById(R.id.text_notes);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        textNoEvents = view.findViewById(R.id.text_no_events);
        recyclerEvents = view.findViewById(R.id.recycler_events);

        setupStatusSpinner();
        setupEvents();
        setupFab(view);
        setupMenu();
        observe();
    }

    private void setupStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, StatusUtils.allStatusLabels(requireContext()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selected = Constants.STATUSES[position];
                if (currentApp != null && !selected.equals(currentApp.status)) {
                    currentApp.status = selected;
                    viewModel.updateStatus(applicationId, selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupEvents() {
        eventAdapter = new EventAdapter(new EventAdapter.OnEventActionListener() {
            @Override
            public void onEditEvent(EventEntity event) {
                Bundle args = new Bundle();
                args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
                args.putInt(Constants.ARG_EVENT_ID, event.id);
                navController.navigate(R.id.action_applicationDetail_to_addEditEvent, args);
            }

            @Override
            public void onDeleteEvent(EventEntity event) {
                confirmDeleteEvent(event);
            }
        });
        recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerEvents.setAdapter(eventAdapter);
    }

    private void setupFab(View view) {
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab_add_event);
        fab.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
            args.putInt(Constants.ARG_EVENT_ID, Constants.NEW_ID);
            navController.navigate(R.id.action_applicationDetail_to_addEditEvent, args);
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_detail, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_edit) {
                    Bundle args = new Bundle();
                    args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
                    navController.navigate(R.id.action_applicationDetail_to_addEditApplication, args);
                    return true;
                } else if (id == R.id.action_delete) {
                    confirmDeleteApplication();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void observe() {
        viewModel.getApplication(applicationId).observe(getViewLifecycleOwner(), app -> {
            if (app == null) {
                return;
            }
            currentApp = app;
            bindApplication(app);
        });

        viewModel.getEvents(applicationId).observe(getViewLifecycleOwner(), events -> {
            currentEvents = events;
            eventAdapter.submitList(events);
            boolean empty = events == null || events.isEmpty();
            textNoEvents.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerEvents.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void bindApplication(ApplicationEntity app) {
        textCompany.setText(app.company);
        textRole.setText(app.role);

        if (app.link == null || app.link.trim().isEmpty()) {
            textLink.setText(R.string.not_set);
        } else {
            textLink.setText(app.link);
        }

        String dateApplied = DateUtils.displayDate(app.dateApplied);
        textDateApplied.setText(dateApplied != null ? dateApplied : getString(R.string.not_set));

        if (app.notes == null || app.notes.trim().isEmpty()) {
            textNotes.setText(R.string.not_set);
        } else {
            textNotes.setText(app.notes);
        }

        spinnerStatus.setSelection(StatusUtils.statusIndex(app.status));
    }

    private void confirmDeleteApplication() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete_application_title)
                .setMessage(R.string.confirm_delete_application_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (currentApp != null) {
                        viewModel.deleteApplication(currentApp, currentEvents);
                        navController.popBackStack();
                    }
                })
                .show();
    }

    private void confirmDeleteEvent(EventEntity event) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete_event_title)
                .setMessage(R.string.confirm_delete_event_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteEvent(event))
                .show();
    }
}
