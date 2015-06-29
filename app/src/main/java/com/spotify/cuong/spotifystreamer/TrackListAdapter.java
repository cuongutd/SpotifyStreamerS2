package com.spotify.cuong.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cuong on 6/11/2015.
 */
public class TrackListAdapter extends ArrayAdapter<MyTrack> {
    Context context;

    public TrackListAdapter(Context context, int resourceId, //resourceId=your layout
                             List<MyTrack> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtAlbum;
        TextView txtTrack;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        MyTrack rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.track_album, null);
            holder = new ViewHolder();
            holder.txtAlbum = (TextView) convertView.findViewById(R.id.albumText);
            holder.txtTrack = (TextView) convertView.findViewById(R.id.trackName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.trackImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtAlbum.setText(rowItem.getAlbumName());
        holder.txtTrack.setText(rowItem.getTrackName());


        if (!Utils.isEmptyString(rowItem.getTrackImageUrl()))
            Picasso.with(context).load(rowItem.getTrackImageUrl()).into(holder.imageView);
        else
            holder.imageView.setImageDrawable(null);



        return convertView;
    }

}
