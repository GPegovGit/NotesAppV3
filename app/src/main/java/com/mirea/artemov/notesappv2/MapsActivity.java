package com.mirea.artemov.notesappv2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mirea.artemov.notesappv2.Models.Notes;
import com.mirea.artemov.notesappv2.databinding.ActivityMapsBinding;

import java.io.Serializable;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int REQUEST_COSE_PERMISSION_ACCESS_FINE_LOCATION = 700;
    private static final int REQUEST_COSE_PERMISSION_ACCESS_COARSE_LOCATION = 701;
    private Marker marker;
    private LatLng latLngMarker=new LatLng(0, 0);
    private String titleMarker="";
    private String snippetMarker="";
    private ArrayList<Marker> markers = new ArrayList<>();
    /*Notes notes= new Notes();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

        mMap.setOnMapClickListener( this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COSE_PERMISSION_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COSE_PERMISSION_ACCESS_COARSE_LOCATION);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Location userLocation = mMap.getMyLocation();
        LatLng moscowLatLng= new LatLng(55.751244, 37.618423);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(moscowLatLng).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //Если понадовится добавить отображение пробок
        //mMap.setTrafficEnabled(true);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));*/
        //LatLng userLocation=new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        resetSelectedMarker();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        marker=mMap.addMarker(new MarkerOptions().title("Your destination").snippet("New place").position(latLng));
        markers.add(marker);


        Intent mapIntent = new Intent(MapsActivity.this, NotesTakerActivity.class);
        mapIntent.putExtra("markerPosition", String.valueOf(marker.getPosition()));
        Log.d(MapsActivity.class.getSimpleName(), "Maps intent: "  + mapIntent );
        startActivity(mapIntent);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void removeMarkers(){
        for(Marker marker : markers){
            marker.remove();
        }
    }

    private void resetSelectedMarker(){
        if(marker!=null){
            marker.setVisible(true);
            marker=null;
            removeMarkers();
        }
    }
}