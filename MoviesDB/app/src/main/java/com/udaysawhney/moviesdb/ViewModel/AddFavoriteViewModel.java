package com.udaysawhney.moviesdb.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udaysawhney.moviesdb.database.AppDatabase;
import com.udaysawhney.moviesdb.database.FavoriteEntry;

public class AddFavoriteViewModel extends ViewModel {
    private LiveData<FavoriteEntry> favorite;

    public AddFavoriteViewModel(AppDatabase database, int favoriteId) {
        favorite = database.favoriteDao().loadFavoriteById(favoriteId);
    }

    public LiveData<FavoriteEntry> getFavorite() {
        return favorite;
    }
}
