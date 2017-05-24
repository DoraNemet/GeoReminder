package com.ferit.dfundak.georeminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

public class AddNewItem extends AppCompatActivity {

    //permissions
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //UI
    private LinearLayout addTime;
    private LinearLayout addLocation;
    private LinearLayout addImage;

    //
    public static final LatLng KEY_LAT_LONG = new LatLng(0, 0);
    public static final float KEY_RADIUS = 1;
    public static final int KEY_REQUEST_LOCATION = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        addImage = (LinearLayout) findViewById(R.id.add_image);
        addLocation = (LinearLayout) findViewById(R.id.add_location);
        addTime = (LinearLayout) findViewById(R.id.add_time);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkPermission("LOCATION")) {
                    requestPermission("LOCATION");
                }else{
                    startLocationActivity();
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void startLocationActivity() {
        Intent intent = new Intent(AddNewItem.this, LocationActivity.class);
        startActivityForResult(intent, KEY_REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case KEY_REQUEST_LOCATION:
                if(resultCode == RESULT_OK){

                }
                break;
        }
    }

    private void requestPermission(String key){
        switch (key) {
            case "LOCATION": {
                Log.i("dora", "request location permission");
                String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(AddNewItem.this, permission, REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    private boolean checkPermission(String key){
        switch (key) {
            case "LOCATION": {
                if(ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    return true;
                }
                else return false;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    Log.i("dora", "on result");
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("AddNewItem", "Location permission granted");
                        startLocationActivity();
                    } else {
                        Log.d("AddNewItem", "Location permission denyed");
                        askForLocationPermission();
                    }
                    break;
                }
        }
    }

    private void askForLocationPermission() {
        boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (explain) {
            this.displayDialog();
        } else {
            Log.i("AddNewItem", "No permission");
        }
    }

    private void displayDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Location permission")
                .setMessage("App needs your permission to display your location")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission("LOCATION");
                        dialog.cancel();
                    }
                })
                .show();
    }
}
