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
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todoapp.Entity.GhiChu;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, SuaGhiChuActivity.class);
        GhiChu ghiChu = intent.getExtras().getParcelable("ghiChu");
        nextActivity.putExtra("ghiChuDaTao", ghiChu);
        nextActivity.putExtra("loai", "sua");
        nextActivity.putExtra("notify","true");
        Log.d("MyApp", "Value of 'loai' in onReceive: " + nextActivity.getStringExtra("loai"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidknowledge")
                .setSmallIcon(R.drawable.baseline_lightbulb_24)
                .setContentText("Hôm nay, " + ghiChu.getGioNhac())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        if(ghiChu.getTieuDe()!=null && !ghiChu.getTieuDe().isEmpty()){
            builder.setContentTitle(ghiChu.getTieuDe());
        } else {
            builder.setContentTitle(ghiChu.getNoiDung());
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(ghiChu.getMaGhiChu(), builder.build());
    }
}