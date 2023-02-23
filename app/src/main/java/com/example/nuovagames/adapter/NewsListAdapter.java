package com.example.nuovagames.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nuovagames.DateTimeUtil;
import com.example.nuovagames.R;
import com.example.nuovagames.model.Games;

import java.util.List;

/**
 * Custom adapter that extends ArrayAdapter to show an ArrayList of News.
 */
public class NewsListAdapter extends ArrayAdapter<Games> {

    private final List<Games> newsList;
    private final int layout;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;

    /**
     * Interface to associate a listener to other elements defined in the layout
     * chosen for the ListView item (e.g., a Button).
     */
    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(Games news);
    }

    public NewsListAdapter(@NonNull Context context, int layout, @NonNull List<Games> newsList,
                           OnFavoriteButtonClickListener onDeleteButtonClickListener) {
        super(context, layout, newsList);
        this.layout = layout;
        this.newsList = newsList;
        this.onFavoriteButtonClickListener = onDeleteButtonClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }

        TextView textViewTitle = convertView.findViewById(R.id.textview_title);
        TextView textViewDate = convertView.findViewById(R.id.textview_date);
        ImageView imageViewFavoriteNews = convertView.findViewById(R.id.imageview_favorite_news);

        imageViewFavoriteNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteButtonClickListener.onFavoriteButtonClick(newsList.get(position));
            }
        });

        textViewTitle.setText(newsList.get(position).getName());
        textViewDate.setText(newsList.get(position).getOriginal_release_date());

        return convertView;
    }
}