package com.ferit.dfundak.georeminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.ferit.dfundak.georeminder.MainActivity.MY_PREFS_NAME;

public class AddNewItem extends AppCompatActivity {

    //permissions
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_STORAGE_WRITING = 3;
    private static final int REQUEST_RECORD_AUDIO = 4;

    //UI
    private LinearLayout addTime;
    private LinearLayout addLocation;
    private LinearLayout addImage;
    private LinearLayout addDate;
    private ImageView addAudio;
    private ImageView playAudio;
    private TextView locationAddress;
    private EditText titleET;
    private EditText descriptionET;
    private ImageView imageView;
    private static TextView timeText;
    private static TextView dateText;
    private Button okButton;
    private Button cancelButton;

    //icons
    private ImageView locationIcon;
    private ImageView cameraIcon;
    private static ImageView timeIcon;
    private static ImageView dateIcon;

    //Intent
    public static final int KEY_REQUEST_LOCATION = 10;
    public static final double KEY_LAT = 0;
    public static final double KEY_LNG = 0;
    public static final float KEY_RADIUS = 1;

    //time and date
    private String mCurrentPhotoPath;
    private static int hour = 0;
    private static int minute = 0;
    private static int day = 0;
    private static int month;
    private static int year;

    // sound
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    //data to save
    private LatLng location = null;
    private double radius = 0;
    private String title = null;
    private String description = null;
    private String date = null;
    private String time = null;
    private String address = null;

    //other
    private String requestFrom = null;

