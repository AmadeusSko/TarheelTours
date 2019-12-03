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

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        PopupDialog.Listener, InfoDialog.InfoListener {

    private static final int REQUEST_CODE = 73;
    private LocationManager mLocationManager;
    private MapView mapView;
    private GoogleMap map;
    private Location oldWellLocation;
    SQLHelper SQLHelper;
    boolean initiate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLHelper = new SQLHelper(this);
        if (initiate) {
            ContentValues oldWell = new ContentValues();
            oldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Old Well");
            oldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, R.string.old_well_info);
            SQLiteDatabase db = SQLHelper.getWritableDatabase();
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, oldWell);
            initiate = false;
        }
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapparino);
        mapView.getMapAsync(this);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Toast.makeText(this, "mlocationmanager acquire", Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            accessLocation();
            Toast.makeText(this, "Has Location Permissions.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Toast.makeText(this, "Requesting for above M", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);

            }
            else{
                Toast.makeText(this,
                        "Unable to get location permissions.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Inserter extends AsyncTask<ContentValues, Void, Cursor>{
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
        try {
            if (oldWellLocation.distanceTo(location) <= 10) {
                makeDialog(oldWellLocation);

            }
        }catch(NullPointerException e){
            Log.e("Skozboi", e.toString());
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
        makeDialog(info, title);
    }

    @Override
    public void onCancelledListener() {

    }

    @Override
    public void infoCancelledListener() {

    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady running", Toast.LENGTH_SHORT).show();
        LatLng oldWell = new LatLng(35.9121, 35.9121);
        oldWellLocation = new Location("Old Well");
        oldWellLocation.setLatitude(35.9121);
        oldWellLocation.setLongitude(35.9121);
        map.addMarker(new MarkerOptions().position(oldWell).title("The Old Well"));
    }
}
