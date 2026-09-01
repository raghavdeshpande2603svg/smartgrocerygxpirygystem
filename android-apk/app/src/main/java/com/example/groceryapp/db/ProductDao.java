package com.example.groceryapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProductEntity product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);

    @Query("SELECT * FROM products ORDER BY name ASC")
    List<ProductEntity> getAll();

    @Query("SELECT * FROM products WHERE name = :name AND category = :category LIMIT 1")
    ProductEntity findByNameAndCategory(String name, String category);
}
