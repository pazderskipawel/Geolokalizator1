package com.example.geolokalizator1.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationsDao {

    @Query("SELECT * FROM Loc")
    List<Loc> getAllLocations();

    @Query("SELECT longitude ||' '|| latitude AS save FROM Loc ORDER BY lid DESC LIMIT 1")
     String getLastSave();

    @Query("SELECT * FROM Loc WHERE date LIKE :date")
    List<Loc> getSelectedDate(String date);

    @Query("UPDATE Loc SET time = time + 1 WHERE lid=(SELECT MAX(lid) FROM Loc)")
    void update();

    @Query("INSERT INTO Loc(latitude,longitude,address, date) VALUES(:lat, :lon, :add, :dat) ")
    void insertNewLocation(String lat, String lon, String add, String dat);

    @Query("DELETE FROM Loc")
    void deleteDb();

    }
