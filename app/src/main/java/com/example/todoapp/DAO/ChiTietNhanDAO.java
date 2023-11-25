package com.example.todoapp.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.Nhan;

import java.util.List;

@Dao
public interface ChiTietNhanDAO {
    @Query("SELECT nhan_table.* FROM nhan_table " +
            "INNER JOIN ChiTietNhanDAO ON nhan_table.maNhan = ChiTietNhanDAO.maNhan " +
            "WHERE ChiTietNhanDAO.maGhiChu = :maGhiChu")
    List<Nhan> getListNhanOfGhiChu(int maGhiChu);
    @Insert
    long themChiTietNhan(ChiTietNhan chiTietNhan);
    @Query("DELETE FROM ChiTietNhanDAO WHERE maNhan = :maNhan AND maGhiChu = :maGhiChu")
    int xoaChiTietNhan(int maNhan, int maGhiChu);
}
