package com.ferit.dfundak.georeminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddNewItem extends AppCompatActivity {

    //permissions
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //UI
    private LinearLayout addTime;
    private LinearLayout addLocation;
    private LinearLayout addImage;
    private TextView locationAddress;

    //
    public static final int KEY_REQUEST_LOCATION = 10;
    public static final double KEY_LAT = 0;
    public static final double KEY_LNG = 0;
    public static final float KEY_RADIUS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        addImage = (LinearLayout) findViewById(R.id.add_image);
        addLocation = (LinearLayout) findViewById(R.id.add_location);
        addTime = (LinearLayout) findViewById(R.id.add_time);
        locationAddress = (TextView) findViewById(R.id.location_textView);


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
                if(!checkPermission("CAMERA")) {
                    requestPermission("CAMERA");
                }else{
                    startLocationActivity();
                }
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
        this.startActivityForResult(intent, KEY_REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case KEY_REQUEST_LOCATION:
                if(resultCode == RESULT_OK) {
                    if( data.getExtras() != null) {
                        double lat = data.getExtras().getDouble("KEY_LAT");
                        double lng = data.getExtras().getDouble("KEY_LNG");
                        float radius = data.getExtras().getFloat("KEY_RADIUS");
                        Log.i("dora ", lat +" "+ lng + "  " + radius);
                        LatLng location = new LatLng(lat, lng);
                        setLocationAddress(location);
                    }
                    else Log.i("dora", "fail");
                    break;
                }
        }
    }

    private void requestPermission(String key){
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
            case "CAMERA": {
                if(ActivityCompat.checkSelfPermission(AddNewItem.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
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
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0) {
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
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("AddNewItem", "Camera permission granted");
                        //start camera
                    } else {
                        Log.d("AddNewItem", "Camera permission denyed");
                        askForCameraPermission();
                    }
                    break;
                }
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
    private void askForCameraPermission() {
        boolean explain = ActivityCompat.shouldShowRequestPermissionRationale(AddNewItem.this, android.Manifest.permission.CAMERA);
        if (explain) {
            this.displayDialog();
        } else {
            Log.i("AddNewItem", "No permission");
        }
    }

    //todo prima sendera kako bi mogao pozvati potreni funkciju
    private void displayDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Permission")
                .setMessage("App needs your permission to give you full experience")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //todo switch case za permissione
                        requestPermission("LOCATION");
                        dialog.cancel();
                    }
                })
                .show();
    }

    void setLocationAddress(LatLng latLng){
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
}
