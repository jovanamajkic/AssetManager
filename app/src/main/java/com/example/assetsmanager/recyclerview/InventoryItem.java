package com.example.assetsmanager.recyclerview;

import com.example.assetsmanager.db.model.Inventory;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private Inventory inventory;
    private String assetName;
    private String currEmployee;
    private String newEmployee;
    private String currLocation;
    private String newLocation;

    public InventoryItem(Inventory inventory, String assetName, String currEmployee, String newEmployee, String currLocation, String newLocation) {
        this.inventory = inventory;
        this.assetName = assetName;
        this.currEmployee = currEmployee;
        this.newEmployee = newEmployee;
        this.currLocation = currLocation;
        this.newLocation = newLocation;
    }

    public InventoryItem(){}

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getCurrEmployee() {
        return currEmployee;
    }

    public void setCurrEmployee(String currEmployee) {
        this.currEmployee = currEmployee;
    }

    public String getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(String newEmployee) {
        this.newEmployee = newEmployee;
    }

    public String getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(String currLocation) {
        this.currLocation = currLocation;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }
}
