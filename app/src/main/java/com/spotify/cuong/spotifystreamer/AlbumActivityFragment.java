package com.spotify.cuong.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class AlbumActivityFragment extends Fragment {

    private TrackListAdapter trackListAdapter;
    private View rootView;
    private ArrayList<MyTrack> myTracks;
    private String mSpotifyId;
    private boolean mIsTwoPane;
    private MyArtist mArtist;

    //PlaybackActivityFragment mPlayback;//keep dialog fragment when its dismissed to control current played track

    //private MediaPlayer mMediaPlayer;


    public AlbumActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_album, container, false);

        ListView v = (ListView) rootView.findViewById(R.id.trackListView);

        if (savedInstanceState != null)
            myTracks = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST);
        else
            //initiate a blank result
            myTracks = new ArrayList<MyTrack>();

        trackListAdapter = new TrackListAdapter(getActivity().getApplicationContext(), R.layout.track_album, myTracks);
        v.setAdapter(trackListAdapter);


        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {

                if (args.containsKey(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE))
                    mIsTwoPane = args.getBoolean(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE);

                if (args.containsKey(Constants.BUNDLE_TEXT_ARTIST)) {
                    mArtist = args.getParcelable(Constants.BUNDLE_TEXT_ARTIST);
                    new SearchToptracks().execute(mArtist.getSpotifyId());
                }
            }


        }

        //register listener to the list so open playback

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //start service
                Intent intent = new Intent(getActivity(), PlaybackService.class);
                intent.putExtra(Constants.BUNDLE_TEXT_ARTIST, mArtist)
                        .putParcelableArrayListExtra(Constants.BUNDLE_TEXT_TRACK_LIST, myTracks)
                        .putExtra(Constants.BUNDLE_TEXT_TRACK_LIST_POSITION, position);
                intent.setAction(Constants.PLAYBACK_ACTION_START_SERVICE);
                getActivity().startService(intent);

                //enable Now Playing button
                ((MyActionBarActivity)getActivity()).mMenu.findItem(R.id.menu_item_now_playing).setVisible(true);

                ((Callback) getActivity()).showPlayback(mArtist, myTracks, position);
            }


//            public void showDialog(MyArtist artist, int position){
//
//
//                if (mIsTwoPane) {
//                    // The device is using a large layout, so show the fragment as a dialog
//                    FragmentManager fragmentManager = getFragmentManager();
//
//                    if (mPlayback == null)
//                        mPlayback = new PlaybackActivityFragment();
//
//                    if (fragmentManager.findFragmentByTag("dialog")!= null)
//                        mPlayback = (PlaybackActivityFragment)fragmentManager.findFragmentByTag("dialog");
//
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    // For a little polish, specify a transition animation
//                    //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    Bundle args = new Bundle();
//                    args.putParcelableArrayList(BUNDLE_TEXT_TRACK_LIST, myTracks);
//                    args.putInt(BUNDLE_TEXT_TRACK_LIST_POSITION, position);
//                    args.putParcelable(Constants.BUNDLE_TEXT_ARTIST, artist);
//                    mPlayback.setArguments(args);
//                    mPlayback.show(transaction, "dialog");
//                } else {
//                    Intent intent = new Intent(getActivity(), PlaybackActivity.class)
//                            .putExtra(Constants.BUNDLE_TEXT_ARTIST, artist)
//                            .putParcelableArrayListExtra(BUNDLE_TEXT_TRACK_LIST, myTracks)
//                            .putExtra(BUNDLE_TEXT_TRACK_LIST_POSITION, position)
//                            .putExtra(Constants.BUNDLE_TEXT_BOOLEAN_TWO_PANE, mIsTwoPane);
//                    startActivity(intent);
//                }
//            }
        });


//        if (savedInstanceState == null){
//            //get data from the intent and call spotify api to get top tracks
//            Intent intent = getActivity().getIntent();
//
//            String spotifyId = intent.getStringExtra(MainActivityFragment.SPOTIFY_ID);
//            String artistName = intent.getStringExtra(MainActivityFragment.ARTIST_NAME);
//
//            new SearchToptracks().execute(spotifyId);
//        }

        return rootView;
    }

    public class SearchToptracks extends AsyncTask<String, Integer, List<MyTrack>> {

        private final String LOG_TAG = SearchToptracks.class.getSimpleName();

        @Override
        protected List<MyTrack> doInBackground(String... spotifyId) {
            myTracks = new ArrayList<MyTrack>();

            try {
                //calling spotify api and update searchResultAdapter
                SpotifyApi api = new SpotifyApi();

                SpotifyService spotify = api.getService();

                Map<String, Object> options = new HashMap<>();

                options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

                Tracks sporityResults = spotify.getArtistTopTrack(spotifyId[0], options);
                if (sporityResults != null)
                    for (Track track : sporityResults.tracks)
                        myTracks.add(new MyTrack(track));
            } catch (RetrofitError error) {
                error.printStackTrace();
            }
            return myTracks;
        }

        @Override
        protected void onPostExecute(List<MyTrack> result) {
            trackListAdapter.clear();
            if (result.size() > 0)
                trackListAdapter.addAll(result);
            else {
                Utils.showMsg(getString(R.string.no_result_msg), getActivity());
            }

        }
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(Constants.BUNDLE_TEXT_TRACK_LIST, myTracks);

        super.onSaveInstanceState(outState);
    }
}
