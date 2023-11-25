package com.example.todoapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;

import java.util.List;

@Dao
public interface NhanDAO {
    @Insert
    long themNhan(Nhan nhan);

    @Query("SELECT * FROM nhan_table WHERE UID = :UID")
    List<Nhan> getList(int UID);
    @Delete
    int xoaNhan(Nhan nhan);
    @Update
    int suaNhan(Nhan nhan);
    @Query("SELECT COUNT(*) FROM nhan_table WHERE tenNhan = :tenNhan and UID = :UID")
    int kiemTraTenNhanTonTai(String tenNhan, int UID);
}
