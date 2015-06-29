package com.spotify.cuong.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Cuong on 6/14/2015.
 */
public class MyTrack implements Parcelable {
    private String albumName;
    private String trackName;
    private String trackImageUrl;
    private String trackMidImageUrl;
    private String trackBigImageUrl;
    private String trackUri;

    protected MyTrack(Parcel in) {
        albumName = in.readString();
        trackName = in.readString();
        trackImageUrl = in.readString();
        trackMidImageUrl = in.readString();
        trackBigImageUrl = in.readString();
        trackUri = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(trackName);
        dest.writeString(trackImageUrl);
        dest.writeString(trackMidImageUrl);
        dest.writeString(trackBigImageUrl);
        dest.writeString(trackUri);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyTrack> CREATOR = new Parcelable.Creator<MyTrack>() {
        @Override
        public MyTrack createFromParcel(Parcel in) {
            return new MyTrack(in);
        }

        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
        }
    };

    public MyTrack(Track spotifyTrack){
        if (spotifyTrack != null) {

            trackUri = spotifyTrack.preview_url;

            this.albumName = spotifyTrack.album.name;
            this.trackName = spotifyTrack.name;
            int i = 0;
            for (Image img : spotifyTrack.album.images) {
                if (i==0) //get the first image, which is the biggest one to show on playback screen
                    this.trackBigImageUrl = img.url;
                else if (i == 1)
                    this.trackMidImageUrl = img.url;
                this.trackImageUrl = img.url;//get the last one which is smallest. if no image then no show
                i ++;
            }
        }

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }

    public String getTrackBigImageUrl() {
        return trackBigImageUrl;
    }

    public void setTrackBigImageUrl(String trackBigImageUrl) {
        this.trackBigImageUrl = trackBigImageUrl;
    }

    public String getTrackUri() {
        return trackUri;
    }

    public void setTrackUri(String trackUri) {
        this.trackUri = trackUri;
    }


    public String getTrackMidImageUrl() {
        return trackMidImageUrl;
    }

    public void setTrackMidImageUrl(String trackMidImageUrl) {
        this.trackMidImageUrl = trackMidImageUrl;
    }


}