package com.example.geolokalizator1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.geolokalizator1.db.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button clearDbButton = findViewById(R.id.clearDbButton);
        clearDbButton.setOnClickListener(v -> clearDb());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.informacje);
        //zmiana panelu
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.lokalizacja:
                        startActivity(new Intent(getApplicationContext()
                                ,MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.baza:
                        startActivity(new Intent(getApplicationContext()
                                , ShowDbActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.informacje:
                        return true;
                }
                return false;
            }
        });
    }

    private void clearDb() {
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());
        db.locationsDao().deleteDb();
        Toast.makeText(getApplicationContext(), "Wyczyszczono bazę danych", Toast.LENGTH_SHORT).show();
    }
}