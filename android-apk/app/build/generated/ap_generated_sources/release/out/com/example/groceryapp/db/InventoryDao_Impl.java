package com.example.groceryapp.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InventoryDao_Impl implements InventoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InventoryEntity> __insertionAdapterOfInventoryEntity;

  private final EntityDeletionOrUpdateAdapter<InventoryEntity> __updateAdapterOfInventoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForUser;

  public InventoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInventoryEntity = new EntityInsertionAdapter<InventoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `inventory_items` (`id`,`userId`,`billId`,`productId`,`quantity`,`unit`,`cost`,`purchaseDate`,`expiryDate`,`category`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final InventoryEntity entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        if (entity.billId == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.billId);
        }
        statement.bindLong(4, entity.productId);
        statement.bindDouble(5, entity.quantity);
        if (entity.unit == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.unit);
        }
        statement.bindDouble(7, entity.cost);
        if (entity.purchaseDate == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.purchaseDate);
        }
        if (entity.expiryDate == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.expiryDate);
        }
        if (entity.category == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.category);
        }
        if (entity.status == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.status);
        }
      }
    };
    this.__updateAdapterOfInventoryEntity = new EntityDeletionOrUpdateAdapter<InventoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `inventory_items` SET `id` = ?,`userId` = ?,`billId` = ?,`productId` = ?,`quantity` = ?,`unit` = ?,`cost` = ?,`purchaseDate` = ?,`expiryDate` = ?,`category` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final InventoryEntity entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        if (entity.billId == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.billId);
        }
        statement.bindLong(4, entity.productId);
        statement.bindDouble(5, entity.quantity);
        if (entity.unit == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.unit);
        }
        statement.bindDouble(7, entity.cost);
        if (entity.purchaseDate == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.purchaseDate);
        }
        if (entity.expiryDate == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.expiryDate);
        }
        if (entity.category == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.category);
        }
        if (entity.status == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.status);
        }
        statement.bindLong(12, entity.id);
      }
    };
    this.__preparedStmtOfDeleteAllForUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM inventory_items WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final InventoryEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfInventoryEntity.insertAndReturnId(item);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final InventoryEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfInventoryEntity.handle(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllForUser(final long userId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForUser.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, userId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllForUser.release(_stmt);
    }
  }

  @Override
  public List<InventoryEntity> getByUser(final long userId) {
    final String _sql = "SELECT * FROM inventory_items WHERE userId = ? ORDER BY expiryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
      final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
      final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
      final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
      final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
      final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<InventoryEntity> _result = new ArrayList<InventoryEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final InventoryEntity _item;
        _item = new InventoryEntity();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _item.userId = _cursor.getLong(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfBillId)) {
          _item.billId = null;
        } else {
          _item.billId = _cursor.getLong(_cursorIndexOfBillId);
        }
        _item.productId = _cursor.getLong(_cursorIndexOfProductId);
        _item.quantity = _cursor.getDouble(_cursorIndexOfQuantity);
        if (_cursor.isNull(_cursorIndexOfUnit)) {
          _item.unit = null;
        } else {
          _item.unit = _cursor.getString(_cursorIndexOfUnit);
        }
        _item.cost = _cursor.getDouble(_cursorIndexOfCost);
        if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
          _item.purchaseDate = null;
        } else {
          _item.purchaseDate = _cursor.getString(_cursorIndexOfPurchaseDate);
        }
        if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
          _item.expiryDate = null;
        } else {
          _item.expiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        }
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<InventoryEntity> getByUserAndStatus(final long userId, final String status) {
    final String _sql = "SELECT * FROM inventory_items WHERE userId = ? AND status = ? ORDER BY expiryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
      final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
      final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
      final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
      final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
      final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<InventoryEntity> _result = new ArrayList<InventoryEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final InventoryEntity _item;
        _item = new InventoryEntity();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _item.userId = _cursor.getLong(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfBillId)) {
          _item.billId = null;
        } else {
          _item.billId = _cursor.getLong(_cursorIndexOfBillId);
        }
        _item.productId = _cursor.getLong(_cursorIndexOfProductId);
        _item.quantity = _cursor.getDouble(_cursorIndexOfQuantity);
        if (_cursor.isNull(_cursorIndexOfUnit)) {
          _item.unit = null;
        } else {
          _item.unit = _cursor.getString(_cursorIndexOfUnit);
        }
        _item.cost = _cursor.getDouble(_cursorIndexOfCost);
        if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
          _item.purchaseDate = null;
        } else {
          _item.purchaseDate = _cursor.getString(_cursorIndexOfPurchaseDate);
        }
        if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
          _item.expiryDate = null;
        } else {
          _item.expiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        }
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
