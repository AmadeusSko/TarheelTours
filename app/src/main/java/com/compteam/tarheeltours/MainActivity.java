package com.compteam.tarheeltours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;



public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        PopupDialog.Listener, InfoDialog.InfoListener {

    private static final int REQUEST_CODE = 73;
    private LocationManager mLocationManager;
    private GoogleMap map;
    private Location start;
    private Location oldWellLocation;
    private MarkerOptions[] locations;
    SQLHelper SQLHelper;
    boolean initiate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLHelper = new SQLHelper(this);
        if (initiate) {
            ContentValues theOldWell = new ContentValues();
            theOldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "The Old Well");
            theOldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, R.string.old_well_info);
            ContentValues sitterson = new ContentValues();
            sitterson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Sitterson Hall");
            sitterson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, R.string.sitterson_info);
            ContentValues fedex = new ContentValues();
            fedex.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "FedEx Global Center");
            fedex.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, R.string.fedex_info);
            SQLiteDatabase db = SQLHelper.getWritableDatabase();
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, theOldWell);
            initiate = false;
        }
        setContentView(R.layout.activity_main);
        start = oldWellLocation = new Location("Old Well");
        oldWellLocation.setLatitude(35.9121);
        oldWellLocation.setLongitude(-79.0512);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            accessLocation();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);
            } else {
                Toast.makeText(this,
                        "Unable to get location permissions.", Toast.LENGTH_SHORT).show();
            }
        }
        mapFragment.getMapAsync(this);
    }
    private class Inserter extends AsyncTask<ContentValues, Void, Cursor> {
        String tableName;

        public Inserter(String name){
            tableName = name;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if(cursor == null || cursor.getCount() == 0){
                Log.d("Skozboi", "Inserter was called with no data to be inserted!");
            }
            //super.onPostExecute(cursor);
        }

        @Override
        protected Cursor doInBackground(ContentValues... contentValues) {
            SQLiteDatabase db = SQLHelper.getWritableDatabase();
            for(ContentValues contentValues1 : contentValues){
                db.insert(tableName, null, contentValues1);
            }
            db = SQLHelper.getReadableDatabase();
            String[] projection = null;
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;
            return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        }
    }

    private void accessLocation() {
        if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1, this);
            //start = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to get location updates.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void makeDialog(Location location) {
        String locationName = location.getProvider();
        PopupDialog dialog = new PopupDialog(locationName);
        dialog.show(getSupportFragmentManager(), "Alert");
    }

    public void makeInfoDialog(String location) {
        String info = "";
        String title = location;
        InfoDialog dialog = new InfoDialog(info, title);
        dialog.show(getSupportFragmentManager(), "Info");
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Change received", Toast.LENGTH_SHORT).show();
        LatLng n = new LatLng(location.getLatitude(), location.getLongitude());
        for(MarkerOptions loc: locations){
            Location loc1 = new Location(loc.getTitle());
            loc1.setLatitude(loc.getPosition().latitude);
            loc1.setLongitude(loc.getPosition().longitude);
            if (loc1.distanceTo(location) <= 10) {
                makeDialog(loc1);
                break;
            }
        }
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        map.moveCamera(CameraUpdateFactory.newLatLng(n));
        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
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
    public void onAcceptedListener(String location) {
        makeInfoDialog(location);
    }

    @Override
    public void onCancelledListener() {

    }

    @Override
    public void infoCancelledListener() {

    }

    public MarkerOptions addMarker(String title, LatLng coord){
        MarkerOptions options = new MarkerOptions().position(coord).title(title);
        return options;
    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),
                R.raw.mapstyle));
        LatLng oldWell = new LatLng(35.9121, -79.0512);
        MarkerOptions well = addMarker("The Old Well", oldWell);
        map.addMarker(well);
        locations = new MarkerOptions[]{well};
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(start.getLatitude(),
                start.getLongitude())));

    }
}
