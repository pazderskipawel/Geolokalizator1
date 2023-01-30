package com.example.geolokalizator1.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Loc {

    @PrimaryKey(autoGenerate = true)
    public int lid;

    @ColumnInfo(name = "latitude")
    public String locLatitude;

    @ColumnInfo(name = "longitude")
    public String locLongitude;

    @ColumnInfo(name = "address")
    public String locAddress;

    @ColumnInfo(name = "date")
    public String locDate;

    @ColumnInfo(name = "time", defaultValue = "0")
    public String locTime;
}
