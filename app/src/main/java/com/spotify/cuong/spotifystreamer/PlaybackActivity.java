package com.spotify.cuong.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class PlaybackActivity extends MyActionBarActivity {

    private static final String FRAGMENT_TAG = "dialog";

    private static final String LOG_TAG = PlaybackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        //get intent data
        //start fragment
        Intent intent = getIntent();

        MyArtist artist = intent.getParcelableExtra(Constants.BUNDLE_TEXT_ARTIST);
        ArrayList<MyTrack> tracks = intent.getParcelableArrayListExtra(Constants.BUNDLE_TEXT_TRACK_LIST);
        int position = intent.getIntExtra(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION, 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        PlaybackActivityFragment playbackFragment = new PlaybackActivityFragment();
        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) {
            Log.d(LOG_TAG, "found fragment");
            playbackFragment = (PlaybackActivityFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        } else {
            Log.d(LOG_TAG, "NOT found fragment");
        }


        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.replace(R.id.id_fragment_playback, playbackFragment, FRAGMENT_TAG).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
