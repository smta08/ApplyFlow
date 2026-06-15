package com.applyflow.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.applyflow.data.dao.ApplicationDao;
import com.applyflow.data.db.AppDatabase;
import com.applyflow.data.db.ApplicationEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationRepository {

    public interface OnLoadedCallback<T> {
        void onLoaded(T value);
    }

    private final ApplicationDao applicationDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ApplicationRepository(Context context) {
        applicationDao = AppDatabase.getInstance(context).applicationDao();
    }

    public LiveData<List<ApplicationEntity>> getAll() {
        return applicationDao.getAllLive();
    }

    public LiveData<List<ApplicationEntity>> getByStatus(String status) {
        return applicationDao.getByStatusLive(status);
    }

    public LiveData<ApplicationEntity> getById(int id) {
        return applicationDao.getByIdLive(id);
    }

    public void insert(ApplicationEntity application) {
        executor.execute(() -> applicationDao.insert(application));
    }

    public void update(ApplicationEntity application) {
        executor.execute(() -> applicationDao.update(application));
    }

    public void updateStatus(int id, String status) {
        executor.execute(() -> applicationDao.updateStatus(id, status));
    }

    public void delete(ApplicationEntity application) {
        executor.execute(() -> applicationDao.delete(application));
    }

    public void loadById(int id, OnLoadedCallback<ApplicationEntity> callback) {
        executor.execute(() -> {
            ApplicationEntity entity = applicationDao.getByIdSync(id);
            mainHandler.post(() -> callback.onLoaded(entity));
        });
    }
}
