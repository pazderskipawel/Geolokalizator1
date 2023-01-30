package com.example.geolokalizator1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.geolokalizator1.db.Loc;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.example.geolokalizator1.db.AppDatabase;

public class ShowDbActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    private UserLocationAdapter userLocationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_db);

        initRecyclerView();

        String defDate = getDate();
        loadLocationList(defDate);

        Button btAll = findViewById(R.id.btall);
        Button bt00 = findViewById(R.id.bt00);
        Button bt01 = findViewById(R.id.bt01);

        btAll.setOnClickListener(v -> {
            String date = getDate();
            loadLocationList(date);
            Log.d("DataGodzina", date);
        });
        bt00.setOnClickListener(v -> {
            String date = getDate(0);
            loadLocationList(date);
            Log.d("DataGodzina", date);
        });
        bt01.setOnClickListener(v -> {
            String date = getDate(-1);
            loadLocationList(date);
            Log.d("DataGodzina", date);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.baza);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.lokalizacja:
                    startActivity(new Intent(getApplicationContext()
                            ,MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.baza:
                    return true;
                case R.id.informacje:
                    startActivity(new Intent(getApplicationContext()
                            , AboutActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private String getDate(){
        return " ";
    }
    private String getDate(int days){
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd' ,%'");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return df.format(c.getTime());
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        userLocationAdapter = new UserLocationAdapter(this);
        recyclerView.setAdapter(userLocationAdapter);
    }

    private void loadLocationList(String str) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Loc> locationList;
        if (str.equals(" ")){
            locationList = db.locationsDao().getAllLocations();
        } else {
            locationList = db.locationsDao().getSelectedDate(str);
        }
        userLocationAdapter.setLocList(locationList);
    }





}