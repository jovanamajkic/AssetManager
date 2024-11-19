package com.example.assetsmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetsmanager.db.model.Inventory;
import com.example.assetsmanager.util.Constants;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    long insertInventory(Inventory inventory);
    @Update
    void updateInventory(Inventory inventory);
    @Delete
    void deleteInventory(Inventory inventory);
    @Delete
    void deleteInventories(Inventory... inventories);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_INVENTORY)
    List<Inventory> getInventories();
}
