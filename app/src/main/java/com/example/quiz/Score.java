package com.example.quiz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Score extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private LocationManager locationManager;
    private Marker currentLocationMarker;

    ImageView imageView;
    Button bLogout, bTry;
    ProgressBar progressBar;
    TextView tvScore;
    FirebaseAuth mAuth;
    int score;
    String userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvScore = findViewById(R.id.etscore);
        progressBar = findViewById(R.id.etprogress);
        bLogout = findViewById(R.id.bLogout);
        bTry = findViewById(R.id.bTry);
        imageView = findViewById(R.id.logoo2);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz2-4121b.appspot.com/o/logoo.jpg?alt=media&token=e6b26d8d-8432-491e-a1dc-2178e4eb4085")
                .into(imageView);

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        progressBar.setProgress((100 * score) / 5);
        tvScore.setText((100 * score) / 5 + " %");

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Thank you for your participation !", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Score.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        bTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Score.this, Quiz.class));
            }
        });
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng currentLocation = new LatLng(latitude, longitude);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("User");

            usersRef.whereEqualTo("email", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userAuth = String.valueOf(document.get("nom"));
                            if (gMap != null) {
                                if (currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }

                                LatLng currentLocation = new LatLng(latitude, longitude);
                                GeoPoint geoPoint = new GeoPoint(currentLocation.latitude, currentLocation.longitude);
                                String userId = mAuth.getCurrentUser().getUid();
                                DocumentReference userRef = db.collection("User").document(userId);
                                userRef.update(new HashMap<String, Object>() {{
                                    put("score", (100 * score) / 5);
                                    put("gps",geoPoint);;
                                }});

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currentLocation);
                                markerOptions.title(userAuth);
                                markerOptions.snippet("Score : " + (100 * score) / 5 + "%");
                                Marker marker = gMap.addMarker(markerOptions);
                                marker.showInfoWindow();

                                currentLocationMarker = gMap.addMarker(markerOptions);

                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

                                locationManager.removeUpdates(locationListener);


                                usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String userNom = document.getString("nom");
                                                double score = document.getDouble("score");
                                                GeoPoint geoPoint = document.getGeoPoint("gps");

                                                if (geoPoint != null && !userNom.equals(userAuth) ) {
                                                    double latitude = geoPoint.getLatitude();
                                                    double longitude = geoPoint.getLongitude();
                                                    LatLng userLocation = new LatLng(latitude, longitude);

                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    markerOptions.position(userLocation);
                                                    markerOptions.title(userNom);
                                                    markerOptions.snippet("Score : " + score + "%");

                                                    gMap.addMarker(markerOptions);
                                                }
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des utilisateurs", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User introuvable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }
}
