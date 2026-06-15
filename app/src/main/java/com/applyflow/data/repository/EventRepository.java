package com.applyflow.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.applyflow.data.dao.EventDao;
import com.applyflow.data.db.AppDatabase;
import com.applyflow.data.db.EventEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {

    public interface OnInsertedCallback {
        void onInserted(long id);
    }

    public interface OnLoadedCallback<T> {
        void onLoaded(T value);
    }

    private final EventDao eventDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public EventRepository(Context context) {
        eventDao = AppDatabase.getInstance(context).eventDao();
    }

    public LiveData<List<EventEntity>> getForApplication(int applicationId) {
        return eventDao.getForApplicationLive(applicationId);
    }

    public LiveData<List<EventDao.UpcomingEvent>> getUpcoming(String startIso, String endIso) {
        return eventDao.getUpcomingLive(startIso, endIso);
    }

    public void insert(EventEntity event, OnInsertedCallback callback) {
        executor.execute(() -> {
            long id = eventDao.insert(event);
            mainHandler.post(() -> callback.onInserted(id));
        });
    }

    public void update(EventEntity event) {
        executor.execute(() -> eventDao.update(event));
    }

    public void delete(EventEntity event) {
        executor.execute(() -> eventDao.delete(event));
    }

    public void loadById(int id, OnLoadedCallback<EventEntity> callback) {
        executor.execute(() -> {
            EventEntity entity = eventDao.getByIdSync(id);
            mainHandler.post(() -> callback.onLoaded(entity));
        });
    }
}
