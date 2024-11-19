package com.example.assetsmanager.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.assetsmanager.util.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_LOCATION)
public class Location implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String city;
    private double latitude;
    private double longitude;

    public Location(int id, String city, double latitude, double longitude) {
        this.id = id;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id && Double.compare(latitude, location.latitude) == 0 && Double.compare(longitude, location.longitude) == 0 && Objects.equals(city, location.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, latitude, longitude);
    }
}
