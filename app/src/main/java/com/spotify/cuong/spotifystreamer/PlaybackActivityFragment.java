package com.spotify.cuong.spotifystreamer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 */
public class PlaybackActivityFragment extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = PlaybackActivityFragment.class.getSimpleName();

    private boolean mIsPlaying;
    private TextView mArtistName;
    private TextView mAlbum;
    private ImageView mImg;
    private TextView mTrackName;
    private SeekBar mSeekBar;
    private TextView mBeginDuration;
    private TextView mEndDuration;
    private ImageButton mPlayButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;

    private PlaybackService mPlaybackService; //will be bound on startup

    private MyBroadcastReceiver mMyBroadcastReceiver;

    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mPlaybackService.seekMusicTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d("ServiceConnection", "connected");

            mPlaybackService = ((PlaybackService.MyBinder) binder).getService();

            mArtistName.setText(mPlaybackService.getmArtist().getArtistName());
            mAlbum.setText(mPlaybackService.getmTrack().getAlbumName());
            mTrackName.setText(mPlaybackService.getmTrack().getTrackName());
            Picasso.with(getActivity()).load(mPlaybackService.getmTrack().getTrackMidImageUrl()).into(mImg);

        }
        //binder comes from server to communicate with method's of

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            mPlaybackService = null;
        }
    };

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getBooleanExtra(Constants.BUNDLE_TEXT_TRACK_IS_NEW_TRACK, true)) {
                mArtistName.setText(intent.getStringExtra(Constants.BUNDLE_TEXT_TRACK_ARTIST_NAME));
                mAlbum.setText(intent.getStringExtra(Constants.BUNDLE_TEXT_TRACK_ALBUM_NAME));
                mTrackName.setText(intent.getStringExtra(Constants.BUNDLE_TEXT_TRACK_NAME));
                Picasso.with(getActivity()).load(intent.getStringExtra(Constants.BUNDLE_TEXT_TRACK_ALBUM_IMG_URL)).into(mImg);
            }
            //get info from intent and update playerUI
            mSeekBar.setMax(intent.getIntExtra(Constants.BUNDLE_TEXT_TRACK_DURATION, 0));

            mSeekBar.setProgress(intent.getIntExtra(Constants.BUNDLE_TEXT_TRACK_CURRENT_POSITION, 0));

            mIsPlaying = intent.getBooleanExtra(Constants.BUNDLE_TEXT_TRACK_IS_PLAYING, false);

            if (mIsPlaying)
                mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            else
                mPlayButton.setImageResource(android.R.drawable.ic_media_play);

            mBeginDuration.setText(formatDuration(mSeekBar.getProgress()));

            mEndDuration.setText("-" + formatDuration(mSeekBar.getMax() - mSeekBar.getProgress()));
        }

    }


    public PlaybackActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");

        super.onStart();
    }

    @Override
    public void onResume() {
        //startUpdatingSeekBar();
        Log.d(LOG_TAG, "onResume");
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(mMyBroadcastReceiver, new IntentFilter("UPDATE_PLAYER"));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //there are 3 cases this fragment is called:
        //from track list which means args should be not null
        //from screen rotation
        //from Now Playing button
        //the latters music info should be captured from service

        View v = inflater.inflate(R.layout.fragment_playback, container, false);

        Intent intent = new Intent(this.getActivity(), PlaybackService.class);

        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        mArtistName = (TextView) v.findViewById(R.id.player_artistname_textview);

        mAlbum = (TextView) v.findViewById(R.id.player_albumname_textview);

        mTrackName = (TextView) v.findViewById(R.id.player_trackname_textview);

        mImg = (ImageView) v.findViewById(R.id.player_albumicon_imageview);

        mPlayButton = (ImageButton) v.findViewById(R.id.player_play_imagebutton);
        mNextButton = (ImageButton) v.findViewById(R.id.player_next_imagebutton);
        mPrevButton = (ImageButton) v.findViewById(R.id.player_prev_imagebutton);

        mPlayButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);

        mBeginDuration = (TextView) v.findViewById(R.id.player_begin_duration_textview);
        mEndDuration = (TextView) v.findViewById(R.id.player_end_duration_textview);

        mSeekBar = (SeekBar) v.findViewById(R.id.player_seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarListener());

        return v;


    }


    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        getActivity().unregisterReceiver(mMyBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        getActivity().unbindService(mServiceConnection);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDiaglog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_TAG, "onDismiss");
        super.onDismiss(dialog);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView");
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(LOG_TAG, "onCancel");
        super.onCancel(dialog);
    }

    private static String formatDuration(int currentPosition) {
        //to make it simple assuming all preview musics have less than 1 hour playback
        int minute = currentPosition / 1000 / 60;
        int second = (currentPosition / 1000) % 60;

        return String.valueOf(minute) + ":" + String.valueOf(String.format("%2s", second).replace(" ", "0"));
    }

    private void playMusic() {
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            mSeekBar.setProgress(0);
            mBeginDuration.setText(formatDuration(mSeekBar.getProgress()));
            mEndDuration.setText("-" + formatDuration(mSeekBar.getMax() - mSeekBar.getProgress()));
        }
        mPlaybackService.playMusic();
    }

    private void pauseMusic() {
        mPlaybackService.pauseMusic();
        mPlayButton.setImageResource(android.R.drawable.ic_media_play);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mPlayButton)) {
            if (mIsPlaying) { //playing, pause it
                pauseMusic();
            } else {
                playMusic();
            }
        } else if (v.equals(mPrevButton)) {
            mPlaybackService.playPrevTrack();
        } else if (v.equals(mNextButton)) {
            mPlaybackService.playNextTrack();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
