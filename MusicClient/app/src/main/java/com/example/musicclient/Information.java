package com.example.musicclient;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public final class Information implements Parcelable {

    Bitmap image;
    String name;
    String artist;

    public Information(Bitmap image, String name, String artist) {
        this.image = image;
        this.name = name;
        this.artist = artist;
    }

    public static final Parcelable.Creator<Information> CREATOR = new Parcelable.Creator<Information>() {
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    private Information(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(image, flags);
        out.writeString(name);
        out.writeString(artist);
    }

    public void readFromParcel(Parcel in) {
        image = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        artist = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getImage() {
        return image;
    }
}