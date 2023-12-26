package com.example.todoapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;

import com.example.todoapp.Adapters.MyDialog;
import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.Services.ChiTietNhanService;
import com.example.todoapp.Services.GhiChuService;
import com.example.todoapp.databinding.ActivityThemGhiChuBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SuaGhiChuActivity extends AppCompatActivity {
    GhiChuService ghiChuService;
    private boolean isGhiChuEmpty = true;
    private boolean isChange = false;
    ChiTietNhanService chiTietNhanService;
    private ActionMenuView amvMenu;
    GhiChu ghiChu;
    private ActivityResultLauncher<Intent> ganNhanLauncher;
    private ActivityThemGhiChuBinding binding;
    MyDialog dialog;
    String loai;
    String notify;
    TextView dateTimeTextView;
    TextView notifyTextView;
    private AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
        ChiTietNhanDAO chiTietNhanDAO = appDatabase.chiTietNhanDAO();
        this.ghiChuService = new GhiChuService(ghiChuDAO);
        this.chiTietNhanService = new ChiTietNhanService(chiTietNhanDAO);

        binding = ActivityThemGhiChuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar t = findViewById(R.id.bottomBar);
        amvMenu = t.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ghiChu = getIntent().getParcelableExtra("ghiChuDaTao");
        loai = getIntent().getStringExtra("loai");
        notify = getIntent().getStringExtra("notify");
        if(notify==null){
            notify = "false";
        }

        if(loai != null && getIntent().getStringExtra("loai").equals("sua")){
            binding.tieuDe.setText(ghiChu.getTieuDe());
            binding.noiDung.setText(ghiChu.getNoiDung());
        }
        listNhan();
        addDateTimeLabel(ghiChu);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isChange = true;
                isGhiChuEmpty = false;
            }
        };
        binding.noiDung.addTextChangedListener(textWatcher);
        binding.tieuDe.addTextChangedListener(textWatcher);

        String thoiGianChinhSua = createThoiGianChinhSuaText(ghiChu.getNgaySuaDoi(), ghiChu.getGioSuaDoi());
        binding.thayDoiLanCuoi.setText(thoiGianChinhSua);

        binding.topbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGhiChuEmpty && !getIntent().getStringExtra("loai").equals("sua")) {
                    SharedPreferences preferences = getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
                    ghiChuService.xoaGhiChu(ghiChu);
                    ghiChuDAO.giamOrder(ghiChu.getOrder(),preferences.getString("UID", ""));
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("huyghichurong", "true");
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    if(notify.equals("true")){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                    onBackPressed();
                } else {
                    Intent resultIntent = new Intent();
                    ghiChu.setTieuDe(String.valueOf(binding.tieuDe.getText()));

                    if(binding.noiDung.getText()==null){
                        ghiChu.setNoiDung("");
                    }
                    else{
                        ghiChu.setNoiDung(String.valueOf(binding.noiDung.getText()));
                    }
                    if(loai != null && getIntent().getStringExtra("loai").equals("sua")){
                        resultIntent.putExtra("loai","sua");
                        resultIntent.putExtra("position", getIntent().getIntExtra("position",-1));
                    }
                    else{
                        resultIntent.putExtra("loai","them");
                    }
                    resultIntent.putExtra("new_note", ghiChu);
                    if (isChange){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date currentTime = new Date();
                        String formattedDate = dateFormat.format(currentTime);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String formattedTime = timeFormat.format(currentTime);
                        ghiChu.setNgaySuaDoi(formattedDate);
                        ghiChu.setGioSuaDoi(formattedTime);
                        ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
                    }

                    setResult(RESULT_OK, resultIntent);
                    finish();
                    if(notify.equals("true")){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                    onBackPressed();
                }
                }
        });
        ganNhanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        listNhan();
                        addDateTimeLabel(ghiChu);
                    }
                }
        );

        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        MaterialToolbar bottomBar = findViewById(R.id.bottomBar);
        MaterialToolbar topBar = findViewById(R.id.topbar1);
        Window window = getWindow();
        bottomBar.setBackgroundColor(getResources().getColor(R.color.white));
        window.setNavigationBarColor(getResources().getColor(R.color.white));
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = nestedScrollView.getScrollY();

                if (!nestedScrollView.canScrollVertically(1)) {
                    bottomBar.setBackgroundColor(getResources().getColor(R.color.white));
                    window.setNavigationBarColor(getResources().getColor(R.color.white));
                } else {
                    bottomBar.setBackgroundColor(getResources().getColor(R.color.md_theme_light_primary));
                    window.setNavigationBarColor(getResources().getColor(R.color.md_theme_light_primary));
                }
                if (!nestedScrollView.canScrollVertically(-1)) {
                    topBar.setBackgroundColor(getResources().getColor(R.color.white));
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                } else {
                    topBar.setBackgroundColor(getResources().getColor(R.color.md_theme_light_primary));
                    window.setStatusBarColor(getResources().getColor(R.color.md_theme_light_primary));
                }
            }
        });
        notifyTextView = findViewById(R.id.notify);
        Drawable yourIconDrawable = getResources().getDrawable(R.drawable.baseline_done_24_white);
        notifyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, yourIconDrawable, null);
        if(ghiChu.getDaGui()==1){
            if(ghiChu.getDaXong()!=1){
                notifyTextView.setVisibility(View.VISIBLE);
            } else{
                dateTimeTextView.setPaintFlags(dateTimeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        if(ghiChu.getCoLoiNhac()==1 && ghiChu.getDaGui()==1){
            String dateTime = formatDateTime(ghiChu.getNgayNhac(), ghiChu.getGioNhac());
            notifyTextView.setText("Đã gửi vào " + dateTime);
        }
        notifyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ghiChu.getNhacLapLai()!=0){
                    ghiChu.setDaGui(0);
                    ghiChu.setDaXong(0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrow = calendar.getTime();
                    ghiChu.setNgayNhac(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(tomorrow));
                    dateTimeTextView.setTextColor(0xFF313131);
                    String dateTime = formatDateTime(ghiChu.getNgayNhac(), ghiChu.getGioNhac());
                    dateTimeTextView.setText(dateTime);
                    Drawable drawable;
                    drawable = getResources().getDrawable(R.drawable.background_label1);
                    dateTimeTextView.setBackground(drawable);
                    Drawable clockIcon = getResources().getDrawable(R.drawable.baseline_repeat_24);
                    dateTimeTextView.setCompoundDrawablesWithIntrinsicBounds(clockIcon, null, null, null);
                } else{
                    ghiChu.setDaXong(1);
                    dateTimeTextView.setPaintFlags(dateTimeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                ghiChuService.suaGhiChu(ghiChu,ghiChu.getMaGhiChu());
                notifyTextView.setVisibility(View.GONE);
            }
        });
    }
    private String createThoiGianChinhSuaText(String ngayChinhSua, String gioChinhSua) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String homNay = dateFormat.format(calendar.getTime());
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date ngaySua = inputFormat.parse(ngayChinhSua + " " + gioChinhSua);

            Calendar ngaySuaCalendar = Calendar.getInstance();
            ngaySuaCalendar.setTime(ngaySua);
            ngaySuaCalendar.set(Calendar.HOUR_OF_DAY, 0);
            ngaySuaCalendar.set(Calendar.MINUTE, 0);
            ngaySuaCalendar.set(Calendar.SECOND, 0);
            ngaySuaCalendar.set(Calendar.MILLISECOND, 0);
            ngaySuaCalendar.set(Calendar.SECOND, 0);

            Calendar homNayCalendar = Calendar.getInstance();
            homNayCalendar.setTimeInMillis(System.currentTimeMillis());
            homNayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            homNayCalendar.set(Calendar.MINUTE, 0);
            homNayCalendar.set(Calendar.SECOND, 0);
            homNayCalendar.set(Calendar.MILLISECOND, 0);

            if (ngaySuaCalendar.equals(homNayCalendar)) {
                SimpleDateFormat gioChinhSuaFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String gioChinhSuaWithoutSeconds = gioChinhSuaFormat.format(ngaySua);
                return "Đã chỉnh sửa " + gioChinhSuaWithoutSeconds;
            } else {
                homNayCalendar.add(Calendar.DAY_OF_YEAR, -1);
                if (ngaySuaCalendar.equals(homNayCalendar)) {
                    SimpleDateFormat gioChinhSuaFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String gioChinhSuaWithoutSeconds = gioChinhSuaFormat.format(ngaySua);
                    return "Đã chỉnh sửa hôm qua " + gioChinhSuaWithoutSeconds;
                } else {
                    SimpleDateFormat ngayThangFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat gioChinhSuaFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String ngayThang = ngayThangFormat.format(ngaySua);
                    String gioChinhSuaWithoutSeconds = gioChinhSuaFormat.format(ngaySua);
                    return "Đã chỉnh sửa " + ngayThang + " " + gioChinhSuaWithoutSeconds;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottombar1, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.label){
            Intent intent = new Intent(this, GanNhanActivity.class);
            GhiChu ghiChu = getIntent().getParcelableExtra("ghiChuDaTao");
            intent.putExtra("maGhiChu",ghiChu.getMaGhiChu());
            ganNhanLauncher.launch(intent);
        }
        return true;
    }
    public void listNhan(){
        binding.listnhan.removeAllViews();
        ArrayList<Nhan> listNhan = (ArrayList<Nhan>) chiTietNhanService.getListNhanOfGhiChu(ghiChu.getMaGhiChu());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.margin_12dp);
        layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.margin_12dp);
        for(int i=0; i<listNhan.size();i++){
            TextView textView = new TextView(this); // 'this' là một tham chiếu đến Context (hoặc bạn có thể sử dụng getContext() trong một Fragment)
            textView.setText(listNhan.get(i).getTenNhan()); // Đặt nội dung của TextView theo ý muốn
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(16);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), GanNhanActivity.class);
                    GhiChu ghiChu = getIntent().getParcelableExtra("ghiChuDaTao");
                    intent.putExtra("maGhiChu",ghiChu.getMaGhiChu());
                    ganNhanLauncher.launch(intent);
                }
            });
            textView.setTextColor(0xFF313131);
            Drawable drawable = getResources().getDrawable(R.drawable.background_label);
            textView.setBackground(drawable);
            binding.listnhan.addView(textView);
        }
    }
    public void addDateTimeLabel(GhiChu ghiChu) {
        String ngayNhac = ghiChu.getNgayNhac();
        String gioNhac = ghiChu.getGioNhac();


        if (ghiChu.getNgayNhac() != null && !ghiChu.getNgayNhac().isEmpty() &&
                ghiChu.getGioNhac() != null && !ghiChu.getGioNhac().isEmpty()) {
            String dateTime = formatDateTime(ngayNhac, gioNhac);
            dateTimeTextView = new TextView(this);
            dateTimeTextView.setText(dateTime);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.margin_12dp);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.margin_12dp);

            dateTimeTextView.setLayoutParams(layoutParams);
            dateTimeTextView.setTextSize(16);
            if(ghiChu.getDaGui() == 1){
                dateTimeTextView.setTextColor(0xFFA8A7A7);
            } else {
                dateTimeTextView.setTextColor(0xFF313131);
            }
            dateTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = MyDialog.newInstance(new MyDialog.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            dialog.setFirstOpenDialog(true);
                            dialog.setHasNotify(false);
                            dialog.setTimeSeted(true);
                        }
                    });
                    dialog.show(getSupportFragmentManager(),"");
                    dialog.setDialogListener(new MyDialog.DialogButtonListener() {
                        @Override
                        public void onSaveClicked() {

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            String formattedDate = dateFormat.format(dialog.getSelectedDate().getTime());
                            String formattedTime = timeFormat.format(dialog.getSelectedTime().getTime());
                            String dateTimeString = formattedDate + " " + formattedTime;
                            Date dateTime = null;
                            try {
                                dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(dateTimeString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ghiChu.setNgayNhac(formattedDate);
                            ghiChu.setGioNhac(formattedTime);
                            ghiChu.setNhacLapLai(dialog.getRepeat());
                            ghiChu.setDaXong(0);
                            ghiChu.setDaGui(0);
                            ghiChuService.suaGhiChu(ghiChu,ghiChu.getMaGhiChu());
                            dialog.dismiss();
                            dialog.setFirstOpenDialog(true);
                            dialog.setHasNotify(false);
                            dialog.setTimeSeted(true);
                            updateDateTimeLabel(ghiChu);
                            long timeInMillis = dateTime != null ? dateTime.getTime() : 0;

                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(SuaGhiChuActivity.this, AlarmReceiver.class);
                            intent.putExtra("ghiChu", ghiChu);
                            PendingIntent pendingIntent;
                            notifyTextView.setVisibility(View.GONE);
                            if (ghiChu.getNhacLapLai() > 0) {
                                pendingIntent = PendingIntent.getBroadcast(SuaGhiChuActivity.this, ghiChu.getMaGhiChu(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY,pendingIntent);
                            } else {
                                pendingIntent = PendingIntent.getBroadcast(SuaGhiChuActivity.this, ghiChu.getMaGhiChu(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                            }
                        }

                        @Override
                        public void onCancelClicked() {
                            dialog.dismiss(); // If "alertDialog" is your dialog instance
                            dialog.setFirstOpenDialog(true);
                            dialog.setHasNotify(false);
                            dialog.setTimeSeted(true);
                        }

                        @Override
                        public void onDeleteClicked() {
                            ghiChu.setCoLoiNhac(0);
                            ghiChu.setNgayNhac("");
                            ghiChu.setGioNhac("");
                            ghiChu.setNhacLapLai(0);
                            dialog.dismiss();
                            dialog.setFirstOpenDialog(true);
                            dialog.setHasNotify(false);
                            dialog.setTimeSeted(true);
                            updateDateTimeLabel(ghiChu);
                        }
                    });
                    dialog.setAction(new MyDialog.Action() {
                        @Override
                        public void setupDateTime() {
                            dialog.setTitle("Chỉnh sửa lời nhắc");
                            String ngayNhac = ghiChu.getNgayNhac();
                            String gioNhac = ghiChu.getGioNhac();
                            int repeatOption = ghiChu.getNhacLapLai();
                            try {
                                dialog.showButtonDelete();
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
                    });
                }
            });
            Drawable drawable;
            if(ghiChu.getDaGui() == 1){
                drawable = getResources().getDrawable(R.drawable.background_label2);
            } else {
                drawable = getResources().getDrawable(R.drawable.background_label);
            }
            Drawable clockIcon;
            if(ghiChu.getNhacLapLai()!=0){
                if(ghiChu.getDaGui() == 1){
                    clockIcon = getResources().getDrawable(R.drawable.baseline_repeat_24_sent);
                } else {
                    clockIcon = getResources().getDrawable(R.drawable.baseline_repeat_24);
                }
            } else{
                if(ghiChu.getDaGui() == 1) {
                    clockIcon = getResources().getDrawable(R.drawable.baseline_access_alarm_24);
                } else {
                    clockIcon = getResources().getDrawable(R.drawable.baseline_access_alarms_24);
                }
            }

            dateTimeTextView.setCompoundDrawablesWithIntrinsicBounds(clockIcon, null, null, null);
            dateTimeTextView.setCompoundDrawablePadding(20);
            dateTimeTextView.setBackground(drawable);
            binding.listnhan.addView(dateTimeTextView,0);
        }
    }
    public void updateDateTimeLabel(GhiChu ghiChu){
        binding.listnhan.removeViewAt(0);
        addDateTimeLabel(ghiChu);
    }
    private String formatDateTime(String ngayNhac, String gioNhac) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date convertedDate = dateFormat.parse(ngayNhac);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date convertedTime = timeFormat.parse(gioNhac);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDate);

            Calendar homNay = Calendar.getInstance();
            Calendar homQua = Calendar.getInstance();
            homQua.add(Calendar.DAY_OF_YEAR, -1);
            Calendar ngayMai = Calendar.getInstance();
            ngayMai.add(Calendar.DAY_OF_YEAR, 1);

            if (isSameDay(calendar, homNay)) {
                return "Hôm nay, " + timeFormat.format(convertedTime);
            } else if (isSameDay(calendar, homQua)) {
                return "Hôm qua, " + timeFormat.format(convertedTime);
            } else if (isSameDay(calendar, ngayMai)) {
                return "Ngày mai, " + timeFormat.format(convertedTime);
            } else {
                String ngay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                String thang = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String nam = String.valueOf(calendar.get(Calendar.YEAR));
                return ngay + " thg " + thang + ", " + nam + ", " + timeFormat.format(convertedTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}