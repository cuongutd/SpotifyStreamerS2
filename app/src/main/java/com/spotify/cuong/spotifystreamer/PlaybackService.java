package com.spotify.cuong.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cuong on 6/28/2015.
 */
public class PlaybackService extends Service {

    private static final String LOG_TAG = PlaybackService.class.getSimpleName();


    private MediaPlayer mMediaPlayer;

    private MyTrack mTrack;
    private MyArtist mArtist;
    private ArrayList<MyTrack> mTopTracks;
    private int mTrackListPosition;
    private boolean mIsNewTrack = false;


    //these are for broadcasting music to activity
    private Handler mHandler = new Handler();
    private BroadcastPlaybackInfo updateSeekBar = new BroadcastPlaybackInfo();

    public class BroadcastPlaybackInfo implements Runnable {
        @Override
        public void run() {

            Intent intent = new Intent("UPDATE_PLAYER");

            if (mMediaPlayer != null) {
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_DURATION, mMediaPlayer.getDuration());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_CURRENT_POSITION, mMediaPlayer.getCurrentPosition());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_IS_PLAYING, mMediaPlayer.isPlaying());

                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_IS_NEW_TRACK, isNewTrack());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_ARTIST_NAME, mArtist.getArtistName());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_ALBUM_NAME, mTrack.getAlbumName());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_NAME, mTrack.getTrackName());
                intent.putExtra(Constants.BUNDLE_TEXT_TRACK_ALBUM_IMG_URL, mTrack.getTrackMidImageUrl());

                //intent.setAction("UPDATE_PLAYER");
                sendBroadcast(intent);
                mHandler.postDelayed(this, 1000);
            }
        }
    }

    //this for binder

    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlaybackService getService() {
            Log.d(LOG_TAG, "getService");
            return PlaybackService.this;
        }
    }


//    Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text),
//            System.currentTimeMillis());
//    Intent notificationIntent = new Intent(this, ExampleActivity.class);
//    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//    notification.setLatestEventInfo(this, getText(R.string.notification_title),
//    getText(R.string.notification_message), pendingIntent);
//    startForeground(ONGOING_NOTIFICATION_ID, notification);

    //from sof
    private PendingIntent retrievePlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this, PlaybackService.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent(Constants.PLAYBACK_ACTION_PLAYBACK);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent(Constants.PLAYBACK_ACTION_PLAY_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(Constants.PLAYBACK_ACTION_PLAY_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

    Bitmap bigAlbumThump;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bigAlbumThump = bitmap;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNotification() {
        Picasso.with(this).load(getmTrack().getTrackMidImageUrl()).into(target);

        MediaSession mediaSession = new MediaSession(this, "session tag");
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bigAlbumThump)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mArtist.getArtistName())
                .putString(MediaMetadata.METADATA_KEY_ALBUM, mTrack.getAlbumName())
                .putString(MediaMetadata.METADATA_KEY_TITLE, mTrack.getTrackName())
                .build());
        // Indicate you're ready to receive media commands
        mediaSession.setActive(true);
        // Attach a new Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSession.Callback() {

            // Implement your callbacks

        });
        // Indicate you want to receive transport controls via your Callback
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        int playbackButtonIcon;
        if (mMediaPlayer.isPlaying())
            playbackButtonIcon = android.R.drawable.ic_media_pause;
        else
            playbackButtonIcon = android.R.drawable.ic_media_play;


        Notification notification = new Notification.Builder(this)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.headphone2)
                        // Add media control buttons that invoke intents in your media service
                .addAction(android.R.drawable.ic_media_previous, "Previous", retrievePlaybackAction(3)) // #0
                .addAction(playbackButtonIcon, mMediaPlayer.isPlaying() ? "Pause" : "Play", retrievePlaybackAction(1))  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", retrievePlaybackAction(2))     // #2
                        // Apply the media style template
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setContentTitle("Spotify Streamer")
                .setContentText(mTrack.getTrackName())
                .setLargeIcon(bigAlbumThump)
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, notification);

    }

    @Override
    public void onCreate() {


        Log.d(LOG_TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "onStartCommand");

        if (intent.getAction() == Constants.PLAYBACK_ACTION_START_SERVICE) {

            //initialze mediaPlayer

            mTopTracks = intent.getParcelableArrayListExtra(Constants.BUNDLE_TEXT_TRACK_LIST);
            mTrackListPosition = intent.getIntExtra(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION, 0);
            mTrack = mTopTracks.get(mTrackListPosition);
            mArtist = intent.getParcelableExtra(Constants.BUNDLE_TEXT_ARTIST);

            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            prepareMusic();

            //start music when service starts
            playMusic();

            broadcastPlaybackInfo();

        } else if (intent.getAction() == Constants.PLAYBACK_ACTION_PLAYBACK) {
            if (mMediaPlayer.isPlaying())
                pauseMusic();
            else
                playMusic();

        } else if (intent.getAction() == Constants.PLAYBACK_ACTION_PLAY_NEXT) {
            playNextTrack();
        } else if (intent.getAction() == Constants.PLAYBACK_ACTION_PLAY_PREVIOUS) {
            playPrevTrack();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void prepareMusic() {
        try {
            mIsNewTrack = true;
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mTrack.getTrackUri());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(LOG_TAG, "invalid track preview URL: " + mTrack.getTrackUri());
            Log.d(LOG_TAG, e.getMessage());
        }
    }


    public void seekMusicTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    public void playMusic() {
        //For Play button, assuming it was paused
        mMediaPlayer.start();
        createNotification();
    }

    public void pauseMusic() {
        //for pause button, assuming it was playing
        mMediaPlayer.pause();
        createNotification();
    }

    private void broadcastPlaybackInfo() {
        mHandler.removeCallbacks(updateSeekBar);
        mHandler.postDelayed(updateSeekBar, 1000);
    }

    private void stopBroadcastPlaybackInfo() {
        mHandler.removeCallbacks(updateSeekBar);
    }


    public void playNextTrack() {

        if (mTrackListPosition == 0)
            mTrackListPosition = mTopTracks.size() - 1;
        else
            mTrackListPosition--;

        mTrack = mTopTracks.get(mTrackListPosition);

        playNewTrack();
    }

    public void playPrevTrack() {
        if (mTrackListPosition == mTopTracks.size() - 1)
            mTrackListPosition = 0;
        else
            mTrackListPosition++;

        mTrack = mTopTracks.get(mTrackListPosition);

        playNewTrack();
    }


    private void playNewTrack() {
        //for next/prev button
        prepareMusic();
        playMusic();
    }





































    /*Getters and setters section*/


    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }


    public MyTrack getmTrack() {
        return mTrack;
    }

    public void setmTrack(MyTrack mTrack) {
        this.mTrack = mTrack;
    }

    public MyArtist getmArtist() {
        return mArtist;
    }

    public void setmArtist(MyArtist mArtist) {
        this.mArtist = mArtist;
    }

    public ArrayList<MyTrack> getmTopTracks() {
        return mTopTracks;
    }

    public void setmTopTracks(ArrayList<MyTrack> mTopTracks) {
        this.mTopTracks = mTopTracks;
    }

    public int getmTrackListPosition() {
        return mTrackListPosition;
    }

    public void setmTrackListPosition(int mTrackListPosition) {
        this.mTrackListPosition = mTrackListPosition;
    }

    public boolean isNewTrack() {
        if (mIsNewTrack) {
            mIsNewTrack = false; //reset
            return true;
        } else
            return mIsNewTrack;
    }

}
