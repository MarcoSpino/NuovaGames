package com.example.nuovagames;

import android.os.Bundle;
import android.util.Log;
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

        name.findViewById(R.id.textview_news_title);

        Games games = DettaglioActivityArgs.fromBundle(getIntent().getExtras()).getGames();


        Log.e(TAG, String.valueOf(games));

        name.setText(games.getName());
    }


}
