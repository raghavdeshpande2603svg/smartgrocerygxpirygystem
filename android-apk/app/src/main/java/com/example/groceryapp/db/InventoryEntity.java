package com.example.groceryapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "inventory_items",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = ProductEntity.class,
            parentColumns = "id",
            childColumns = "productId",
            onDelete = ForeignKey.NO_ACTION
        )
    },
    indices = {
        @Index("userId"),
        @Index("expiryDate"),
        @Index("status")
    }
)
public class InventoryEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public Long billId;

    public long productId;

    public double quantity;

    @NonNull
    public String unit = "pcs";

    public double cost;

    @NonNull
    public String purchaseDate;

    @NonNull
    public String expiryDate;

    @NonNull
    public String category;

    @NonNull
    public String status = "fresh";

    public InventoryEntity() {
        this.purchaseDate = "";
        this.expiryDate = "";
        this.category = "Other";
    }

    public InventoryEntity(
        long userId,
        Long billId,
        long productId,
        double quantity,
        @NonNull String unit,
        double cost,
        @NonNull String purchaseDate,
        @NonNull String expiryDate,
        @NonNull String category,
        @NonNull String status
    ) {
        this.userId = userId;
        this.billId = billId;
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
        this.cost = cost;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.category = category;
        this.status = status;
    }
}
