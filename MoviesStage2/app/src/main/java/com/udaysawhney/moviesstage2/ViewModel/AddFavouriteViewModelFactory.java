package com.udaysawhney.moviesstage2.ViewModel;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udaysawhney.moviesstage2.roomDatabase.AppDatabase;

public class AddFavouriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mFavouriteId;

    public AddFavouriteViewModelFactory(AppDatabase database, int favoriteId) {
        mDb = database;
        mFavouriteId = favoriteId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddFavouriteViewModel(mDb,mFavouriteId);
    }
}

