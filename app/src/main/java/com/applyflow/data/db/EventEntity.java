package com.applyflow.data.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "events",
        foreignKeys = @ForeignKey(
                entity = ApplicationEntity.class,
                parentColumns = "id",
                childColumns = "application_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("application_id")}
)
public class EventEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "application_id")
    public int applicationId;

    @NonNull
    @ColumnInfo(name = "type")
    public String type;

    @NonNull
    @ColumnInfo(name = "date_time")
    public String dateTime;

    @Nullable
    @ColumnInfo(name = "description")
    public String description;

    public EventEntity(int applicationId,
                       @NonNull String type,
                       @NonNull String dateTime,
                       @Nullable String description) {
        this.applicationId = applicationId;
        this.type = type;
        this.dateTime = dateTime;
        this.description = description;
    }
}
