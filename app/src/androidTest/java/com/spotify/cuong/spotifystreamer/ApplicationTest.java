package com.spotify.cuong.spotifystreamer;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public static void test(){
        String v = formatDuration(61345);
        System.out.println(v);
    }
    private static String formatDuration(int currentPosition){
        //to make it simple assuming all preview musics have less than 1 hour playback
        int minute = currentPosition/1000/60;
        int second = (currentPosition/1000) % 60;

        return String.valueOf(minute) + ":" +String.valueOf(String.format("%2s", second).replace(" ", "0"));
    }
}