package com.example.nuovagames;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nuovagames.databinding.ActivityDettaglioBinding;
import com.example.nuovagames.model.Games;

public class DettaglioActivity extends AppCompatActivity {
    TextView name;

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityDettaglioBinding activityDettaglioBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio);
        activityDettaglioBinding = ActivityDettaglioBinding.inflate(getLayoutInflater());
        View view = activityDettaglioBinding.getRoot();
        setContentView(view);

        Games games = DettaglioActivityArgs.fromBundle(getIntent().getExtras()).getGames();
        //Log.e(TAG, String.valueOf(games));

        activityDettaglioBinding.textviewNewsTitle.setText(games.getName());
    }


}
