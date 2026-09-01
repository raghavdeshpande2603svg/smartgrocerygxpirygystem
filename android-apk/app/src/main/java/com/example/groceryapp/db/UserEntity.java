package com.example.groceryapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "users",
    indices = {@Index(value = {"email"}, unique = true)}
)
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String email;

    @NonNull
    public String passwordHash;

    public UserEntity() {
    }

    public UserEntity(@NonNull String name, @NonNull String email, @NonNull String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
