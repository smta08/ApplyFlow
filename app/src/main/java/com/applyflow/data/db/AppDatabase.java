package com.applyflow.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.applyflow.data.dao.ApplicationDao;
import com.applyflow.data.dao.EventDao;
import com.applyflow.util.Constants;

@Database(
        entities = {ApplicationEntity.class, EventEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ApplicationDao applicationDao();

    public abstract EventDao eventDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    Constants.DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
