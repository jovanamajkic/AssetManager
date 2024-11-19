package com.example.assetsmanager.db.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.assetsmanager.util.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_INVENTORY,
        foreignKeys = {
            @ForeignKey(entity = Asset.class,
                        parentColumns = "barcode",
                        childColumns = "barcode",
                        onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "currEmployeeId",
                        onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "newEmployeeId",
                        onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Location.class,
                        parentColumns = "id",
                        childColumns = "currLocationId",
                        onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Location.class,
                        parentColumns = "id",
                        childColumns = "newLocationId",
                        onDelete = ForeignKey.CASCADE
            ),
        }
)
public class Inventory implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long barcode;
    private int currEmployeeId;
    private int newEmployeeId;
    private int currLocationId;
    private int newLocationId;

    public Inventory(int id, long barcode, int currEmployeeId, int newEmployeeId, int currLocationId, int newLocationId) {
        this.id = id;
        this.barcode = barcode;
        this.currEmployeeId = currEmployeeId;
        this.newEmployeeId = newEmployeeId;
        this.currLocationId = currLocationId;
        this.newLocationId = newLocationId;
    }

    public Inventory(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getCurrEmployeeId() {
        return currEmployeeId;
    }

    public void setCurrEmployeeId(int currEmployeeId) {
        this.currEmployeeId = currEmployeeId;
    }

    public int getNewEmployeeId() {
        return newEmployeeId;
    }

    public void setNewEmployeeId(int newEmployeeId) {
        this.newEmployeeId = newEmployeeId;
    }

    public int getCurrLocationId() {
        return currLocationId;
    }

    public void setCurrLocationId(int currLocationId) {
        this.currLocationId = currLocationId;
    }

    public int getNewLocationId() {
        return newLocationId;
    }

    public void setNewLocationId(int newLocationId) {
        this.newLocationId = newLocationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return id == inventory.id && barcode == inventory.barcode && currEmployeeId == inventory.currEmployeeId && newEmployeeId == inventory.newEmployeeId && currLocationId == inventory.currLocationId && newLocationId == inventory.newLocationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, barcode, currEmployeeId, newEmployeeId, currLocationId, newLocationId);
    }
}
