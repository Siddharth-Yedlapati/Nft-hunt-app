import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback 
{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DatabaseReference mDatabase;
    private LatLng mTargetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set the target location to the fixed coordinate
        mTargetLocation = new LatLng(37.4219999,-122.0840575);

        // Listen for changes to the user's location and update the map accordingly
        mDatabase.child("users").child("current_location").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // Get the user's current location
                Location location = dataSnapshot.getValue(Location.class);

                // If the user is within 10 meters of the target location, show a message on the screen
                float[] results = new float[1];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        mTargetLocation.latitude, mTargetLocation.longitude, results);
                float distanceInMeters = results[0];
                if (distanceInMeters < 5) {
                    // Show message on screen
                    Toast.makeText(MapsActivity.this, "YIPPEE YOU FOUND AN NFT !!",
                            Toast.LENGTH_SHORT).show();
                }

                // Update the map with the user's current location
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                // Same as onChildAdded()
                onChildAdded(dataSnapshot, s);
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for permission to access the user's location
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
        } } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
        
        // Method to check if location permission is granted or not
        private boolean isLocationPermissionGranted() {
            return ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        
        // Method to check if location service is enabled or not
        private boolean isLocationServiceEnabled() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        
        // Method to show message on screen
        private void showMessage(String message) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
        
        // Method to update user's location
        private void updateLocation() {
            if (isLocationPermissionGranted() && isLocationServiceEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng destination = new LatLng(FIXED_LATITUDE, FIXED_LONGITUDE);
                        float distance = currentLocation.distanceTo(destination);
                        if (distance <= 5) {
                            showMessage("Yippee, you found an NFT !!");
                        } else {
                            Location current = new Location("");
                            current.setLatitude(currentLocation.latitude);
                            current.setLongitude(currentLocation.longitude);
                            Location dest = new Location("");
                            dest.setLatitude(destination.latitude);
                            dest.setLongitude(destination.longitude);
                            float bearing = current.bearingTo(dest);
                            if (bearing > 0 && bearing < 90) {
                                showMessage("Move to your Right !!");
                            } else if (bearing > 90 && bearing < 180) {
                                showMessage("Move to your Back !!");
                            } else if (bearing > 180 && bearing < 270) {
                                showMessage("Move to your Left !!");
                            } else if (bearing > 270 && bearing < 360) {
                                showMessage("Move to your Right Ahead !!");
                            }
                        }
                    }
                });
            } else {
                showMessage("Location service is disabled or location permission is not granted.");
            }
        }
}