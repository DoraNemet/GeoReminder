package com.ferit.dfundak.georeminder;

import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationListener mLocationListener;
    LocationManager mLocationManager;
    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        this.initialize();
    }

    private void initialize() {
        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        UiSettings uiSettings = this.mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        this.mGoogleMap.setOnMapClickListener(this.mCustomOnMapClickListener);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.mGoogleMap.setMyLocationEnabled(true);
    }
}

