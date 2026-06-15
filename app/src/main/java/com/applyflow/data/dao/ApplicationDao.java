package com.applyflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.applyflow.data.db.ApplicationEntity;

import java.util.List;

@Dao
public interface ApplicationDao {

    @Insert
    long insert(ApplicationEntity application);

    @Update
    void update(ApplicationEntity application);

    @Delete
    void delete(ApplicationEntity application);

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);

    @Query("SELECT * FROM applications ORDER BY created_at DESC")
    LiveData<List<ApplicationEntity>> getAllLive();

    @Query("SELECT * FROM applications ORDER BY date_applied IS NULL, date_applied DESC, created_at DESC")
    LiveData<List<ApplicationEntity>> getAllByDateAppliedLive();

    @Query("SELECT * FROM applications ORDER BY company COLLATE NOCASE ASC")
    LiveData<List<ApplicationEntity>> getAllByCompanyLive();

    @Query("SELECT * FROM applications ORDER BY status ASC, created_at DESC")
    LiveData<List<ApplicationEntity>> getAllByStatusOrderedLive();

    @Query("SELECT * FROM applications WHERE status = :status ORDER BY created_at DESC")
    LiveData<List<ApplicationEntity>> getByStatusLive(String status);

    @Query("SELECT * FROM applications WHERE status = :status AND date_applied IS NOT NULL "
            + "AND date_applied <= :cutoffDate ORDER BY date_applied ASC")
    LiveData<List<ApplicationEntity>> getNeedsFollowUpLive(String status, String cutoffDate);

    @Query("SELECT * FROM applications WHERE id = :id")
    LiveData<ApplicationEntity> getByIdLive(int id);

    @Query("SELECT * FROM applications WHERE id = :id")
    ApplicationEntity getByIdSync(int id);

    @Query("SELECT * FROM applications ORDER BY created_at DESC")
    List<ApplicationEntity> getAllSync();
}
