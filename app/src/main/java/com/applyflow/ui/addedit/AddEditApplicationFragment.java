package com.applyflow.ui.addedit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.applyflow.R;
import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.ui.applications.ApplicationViewModel;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditApplicationFragment extends Fragment {

    private ApplicationViewModel viewModel;
    private NavController navController;

    private int applicationId;
    private boolean editing;
    private ApplicationEntity editingApp;

    @Nullable
    private String selectedDateApplied;

    private TextInputEditText inputCompany;
    private TextInputEditText inputRole;
    private TextInputEditText inputLink;
    private TextInputEditText inputNotes;
    private Spinner spinnerStatus;
    private MaterialButton buttonPickDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);
        navController = NavHostFragment.findNavController(this);

        applicationId = getArguments() != null
                ? getArguments().getInt(Constants.ARG_APPLICATION_ID, Constants.NEW_ID)
                : Constants.NEW_ID;
        editing = applicationId != Constants.NEW_ID;
        requireActivity().setTitle(editing
                ? R.string.edit_application_title : R.string.add_application_title);

        inputCompany = view.findViewById(R.id.input_company);
        inputRole = view.findViewById(R.id.input_role);
        inputLink = view.findViewById(R.id.input_link);
        inputNotes = view.findViewById(R.id.input_notes);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        buttonPickDate = view.findViewById(R.id.button_pick_date);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, StatusUtils.allStatusLabels(requireContext()));
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        buttonPickDate.setOnClickListener(v -> showDatePicker());

        MaterialButton buttonSave = view.findViewById(R.id.button_save);
        MaterialButton buttonCancel = view.findViewById(R.id.button_cancel);
        buttonSave.setOnClickListener(v -> save(view));
        buttonCancel.setOnClickListener(v -> navController.popBackStack());

        if (editing) {
            viewModel.loadApplication(applicationId, this::prefill);
        }
    }

    private void prefill(@Nullable ApplicationEntity app) {
        if (app == null || !isAdded()) {
            return;
        }
        editingApp = app;
        inputCompany.setText(app.company);
        inputRole.setText(app.role);
        inputLink.setText(app.link);
        inputNotes.setText(app.notes);
        spinnerStatus.setSelection(StatusUtils.statusIndex(app.status));
        selectedDateApplied = app.dateApplied;
        updateDateButton();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Calendar existing = DateUtils.parseDate(selectedDateApplied);
        if (existing != null) {
            calendar = existing;
        }
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(year, month, day);
            selectedDateApplied = DateUtils.formatDate(chosen);
            updateDateButton();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateDateButton() {
        String display = DateUtils.displayDate(selectedDateApplied);
        buttonPickDate.setText(display != null ? display : getString(R.string.pick_date));
    }

    private void save(View root) {
        String company = textOf(inputCompany);
        String role = textOf(inputRole);

        if (TextUtils.isEmpty(company)) {
            Snackbar.make(root, R.string.error_company_required, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(role)) {
            Snackbar.make(root, R.string.error_role_required, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String link = emptyToNull(textOf(inputLink));
        String notes = emptyToNull(textOf(inputNotes));
        String status = Constants.STATUSES[spinnerStatus.getSelectedItemPosition()];

        if (editing && editingApp != null) {
            editingApp.company = company;
            editingApp.role = role;
            editingApp.link = link;
            editingApp.status = status;
            editingApp.notes = notes;
            editingApp.dateApplied = selectedDateApplied;
            viewModel.updateApplication(editingApp);
        } else {
            viewModel.saveNewApplication(company, role, link, status, notes, selectedDateApplied);
        }
        navController.popBackStack();
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    @Nullable
    private static String emptyToNull(String value) {
        return TextUtils.isEmpty(value) ? null : value;
    }
}
