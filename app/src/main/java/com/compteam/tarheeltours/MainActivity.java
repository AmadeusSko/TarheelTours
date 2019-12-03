package com.compteam.tarheeltours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        PopupDialog.Listener, InfoDialog.InfoListener {

    private static final int REQUEST_CODE = 73;
    private LocationManager mLocationManager;
    private GoogleMap map;
    private Location oldWellLocation;
    private int standardZoom = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    public void makeInfoDialog(String info, String title){
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
    public void onAcceptedListener() {
        String info = ""; // Get info from database
        String title = ""; // Get title of location from database
        makeInfoDialog(info, title);
    }

    @Override
    public void onCancelledListener() {

    }

    @Override
    public void infoCancelledListener() {

    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;
        LatLng oldWell = new LatLng(35.9121, -79.0512);
        oldWellLocation = new Location("Old Well");
        oldWellLocation.setLatitude(35.9121);
        oldWellLocation.setLongitude(-79.0512);
        map.addMarker(new MarkerOptions().position(oldWell).title("The Old Well"));
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        map.moveCamera(CameraUpdateFactory.newLatLng(oldWell));

    }
}
