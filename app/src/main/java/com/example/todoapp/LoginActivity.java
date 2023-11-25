package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.todoapp.DatabaseHelper;
import com.example.todoapp.Entity.TaiKhoan;
import com.example.todoapp.databinding.ActivityLoginBinding;
import com.example.todoapp.DAO.TaiKhoanDAO;
import com.example.todoapp.AppDatabase;

public class LoginActivity extends AppCompatActivity {

    private TaiKhoanDAO taiKhoanDAO;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Room Database
        AppDatabase roomDatabase = AppDatabase.getDatabase(this);
        taiKhoanDAO = roomDatabase.taiKhoanDAO();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        SharedPreferences preferences = getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
        String ghiNhoDangNhap = preferences.getString("remember","");
        String taiKhoanGhiNho = preferences.getString("user","");
        if(ghiNhoDangNhap.equals("true") && taiKhoanGhiNho !=""){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        binding.btnDangNhap.setOnClickListener(view -> {
            String taiKhoan = binding.taiKhoan.getText().toString();
            String matKhau = binding.matKhau.getText().toString();
            if (taiKhoan.equals("") || matKhau.equals("")) {
                Toast.makeText(LoginActivity.this, "Hãy nhập tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
            } else {
                // Sử dụng Room để kiểm tra đăng nhập
                TaiKhoan checkDangNhap = taiKhoanDAO.checkDangNhap(taiKhoan, matKhau);
                if (checkDangNhap != null) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    // Lưu thông tin đăng nhập vào Room
                    // Bạn có thể sử dụng SharedPreferences nếu bạn muốn lưu ghi nhớ.
                    // ...
                    SharedPreferences sharedPreferences = getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user",taiKhoan);
                    editor.putString("UID",String.valueOf(checkDangNhap.getUID()));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.chuyenHuongDangKy.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
        });
        SharedPreferences sharedPreferences = getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
        if(sharedPreferences.getString("remember","").equals("true")){
            binding.ghiNhoDangNhap.setChecked(true);
        }
        binding.ghiNhoDangNhap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences sharedPreferences = getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember","true");
                    editor.putString("user","");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Đã ghi nhớ!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember","false");
                    editor.putString("user","");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Đã hủy ghi nhớ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Còn lại, bạn có thể giữ nguyên phần ghi nhớ đăng nhập và các cài đặt khác của bạn.
    }
}
