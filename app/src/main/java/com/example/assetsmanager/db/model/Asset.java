package com.example.assetsmanager.db.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.assetsmanager.util.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_ASSET,
        foreignKeys = {
            @ForeignKey(entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "employeeId",
                        onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Location.class,
                        parentColumns = "id",
                        childColumns = "locationId",
                        onDelete = ForeignKey.CASCADE
            )
        }
)
public class Asset implements Serializable {
    @PrimaryKey
    private long barcode;
    private String name;
    private String description;
    private double price;
    private Date creationDate;
    private int employeeId;
    private int locationId;
    private String image;

    public Asset(long barcode, String name, String description, double price, Date creationDate, int employeeId, int locationId, String image) {
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.creationDate = creationDate;
        this.employeeId = employeeId;
        this.locationId = locationId;
        this.image = image;
    }

    public Asset(){}

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return barcode == asset.barcode && Double.compare(price, asset.price) == 0 && employeeId == asset.employeeId && locationId == asset.locationId && Objects.equals(name, asset.name) && Objects.equals(description, asset.description) && Objects.equals(creationDate, asset.creationDate) && Objects.equals(image, asset.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, name, description, price, creationDate, employeeId, locationId, image);
    }
}
