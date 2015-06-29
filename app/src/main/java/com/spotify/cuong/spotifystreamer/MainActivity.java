package com.spotify.cuong.spotifystreamer;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends MyActionBarActivity implements Callback{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mIsTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.id_fragment_album) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mIsTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                AlbumActivityFragment df = new AlbumActivityFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.id_fragment_album, df, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mIsTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        //ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        //TODO: set fragment twoPane, initiate data load


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_now_playing) {

            showPlayback(null, null, 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Bundle artistInfo) {
        if (mIsTwoPane) {
            AlbumActivityFragment df = new AlbumActivityFragment();
            //add twoPane indicator to second fragment
            artistInfo.putBoolean(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, mIsTwoPane);
            df.setArguments(artistInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.id_fragment_album, df, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, AlbumActivity.class)
                    .putExtra(Constants.BUNDLE_TEXT_ARTIST, artistInfo.getParcelable(Constants.BUNDLE_TEXT_ARTIST))
                    .putExtra(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, mIsTwoPane);
            startActivity(intent);
        }
    }

    @Override
    public void showPlayback(MyArtist artist, ArrayList<MyTrack> tracks, int position) {
        if (mIsTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            PlaybackActivityFragment playbackFragment = new PlaybackActivityFragment();
            if (fragmentManager.findFragmentByTag("dialog") == null) {
                playbackFragment.show(fragmentManager, "dialog");
            }
        }else{
            Intent intent = new Intent(this, PlaybackActivity.class);
            startActivity(intent);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
