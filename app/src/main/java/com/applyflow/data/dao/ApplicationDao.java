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

    @Query("SELECT * FROM applications WHERE status = :status ORDER BY created_at DESC")
    LiveData<List<ApplicationEntity>> getByStatusLive(String status);

    @Query("SELECT * FROM applications WHERE id = :id")
    LiveData<ApplicationEntity> getByIdLive(int id);

    @Query("SELECT * FROM applications WHERE id = :id")
    ApplicationEntity getByIdSync(int id);
}
