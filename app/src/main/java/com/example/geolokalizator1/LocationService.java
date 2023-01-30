package com.example.geolokalizator1;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.geolokalizator1.db.AppDatabase;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationService extends Service {

    private final LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {//poberanie lokalizacji
            super.onLocationResult(locationResult);
            locationResult.getLastLocation();
            double latitude = Objects.requireNonNull(locationResult.getLastLocation()).getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();
            try {
                Geocoder geocoder = new Geocoder(LocationService.this,
                        Locale.getDefault());

                List<Address> addresses = geocoder.getFromLocation(
                        latitude, longitude, 1
                );
                double lon = addresses.get(0).getLongitude();
                String lons = String.format("%.6f",lon);
                double lat = addresses.get(0).getLatitude();
                String lats = String.format("%.6f",lat);
                String adres = addresses.get(0).getAddressLine(0);
                Log.d("Lokalizacja:",  lon + ", " + lat + ", " + adres);
                saveNewLocation(lats, lons, adres);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    //wyslanie lokalizacji do bazy danych
    private void saveNewLocation(String latitude, String longitude, String address) {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd' , ' HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        String compare = longitude + " " + latitude;
        String fromDb = db.locationsDao().getLastSave();
        if (!compare.equals(fromDb)){
            db.locationsDao().insertNewLocation(latitude, longitude, address, date);
            Log.d("LaLo:", "dodano:" + fromDb + compare);
        } else {
            db.locationsDao().update();
            Log.d("LaLo:", "To samo: zakutalizowano rekord");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    private void startLocationService (){//start usługi
        String channelID = "location_notification_channel"; //utworzenie powiadomienia
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelID
        );
        builder.setSmallIcon(R.mipmap.ic_launcher); //właściwości powiadomienia
        builder.setContentTitle("Pobieranie lokalizacji");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Dziala");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                NotificationChannel notificationChannel = new NotificationChannel(channelID,
                        "Pobieranie lokalizacji",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000)
                .setIntervalMillis(60000)
                .setMinUpdateIntervalMillis(60000)
                .build();

        LocationServices.getFusedLocationProviderClient(this) //pobieranie lokalizacji
                .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

        startForeground(175,builder.build()); //rozpoczęcie usługi
    }
    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd' , ' HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        db.locationsDao().insertNewLocation("0", "0", "Zatrzymano pobieranie lokalizacji", date);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if (action != null){
                if(action.equals("startLocationService")){
                    startLocationService();
                } else if (action.equals("stopLocationService")){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
