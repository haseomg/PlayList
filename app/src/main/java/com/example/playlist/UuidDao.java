package com.example.playlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UuidDao {

    @Query("SELECT * FROM uuid")
    LiveData<List<Uuid>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Uuid uuid);

    @Delete
    void delete(Uuid uuid);

    // 전체 메모 삭제
    @Query("DELETE FROM uuid")
    void deleteAll();

    @Query("SELECT * FROM uuid WHERE \"me\"  (:me) AND \"you\" LIKE (:you)")
    List<Uuid> getUuidByMeYou(String me, String you);

    @Query("SELECT * FROM uuid WHERE uuid LIKE '%' || :query || '%'")
    LiveData<List<Uuid>> findByQuery(String query);
}
