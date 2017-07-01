package com.ferit.dfundak.georeminder;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

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

        reminderItem reminder = this.mReminders.get(position);

        reminderViewHolder.titleTV.setText(reminder.getTitle());
        reminderViewHolder.descriptionTV.setText(reminder.getDescription());
        reminderViewHolder.dateTv.setText(reminder.getTime());
        reminderViewHolder.timeTv.setText(reminder.getDate());
        reminderViewHolder.addressTV.setText(reminder.getAddress());

        if(reminder.getImageName() != null){
            String mCurrentPhotoPath = reminder.getImageName();
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                //show image in ImageView
                InputStream ims = new FileInputStream(file);
                reminderViewHolder.picture.setImageBitmap(BitmapFactory.decodeStream(ims));
            } catch (FileNotFoundException e) {

            }
        }

        return convertView;
    }

    public void insert(reminderItem reminder) {
        this.mReminders.add(reminder);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView titleTV, descriptionTV, timeTv, dateTv, addressTV;
        public ImageView picture;

        public ViewHolder(View bookView) {
            titleTV = (TextView) bookView.findViewById(R.id.title);
            descriptionTV = (TextView) bookView.findViewById(R.id.description);
            timeTv = (TextView) bookView.findViewById(R.id.time);
            dateTv = (TextView) bookView.findViewById(R.id.date);
            addressTV = (TextView) bookView.findViewById(R.id.address);
            picture = (ImageView) bookView.findViewById(R.id.picture);
        }
    }
}