    //time picker fragment
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            if (hour == 0) {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourPicked, int minutePicked) {
            //todo srediti s append
            timeText.setText(hourPicked + ":" + minutePicked);
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            dateText.setText(day + "." + (month + 1) + "." + year);
            hour = hourPicked;
            minute = minutePicked;
        }
    }

    //date picker fragment
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();

            if (day == 0) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int yearPicked, int monthPicked, int dayPicked) {
            dateText.setText(day + "." + (month + 1) + "." + year);
            day = dayPicked;
            month = monthPicked;
            year = yearPicked;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/GeoReminder_audio" + System.currentTimeMillis() +".3ppp";

        addImage = (LinearLayout) findViewById(R.id.add_image);
        addLocation = (LinearLayout) findViewById(R.id.add_location);
        addTime = (LinearLayout) findViewById(R.id.add_time);
        addDate = (LinearLayout) findViewById(R.id.add_date);
        addAudio = (ImageView) findViewById(R.id.add_audio);
        playAudio = (ImageView) findViewById(R.id.play_audio);
        locationAddress = (TextView) findViewById(R.id.location_textView);
        imageView = (ImageView) findViewById(R.id.image_view);
        timeText = (TextView) findViewById(R.id.time_textView);
        dateText = (TextView) findViewById(R.id.date_textView);
        okButton = (Button) findViewById(R.id.ok_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        titleET = (EditText) findViewById(R.id.titleET);
        descriptionET = (EditText) findViewById(R.id.descriptionET);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission("LOCATION")) {
                    requestPermission("LOCATION");
                } else {
                    startLocationActivity();
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission("CAMERA") || !checkPermission("STORAGE")) {
                    if (!checkPermission("CAMERA")) {
                        requestPermission("CAMERA");
                    }
                    if (!checkPermission("STORAGE")) {
                        requestFrom = "camera";
                        requestPermission("STORAGE");
                    }
                } else {
                    try {
                        takePicture();
                    } catch (IOException e) {
                        Log.e("dora", "Can't take picture");
                    }
                }
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        addAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermission("AUDIO") || !checkPermission("STORAGE")) {
                    if (!checkPermission("AUDIO")) {
                        requestPermission("AUDIO");
                    }
                    if (!checkPermission("STORAGE")) {
                        requestFrom ="audio";
                        requestPermission("STORAGE");
                    }
                } else {
                    recordAudio();
                }
            }
        });

        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
            }
        });

        playAudio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recordAudio();
                return true;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = Long.toString(System.currentTimeMillis());
                id = id.substring(0, id.length() - 3);

                if (titleET.getText() == null){
                    title  = null;
                } else{
                    title = titleET.getText().toString();
                }

                if(descriptionET.getText()== null){
                    description = null;
                } else{
                    description = descriptionET.getText().toString();
                }

                if(dateText.getText().toString().equals("Add date")){
                    date = null;
                    time = null;
                }else{
                    date = dateText.getText().toString();
                    time = timeText.getText().toString();
                    setAlarm(id);
                }

                if(locationAddress.getText().toString().equals("Add location")){
                    address = null;
                }else{
                    address = locationAddress.getText().toString();
                }
                String audioPath = OUTPUT_FILE;

                reminderItem reminder = new reminderItem(Integer.parseInt(id), location, radius, title, description, date, time, mCurrentPhotoPath ,address, audioPath);

                DatabaseHandler.getInstance(getApplicationContext()).insertReminder(reminder);

                Log.i("dora", "id set" + id);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("fromNotification", false);
                editor.commit();

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setAlarm(String id) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        if (title != null){
            intent.putExtra("title", title);
        } else{
            intent.putExtra("title", "GeoReminder");
        }
        if (description != null){
            intent.putExtra("description", description);
        } else{
            intent.putExtra("description", "You've set up reminder!");
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(id), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long alarmTime = getTime();
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        Toast.makeText(AddNewItem.this, "Reminder set", Toast.LENGTH_SHORT).show();

    }

    private long getTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
        }

    public void recordAudio() {
        Log.i("dora", "in record audio");

        final Dialog popupDialog = new Dialog(this);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.setContentView(R.layout.add_new_item_record_popup);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(popupDialog.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        popupDialog.show();
        popupDialog.getWindow().setAttributes(lp);

        ImageView cancelButton = (ImageView) popupDialog.findViewById(R.id.recording_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        final ImageView recordButton = (ImageView) popupDialog.findViewById(R.id.record_button);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordButton.setImageResource(R.drawable.microphone_green);
                        beginRecording();
                        return true;
                    case MotionEvent.ACTION_UP:
                        recordButton.setImageResource(R.drawable.microphone_black);
                        stopRecording();
                        popupDialog.dismiss();
                        return true;
                }
                return false;
            }
        });
    }

    private void playRecording() {
        stopMediaPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(OUTPUT_FILE);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopPlayback(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    private void stopMediaPlayer() {
        if(mediaPlayer != null){
            try{
                mediaPlayer.release();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if(recorder != null){
            try{
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }catch(RuntimeException stopException){
            }
        }
        addAudio.setVisibility(View.GONE);
        playAudio.setVisibility(View.VISIBLE);
    }

    private void beginRecording() {
        stopMediaRecorder();

        File outFile = new File(OUTPUT_FILE);

        if (outFile.exists()) {
            outFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(OUTPUT_FILE);

        try {
            recorder.prepare();
            recorder.start();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMediaRecorder() {
        if (recorder != null) {
            recorder.release();
        }
    }

    private void startLocationActivity() {
        Intent intent = new Intent(AddNewItem.this, LocationActivity.class);
        this.startActivityForResult(intent, KEY_REQUEST_LOCATION);
    }

    //intent results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case KEY_REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    if (data.getExtras() != null) {
                        double lat = data.getExtras().getDouble("KEY_LAT");
                        double lng = data.getExtras().getDouble("KEY_LNG");
                        radius = data.getExtras().getDouble("KEY_RADIUS");
                        Log.i("dora ", lat + " " + lng + "  " + radius);
                        location = new LatLng(lat, lng);
                        setLocationAddress(location);
                        locationIcon = (ImageView) findViewById(R.id.location_icon);
                        locationIcon.setImageResource(R.drawable.location_green);
                    } else Log.i("dora", "fail");
                    break;
                }
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());
                    try {
                        //show image in ImageView
                        InputStream ims = new FileInputStream(file);
                        imageView.setImageBitmap(BitmapFactory.decodeStream(ims));
                        cameraIcon = (ImageView) findViewById(R.id.camera_icon);
                        cameraIcon.setImageResource(R.drawable.picture_green);
                    } catch (FileNotFoundException e) {
                        return;
                    }
                    MediaScannerConnection.scanFile(AddNewItem.this,
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                    break;
                }
        }
    }

    //PERMISSIONS HANDLING
    private void requestPermission(String key) {
        switch (key) {
            case "LOCATION": {
                Log.i("dora", "request location permission");
                String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(AddNewItem.this, permission, REQUEST_LOCATION_PERMISSION);
                break;
            }
            case "CAMERA": {
                Log.i("dora", "request camera permission");
                String[] permission = new String[]{Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(AddNewItem.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                break;
            }
            case "STORAGE": {
                Log.i("dora", "request storage permission");
                String[] permission = new String[]{Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(AddNewItem.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_WRITING);
                break;
            }
            case "AUDIO": {
                Log.i("dora", "request record audio permission");
                String[] permission = new String[]{Manifest.permission.RECORD_AUDIO};
                ActivityCompat.requestPermissions(AddNewItem.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                break;
            }
        }
    }

    private boolean checkPermission(String key) {
        switch (key) {
            case "LOCATION": {
                if (ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else return false;
            }
            case "CAMERA": {
                if (ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else return false;
            }
            case "STORAGE": {
                if (ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else return false;
            }
            case "AUDIO": {
                if (ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else return false;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("dora", "Location permission granted");
                        startLocationActivity();
                    } else {
                        Log.d("dora", "Location permission denyed");
                        askForPermission("LOCATION");
                    }
                    break;
                }
            }
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("dora", "Camera permission granted");
                        if (checkPermission("STORAGE")) {
                            try {
                                takePicture();
                            } catch (IOException e) {
                                Log.e("dora", "Can't take picture");
                            }
                        } else {
                            requestPermission("STORAGE");
                        }
                    } else {
                        Log.d("dora", "Camera permission denyed");
                        askForPermission("CAMERA");
                    }
                    break;
                }
            }
            case REQUEST_STORAGE_WRITING: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("dora", "Storage permission granted");
                        if (requestFrom.equals("camera")){
                            if (checkPermission("CAMERA")) {
                                try {
                                    takePicture();
                                } catch (IOException e) {
                                    Log.e("dora", "Can't take picture");
                                }
                            } else {
                                requestPermission("CAMERA");
                            }
                        }else if(requestFrom.equals("audio")){
                            if (checkPermission("AUDIO")) {
                                recordAudio();
                            } else {
                                requestPermission("AUDIO");
                            }
                        }
                    } else {
                        Log.d("dora", "Storage permission denyed");
                        askForPermission("STORAGE");
                    }
                    break;
                }
            }
            case REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("dora", "Record audio permission granted");
                        if (checkPermission("STORAGE")) {
                            recordAudio();
                        } else {
                            requestPermission("STORAGE");
                        }
;
                    } else {
                        Log.d("dora", "Record Audio permission denyed");
                        askForPermission("AUDIO");
                    }
                    break;
                }
            }
        }
    }

    private void askForPermission(String key) {
        switch (key) {
            case "LOCATION": {
                boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (explain) {
                    this.displayDialog("LOCATION");
                } else {
                    Log.i("dora", "No LOCATION permission");
                }
                break;
            }
            case "CAMERA": {
                boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, android.Manifest.permission.CAMERA);
                if (explain) {
                    this.displayDialog("CAMERA");
                } else {
                    Log.i("AddNewItem", "No CAMERA permission");
                }
                break;
            }
            case "STORAGE": {
                boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (explain) {
                    this.displayDialog("STORAGE");
                } else {
                    Log.i("AddNewItem", "No STORAGE permission");
                }
            }
            case "AUDIO": {
                boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, Manifest.permission.RECORD_AUDIO);
                if (explain) {
                    this.displayDialog("AUDIO");
                } else {
                    Log.i("AddNewItem", "No AUDIO permission");
                }
            }
        }
    }

    private void displayDialog(final String key) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Permission")
                .setMessage("App needs your permission to give you full experience.")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (key) {
                            case "LOCATION": {
                                requestPermission("LOCATION");
                                dialog.cancel();
                            }
                            case "CAMERA": {
                                requestPermission("CAMERA");
                                dialog.cancel();
                            }
                            case "STORAGE": {
                                requestPermission("STORAGE");
                                dialog.cancel();
                            }
                            case "AUDIO": {
                                requestPermission("AUDIO");
                                dialog.cancel();
                            }
                        }
                    }
                })
                .show();
    }

    //translates address
    void setLocationAddress(LatLng latLng) {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> nearByAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (nearByAddresses.size() > 0) {
                    locationAddress.setText("");
                    StringBuilder stringBuilder = new StringBuilder();
                    Address nearestAddress = nearByAddresses.get(0);
                    stringBuilder
                            .append(nearestAddress.getAddressLine(0))
                            .append(", ")
                            .append(nearestAddress.getLocality())
                            .append(", ")
                            .append(nearestAddress.getCountryName());
                    locationAddress.append(stringBuilder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Uri photoURI;

    private void takePicture() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Check if there is camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File for photo
            File filePhoto = null;
            try {
                filePhoto = createImageFile();
            } catch (IOException e) {
                Log.e("Error.", "" + e);
                return;
            }
            if (filePhoto != null) {
                photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "GeoReminder_image" + System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}