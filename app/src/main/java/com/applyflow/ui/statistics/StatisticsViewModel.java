package com.applyflow.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.applyflow.data.repository.ApplicationRepository;
import com.applyflow.util.StatsCalculator;

public class StatisticsViewModel extends AndroidViewModel {

    private final LiveData<StatsCalculator.Stats> stats;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        ApplicationRepository repository = new ApplicationRepository(application);
        stats = Transformations.map(repository.getAll(), StatsCalculator::compute);
    }

    public LiveData<StatsCalculator.Stats> getStats() {
        return stats;
    }
}
