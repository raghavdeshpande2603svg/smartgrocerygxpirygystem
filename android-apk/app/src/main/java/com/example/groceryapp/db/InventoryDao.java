package com.example.groceryapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    long insert(InventoryEntity item);

    @Update
    void update(InventoryEntity item);

    @Query("SELECT * FROM inventory_items WHERE userId = :userId ORDER BY expiryDate ASC")
    List<InventoryEntity> getByUser(long userId);

    @Query("SELECT * FROM inventory_items WHERE userId = :userId AND status = :status ORDER BY expiryDate ASC")
    List<InventoryEntity> getByUserAndStatus(long userId, String status);

    @Query("DELETE FROM inventory_items WHERE userId = :userId")
    void deleteAllForUser(long userId);
}
