package com.gokulahealth

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GokulaHealthApp : Application() {

    companion object {
        const val CHANNEL_ID = "vaccination_reminders"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming cattle vaccinations"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
