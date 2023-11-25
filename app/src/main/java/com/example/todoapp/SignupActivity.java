package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.RoomDatabase;

import com.example.todoapp.AppDatabase;
import com.example.todoapp.Entity.TaiKhoan;
import com.example.todoapp.databinding.ActivitySignupBinding;
import com.example.todoapp.DAO.TaiKhoanDAO;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private TaiKhoanDAO taiKhoanDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Room Database
        AppDatabase roomDatabase = AppDatabase.getDatabase(this);
        taiKhoanDAO = roomDatabase.taiKhoanDAO();

        binding.btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taiKhoan = binding.taiKhoan.getText().toString();
                String matKhau = binding.matKhau.getText().toString();
                String nhapLaiMatKhau = binding.nhapLaiMatKhau.getText().toString();
                if (taiKhoan.equals("") || matKhau.equals("") || nhapLaiMatKhau.equals("")) {
                    Toast.makeText(SignupActivity.this, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    // Sử dụng Room để kiểm tra tài khoản
                    int taiKhoanDaTonTai = taiKhoanDAO.checkTaiKhoan(taiKhoan);
                    if (taiKhoanDaTonTai == 1) {
                        Toast.makeText(SignupActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (matKhau.equals(nhapLaiMatKhau)) {
                            // Sử dụng Room để thêm tài khoản
                            TaiKhoan taiKhoanMoi = new com.example.todoapp.Entity.TaiKhoan(taiKhoan,matKhau);
                            long taiKhoanId = taiKhoanDAO.themTaiKhoan(taiKhoanMoi);
                            if (taiKhoanId != -1) {
                                Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        binding.chuyenHuongDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
