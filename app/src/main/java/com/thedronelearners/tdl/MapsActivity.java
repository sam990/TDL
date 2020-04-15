package com.thedronelearners.tdl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener{

    private GoogleMap mMap;
    private Marker myMarker = null;
    private Button sendButton;
    private ImageButton satelliteButton;
    private final String CURR_LOCATION = "Send Current Location";
    private final String MARKED_LOCATION = "Send Marked Location";
    private final String CHOOSE_LOCATION = "Choose Location";
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int REQUEST_CHECK_SETTINGS = 2;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    private static final String TAG = "MapActivity";
    private LatLng mDefaultLocation;
    private final int DEFAULT_ZOOM = 10;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mDefaultLocation = new LatLng(25.4484, 78.5685);
        sendButton = findViewById(R.id.send_location_button);
        sendButton.setText(CHOOSE_LOCATION);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
        satelliteButton = findViewById(R.id.satellite_button);
        satelliteButton.setEnabled(false);
        satelliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        satelliteButton.setEnabled(true);
        //mMap.setOnMyLocationButtonClickListener(this);
        getLocationPermission();
        //updateLocationUI();
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }


    @Override
    public void onMapLongClick(LatLng point) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        updateButton();
    }

    @Override
    public void onMapClick(LatLng point) {
        if (myMarker != null) {
            myMarker.remove();
            myMarker = null;
            updateButton();
        }
    }


    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            createLocationRequest();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        //mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                }
            }
        }

    }

    private void createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mLocationPermissionGranted = true;
                updateLocationUI();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });



    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
            getDeviceLocation();
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                final Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation == null){
                                getDeviceLocation();
                                return;
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 20));
                            updateButton();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            mLastKnownLocation = null;
                            updateButton();
                        }
                    }
                });
            }
            else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            Toast toast = Toast.makeText(getApplicationContext(), "Please try again" , Toast.LENGTH_LONG);
                            toast.show();
                            finish();
                        }
                    }
                });
            }
            else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
/*
    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();
        return false;
    }
*/
    private void updateButton(){
        if(mLastKnownLocation == null && myMarker == null){
            sendButton.setEnabled(false);
            sendButton.setText(CHOOSE_LOCATION);
        }

        else if(myMarker != null){
            if(!sendButton.getText().equals(MARKED_LOCATION))
                sendButton.setText(MARKED_LOCATION);
            if(!sendButton.isEnabled()){
                sendButton.setEnabled(true);
            }
        }

        else {
            if(!sendButton.getText().equals(CURR_LOCATION))
                sendButton.setText(CURR_LOCATION);
            if(!sendButton.isEnabled()){
                sendButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        sendButton.setEnabled(false);
        sendButton.setText("Sending Location Data");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UserLocations");


        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.ENGLISH);
        String timeFormat = s.format(new Date());

        if(myMarker != null){
            MyLocation myLocation = new MyLocation(mUser.getDisplayName(), mUser.getPhoneNumber(),
                    timeFormat, myMarker.getPosition().latitude,
                    myMarker.getPosition().longitude);

            myRef.push().setValue(myLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Location sent successfully", Toast.LENGTH_LONG);
                    toast.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Could not send data. Try again later.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        else if(mLastKnownLocation != null){
            updateDeviceLocation();
            MyLocation myLocation = new MyLocation(mUser.getDisplayName(), mUser.getPhoneNumber(),
                    timeFormat, mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude(), mLastKnownLocation.getAccuracy());
            //String location = mLastKnownLocation.getLatitude()+ Double.toString(mLastKnownLocation.getLongitude());
            myRef.push().setValue(myLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Location sent successfully", Toast.LENGTH_LONG);
                    toast.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Could not send data. Try again later.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mLocationPermissionGranted = true;
            }
            else {
                mLocationPermissionGranted = false;
            }
            updateLocationUI();
        }
    }



}
