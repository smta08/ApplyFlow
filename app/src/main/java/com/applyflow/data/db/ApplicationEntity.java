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

    @Nullable
    @ColumnInfo(name = "location")
    public String location;

    @Nullable
    @ColumnInfo(name = "salary")
    public String salary;

    @Nullable
    @ColumnInfo(name = "source")
    public String source;

    @Nullable
    @ColumnInfo(name = "contact_name")
    public String contactName;

    @Nullable
    @ColumnInfo(name = "contact_email")
    public String contactEmail;

    @Nullable
    @ColumnInfo(name = "job_description")
    public String jobDescription;

    @ColumnInfo(name = "priority", defaultValue = "0")
    public int priority;

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
