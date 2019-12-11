package com.compteam.tarheeltours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    private String lastLocation;
    private MarkerOptions[] locations;
    private String locationDescription;
    private String currentLocation;
    SQLHelper SQLHelper;
    boolean initiate = true;
    SQLiteDatabase db;
    static final String OLD_WELL_INFO = "The university’s signature structure is not an academic building but a small neoclassical structure placed over a well between Old East and Old West and in front of South Building. University president Edwin Alderman added the domed Old Well in 1897 as part of a campus beautification effort. He found the well’s original wooden cover shabby and ordered it replaced with a rotunda modeled in part after the Temple of Love at Versailles. Rumor has it that drinking from the Old Well before class on the first day of classes will guarantee you a 4.0 GPA during your time at UNC - though this rumor doesn't seem to have much in the ways of evidence.";
    static final String SITTERSON_INFO = "Sitterson hall has been the home of the Computer Science department since 1987, before which it was spread across West House, New West hall, and three other buildings across campus. The hall is named after distinguished professor J. Carlyle Sitterson, with its newest addition, the Frederick P. Brooks hall, named after the department's founder of the same name.";
    static final String FEDEX_INFO = "The FedEx Global Center is among the University's newest buildings, constructed in 2007 by the Global department. Its purpose is described as \"an inviting environment that expands upon the University’s public service mission domestically and across the globe\". It has excellent classrooms, seminar rooms, events, and exhibition space. It also has a green roof that collects and filters up to 50,000 gallons of rainwater underneath, which are recycled for use in the restrooms.";
    static final String GENOME_INFO = "The Genome Science Laboratory building is one of UNC's most distinctive-looking buildings. Completed in 2012, it is both the home of UNC's newest research facilities and a hub for student and faculty interaction. It features three sections which contain wet and dry lab space, as well as its easily recognizable greenhouse at the top level with a similar water recycling system as found in the FedEx Center to capture rainwater and reuse it as restroom water. The Genome Science Building also was built with environmental conscience in mind, attainting a LEED Gold rating.";
    static final String DAVIS_INFO = "Walter Royal Davis Library, decidedly the largest of the libraries on UNC's campus, is sometimes referred to as simply \"the\" library. It houses eight floors, seven of which contain material on humanities, social sciences, business, and foreign language, but it also contains MANY individual and group studying locations, even treadmill desks to get some workout in while studying! Lesser known, Davis library also contains basement levels with millions of government documents, some unique to Davis Library alone. Davis Library is also an easy building to look at to determine whether or not it's exam week - if the library's still packed at 1 AM, it's for sure finals week.";
    static final String UGL_INFO = "The Robert B. House Undergraduate Library, commonly referred to as the UL, is one of UNC's smaller libraries, home to material targeted for undergraduates to develop learning strategies. Its main attraction however is that it is open 24 hours Sunday-Thursday. But if you (sadly) plan to take advantage of those hours, don't forget your OneCard - building access is limited to faculty and staff from 12 AM to 7 AM! The UL also contains the University's ITS Help Desk and a VR station, a creative hub with Mac computers, and several group study rooms with glassboards along the walls for marking down ideas.";
    static final String KESSING_INFO = "The Kessing Outdoor Pool was originally built in 1943 as a part of UNC's Navy Aviation Eastern Pre-flight Training School. An easy way to see this is the still-present Navy anchor embedded in the concrete beside the pool. Thousands of Naval aviators used the pool for water-borne training, including former U.S. Presidents Gerald R. Ford and George H. W. Bush. The pool was renamed from the Navy Pool to Kessing Outdoor Pool as a commemoration to Oliver Owen Kessing, who was the first Commanding Officer of the Eastern Pre-flight School using the pool on UNC's campus.";
    static final String WILSON_INFO = "The Louis Round Wilson Special Collections Library is a more mysterious one on campus, as it houses very old, fragile documents. This means that not many undergraduates set foot in the building, but it really is a place of wonder. Inside, on the second floor, is a very nice and quiet study area that looks as if it was taken out of a scene in the Harry Potter films, and deep in the halls of the building, restoration work on old documents and preservation efforts are always in swing. A media library is also present on sublevel 1, which could prove useful to those looking for older media.";
    static final String SOUTH_INFO = "The South building, sitting as is self-explanatory at the North end of the main campus quad, is actually so named because it faces South, in the direction of the Wilson Library and the Bell Tower. This building is the main office for the university's chancellor (or in the case of recent years, the interim chancellor), and is a hot hub for visitors to the university, as it is in the center of UNC's main northern campus.";
    static final String BELL_INFO = "The Morehead-Patterson Bell Tower is one of the central landmarks at UNC. It can be heard ringing every fifteen minutes, and on special occasions will ring out other tunes in celebration (as with the Alma Mater) or seasonal songs during the winter or on major holidays. Prior to home football games, alumni, students, and UNC fans are invited out to climb the Bell Tower and see the view, and upkeep of the Bell Tower is covered by the General Alumni Association's member dues. Students of UNC often try to immortalize themselves and leave their mark quite literally by writing on bricks inside the Bell Tower.";
    static final String DOME_INFO = "The Dean E. Smith Student Activities Center is a mouthful to say - and so is often shortened to the Dean Dome. It is a massive hub of student energy during college basketball season since its construction was completed in 1986, with a recent renovation in 2018. A top tip: draw as many paths as you can from the Dean Dome to Franklin Street, and still you will not be able to come up with all the ways that students sprint from the Dome all the way to Franklin Street after a home victory against Duke or a National Champtionship win. The energy at the Dean Dome during basketball season is simply electrifying.";


