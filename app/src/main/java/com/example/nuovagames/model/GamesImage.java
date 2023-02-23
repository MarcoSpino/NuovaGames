package com.example.nuovagames.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class GamesImage implements Parcelable {

    private String original_url;

    public GamesImage(String original_url) {
        this.original_url = original_url;
    }

    protected GamesImage(Parcel in) {
        original_url = in.readString();
    }

    public static final Creator<GamesImage> CREATOR = new Creator<GamesImage>() {
        @Override
        public GamesImage createFromParcel(Parcel in) {
            return new GamesImage(in);
        }

        @Override
        public GamesImage[] newArray(int size) {
            return new GamesImage[size];
        }
    };

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamesImage that = (GamesImage) o;
        return Objects.equals(original_url, that.original_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original_url);
    }

    @Override
    public String toString() {
        return "GamesImage{" +
                "original_url='" + original_url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(original_url);
    }
}
