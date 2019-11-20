package com.compteam.tarheeltours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.MapView;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        PopupDialog.Listener, InfoDialog.InfoListener {

    private static final int REQUEST_CODE = 73;
    private LocationManager mLocationManager;
    private MapView mapView;
    private GoogleMap map;
    private Location oldWellLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        mapView.getMapAsync(this);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            accessLocation();
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);
            }
            else{
                Toast.makeText(this,
                        "Unable to get location permissions.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void accessLocation(){
        if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,1,this);

        } catch(SecurityException e){
            e.printStackTrace();
            Toast.makeText(this, "Unable to get location updates.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void makeDialog(Location location){
        String locationName = location.getProvider();
        PopupDialog dialog = new PopupDialog(locationName);
        dialog.show(getSupportFragmentManager(), "Alert");
    }

    public void makeDialog(String info, String title){
        InfoDialog dialog = new InfoDialog(info, title);
        dialog.show(getSupportFragmentManager(), "Info");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(oldWellLocation.distanceTo(location) <= 10){
            makeDialog(oldWellLocation);

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng oldWell = new LatLng(35.9121, 35.9121);
        oldWellLocation = new Location("Old Well");
        oldWellLocation.setLatitude(35.9121);
        oldWellLocation.setLongitude(35.9121);
        map.addMarker(new MarkerOptions().position(oldWell).title("The Old Well"));
    }

    @Override
    public void onAcceptedListener() {
        String info = ""; // Get info from database
        String title = ""; // Get title of location from database
        makeDialog(info, title);
    }

    @Override
    public void onCancelledListener() {

    }

    @Override
    public void infoCancelledListener() {

    }
}
