package com.example.assetsmanager.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.assetsmanager.db.dao.AssetDao;
import com.example.assetsmanager.db.dao.EmployeeDao;
import com.example.assetsmanager.db.dao.InventoryDao;
import com.example.assetsmanager.db.dao.LocationDao;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Inventory;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.util.Constants;
import com.example.assetsmanager.util.DateRoomConverter;

@Database(entities = { Asset.class, Employee.class, Location.class, Inventory.class }, version = 3)
@TypeConverters({DateRoomConverter.class})
public abstract class AssetsManagerDatabase extends RoomDatabase {
    public abstract AssetDao getAssetDao();
    public abstract EmployeeDao getEmployeeDao();
    public abstract LocationDao getLocationDao();
    public abstract InventoryDao getInventoryDao();
    private static AssetsManagerDatabase assetsManagerDb;

    public static AssetsManagerDatabase getInstance(Context context) {
        if (null == assetsManagerDb) {
            assetsManagerDb = buildDatabaseInstance(context);
        }
        return assetsManagerDb;
    }

    private static AssetsManagerDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context, AssetsManagerDatabase.class, Constants.DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public  void cleanUp(){
        assetsManagerDb = null;
    }
}
