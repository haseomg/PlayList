package com.example.playlist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Uuid.class}, version = 1, exportSchema = false)
public abstract class UUIDDatabase extends RoomDatabase {
    public abstract UuidDao uuidDao();
}
