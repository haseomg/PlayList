package com.example.playlist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Uuid {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "uuid")
    public String uuid;

    public Uuid(String uuid) {
        this.uuid = uuid;
    }
}
