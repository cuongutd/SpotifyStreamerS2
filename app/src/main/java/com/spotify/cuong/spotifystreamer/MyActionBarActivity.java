package com.spotify.cuong.spotifystreamer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

/**
 * Created by Cuong on 6/27/2015.
 */
public class MyActionBarActivity extends ActionBarActivity{

    protected Menu mMenu;

    private boolean isPlaybackServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//use context received in broadcastreceiver

        boolean bReturn = false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (PlaybackService.class.getName().equals(service.service.getClassName()))
                return true;
        }

        return bReturn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onPostCreate");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(this.getClass().getSimpleName(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        Log.d(this.getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        Log.d(this.getClass().getSimpleName(), "onPostResume");
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        Log.d(this.getClass().getSimpleName(), "onTitleChanged");
        super.onTitleChanged(title, color);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(this.getClass().getSimpleName(), "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Log.d(this.getClass().getSimpleName(), "onBackPressed");
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.d(this.getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume");
        controlNowPlaying();
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        Log.d(this.getClass().getSimpleName(), "onResumeFragments");
        super.onResumeFragments();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        Log.d(this.getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(this.getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.d(this.getClass().getSimpleName(), "onAttachFragment");
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        Log.d(this.getClass().getSimpleName(), "onRestart");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(this.getClass().getSimpleName(), "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        controlNowPlaying();
        return true;
    }

    protected void controlNowPlaying(){
        if( mMenu != null)
            if (isPlaybackServiceRunning())
                mMenu.findItem(R.id.menu_item_now_playing).setVisible(true);
            else
                mMenu.findItem(R.id.menu_item_now_playing).setVisible(false);

    }


}
