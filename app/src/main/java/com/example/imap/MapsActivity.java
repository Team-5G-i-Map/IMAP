package com.example.imap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ComponentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;
    SearchView searchView;

    public void onClick(View view){

        Intent i = new Intent(MapsActivity.this,Page3.class);
        startActivity( i );


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );

        fab = findViewById( R.id.fab );
        searchView=findViewById( R.id.search_bar );

        checkMyPermission();

        initMap();

        mLocationClient = new FusedLocationProviderClient( this );

        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        } );

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if( location != null || !location.equals( "" )) {

                    Geocoder geocoder = new Geocoder( MapsActivity.this );
                    try {
                        addressList = geocoder.getFromLocationName( location, 1 );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng( address.getLatitude(), address.getLongitude() );
                    mGoogleMap.addMarker( new MarkerOptions().position( latLng ).title( location ) );
                    mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng,10 ) );

                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        } );






    }




    private void initMap() {

        if(isPermissionGranted){
            if(isGPSenable()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.fragment );
                supportMapFragment.getMapAsync( this );

            }

        }


    }




    private  boolean isGPSenable(){

        LocationManager locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnable = locationManager.isProviderEnabled( locationManager.GPS_PROVIDER );

        if(providerEnable){
            return true;
        } else {

            AlertDialog alertDialog =new AlertDialog.Builder( this )
                    .setTitle( "GPS Permission" )
                    .setMessage( " GPS is required for this app to work . please enable GPS" )
                    .setPositiveButton( "yes", ((dialogInterface, i)-> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                    }))
                    .setCancelable(false )
                    .show();

        }

        return false;

    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        mLocationClient.getLastLocation().addOnCompleteListener( task -> {

            if(task.isSuccessful()){
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());

            }
        } );
    }

    private void gotoLocation(double latitude, double longitude) {

        LatLng LatLng = new LatLng( latitude, longitude );

        CameraUpdate cameraUpdate =  CameraUpdateFactory.newLatLngZoom( LatLng,18 );
        mGoogleMap.moveCamera( cameraUpdate );
        mGoogleMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
    }


    private void checkMyPermission() {

        Dexter.withContext(this).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                Toast.makeText( MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT ).show();
                isPermissionGranted=true;

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                Uri uri = Uri.fromParts( "pakage",getPackageName(),"" );
                intent.setData( uri );
                startActivity( intent );
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();

            }
        } ).check();
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady( GoogleMap googleMap) {

        mGoogleMap= googleMap;
        mGoogleMap.setMyLocationEnabled( true );




    }


    @Override
    public void onConnected(@Nullable  Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

            boolean providerEnable = locationManager.isProviderEnabled( locationManager.GPS_PROVIDER );

            if(providerEnable){
                Toast.makeText( this, "GPS is enable", Toast.LENGTH_SHORT ).show();
            }
            else {
                Toast.makeText( this, "GPS is not enable", Toast.LENGTH_SHORT ).show();
            }
        }
    }
}