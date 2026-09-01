package com.example.groceryapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;

@Entity(
    tableName = "categories",
    indices = {@Index(value = {"name"}, unique = true)}
)
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    public CategoryEntity() {
    }

    public CategoryEntity(@NonNull String name) {
        this.name = name;
    }

    public static List<CategoryEntity> getDefaultCategories() {
        return Arrays.asList(
            new CategoryEntity("Vegetables"),
            new CategoryEntity("Dairy"),
            new CategoryEntity("Grains"),
            new CategoryEntity("Fruits"),
            new CategoryEntity("Beverages"),
            new CategoryEntity("Snacks"),
            new CategoryEntity("Frozen"),
            new CategoryEntity("Other")
        );
    }
}
