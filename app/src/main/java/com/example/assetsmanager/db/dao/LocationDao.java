package com.example.assetsmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.util.Constants;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    long insertLocation(Location location);
    @Update
    void updateLocation(Location location);
    @Delete
    void deleteLocation(Location location);
    @Delete
    void deleteLocations(Location... locations);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION)
    List<Location> getLocations();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION + " WHERE id=:locationId")
    Location getById(int locationId);
}