//    @Override
//    protected void onPause() {
//        super.onPause();
//        mLocationManager.removeUpdates(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_CODE);
//        } else {
//            Toast.makeText(this,
//                    "Unable to get location permissions.", Toast.LENGTH_SHORT).show();
//        }    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLHelper = new SQLHelper(this);
        try {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        if (initiate) {
            ContentValues theOldWell = new ContentValues();
            theOldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "The Old Well");
            theOldWell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, OLD_WELL_INFO);

            ContentValues sitterson = new ContentValues();
            sitterson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Sitterson Hall");
            sitterson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, SITTERSON_INFO);

            ContentValues fedex = new ContentValues();
            fedex.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "FedEx Global Center");
            fedex.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, FEDEX_INFO);

            ContentValues genome = new ContentValues();
            genome.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Genome Science Labs");
            genome.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, GENOME_INFO);

            ContentValues libraryBigBoi = new ContentValues();
            libraryBigBoi.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Davis Library");
            libraryBigBoi.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, DAVIS_INFO);

            ContentValues ugl = new ContentValues();
            ugl.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Undergraduate Library");
            ugl.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, UGL_INFO);

            ContentValues kessing = new ContentValues();
            kessing.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Kessing Outdoor Pool");
            kessing.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, KESSING_INFO);

            ContentValues wilson = new ContentValues();
            wilson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Wilson Library");
            wilson.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, WILSON_INFO);

            ContentValues south = new ContentValues();
            south.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "South Building");
            south.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, SOUTH_INFO);

            ContentValues bell = new ContentValues();
            bell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "The Bell Tower");
            bell.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, BELL_INFO);

            ContentValues dome = new ContentValues();
            dome.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME, "Dean Dome");
            dome.put(LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO, DOME_INFO);


            db = SQLHelper.getWritableDatabase();
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, theOldWell);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, sitterson);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, fedex);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, genome);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, libraryBigBoi);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, ugl);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, kessing);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, wilson);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, south);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, bell);
            db.insert(LandmarkInformation.LandmarkTable.TABLE_NAME, null, dome);
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
                    5000, 1, this);

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to get location updates.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void makeDialog(String location) {
        String locationName = location;
        PopupDialog dialog = new PopupDialog(locationName);
        dialog.show(getSupportFragmentManager(), "Alert");
    }

    public void makeInfoDialog(String location) {
        String title = location;
        InfoDialog dialog = new InfoDialog(locationDescription, title);
        dialog.show(getSupportFragmentManager(), "Info");
    }

    public boolean hasDialoge(String title){
        if(lastLocation == null){
            lastLocation = title;
            return false;
        }
        else if(lastLocation == title){
            return true;
        }
        else{
            return false;
        }
    }
    public void setLastLocation(String last){
        lastLocation = last;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng n = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        map.moveCamera(CameraUpdateFactory.newLatLng(n));
        for(MarkerOptions loc: locations){
            Location loc1 = new Location(loc.getTitle());
            loc1.setLatitude(loc.getPosition().latitude);
            loc1.setLongitude(loc.getPosition().longitude);
            float dist = loc1.distanceTo(location);
            if (dist <= 50.0) {
                Toast.makeText(this, "Distance to " + loc.getTitle() + ": " + dist, Toast.LENGTH_SHORT).show();
                if(!hasDialoge(loc.getTitle())) {
                    currentLocation = loc.getTitle();
                    makeDialog(loc.getTitle());
                    new Searcher().execute();
                    setLastLocation(loc.getTitle());
                    break;
                }
            }
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

        LatLng sn = new LatLng(35.909754, -79.053506);
        MarkerOptions sitterson = addMarker("Sitterson Hall", sn);
        map.addMarker(sitterson);

        LatLng fedEx = new LatLng(35.907880, -79.054335);
        MarkerOptions FedExCenter = addMarker("FedEx Global Center", fedEx);
        map.addMarker(FedExCenter);

        LatLng gsl = new LatLng(35.907727, -79.050498);
        MarkerOptions genomeLabs = addMarker("Genome Science Labs", gsl);
        map.addMarker(genomeLabs);

        LatLng davis = new LatLng(35.910694, -79.048432);
        MarkerOptions davisLib = addMarker("Davis Library", davis);
        map.addMarker(davisLib);

        LatLng ugl = new LatLng(35.910047, -79.049162);
        MarkerOptions undergradLib = addMarker("Undergraduate Library", ugl);
        map.addMarker(undergradLib);

        LatLng kop = new LatLng(35.908774, -79.046356);
        MarkerOptions kessing = addMarker("Kessing Outdoor Pool", kop);
        map.addMarker(kessing);

        LatLng wilson = new LatLng(35.909855, -79.049939);
        //LatLng wilson  = new LatLng(35.914015, -79.047813);
        MarkerOptions wilsonLib = addMarker("Wilson Library", wilson);
        map.addMarker(wilsonLib);

        LatLng south = new LatLng(35.911624, -79.050912);
        //LatLng south  = new LatLng(35.914015, -79.047813);
        MarkerOptions SBLD = addMarker("South Building", south);
        map.addMarker(SBLD);

        LatLng bt = new LatLng(35.908823, -79.049349);
        MarkerOptions BT7274 = addMarker("The Bell Tower", bt);
        map.addMarker(BT7274);

        LatLng dome = new LatLng(35.900336, -79.043247);
        MarkerOptions deanDome = addMarker("Dean Dome", dome);
        map.addMarker(deanDome);

        locations = new MarkerOptions[]{well, sitterson, FedExCenter, genomeLabs, davisLib,
                undergradLib, kessing, wilsonLib, SBLD, BT7274, deanDome};
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(start.getLatitude(),
                start.getLongitude())));

    }

    public class Searcher extends AsyncTask<Void, Void, String>{
        @Override
        protected void onPostExecute(String result) {
            locationDescription = result;
        }

        @Override
        protected String doInBackground(Void... voids) {
            db = SQLHelper.getWritableDatabase();
            String sql = "SELECT " + LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_INFO + " FROM " + LandmarkInformation.LandmarkTable.TABLE_NAME + " WHERE " + LandmarkInformation.LandmarkTable.COLUMN_LANDMARK_NAME + " = " + "\"" + currentLocation + "\"" + ";";
            Cursor result = db.rawQuery(sql, null);
            String answer = "NO RESULT FROM DATABASE SEARCH";
            try{
                result.moveToNext();
                answer = result.getString(0);
            } catch(Exception e){
                e.printStackTrace();
                Log.d("SKOZ BOI", e.toString());
            }
            result.close();
            return answer;
        }
    }
}
