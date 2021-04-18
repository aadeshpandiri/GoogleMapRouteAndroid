package com.example.googlemaproute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    EditText etsource, etdestination;
    Button track;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etsource = findViewById(R.id.et_source);
        etdestination = findViewById(R.id.et_destination);
        track = findViewById(R.id.track);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String source = etsource.getText().toString().trim();
                String destination = etdestination.getText().toString().trim();

                if (source.equals("") && destination.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter details Correctly ", Toast.LENGTH_SHORT).show();
                } else {


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        String res = getCurrentLocation();
                        String resarray[] = res.split(",");
//                        while(resarray[0].equals("") && resarray[1].equals(""))
//                        {
//                             res = getCurrentLocation();
//                             resarray = res.split(",");
//                        }
//                        if(!(resarray[0].equals("") && resarray[1].equals(""))){
//                            System.out.println("details for null check :"+latitude+","+longitude);
//                            DisplayTrack(resarray[0],resarray[1],source); }
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                100);
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==100 && grantResults.length>0 && (grantResults[0]+grantResults[1]
        == PackageManager.PERMISSION_GRANTED))
        {
            getCurrentLocation();
        }
        else
        {
            Toast.makeText(this, "Give Permission to get Location !", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private String getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                 Location location = task.getResult();
                 if(location!=null)
                 {
                     latitude = String.valueOf(location.getLatitude());
                     longitude = String.valueOf(location.getLongitude());

                     System.out.println("details:"+latitude+","+longitude);

                     DisplayTrack(latitude,longitude,etsource.getText().toString().trim());

                 }
                 else
                 {
                     LocationRequest locationRequest = new LocationRequest()
                             .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                             .setInterval(10000)
                             .setFastestInterval(1000)
                             .setNumUpdates(1);

                     LocationCallback locationCallback = new LocationCallback(){
                         @Override
                         public void onLocationResult(LocationResult locationResult) {
                             Location location1 = locationResult.getLastLocation();
                             latitude = String.valueOf(location1.getLatitude());
                             longitude = String.valueOf(location1.getLongitude());
                             DisplayTrack(latitude,longitude,etsource.getText().toString().trim());
                         }
                     };

                     fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback
                     , Looper.myLooper());
                 }
                }
            });
        }
        else
        {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        return (latitude+","+longitude);
    }

    private void DisplayTrack(String latitude, String longitude,String destination) {
        try{
            //Uri uri = Uri.parse("https://www.google.co.in/maps/dir/"+source+"/"+destination);
            System.out.println("details for track :"+latitude+","+longitude);
            Uri uri = Uri.parse("https://www.google.com/maps/dir/"+(latitude)+",+"+(longitude)+"/"+destination);
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            i.setPackage("com.google.android.apps.maps");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        catch (ActivityNotFoundException e)
        {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent in = new Intent(Intent.ACTION_VIEW,uri);

            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }
    }







}