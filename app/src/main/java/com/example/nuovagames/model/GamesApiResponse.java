package com.example.nuovagames.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class GamesApiResponse implements Parcelable {
    private String error;
    private int number_of_total_results;
    private List<Games> results;

    public GamesApiResponse() {}


    public GamesApiResponse(List<Games> results) {
        this.results = results;
    }

    public GamesApiResponse(String error, int number_of_total_results, List<Games> results) {
        this.error = error;
        this.number_of_total_results = number_of_total_results;
        this.results = results;
    }

    protected GamesApiResponse(Parcel in) {
        error = in.readString();
        number_of_total_results = in.readInt();
        results = in.createTypedArrayList(Games.CREATOR);
    }

    public static final Creator<GamesApiResponse> CREATOR = new Creator<GamesApiResponse>() {
        @Override
        public GamesApiResponse createFromParcel(Parcel in) {
            return new GamesApiResponse(in);
        }

        @Override
        public GamesApiResponse[] newArray(int size) {
            return new GamesApiResponse[size];
        }
    };

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getNumber_of_total_results() {
        return number_of_total_results;
    }

    public void setNumber_of_total_results(int number_of_total_results) {
        this.number_of_total_results = number_of_total_results;
    }

    public List<Games> getResults() {
        return results;
    }

    public void setResults(List<Games> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(error);
        dest.writeInt(number_of_total_results);
        dest.writeTypedList(results);
    }
}
