package com.example

import android.app.Application
import com.example.data.repository.ClassRepository
import com.example.reminder.NotificationHelper

class XI7Application : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        val repo = ClassRepository(this)
        if (repo.isNotificationEnabled()) {
            val parts = repo.getNotificationTime().split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 6
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 30
            NotificationHelper.scheduleDailyReminder(this, h, m)
        }
    }
}
