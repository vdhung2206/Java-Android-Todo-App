package com.example.todoapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todoapp.Entity.GhiChu;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, MainActivity.class);
        GhiChu ghiChu = intent.getExtras().getParcelable("ghiChu");
        Log.d("AlarmTest", "Received alarm for GhiChu: " + ghiChu.getTieuDe());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity,0);
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

//        int nhacLapLai = ghiChu.getNhacLapLai();
//        if (nhacLapLai > 0) {
//            // Create a new intent for the next action (if needed)
//            Intent nextIntent = new Intent(context, MainActivity.class);
//            PendingIntent nextPendingIntent = PendingIntent.getActivity(context, 0, nextIntent, 0);
//
//            // Create a new alarm for the next action
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            if (alarmManager != null) {
//                // Set the time for the next action based on nhacLapLai
//                long nextTimeInMillis = calculateNextTime(nhacLapLai);
//
//                // Set the new alarm with the new PendingIntent
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTimeInMillis, nextPendingIntent);
//            }
//        }
    }
//    private long calculateNextTime(int nhacLapLai) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//
//        // Tính toán thời gian cho lần nhắc tiếp theo dựa trên giá trị nhacLapLai
//        switch (nhacLapLai) {
//            case 1:
//                // Lời nhắc tiếp theo vào ngày mai
//                calendar.add(Calendar.DAY_OF_YEAR, 1);
//                break;
//            case 2:
//                // Lời nhắc tiếp theo vào ngày đó của tuần sau
//                calendar.add(Calendar.WEEK_OF_YEAR, 1);
//                break;
//            case 3:
//                // Lời nhắc tiếp theo vào ngày đó của tháng sau
//                calendar.add(Calendar.MONTH, 1);
//                break;
//            case 4:
//                // Lời nhắc tiếp theo vào ngày đó của năm sau
//                calendar.add(Calendar.YEAR, 1);
//                break;
//            default:
//                // Không có lời nhắc tiếp theo
//                break;
//        }
//
//        return calendar.getTimeInMillis();
//    }
}