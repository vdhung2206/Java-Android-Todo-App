package com.example.todoapp.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ChiTietNhanDAO",
        foreignKeys = {
                @ForeignKey(
                        entity = GhiChu.class,
                        parentColumns = "maGhiChu",
                        childColumns = "maGhiChu",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Nhan.class,
                        parentColumns = "maNhan",
                        childColumns = "maNhan",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ChiTietNhan {
    @PrimaryKey(autoGenerate = true)
    int id;
    int maNhan;
    int maGhiChu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaNhan() {
        return maNhan;
    }

    public int getMaGhiChu() {
        return maGhiChu;
    }

    public void setMaGhiChu(int maGhiChu) {
        this.maGhiChu = maGhiChu;
    }

    public void setMaNhan(int maNhan) {
        this.maNhan = maNhan;
    }

}
