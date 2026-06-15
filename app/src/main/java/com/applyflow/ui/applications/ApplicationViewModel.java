package com.applyflow.ui.applications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.data.db.EventEntity;
import com.applyflow.data.repository.ApplicationRepository;
import com.applyflow.data.repository.EventRepository;
import com.applyflow.notifications.NotificationHelper;
import com.applyflow.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ApplicationViewModel extends AndroidViewModel {

    private final ApplicationRepository applicationRepository;
    private final EventRepository eventRepository;

    private final MutableLiveData<String> filter = new MutableLiveData<>(null);
    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private int sortMode = Constants.SORT_RECENT;

    private LiveData<List<ApplicationEntity>> base;
    private final MediatorLiveData<List<ApplicationEntity>> applications = new MediatorLiveData<>();

    private LiveData<ApplicationEntity> applicationLive;
    private int applicationLiveId = Constants.NEW_ID;
    private LiveData<List<EventEntity>> eventsLive;
    private int eventsLiveAppId = Constants.NEW_ID;

    public ApplicationViewModel(@NonNull Application application) {
        super(application);
        applicationRepository = new ApplicationRepository(application);
        eventRepository = new EventRepository(application);

        base = applicationRepository.getSorted(sortMode);
        applications.addSource(base, list -> recompute());
        applications.addSource(filter, f -> recompute());
        applications.addSource(query, q -> recompute());
    }

    public LiveData<List<ApplicationEntity>> getApplications() {
        return applications;
    }

    public void setFilter(@Nullable String status) {
        filter.setValue(status);
    }

    public void setQuery(@Nullable String text) {
        query.setValue(text == null ? "" : text);
    }

    public void setSortMode(int mode) {
        if (mode == sortMode) {
            return;
        }
        applications.removeSource(base);
        sortMode = mode;
        base = applicationRepository.getSorted(sortMode);
        applications.addSource(base, list -> recompute());
    }

    public int getSortMode() {
        return sortMode;
    }

    private void recompute() {
        applications.setValue(applyFilters(base.getValue()));
    }

    private List<ApplicationEntity> applyFilters(List<ApplicationEntity> source) {
        List<ApplicationEntity> result = new ArrayList<>();
        if (source == null) {
            return result;
        }
        String status = filter.getValue();
        String q = query.getValue();
        String needle = q == null ? "" : q.trim().toLowerCase(Locale.getDefault());
        for (ApplicationEntity app : source) {
            if (status != null && !status.equals(app.status)) {
                continue;
            }
            if (!needle.isEmpty()) {
                String haystack = (app.company + " " + app.role).toLowerCase(Locale.getDefault());
                if (!haystack.contains(needle)) {
                    continue;
                }
            }
            result.add(app);
        }
        return result;
    }

    public LiveData<ApplicationEntity> getApplication(int id) {
        if (applicationLive == null || applicationLiveId != id) {
            applicationLive = applicationRepository.getById(id);
            applicationLiveId = id;
        }
        return applicationLive;
    }

    public LiveData<List<EventEntity>> getEvents(int applicationId) {
        if (eventsLive == null || eventsLiveAppId != applicationId) {
            eventsLive = eventRepository.getForApplication(applicationId);
            eventsLiveAppId = applicationId;
        }
        return eventsLive;
    }

    public void updateStatus(int id, String status) {
        applicationRepository.updateStatus(id, status);
    }

    public void loadApplication(int id, ApplicationRepository.OnLoadedCallback<ApplicationEntity> callback) {
        applicationRepository.loadById(id, callback);
    }

    public void insertApplication(ApplicationEntity entity) {
        applicationRepository.insert(entity);
    }

    public void updateApplication(ApplicationEntity entity) {
        applicationRepository.update(entity);
    }

    public void deleteApplication(ApplicationEntity application, @Nullable List<EventEntity> events) {
        if (events != null) {
            for (EventEntity event : events) {
                NotificationHelper.cancelEventReminder(getApplication(), event.id);
            }
        }
        applicationRepository.delete(application);
    }

    public void loadEvent(int id, EventRepository.OnLoadedCallback<EventEntity> callback) {
        eventRepository.loadById(id, callback);
    }

    public void saveEvent(int eventId, int applicationId, String type,
                          String dateTime, @Nullable String description) {
        if (eventId == Constants.NEW_ID) {
            EventEntity entity = new EventEntity(applicationId, type, dateTime, description);
            eventRepository.insert(entity, newId ->
                    scheduleReminder((int) newId, applicationId, type, dateTime));
        } else {
            NotificationHelper.cancelEventReminder(getApplication(), eventId);
            EventEntity entity = new EventEntity(applicationId, type, dateTime, description);
            entity.id = eventId;
            eventRepository.update(entity);
            scheduleReminder(eventId, applicationId, type, dateTime);
        }
    }

    public void deleteEvent(EventEntity event) {
        NotificationHelper.cancelEventReminder(getApplication(), event.id);
        eventRepository.delete(event);
    }

    private void scheduleReminder(int eventId, int applicationId, String type, String dateTime) {
        applicationRepository.loadById(applicationId, app -> {
            String company = app != null ? app.company : "";
            NotificationHelper.scheduleEventReminder(
                    getApplication(), eventId, applicationId, type, company, dateTime);
        });
    }
}
