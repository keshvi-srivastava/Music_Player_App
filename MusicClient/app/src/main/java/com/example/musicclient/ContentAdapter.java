package com.example.musicclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.musicclient.MainActivity.TAG;

// Custom adapter for each row in the list
public class ContentAdapter extends BaseAdapter {

    private static final int PADDING = 8;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    private RadioButton selected = null;
    int previousButton;
    private Context mContext;              // This will have to be passed to the LayoutInflater
    private String[] songNames;            // String array for song names
    private String[] artistNames;          // String array for song release artists
    private Bitmap[] images;               // Bitmap array for images

    public static ArrayList<Boolean> selectedAnswers = new ArrayList<>();

    // Save the list of image IDs, names, artists and the context
    // Constructor
    public ContentAdapter(Context c, Bitmap[] images, String[] names, String[] artists) {
        for (int i = 0; i < 5; i++) {
            selectedAnswers.add(false);
        }
        mContext = c;
        this.images = images;
        this.songNames = names;
        this.artistNames = artists;
    }

    // Return the number of items in the Adapter
    @Override
    public int getCount() {
        return (songNames.length);
    }

    // Return the data item at position
    @Override
    public Object getItem(int position) {
        return (songNames[position]);
    }

    // Will get called to provide the ID that
    // is passed to OnItemClickListener.onItemClick()
    @Override
    public long getItemId(int position) {
        return (position);
    }

    // Return an ViewHolder View for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        final View result;

        // Check to recycle the list item views
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.song_list, parent, false);
            viewHolder.song = (TextView) convertView.findViewById(R.id.name);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.button = (RadioButton) convertView.findViewById(R.id.select_button);


            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (selected != null) {
                        selected.setChecked(false);
                        selectedAnswers.set(previousButton, false);
                    }
                    viewHolder.button.setChecked(true);
                    selectedAnswers.set(position, true);
                    previousButton = position;
                    selected = viewHolder.button;
                }
            });

            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.image.setImageBitmap(images[position]);
        viewHolder.song.setText(songNames[position]);
        viewHolder.artist.setText(artistNames[position]);

        return convertView;


    }

    // Class to compose of all the different views within a row
    private static class ViewHolder {

        TextView song;
        TextView artist;
        ImageView image;
        RadioButton button;


    }

}


