package com.udaysawhney.moviesstage2.roomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Query("SELECT * FROM favoritetable")
    LiveData<List<FavouriteEntry>> loadAllFavorite();

    @Query("SELECT * FROM favoritetable WHERE title = :title")
    List<FavouriteEntry> loadAll(String title);

    @Insert
    void insertFavorite(FavouriteEntry favoriteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(FavouriteEntry favoriteEntry);

    @Delete
    void deleteFavorite(FavouriteEntry favoriteEntry);

    @Query("DELETE FROM favoritetable WHERE movieid = :movie_id")
    void deleteFavoriteWithId(int movie_id);

    @Query("SELECT * FROM favoritetable WHERE id = :id")
    LiveData<FavouriteEntry> loadFavoriteById(int id);
}