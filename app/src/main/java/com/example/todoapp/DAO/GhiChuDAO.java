package com.example.todoapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todoapp.Entity.GhiChu;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface GhiChuDAO {

    @Query("SELECT * FROM GhiChu WHERE UID = :taiKhoanGhiNho AND daXoa = 0 ORDER BY `order`")
    List<GhiChu> getList(String taiKhoanGhiNho);

    @Query("SELECT * FROM GhiChu WHERE UID = :taiKhoanGhiNho AND daXoa = 0 AND isPin = 1 ORDER BY `order`")
    List<GhiChu> getPinedList(String taiKhoanGhiNho);

    @Query("SELECT * FROM GhiChu WHERE maGhiChu = :maGhiChu")
    GhiChu getGhiChuById(int maGhiChu);

    @Insert
    long themGhiChu(GhiChu ghichu);

    @Update
    int suaGhiChu(GhiChu ghiChu);
    @Query("UPDATE GhiChu SET `order` = `order` + 1 WHERE `order` >= :newOrder AND UID = :taiKhoanGhiNho AND daXoa = 0")
    void tangOrder(int newOrder, String taiKhoanGhiNho);
    @Query("UPDATE GhiChu SET `order` = `order` + 1 WHERE `order` >= :newOrder  AND isPin = 1 AND UID = :taiKhoanGhiNho AND daXoa = 0")
    void tangPinedOrder(int newOrder, String taiKhoanGhiNho);
    @Query("UPDATE GhiChu SET `order` = `order` - 1 WHERE `order` > :oldOrder AND isPin = 0 AND UID = :taiKhoanGhiNho AND daXoa = 0")
    void giamOrder(int oldOrder, String taiKhoanGhiNho);

    @Query("UPDATE GhiChu SET `order` = :newOrder WHERE maGhiChu = :maGhiChu")
    void capNhatOrder(int maGhiChu, int newOrder);
    @Query("SELECT * FROM GhiChu WHERE UID = :taiKhoanGhiNho AND daXoa = 0 AND (LOWER(tieude) LIKE LOWER(:query) OR LOWER(noidung) LIKE LOWER(:query) OR LOWER(tieude) LIKE LOWER(:queryWithWildcard) OR LOWER(noidung) LIKE LOWER(:queryWithWildcard)) ORDER BY `order`")
    List<GhiChu> timKiemGhiChu(String taiKhoanGhiNho, String query, String queryWithWildcard);

    @Delete
    int xoaGhiChu(GhiChu ghiChu);
}
