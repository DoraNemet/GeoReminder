package com.ferit.dfundak.georeminder;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.ferit.dfundak.georeminder.R.id.address;

/**
 * Created by Dora on 30/06/2017.
 */

public class ReminderAdapter extends BaseAdapter {

    private ArrayList<reminderItem> mReminders;

    public ReminderAdapter(ArrayList<reminderItem> reminders) { mReminders = reminders; }

    @Override
    public int getCount() { return this.mReminders.size(); }

    @Override
    public Object getItem(int position) { return this.mReminders.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder reminderViewHolder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            reminderViewHolder = new ViewHolder(convertView);
            convertView.setTag(reminderViewHolder);
        }
        else{
            reminderViewHolder = (ViewHolder) convertView.getTag();
        }

        final reminderItem reminder = this.mReminders.get(position);

        if(reminder.getTitle() == null){
            reminderViewHolder.titleTV.setVisibility(View.GONE);
        }else{
            reminderViewHolder.titleTV.setText(reminder.getTitle());
        }

        if(reminder.getDescription() == null){
            reminderViewHolder.descriptionTV.setVisibility(View.GONE);
        }else{
            reminderViewHolder.descriptionTV.setText(reminder.getDescription());
        }


       if(reminder.getTime() == null){
           reminderViewHolder.dateTv.setVisibility(View.GONE);
           reminderViewHolder.timeTv.setVisibility(View.GONE);
           if(reminder.getImageName()== null){
               reminderViewHolder.timeImageLayout.setVisibility(View.GONE);
           }
       }else {
           reminderViewHolder.dateTv.setText(reminder.getTime());
           reminderViewHolder.timeTv.setText(reminder.getDate());
       }
        if(reminder.getAddress() == null){
            reminderViewHolder.addressIcon.setVisibility(View.GONE);
            reminderViewHolder.addressTV.setVisibility(View.GONE);
            reminderViewHolder.addressLayout.setVisibility(View.GONE);
        }else{
            reminderViewHolder.addressTV.setText(reminder.getAddress());
        }
        if(reminder.getDescription().equals("") && reminder.getAudioName() == null){
            reminderViewHolder.descriptionLayout.setVisibility(View.GONE);
        }
        if(reminder.getImageName() != null){
            String mCurrentPhotoPath = reminder.getImageName();
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            Picasso.with(parent.getContext())
                    .load(file)
                    .rotate(90f)
                    .resize(600, 200)
                    .into(reminderViewHolder.picture);
        }else{
            reminderViewHolder.picture.setVisibility(View.GONE);
        }

        if(reminder.getAudioName() != null){
            reminderViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(reminder.getAudioName());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else{
            reminderViewHolder.playButton.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void insert(reminderItem reminder) {
        this.mReminders.add(reminder);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView titleTV, descriptionTV, timeTv, dateTv, addressTV;
        public ImageView picture, playButton, addressIcon;
        public LinearLayout addressLayout, descriptionLayout, timeImageLayout;

        public ViewHolder(View bookView) {
            titleTV = (TextView) bookView.findViewById(R.id.title);
            descriptionTV = (TextView) bookView.findViewById(R.id.description);
            timeTv = (TextView) bookView.findViewById(R.id.time);
            dateTv = (TextView) bookView.findViewById(R.id.date);
            addressTV = (TextView) bookView.findViewById(address);
            picture = (ImageView) bookView.findViewById(R.id.picture);
            playButton = (ImageView) bookView.findViewById(R.id.play_button);
            addressIcon = (ImageView) bookView.findViewById(R.id.address_icon);
            addressLayout = (LinearLayout) bookView.findViewById(R.id.address_layout);
            descriptionLayout = (LinearLayout) bookView.findViewById(R.id.description_layout);
            timeImageLayout = (LinearLayout) bookView.findViewById(R.id.date_time_layout);
        }
    }
}
