package com.applyflow.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.applyflow.data.dao.ApplicationDao;
import com.applyflow.data.dao.EventDao;
import com.applyflow.util.Constants;

@Database(
        entities = {ApplicationEntity.class, EventEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ApplicationDao applicationDao();

    public abstract EventDao eventDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE applications ADD COLUMN location TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN salary TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN source TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN contact_name TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN contact_email TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN job_description TEXT");
            db.execSQL("ALTER TABLE applications ADD COLUMN priority INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    Constants.DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
