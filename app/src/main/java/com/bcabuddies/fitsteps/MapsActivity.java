package com.bcabuddies.fitsteps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    @BindView(R.id.bottom_finishBtn)
    Button bottomFinishBtn;
    @BindView(R.id.bottom_distanceTV)
    TextView bottomDistanceTV;
    @BindView(R.id.bottom_calTV)
    TextView bottomCalTV;
    @BindView(R.id.bottom_stepTV)
    TextView bottomStepTV;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private Marker mCurrLocationMarker;
    private BottomSheetBehavior sheetBehavior;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private ArrayList<LatLng> points;
    private int i = 0;
    private final static String TAG = "MapsActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        points = new ArrayList<>();

        View bottomSheet = findViewById(R.id.run_bottomSheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    i = 1;
                }
                if (i == 1) {
                    i = 0;
                }
            }
        });
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
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera
        LatLng delhi = new LatLng(28, 77);
        mMap.addMarker(new MarkerOptions().position(delhi).title("Marker in New Delhi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: location changed ");
        currentLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        points.add(latLng);

        //to draw line
        redrawLine();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        //calculate distance
        try {
            LatLng lastLocation = points.get(points.size() - 1);
            LatLng firstLocation = points.get(0);
            Log.e(TAG, "onViewClicked: lastLocation = " + lastLocation + " FirstsLocation = " + firstLocation);
            Double d = distanceCal(lastLocation.latitude, lastLocation.longitude, firstLocation.latitude, firstLocation.longitude);
            Log.e(TAG, "onViewClicked: d " + d);
            String dd = String.valueOf(d);
            Log.e(TAG, "onViewClicked: dd " + dd);
            dd = dd.substring(0, 3);
            Log.e(TAG, "onViewClicked: dd after " + dd);
            int dist = Integer.valueOf(dd.substring(0, 3));
            Log.e(TAG, "onViewClicked: distance = " + dist);
            //distance
            bottomDistanceTV.setText("Distance :" + dist + " KM");
            //steps = 1.4 in 1meter
            String steps = String.valueOf(d * 1.25 * 1000);
            steps = steps.substring(0, 4);
            bottomStepTV.setText("Steps : " + steps);
            //cal
            String cal = String.valueOf(d * 25);
            cal = cal.substring(0, 5);
            bottomCalTV.setText("Calories : " + cal);

            Log.e(TAG, "onViewClicked: cal " + cal + " steps " + steps);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClicked: exception " + e.getMessage());
        }
        /*//stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
        }*/
    }

    private void redrawLine() {
        Log.e(TAG, "redrawLine: redrawLine Called");
        mMap.clear();

        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        for (int j = 0; j < points.size(); j++) {
            LatLng point = points.get(j);
            options.add(point);
        }
        //to add current location marker
        addMarket();
    }

    private void addMarket() {
        //Place current location marker
        Log.e(TAG, "addMarket: add marker called ");
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50);
        mLocationRequest.setFastestInterval(25);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //noinspection deprecation
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.bottom_finishBtn)
    public void onViewClicked() {
        Log.e(TAG, "onViewClicked: end run clicked ");
        try {
            LatLng lastLocation = points.get(points.size() - 1);
            LatLng firstLocation = points.get(0);
            Log.e(TAG, "onViewClicked: lastLocation = " + lastLocation + " FirstsLocation = " + firstLocation);
            //change lat/lon with lastLocation.lat/lon, long=77 lat=28 delhi
            Double d = distanceCal(lastLocation.latitude, lastLocation.longitude, firstLocation.latitude, firstLocation.longitude);
            Log.e(TAG, "onViewClicked: d " + d);
            String dist = String.valueOf(d);
            Log.e(TAG, "onViewClicked: dd " + dist);
            dist = dist.substring(0, 3);
            Log.e(TAG, "onViewClicked: dd after " + dist);
            Log.e(TAG, "onViewClicked: distance = " + dist);
            //distance
            bottomDistanceTV.setText("Distance :" + dist + " KM");
            //steps = 1.4 in 1meter
            String steps = String.valueOf(d * 1.25 * 1000);
            steps = steps.substring(0, 4);
            bottomStepTV.setText("Steps : " + steps);

            //cal
            String cal = String.valueOf(d * 25);
            cal = cal.substring(0, 5);
            bottomCalTV.setText("Calories : " + cal);

            //use this data to store in firebase.
            Log.e(TAG, "onViewClicked: cal " + cal + " steps " + steps+" dist "+dist);

            //Storing data into firebase
            firebaseFirestore=FirebaseFirestore.getInstance();
            auth=FirebaseAuth.getInstance();
            userId=auth.getCurrentUser().getUid();

            Map<String,Object> map=new HashMap<>();
            map.put("distance",dist);
            map.put("calories",cal);
            map.put("steps",steps);
            map.put("uid",userId);
            map.put("time",FieldValue.serverTimestamp());

            firebaseFirestore.collection("RunData").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(MapsActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();      
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClicked: exception " + e.getMessage());
        }
    }

    private Double distanceCal(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat2);
        startPoint.setLongitude(lon2);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat1);
        endPoint.setLongitude(lon1);

        return (double) (startPoint.distanceTo(endPoint) / 1000);
    }
}