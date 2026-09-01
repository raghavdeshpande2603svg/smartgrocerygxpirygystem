package com.example.groceryapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "products",
    foreignKeys = {
        @ForeignKey(
            entity = CategoryEntity.class,
            parentColumns = "name",
            childColumns = "category",
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"name", "category"}, unique = true),
        @Index("category")
    }
)
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String category;

    public int defaultShelfLifeDays;

    public ProductEntity() {
    }

    public ProductEntity(@NonNull String name, @NonNull String category, int defaultShelfLifeDays) {
        this.name = name;
        this.category = category;
        this.defaultShelfLifeDays = defaultShelfLifeDays;
    }
}
