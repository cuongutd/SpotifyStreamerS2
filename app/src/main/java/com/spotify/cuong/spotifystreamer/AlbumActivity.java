package com.spotify.cuong.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class AlbumActivity extends MyActionBarActivity implements Callback{

    MyArtist mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(Constants.BUNDLE_TEXT_ARTIST)){
            mArtist = intent.getParcelableExtra(Constants.BUNDLE_TEXT_ARTIST);
            boolean isTwoPane = intent.getBooleanExtra(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, false);
            getSupportActionBar().setSubtitle(mArtist.getArtistName());

            Bundle args = new Bundle();
            args.putParcelable(Constants.BUNDLE_TEXT_ARTIST, mArtist);
            args.putBoolean(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, isTwoPane);
            AlbumActivityFragment df = new AlbumActivityFragment();
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.id_fragment_album, df).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.BUNDLE_TEXT_ARTIST, mArtist);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(Bundle artistInfo) {
        //not being used in the detail activity
    }

    @Override
    public void showPlayback(MyArtist artist, ArrayList<MyTrack> tracks, int position) {
        Intent intent = new Intent(this, PlaybackActivity.class);
        startActivity(intent);
    }

}
