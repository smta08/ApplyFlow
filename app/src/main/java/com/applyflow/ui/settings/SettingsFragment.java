package com.applyflow.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.applyflow.R;
import com.applyflow.data.repository.ApplicationRepository;
import com.applyflow.util.Constants;
import com.applyflow.util.CsvExporter;
import com.applyflow.util.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class SettingsFragment extends Fragment {

    private ApplicationRepository applicationRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.settings_title);
        applicationRepository = new ApplicationRepository(requireContext());

        setupThemeToggle(view);
        setupFollowUpToggle(view);

        view.findViewById(R.id.button_export_csv).setOnClickListener(v -> exportCsv(view));
    }

    private void setupThemeToggle(View view) {
        MaterialButtonToggleGroup group = view.findViewById(R.id.theme_toggle);
        group.check(buttonIdForMode(ThemeManager.getSavedMode(requireContext())));
        group.addOnButtonCheckedListener((g, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            int mode = modeForButtonId(checkedId);
            if (mode != ThemeManager.getSavedMode(requireContext())) {
                ThemeManager.setMode(requireContext(), mode);
                requireActivity().recreate();
            }
        });
    }

    private void setupFollowUpToggle(View view) {
        MaterialButtonToggleGroup group = view.findViewById(R.id.followup_toggle);
        group.check(buttonIdForDays(ThemeManager.getFollowUpDays(requireContext())));
        group.addOnButtonCheckedListener((g, checkedId, isChecked) -> {
            if (isChecked) {
                ThemeManager.setFollowUpDays(requireContext(), daysForButtonId(checkedId));
            }
        });
    }

    private int buttonIdForMode(int mode) {
        switch (mode) {
            case Constants.THEME_LIGHT:
                return R.id.theme_light;
            case Constants.THEME_DARK:
                return R.id.theme_dark;
            default:
                return R.id.theme_system;
        }
    }

    private int modeForButtonId(int id) {
        if (id == R.id.theme_light) {
            return Constants.THEME_LIGHT;
        }
        if (id == R.id.theme_dark) {
            return Constants.THEME_DARK;
        }
        return Constants.THEME_SYSTEM;
    }

    private int buttonIdForDays(int days) {
        if (days == 3) {
            return R.id.followup_3;
        }
        if (days == 14) {
            return R.id.followup_14;
        }
        return R.id.followup_7;
    }

    private int daysForButtonId(int id) {
        if (id == R.id.followup_3) {
            return 3;
        }
        if (id == R.id.followup_14) {
            return 14;
        }
        return 7;
    }

    private void exportCsv(View root) {
        applicationRepository.loadAll(applications -> {
            if (applications == null || applications.isEmpty()) {
                Snackbar.make(root, R.string.export_empty, Snackbar.LENGTH_SHORT).show();
                return;
            }
            try {
                File file = CsvExporter.export(requireContext(), applications);
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider", file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/csv");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, getString(R.string.export_csv)));
            } catch (Exception e) {
                Snackbar.make(root, e.getMessage() != null ? e.getMessage() : "Export failed",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
