package com.gokulahealth.notification

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.gokulahealth.GokulaHealthApp
import com.gokulahealth.R

class VaccinationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val vaccinationId = intent.getLongExtra("vaccination_id", 0)
        val vaccineName = intent.getStringExtra("vaccine_name") ?: "Vaccination"
        val cattleName = intent.getStringExtra("cattle_name") ?: "your cattle"

        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, GokulaHealthApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_syringe)
            .setContentTitle("Vaccination Reminder")
            .setContentText("💉 $vaccineName due for $cattleName today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(vaccinationId.toInt(), notification)
    }
}
