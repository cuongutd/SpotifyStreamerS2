package com.spotify.cuong.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Cuong on 6/15/2015.
 */
public class MyArtist implements Parcelable {
    private String spotifyId;
    private String artistName;
    private String thumbnailUrl;

    public MyArtist(Artist spotifyArtist){

        if(spotifyArtist != null) {

            this.spotifyId = spotifyArtist.id;

            this.artistName = spotifyArtist.name;

            for (Image img : spotifyArtist.images)//get the last one which is smallest. if no image then no show
                this.thumbnailUrl = img.url;
        }

    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    protected MyArtist(Parcel in) {
        spotifyId = in.readString();
        artistName = in.readString();
        thumbnailUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(spotifyId);
        dest.writeString(artistName);
        dest.writeString(thumbnailUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyArtist> CREATOR = new Parcelable.Creator<MyArtist>() {
        @Override
        public MyArtist createFromParcel(Parcel in) {
            return new MyArtist(in);
        }

        @Override
        public MyArtist[] newArray(int size) {
            return new MyArtist[size];
        }
    };


}