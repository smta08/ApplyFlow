package com.applyflow.ui.addedit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
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
import com.applyflow.data.db.EventEntity;
import com.applyflow.ui.applications.ApplicationViewModel;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatusUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditEventFragment extends Fragment {

    private ApplicationViewModel viewModel;
    private NavController navController;

    private int applicationId;
    private int eventId;
    private boolean editing;

    @Nullable
    private String selectedDateTime;

    private Spinner spinnerType;
    private MaterialButton buttonPickDateTime;
    private TextInputEditText inputDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);
        navController = NavHostFragment.findNavController(this);

        Bundle args = getArguments();
        applicationId = args != null ? args.getInt(Constants.ARG_APPLICATION_ID, Constants.NEW_ID) : Constants.NEW_ID;
        eventId = args != null ? args.getInt(Constants.ARG_EVENT_ID, Constants.NEW_ID) : Constants.NEW_ID;
        editing = eventId != Constants.NEW_ID;
        requireActivity().setTitle(editing ? R.string.edit_event_title : R.string.add_event_title);

        spinnerType = view.findViewById(R.id.spinner_type);
        buttonPickDateTime = view.findViewById(R.id.button_pick_date_time);
        inputDescription = view.findViewById(R.id.input_description);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, StatusUtils.allEventTypeLabels(requireContext()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        buttonPickDateTime.setOnClickListener(v -> showDateTimePicker());

        MaterialButton buttonSave = view.findViewById(R.id.button_save);
        MaterialButton buttonCancel = view.findViewById(R.id.button_cancel);
        buttonSave.setOnClickListener(v -> save(view));
        buttonCancel.setOnClickListener(v -> navController.popBackStack());

        if (editing) {
            viewModel.loadEvent(eventId, this::prefill);
        }
    }

    private void prefill(@Nullable EventEntity event) {
        if (event == null || !isAdded()) {
            return;
        }
        spinnerType.setSelection(StatusUtils.eventTypeIndex(event.type));
        inputDescription.setText(event.description);
        selectedDateTime = event.dateTime;
        updateDateTimeButton();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        Calendar existing = DateUtils.parseDateTime(selectedDateTime);
        if (existing != null) {
            calendar = existing;
        }
        final Calendar working = calendar;
        DatePickerDialog dateDialog = new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
            working.set(Calendar.YEAR, year);
            working.set(Calendar.MONTH, month);
            working.set(Calendar.DAY_OF_MONTH, day);
            TimePickerDialog timeDialog = new TimePickerDialog(requireContext(), (tp, hour, minute) -> {
                working.set(Calendar.HOUR_OF_DAY, hour);
                working.set(Calendar.MINUTE, minute);
                selectedDateTime = DateUtils.formatDateTime(working);
                updateDateTimeButton();
            }, working.get(Calendar.HOUR_OF_DAY), working.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(requireContext()));
            timeDialog.show();
        }, working.get(Calendar.YEAR), working.get(Calendar.MONTH), working.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void updateDateTimeButton() {
        String display = DateUtils.displayDateTime(selectedDateTime);
        buttonPickDateTime.setText(display != null ? display : getString(R.string.pick_date_time));
    }

    private void save(View root) {
        if (TextUtils.isEmpty(selectedDateTime)) {
            Snackbar.make(root, R.string.error_date_time_required, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String type = Constants.EVENT_TYPES[spinnerType.getSelectedItemPosition()];
        String description = emptyToNull(textOf(inputDescription));

        viewModel.saveEvent(eventId, applicationId, type, selectedDateTime, description);

        boolean inPast = DateUtils.dateTimeToMillis(selectedDateTime) < System.currentTimeMillis();
        if (inPast) {
            Snackbar.make(root, R.string.warn_event_in_past, Snackbar.LENGTH_SHORT).show();
            root.postDelayed(() -> {
                if (isAdded()) {
                    navController.popBackStack();
                }
            }, 1200);
        } else {
            navController.popBackStack();
        }
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    @Nullable
    private static String emptyToNull(String value) {
        return TextUtils.isEmpty(value) ? null : value;
    }
}
