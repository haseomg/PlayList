package com.example.playlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UuidDao {
    @Query("SELECT * FROM uuid")
    LiveData<List<Uuid>> getAll();

    @Insert
    void insert(Uuid uuid);

    @Delete
    void delete(Uuid uuid);

    // 전체 메모 삭제
    @Query("DELETE FROM Uuid")
    void deleteAll();
}
