package com.example.groceryapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categories);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<CategoryEntity> getAll();

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    CategoryEntity findByName(String name);
}
