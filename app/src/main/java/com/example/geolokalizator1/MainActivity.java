package com.example.geolokalizator1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView txlatitude,txlongitude, txdate,txaddress;
    Button btstart, btstop;
    private double lat, lon;
    private String lats, lons, adres, date;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        txlatitude = findViewById(R.id.txv_lat);
        txlongitude = findViewById(R.id.txv_lon);
        txdate = findViewById(R.id.txv_date);
        txaddress = findViewById(R.id.txv_addr);
        btstart = findViewById(R.id.bt_start);
        btstop = findViewById(R.id.bt_stop);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Aplikacja potrzebuje stałego dostępu do lokalizacji wybierz \"Zawsze zezwalaj\" w następnym oknie")
                            .setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1))
                            .setNegativeButton("Anuluj", (dialog, which) -> {
                                Toast.makeText(getApplicationContext(), "Użytkownik nie zezwolił na ciągłe pobieranie lokalizacji", Toast.LENGTH_SHORT).show();
                            })
                            .create().show();
                }
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1);
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else getCurrentLocation();

        //po kliknięciu przycisku następuje zatrzymanie usługi
        btstart.setOnClickListener(v -> startLocationService());

        //po kliknięciu przycisku następuje zatrzymanie usługi
        btstop.setOnClickListener(v -> stopLocationService());

        bottomNavigationView.setSelectedItemId(R.id.lokalizacja);
        //zmiana panelu
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.lokalizacja:
                    getCurrentLocation();
                    return true;
                case R.id.baza:
                    startActivity(new Intent(getApplicationContext()
                            , ShowDbActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.informacje:
                    startActivity(new Intent(getApplicationContext()
                            , AboutActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }
@Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
}

    @SuppressLint({"MissingPermission", "DefaultLocale"})
    private void getCurrentLocation() {//pobieranie lokalizacji
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if(location != null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //utworzenie listy na podstawnie pobranej lokalizacji
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //przypisanie tekstu do pól
                        lon = addresses.get(0).getLongitude();
                        lons = String.format("%.6f" , lon);
                        lat = addresses.get(0).getLatitude();
                        lats = String.format("%.6f",lat);
                        adres = addresses.get(0).getAddressLine(0);
                        @SuppressLint("SimpleDateFormat")
                        DateFormat df = new SimpleDateFormat("yyyy.MM.dd' , ' HH:mm:ss");
                        date = df.format(Calendar.getInstance().getTime());
                        txlatitude.setText(lats);
                        txlongitude.setText(lons);
                        txaddress.setText(adres);
                        txdate.setText(date);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        assert mapFragment != null;
                        mapFragment.getMapAsync(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public void onMapReady(GoogleMap googleMap) {
        LatLng current = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Jesteś tu"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    private boolean isLocationServiceRunning(){//sprawdzenie czy usluga jest włączona
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for (ActivityManager.RunningServiceInfo service:
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private void startLocationService(){//włączenie usługi ciągłego wysyłania lokalizacji
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction("startLocationService");
            startService(intent);
            Toast.makeText(this, "Rozpoczęto wysyłanie lokalizacji", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){//wyłączenie usługi ciągłego wysyłania lokalizacji
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction("stopLocationService");
            startService(intent);
            Toast.makeText(this, "Zatrzymano wysyłanie lokalizacji", Toast.LENGTH_SHORT).show();
        }
    }
}