package com.udaysawhney.moviesstage2.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udaysawhney.moviesstage2.roomDatabase.AppDatabase;
import com.udaysawhney.moviesstage2.roomDatabase.FavouriteEntry;

public class AddFavouriteViewModel extends ViewModel {
    private LiveData<FavouriteEntry> favorite;

    public AddFavouriteViewModel(AppDatabase database, int favoriteId) {
        favorite = database.favoriteDao().loadFavoriteById(favoriteId);
    }

    public LiveData<FavouriteEntry> getFavorite() {
        return favorite;
    }
}
