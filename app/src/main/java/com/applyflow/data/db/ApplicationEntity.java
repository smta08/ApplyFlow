package com.applyflow.data.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "applications")
public class ApplicationEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "company")
    public String company;

    @NonNull
    @ColumnInfo(name = "role")
    public String role;

    @Nullable
    @ColumnInfo(name = "link")
    public String link;

    @NonNull
    @ColumnInfo(name = "status")
    public String status;

    @Nullable
    @ColumnInfo(name = "notes")
    public String notes;

    @Nullable
    @ColumnInfo(name = "date_applied")
    public String dateApplied;

    @NonNull
    @ColumnInfo(name = "created_at")
    public String createdAt;

    public ApplicationEntity(@NonNull String company,
                             @NonNull String role,
                             @Nullable String link,
                             @NonNull String status,
                             @Nullable String notes,
                             @Nullable String dateApplied,
                             @NonNull String createdAt) {
        this.company = company;
        this.role = role;
        this.link = link;
        this.status = status;
        this.notes = notes;
        this.dateApplied = dateApplied;
        this.createdAt = createdAt;
    }
}
