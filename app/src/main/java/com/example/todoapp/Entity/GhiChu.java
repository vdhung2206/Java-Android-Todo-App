package com.example.todoapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GhiChu")
public class GhiChu implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int maGhiChu;
    private String tieuDe;
    private String noiDung;
    private String ngaySuaDoi;
    private String gioSuaDoi;
    private int daXoa;
    private String ngayXoa;
    private String gioXoa;
    private String hinhAnh;
    private int dangDanhSach;
    private int coLoiNhac;
    private String ngayNhac;
    private String gioNhac;
    private int nhacLapLai;
    private int UID;
    private boolean isSelected = false;
    private int order;
    private int isPin;

    protected GhiChu(Parcel in) {
        maGhiChu = in.readInt();
        UID = in.readInt();
        tieuDe = in.readString();
        noiDung = in.readString();
        ngaySuaDoi = in.readString();
        gioSuaDoi = in.readString();
        daXoa = in.readInt();
        ngayXoa = in.readString();
        gioXoa = in.readString();
        hinhAnh = in.readString();
        dangDanhSach = in.readInt();
        coLoiNhac = in.readInt();
        ngayNhac = in.readString();
        gioNhac = in.readString();
        nhacLapLai = in.readInt();
        order = in.readInt();
        isPin = in.readInt();
    }

    public static final Creator<GhiChu> CREATOR = new Creator<GhiChu>() {
        @Override
        public GhiChu createFromParcel(Parcel in) {
            return new GhiChu(in);
        }

        @Override
        public GhiChu[] newArray(int size) {
            return new GhiChu[size];
        }
    };

    public GhiChu() {
    }

    public int getMaGhiChu() {
        return maGhiChu;
    }

    public void setMaGhiChu(int maGhiChu) {
        this.maGhiChu = maGhiChu;
    }

    public int getIsPin() {
        return isPin;
    }

    public void setIsPin(int isPin) {
        this.isPin = isPin;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNgaySuaDoi() {
        return ngaySuaDoi;
    }

    public void setNgaySuaDoi(String ngaySuaDoi) {
        this.ngaySuaDoi = ngaySuaDoi;
    }

    public String getGioSuaDoi() {
        return gioSuaDoi;
    }

    public void setGioSuaDoi(String gioSuaDoi) {
        this.gioSuaDoi = gioSuaDoi;
    }

    public int getDaXoa() {
        return daXoa;
    }

    public void setDaXoa(int daXoa) {
        this.daXoa = daXoa;
    }

    public String getNgayXoa() {
        return ngayXoa;
    }

    public void setNgayXoa(String ngayXoa) {
        this.ngayXoa = ngayXoa;
    }

    public String getGioXoa() {
        return gioXoa;
    }

    public void setGioXoa(String gioXoa) {
        this.gioXoa = gioXoa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getDangDanhSach() {
        return dangDanhSach;
    }

    public void setDangDanhSach(int dangDanhSach) {
        this.dangDanhSach = dangDanhSach;
    }

    public int getCoLoiNhac() {
        return coLoiNhac;
    }

    public void setCoLoiNhac(int coLoiNhac) {
        this.coLoiNhac = coLoiNhac;
    }

    public String getNgayNhac() {
        return ngayNhac;
    }

    public void setNgayNhac(String ngayNhac) {
        this.ngayNhac = ngayNhac;
    }

    public String getGioNhac() {
        return gioNhac;
    }

    public void setGioNhac(String gioNhac) {
        this.gioNhac = gioNhac;
    }

    public int getNhacLapLai() {
        return nhacLapLai;
    }

    public void setNhacLapLai(int nhacLapLai) {
        this.nhacLapLai = nhacLapLai;
    }
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(maGhiChu);
        dest.writeInt(UID);
        dest.writeString(tieuDe);
        dest.writeString(noiDung);
        dest.writeString(ngaySuaDoi);
        dest.writeString(gioSuaDoi);
        dest.writeInt(daXoa);
        dest.writeString(ngayXoa);
        dest.writeString(gioXoa);
        dest.writeString(hinhAnh);
        dest.writeInt(dangDanhSach);
        dest.writeInt(coLoiNhac);
        dest.writeString(ngayNhac);
        dest.writeString(gioNhac);
        dest.writeInt(nhacLapLai);
        dest.writeInt(order);
        dest.writeInt(isPin);
    }
}
