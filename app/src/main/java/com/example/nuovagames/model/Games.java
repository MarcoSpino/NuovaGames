package com.example.nuovagames.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity
public class Games implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    @Embedded(prefix = "image")
    private GamesImage image;

    private String original_release_date;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    public Games() {}

    public Games(long id, String name, GamesImage image, String original_release_date, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.original_release_date = original_release_date;
        this.isFavorite = isFavorite;
    }

    protected Games(Parcel in) {
        id = in.readLong();
        name = in.readString();
        original_release_date = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Games> CREATOR = new Creator<Games>() {
        @Override
        public Games createFromParcel(Parcel in) {
            return new Games(in);
        }

        @Override
        public Games[] newArray(int size) {
            return new Games[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GamesImage getImage() {
        return image;
    }

    public void setImage(GamesImage image) {
        this.image = image;
    }

    public String getOriginal_release_date() {
        return original_release_date;
    }

    public void setOriginal_release_date(String original_release_date) {
        this.original_release_date = original_release_date;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "Games{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image=" + image +
                ", original_release_date='" + original_release_date + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Games games = (Games) o;
        return id == games.id && isFavorite == games.isFavorite && Objects.equals(name, games.name) && Objects.equals(image, games.image) && Objects.equals(original_release_date, games.original_release_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, original_release_date, isFavorite);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(original_release_date);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
