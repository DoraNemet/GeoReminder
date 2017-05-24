package com.ferit.dfundak.georeminder;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    //for map
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;

    //UI
    private TextView addressTextView;
    private Button okButton;
    private EditText radiusInput;

    //address
    LatLng pinnedLocation;
    float radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        this.addressTextView = (TextView) findViewById(R.id.address);
        this.okButton = (Button) findViewById(R.id.ok_button);
        this.radiusInput = (EditText) findViewById(R.id.radius_input);

        this.initialize();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  radius = Float.parseFloat(radiusInput.getText().toString());
                Intent resultIntent = new Intent();
                resultIntent.putExtra(AddNewItem.KEY_LAT_LONG, pinnedLocation);
                resultIntent.putExtra(AddNewItem.KEY_RADIUS, radius);
                this.setResult(RESULT_OK, resultIntent);
                this.finish();
                */
            }
        });
    }

    private void initialize() {
        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                mGoogleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.addMarker(markerOptions);
                pinnedLocation = latLng;
                getLocationAddress(latLng);
                }
        };
    }

    void getLocationAddress(LatLng latLng){
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> nearByAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (nearByAddresses.size() > 0) {
                    addressTextView.setText("");
                    StringBuilder stringBuilder = new StringBuilder();
                    Address nearestAddress = nearByAddresses.get(0);
                    stringBuilder
                            .append(nearestAddress.getAddressLine(0))
                            .append(", ")
                            .append(nearestAddress.getLocality())
                            .append(", ")
                            .append(nearestAddress.getCountryName());
                    addressTextView.append(stringBuilder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

