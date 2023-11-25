package com.example.todoapp.Entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "TaiKhoan")
public class TaiKhoan {
    @PrimaryKey(autoGenerate = true)
    int UID;
    @ColumnInfo(name = "taiKhoan")
    String taikhoan;
    @ColumnInfo(name = "matKhau")
    String matKhau;
    public TaiKhoan(int UID, String taikhoan, String matKhau) {
        this.UID = UID;
        this.taikhoan = taikhoan;
        this.matKhau = matKhau;
    }
    @Ignore
    public TaiKhoan(String taikhoan, String matKhau) {
        this.taikhoan = taikhoan;
        this.matKhau = matKhau;
    }

    public int getUID() {
        return UID;
    }
    public String getTaikhoan() {
        return taikhoan;
    }

    public void setTaikhoan(String taikhoan) {
        this.taikhoan = taikhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
