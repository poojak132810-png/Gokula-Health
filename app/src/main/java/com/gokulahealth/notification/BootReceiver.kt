package com.gokulahealth.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gokulahealth.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all vaccination alarms after device reboot
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val vaccinations = db.vaccinationDao().getAllVaccinationsSync()
                val cattleDao = db.cattleDao()

                for (vaccination in vaccinations) {
                    if (vaccination.nextDueDate > System.currentTimeMillis()) {
                        val cattle = cattleDao.getCattleByIdSync(vaccination.cattleId)
                        val cattleName = cattle?.name ?: "Unknown"
                        NotificationHelper.scheduleVaccinationAlarm(
                            context,
                            vaccination.id,
                            vaccination.vaccineName,
                            cattleName,
                            vaccination.nextDueDate
                        )
                    }
                }
            }
        }
    }
}
