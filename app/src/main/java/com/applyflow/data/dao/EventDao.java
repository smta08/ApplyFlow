package com.applyflow.data.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.applyflow.data.db.EventEntity;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    long insert(EventEntity event);

    @Update
    void update(EventEntity event);

    @Delete
    void delete(EventEntity event);

    @Query("SELECT * FROM events WHERE application_id = :applicationId ORDER BY date_time ASC")
    LiveData<List<EventEntity>> getForApplicationLive(int applicationId);

    @Query("SELECT * FROM events WHERE id = :id")
    EventEntity getByIdSync(int id);

    @Query("SELECT e.id AS id, e.application_id AS application_id, e.type AS type, "
            + "e.date_time AS date_time, e.description AS description, a.company AS company "
            + "FROM events e JOIN applications a ON e.application_id = a.id "
            + "WHERE e.date_time >= :startIso AND e.date_time <= :endIso "
            + "ORDER BY e.date_time ASC")
    LiveData<List<UpcomingEvent>> getUpcomingLive(String startIso, String endIso);

    class UpcomingEvent {
        @ColumnInfo(name = "id")
        public int id;

        @ColumnInfo(name = "application_id")
        public int applicationId;

        @NonNull
        @ColumnInfo(name = "type")
        public String type = "";

        @NonNull
        @ColumnInfo(name = "date_time")
        public String dateTime = "";

        @Nullable
        @ColumnInfo(name = "description")
        public String description;

        @NonNull
        @ColumnInfo(name = "company")
        public String company = "";
    }
}
