package com.example.assetsmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.util.Constants;

import java.util.List;

@Dao
public interface AssetDao {
    @Insert
    long insertAsset(Asset asset);
    @Update
    void updateAsset(Asset newAsset);
    @Delete
    void deleteAsset(Asset asset);
    @Delete
    void deleteAssets(Asset... assets);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET)
    List<Asset> getAssets();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE locationId=:locId")
    List<Asset> getAssetsByLocation(int locId);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE barcode=:id")
    Asset getById(long id);
}
