package com.spotify.cuong.spotifystreamer;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    //to save/restore search result when rotation/back/up
    public static final String BUNDLE_TEXT_ARTIST_LIST = "BUNDLE_TEXT_ARTIST_LIST";
    public static final String BUNDLE_TEXT_SEARCH_TEXT = "BUNDLE_TEXT_SEARCH_TEXT";
    public static final String BUNDLE_TEXT_ITEM_POSITION = "BUNDLE_TEXT_ITEM_POSITION";

    private ArrayList<MyArtist> myArtists;
    private String searchText;
    private int mSelectedItemPosition;

    private View rootView;
    private SearchView artistNameSearch;
    private ArtistListAdapter searchResultAdapter;
    private Toast msgToast;
    private AsyncTask<String, Integer, List<MyArtist>> task;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView v = (ListView) rootView.findViewById(R.id.artistList);

        if (savedInstanceState != null) {
            myArtists = savedInstanceState.getParcelableArrayList(BUNDLE_TEXT_ARTIST_LIST);
            searchText = savedInstanceState.getString(BUNDLE_TEXT_SEARCH_TEXT);
            mSelectedItemPosition = savedInstanceState.getInt(BUNDLE_TEXT_ITEM_POSITION);
            v.smoothScrollToPosition(mSelectedItemPosition);
        }
        else {
            //initiate a blank result
            myArtists = new ArrayList<MyArtist>();
            searchText = "";
        }

        //for each search result, register a listener which will open artist detail screen
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedItemPosition = position;

                MyArtist artist = searchResultAdapter.getItem(position);

                Bundle artistInfo = new Bundle();

                artistInfo.putParcelable(Constants.BUNDLE_TEXT_ARTIST, artist);

                ((Callback)getActivity()).onItemSelected(artistInfo);

            }
        });

        searchResultAdapter = new ArtistListAdapter(getActivity().getApplicationContext(), R.layout.artist_main, myArtists);
        v.setAdapter(searchResultAdapter);

        //find the search textbox and register the listener, which will call spotify web service to get search result when txt change
        artistNameSearch = (SearchView)rootView.findViewById(R.id.artistNameSearch);
        artistNameSearch.clearFocus();

        artistNameSearch.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d(LOG_TAG, "afterTextChanged");

                        if (!searchText.equals(newText)) //screen rotation also trigger this afterTextChanged. this if is to prevent from firing another spotify request
                            if (newText.length() > 2) { //dont search for short text
                                searchText = newText;
                                if (task != null)
                                    task.cancel(true); //cancel running task
                                task = new SearchArtists();
                                task.execute(searchText);

                            }
                        return false;
                    }
                });

        return rootView;
    }

    public class SearchArtists extends AsyncTask<String, Integer, List<MyArtist>> {

        private final String LOG_TAG = SearchArtists.class.getSimpleName();

        @Override
        protected List<MyArtist> doInBackground(String... artistName){
            List<MyArtist> returnList = new ArrayList<MyArtist>();
            //set a delay time in case its being cancelled
            try {
                Thread.sleep(50);

                //calling spotify api and update searchResultAdapter
                SpotifyApi api = new SpotifyApi();

                SpotifyService spotify = api.getService();

                ArtistsPager results = spotify.searchArtists(artistName[0]);

                if (results!= null)
                    for (Artist artist : results.artists.items)
                        returnList.add(new MyArtist((artist)));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RetrofitError error){
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                spotifyError.printStackTrace();
            }

            return returnList;
        }

        @Override
        protected void onPostExecute(List<MyArtist> result) {
            searchResultAdapter.clear();
            if (result.size()> 0) {
                if (msgToast != null)//clear msg
                    msgToast.cancel();
                searchResultAdapter.addAll(result);
            }
            else{
                msgToast = Toast.makeText(getActivity(), getString(R.string.no_result_msg), Toast.LENGTH_SHORT);
                msgToast.show();

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(BUNDLE_TEXT_ARTIST_LIST, myArtists);
        outState.putString(BUNDLE_TEXT_SEARCH_TEXT, searchText);
        outState.putInt(BUNDLE_TEXT_ITEM_POSITION, mSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }
}
