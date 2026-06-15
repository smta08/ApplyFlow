package com.applyflow.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.applyflow.data.dao.EventDao;
import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.data.repository.ApplicationRepository;
import com.applyflow.data.repository.EventRepository;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;
import com.applyflow.util.ThemeManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends AndroidViewModel {

    private final ApplicationRepository applicationRepository;

    private final LiveData<Map<String, Integer>> statusCounts;
    private final LiveData<List<ApplicationEntity>> applications;

    private final MutableLiveData<String[]> window = new MutableLiveData<>();
    private final LiveData<List<EventDao.UpcomingEvent>> upcomingEvents;

    private final MutableLiveData<String> followUpCutoff = new MutableLiveData<>();
    private final LiveData<List<ApplicationEntity>> needsAttention;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        applicationRepository = new ApplicationRepository(application);
        EventRepository eventRepository = new EventRepository(application);

        applications = applicationRepository.getAll();
        statusCounts = Transformations.map(applications, DashboardViewModel::countByStatus);

        upcomingEvents = Transformations.switchMap(window,
                w -> eventRepository.getUpcoming(w[0], w[1]));

        needsAttention = Transformations.switchMap(followUpCutoff,
                cutoff -> applicationRepository.getNeedsFollowUp(Constants.STATUS_APPLIED, cutoff));

        refresh();
    }

    public LiveData<Map<String, Integer>> getStatusCounts() {
        return statusCounts;
    }

    public LiveData<List<ApplicationEntity>> getApplications() {
        return applications;
    }

    public LiveData<List<EventDao.UpcomingEvent>> getUpcomingEvents() {
        return upcomingEvents;
    }

    public LiveData<List<ApplicationEntity>> getNeedsAttention() {
        return needsAttention;
    }

    public void refresh() {
        window.setValue(new String[]{
                DateUtils.startOfTodayDateTime(),
                DateUtils.dateTimePlusDaysEndOfDay(Constants.UPCOMING_WINDOW_DAYS)
        });
        int days = ThemeManager.getFollowUpDays(getApplication());
        followUpCutoff.setValue(DateUtils.dateMinusDays(days));
    }

    private static Map<String, Integer> countByStatus(List<ApplicationEntity> applications) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String status : Constants.STATUSES) {
            counts.put(status, 0);
        }
        if (applications != null) {
            for (ApplicationEntity app : applications) {
                Integer current = counts.get(app.status);
                counts.put(app.status, (current == null ? 0 : current) + 1);
            }
        }
        return counts;
    }
}
