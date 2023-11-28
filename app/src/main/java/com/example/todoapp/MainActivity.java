package com.example.todoapp;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapters.MyDialog;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.Services.GhiChuService;
import com.example.todoapp.Services.NhanService;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NhanService nhanService;
    List<Nhan> arrayList;
    FirstFragment firstFragment;
    MaterialToolbar collapsingToolbar;
    AppBarLayout hiddenTopBarLayout;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GhiChuService ghiChuService;
    private List<Nhan> listNhan;
    private ActivityResultLauncher<Intent> editNhanLauncher;
    private RecyclerView recyclerView;
    MyDialog dialog;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();
        SharedPreferences preferences = getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
        // Khởi tạo Room Database
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        ghiChuService = new GhiChuService(appDatabase.ghiChuDAO());
        nhanService = new NhanService(appDatabase.nhanDAO());
        listNhan = nhanService.getList(Integer.valueOf(preferences.getString("UID", "")));
        Toolbar toolbar = findViewById(R.id.topbar);
        hiddenTopBarLayout = findViewById(R.id.hiddentopbarlayout);
        setSupportActionBar(toolbar);
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.topbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnDangXuat) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remember", "false");
                editor.putString("user", "");
                editor.apply();
                startActivity(intent);
            }
            return true;
        });

        final ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            GhiChu newNote = result.getData().getParcelableExtra("new_note");
                            if (newNote != null) {
                                FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                                if (result.getData().getStringExtra("loai").equals("them")) {
                                    if (firstFragment != null) {
                                        firstFragment.addNewNote(newNote);
                                    }
                                }
                            }
                            if (result.getData().getStringExtra("huyghichurong") != null) {
                                if (result.getData().getStringExtra("huyghichurong").equals("true")) {
                                    Toast.makeText(this, "Đã hủy ghi chú rỗng!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );
        editNhanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            listNhan = nhanService.getList(Integer.parseInt(preferences.getString("UID", "")));
                            updateNavigationView();
                            firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                            firstFragment.reLoad();
                        }
                    }
                }
        );

        binding.btnThemGhiChu.setOnClickListener(view -> {
            GhiChu ghiChu = new GhiChu();

            String taiKhoanGhiNho = preferences.getString("UID", "");
            ghiChu.setUID(Integer.valueOf(taiKhoanGhiNho));
            ghiChu.setNoiDung("");

            Date currentTime = new Date();

            // Định dạng thời gian thành chuỗi
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(currentTime);

            // Định dạng giờ/phút/giây
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = timeFormat.format(currentTime);
            ghiChu.setNgaySuaDoi(formattedDate);
            ghiChu.setGioSuaDoi(formattedTime);

            ghiChu.setOrder(1);

            GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
            ghiChuDAO.tangOrder(1, taiKhoanGhiNho);

            long maGhiChu = ghiChuDAO.themGhiChu(ghiChu);
            Intent intent = new Intent(getApplicationContext(), SuaGhiChuActivity.class);
            ghiChu.setMaGhiChu((int) maGhiChu);
            intent.putExtra("ghiChuDaTao", ghiChu);
            intent.putExtra("loai", "them");
            editNoteLauncher.launch(intent);
        });
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTopBar();
                firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                firstFragment.changeMode();
            }
        });
        collapsingToolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_pin) {
                FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                firstFragment.PinChoices();
            } else if (itemId == R.id.menu_notify) {
//                openDateTimePickerDialog();
//                MyDialog dialog = MyDialog.newInstance();
//                dialog.show(getSupportFragmentManager(),"");
                dialog = MyDialog.newInstance(new MyDialog.OnDialogDismissListener() {
                    @Override
                    public void onDismiss() {
                        dialog.setFirstOpenDialog(true);
                        dialog.setHasNotify(false);
                        dialog.setTimeSeted(true);
                        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                        firstFragment.changeMode();
                        hideTopBar();
                    }
                });
                dialog.show(getSupportFragmentManager(),"");
                dialog.setDialogListener(new MyDialog.DialogButtonListener() {
                    @Override
                    public void onSaveClicked() {
                        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String formattedDate = dateFormat.format(dialog.getSelectedDate().getTime());
                        String formattedTime = timeFormat.format(dialog.getSelectedTime().getTime());

                        ArrayList<GhiChu> list = firstFragment.getSelectedNotes();

                        String dateTimeString = formattedDate + " " + formattedTime;
                        Date dateTime = null;
                        try {
                            dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(dateTimeString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        firstFragment.setNotifyForSelectedNotes(formattedDate, formattedTime, dialog.getRepeat());

                        for (int i = 0; i < list.size(); i++) {
                            GhiChu ghiChu = list.get(i);
                            long timeInMillis = dateTime != null ? dateTime.getTime() : 0;

                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                            intent.putExtra("ghiChu", ghiChu);
                            PendingIntent pendingIntent;

                            if (ghiChu.getNhacLapLai() > 0) {
                                // Sử dụng setRepeating nếu nhacLapLai > 0
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ghiChu.getMaGhiChu(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY,pendingIntent);
                            } else {
                                // Sử dụng setExactAndAllowWhileIdle nếu nhacLapLai = 0
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ghiChu.getMaGhiChu(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                            }
                        }

                        dialog.dismiss();
                        dialog.setFirstOpenDialog(true);
                        dialog.setHasNotify(false);
                        dialog.setTimeSeted(true);
                        firstFragment.changeMode();
                    }


                    @Override
                    public void onCancelClicked() {
                        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                        dialog.dismiss(); // If "alertDialog" is your dialog instance
                        dialog.setFirstOpenDialog(true);
                        dialog.setHasNotify(false);
                        dialog.setTimeSeted(true);
                        firstFragment.changeMode();
                    }

                    @Override
                    public void onDeleteClicked() {
                        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                        firstFragment.deleteNotifyForSelectedNotes();
                        dialog.dismiss();
                        dialog.setFirstOpenDialog(true);
                        dialog.setHasNotify(false);
                        dialog.setTimeSeted(true);
                    }
                });
                dialog.setAction(new MyDialog.Action() {
                    @Override
                    public void setupDateTime() {
                        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                        ArrayList<GhiChu> list = firstFragment.getSelectedNotes();
                        if(list.size()==1){
                            GhiChu ghiChu = list.get(0);
                            if(ghiChu.getCoLoiNhac()==0){
                                dialog.setTitle("Thêm lời nhắc");
                            } else{
                                dialog.showButtonDelete();
                                dialog.setTitle("Chỉnh sửa lời nhắc");
                                dialog.setHasNotify(true);
                                dialog.setTimeSeted(false);
                                String ngayNhac = ghiChu.getNgayNhac();
                                String gioNhac = ghiChu.getGioNhac();
                                int repeatOption = ghiChu.getNhacLapLai();
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    Date convertedDate = dateFormat.parse(ngayNhac);
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                    Date convertedTime = timeFormat.parse(gioNhac);
                                    dialog.setSelectedDate(convertedDate);
                                    dialog.setSelectedTime(convertedTime);
                                    dialog.repeatOption = repeatOption;
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } else{
                            dialog.setTitle("Thêm lời nhắc");
                        }
                        Calendar currentTime = Calendar.getInstance();
                        currentTime.add(Calendar.HOUR_OF_DAY, 3);
                        if(!dialog.getHasNotify()){
                            if (currentTime.get(Calendar.DAY_OF_YEAR) > dialog.getSelectedDate().get(Calendar.DAY_OF_YEAR)) {
                                dialog.selectedDate = (Calendar) currentTime.clone(); // Sao chép ngày mới
                                dialog.selectedTime.set(Calendar.HOUR_OF_DAY,8);
                                dialog.selectedTime.set(Calendar.MINUTE,0);
                            } else {
                                dialog.selectedTime = currentTime; // Nếu không sang ngày mới, chỉ cập nhật thời gian.

                                Calendar toDay = Calendar.getInstance();
                                dialog.selectedDate = toDay;
                                int minute = dialog.selectedTime.get(Calendar.MINUTE);
                                if (minute < 15) {
                                    dialog.selectedTime.set(Calendar.MINUTE, 0);
                                } else if (minute < 30) {
                                    dialog.selectedTime.set(Calendar.MINUTE, 30);
                                } else if (minute < 45) {
                                    dialog.selectedTime.set(Calendar.MINUTE, 30);
                                } else {
                                    dialog.selectedTime.add(Calendar.HOUR, 1);
                                    dialog.selectedTime.set(Calendar.MINUTE, 0);
                                    if (dialog.selectedTime.get(Calendar.HOUR_OF_DAY) == 0) {
                                        dialog.selectedDate.add(Calendar.DAY_OF_YEAR, 1);
                                        dialog.selectedTime.set(Calendar.HOUR_OF_DAY, 8);
                                    }
                                }
                            }
                        }
                    }
                });
//                ok();
            } else if (itemId == R.id.menu_delete) {
                FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                firstFragment.markSelectedItemsAsDeleted();
                return true;
            } else if (itemId == R.id.more) {
                // Xử lý khi người dùng chọn "Thêm"
            } else {
                // Xử lý cho trường hợp mặc định (nếu cần)
            }
            return false;
        });
        replaceFragment(new FirstFragment());
//        replaceFragment2(new SecondFragment());
    }
    public void hideTopBar() {
        AppBarLayout topBarLayout = findViewById(R.id.topbarlayout);
        MaterialToolbar topbar = findViewById(R.id.topbar);
        topbar.bringToFront();
        Animation slideOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);
        hiddenTopBarLayout.startAnimation(slideOutAnimation);
        collapsingToolbar.setVisibility(View.INVISIBLE);
        //hiddenTopBarLayout.setVisibility(View.INVISIBLE);
        topbar.setVisibility(View.VISIBLE);
        topBarLayout.setVisibility(View.VISIBLE);
        Animation slideInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);
        topBarLayout.startAnimation(slideInAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int maxWidth = 200; // Ví dụ: giới hạn kích thước là 200px
        getMenuInflater().inflate(R.menu.topbar, menu);
        navigationView.inflateMenu(R.menu.sidebar);

        Menu navigationMenu = navigationView.getMenu();

        SubMenu parentMenu = navigationMenu.findItem(R.id.menuNhan).getSubMenu();
        parentMenu.clear();
        int lastPosition = 0;
        if (listNhan.size() > 0) {
            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
            for (Nhan nhan : listNhan) {
                parentMenu.add(Menu.NONE, Menu.NONE, 1, nhan.getTenNhan());
            }
            lastPosition = parentMenu.size();
            for (int i = 0; i < listNhan.size(); i++) {
                MenuItem menuItemMucMoi = parentMenu.getItem(i);
                menuItemMucMoi.setIcon(R.drawable.outline_label_24);
                final String tenNhan = listNhan.get(i).getTenNhan();
                menuItemMucMoi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MainActivity.this, "Bạn đã chọn: " + tenNhan, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                if (getMenuItemTextWidth(menuItemMucMoi) > maxWidth) {
                    // Văn bản vượt quá giới hạn, thêm dấu "..."
                    String truncatedText = truncateTextWithEllipsis(tenNhan, maxWidth);
                    menuItemMucMoi.setTitle(truncatedText);
                }
            }
        }
        MenuItem newItem = parentMenu.add(Menu.NONE, Menu.NONE, lastPosition, "Quản lý nhãn");
        newItem.setIcon(R.drawable.outline_edit_24); // Đặt icon nếu cần
        newItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Gọi đến trang editNhan ở đây
                Intent intent = new Intent(MainActivity.this, ChinhSuaNhanActivity.class);
                editNhanLauncher.launch(intent);
                return true; // Đánh dấu rằng sự kiện đã được xử lý
            }
        });
        return true;
    }
    private void updateNavigationView() {
        int maxWidth = 200; // Ví dụ: giới hạn kích thước là 200px
        Menu navigationMenu = navigationView.getMenu();
        // Tạo lại các mục menu với danh sách nhãn mới
        SubMenu parentMenu = navigationMenu.findItem(R.id.menuNhan).getSubMenu();
        parentMenu.clear();
        for (Nhan nhan : listNhan) {
            parentMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, nhan.getTenNhan());
        }
        // Đặt icon và sự kiện onClick cho các mục menu
        for (int i = 0; i < listNhan.size(); i++) {
            MenuItem menuItemMucMoi = parentMenu.getItem(i);
            menuItemMucMoi.setIcon(R.drawable.outline_label_24);
            final String tenNhan = listNhan.get(i).getTenNhan();
            menuItemMucMoi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(MainActivity.this, "Bạn đã chọn: " + tenNhan, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            if (getMenuItemTextWidth(menuItemMucMoi) > maxWidth) {
                // Văn bản vượt quá giới hạn, thêm dấu "..."
                String truncatedText = truncateTextWithEllipsis(tenNhan, maxWidth);
                menuItemMucMoi.setTitle(truncatedText);
            }
        }
        MenuItem newItem = parentMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Quản lý nhãn");
        newItem.setIcon(R.drawable.outline_edit_24); // Đặt icon nếu cần
        newItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Gọi đến trang editNhan ở đây
                Intent intent = new Intent(MainActivity.this, ChinhSuaNhanActivity.class);
                editNhanLauncher.launch(intent);
                return true; // Đánh dấu rằng sự kiện đã được xử lý
            }
        });
    }

    private int getMenuItemTextWidth(MenuItem menuItem) {
        String text = menuItem.getTitle().toString();
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.text_size));
        return (int) paint.measureText(text);
    }
    private String truncateTextWithEllipsis(String text, int maxWidth) {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.text_size)); // Đặt kích thước văn bản tùy chọn
        String ellipsis = "...";
        int textWidth = (int) paint.measureText(text);
        int ellipsisWidth = (int) paint.measureText(ellipsis);
        while (textWidth + ellipsisWidth > maxWidth) {
            text = text.substring(0, text.length() - 1);
            textWidth = (int) paint.measureText(text);
        }
        return text + ellipsis;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.loiNhac) {
            Toast.makeText(this, "Lời nhắc", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.ghiChu) {
            Toast.makeText(this, "Ghi chú", Toast.LENGTH_SHORT).show();
        }
//        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}