package com.spotify.cuong.spotifystreamer;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Cuong on 6/12/2015.
 */
public class Utils {

    public static void showMsg(String msg, Activity activity){
        Toast noResultMsg=Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        noResultMsg.show();
    }

    public static boolean isEmptyString(String input){
        if (input == null || input.length()==0)
            return true;
        else
            return false;
    }
}
