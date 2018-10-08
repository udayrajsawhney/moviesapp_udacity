package com.udaysawhney.moviesstage2.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udaysawhney.moviesstage2.Fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
