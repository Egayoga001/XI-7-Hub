package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.model.DayOfWeekEnum
import com.example.data.repository.ClassRepository
import java.util.Calendar

class PicketNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repo = ClassRepository(context)

        // Reschedule alarm on boot
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val timeParts = repo.getNotificationTime().split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 6
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 30
            NotificationHelper.scheduleDailyReminder(context, hour, minute)
            return
        }

        if (!repo.isNotificationEnabled()) {
            return
        }

        val studentName = repo.getActiveStudentName()
        val calendar = Calendar.getInstance()
        val dayEnum = DayOfWeekEnum.fromDayNumber(calendar.get(Calendar.DAY_OF_WEEK))

        // On weekends, don't trigger picket alarms unless explicitly testing
        if (dayEnum == DayOfWeekEnum.SABTU || dayEnum == DayOfWeekEnum.MINGGU) {
            return
        }

        val picketMap = repo.getPicketSchedule()
        val dutyList = picketMap[dayEnum] ?: emptyList()
        val isPicketToday = dutyList.contains(studentName)

        val lessons = ClassRepository.getDefaultLessons().filter { it.day == dayEnum && !it.isBreak }
        val sched = if (lessons.isNotEmpty()) {
            "Mapel hari ini: " + lessons.take(3).joinToString(", ") { it.subject }
        } else {
            "Tidak ada jam pelajaran."
        }

        NotificationHelper.showPicketNotification(
            context = context,
            studentName = studentName,
            isPicketToday = isPicketToday,
            friendsList = dutyList,
            scheduleSummary = sched
        )
    }
}
