package com.example.todoapp;

import static android.app.Activity.RESULT_OK;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Services.GhiChuService;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    GhiChuService ghiChuService;
    @Override
    public void onReceive(Context context, Intent intent) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
        this.ghiChuService = new GhiChuService(ghiChuDAO);

        Intent nextActivity = new Intent(context, SuaGhiChuActivity.class);
        GhiChu ghiChu = intent.getExtras().getParcelable("ghiChu");
        ghiChu.setDaGui(1);
        ghiChuService.suaGhiChu(ghiChu,ghiChu.getMaGhiChu());
        nextActivity.putExtra("ghiChuDaTao", ghiChu);
        nextActivity.putExtra("loai", "sua");
        nextActivity.putExtra("notify", "true");
        Log.d("MyApp", "Value of 'loai' in onReceive: " + nextActivity.getStringExtra("loai"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Generate a unique request code for each notification
        int requestCode = ghiChu.getMaGhiChu();

        // Add a unique data to the Intent to make it distinct
        nextActivity.setData(Uri.parse("custom://" + requestCode));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, nextActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidknowledge")
                .setSmallIcon(R.drawable.baseline_lightbulb_24)
                .setContentText("Hôm nay, " + ghiChu.getGioNhac())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if(ghiChu.getTieuDe()!=null && !ghiChu.getTieuDe().isEmpty()){
            builder.setContentTitle(ghiChu.getTieuDe());
        } else {
            builder.setContentTitle(ghiChu.getNoiDung());
        }

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(ghiChu.getMaGhiChu(), builder.build());
    }

}