package com.applyflow.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.applyflow.data.dao.EventDao;
import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.data.repository.ApplicationRepository;
import com.applyflow.data.repository.EventRepository;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends AndroidViewModel {

    private final LiveData<Map<String, Integer>> statusCounts;
    private final LiveData<List<EventDao.UpcomingEvent>> upcomingEvents;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        ApplicationRepository applicationRepository = new ApplicationRepository(application);
        EventRepository eventRepository = new EventRepository(application);

        statusCounts = Transformations.map(applicationRepository.getAll(), DashboardViewModel::countByStatus);

        String startIso = DateUtils.nowDateTime();
        String endIso = DateUtils.dateTimePlusDays(Constants.UPCOMING_WINDOW_DAYS);
        upcomingEvents = eventRepository.getUpcoming(startIso, endIso);
    }

    public LiveData<Map<String, Integer>> getStatusCounts() {
        return statusCounts;
    }

    public LiveData<List<EventDao.UpcomingEvent>> getUpcomingEvents() {
        return upcomingEvents;
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
