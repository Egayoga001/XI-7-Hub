package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ClassPost
import com.example.data.model.DayOfWeekEnum
import com.example.data.model.LessonPeriod
import com.example.data.repository.ClassRepository
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_ID = "xi7_picket_channel"
    const val CHANNEL_NAME = "Pengingat Piket & Pelajaran XI-7"
    const val NOTIFICATION_ID_PICKET = 1007
    const val NOTIFICATION_ID_LESSON = 1008
    const val NOTIFICATION_ID_POST = 1009
    const val ACTION_PICKET_REMINDER = "com.aistudio.xi7hub.ACTION_PICKET_REMINDER"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Notifikasi pengingat jam pelajaran, jadwal piket kelas, dan postingan materi XI-7 SMAN 3 Blitar"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPicketNotification(
        context: Context,
        studentName: String,
        isPicketToday: Boolean,
        friendsList: List<String>,
        scheduleSummary: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title: String
        val message: String

        if (studentName == "Semua Siswa") {
            title = "🔔 Jadwal Piket & KBM XI-7 Hari Ini"
            val petugas = friendsList.joinToString(", ")
            message = "Petugas piket hari ini: $petugas. $scheduleSummary"
        } else if (isPicketToday) {
            title = "🧹 Giliran Piketmu Hari Ini (XI-7)!"
            val partners = friendsList.filter { it != studentName }.joinToString(", ")
            message = "Halo $studentName! Hari ini giliranmu piket bersama: $partners. Harap hadir sebelum jam 06.45 WIB untuk kebersihan kelas ya! ✨"
        } else {
            title = "📚 Jadwal Pelajaran XI-7 Hari Ini"
            message = "Halo $studentName! Semangat belajar hari ini. $scheduleSummary"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_hub)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PICKET, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Requirement #3:
     * Pengingat jam pelajaran ke-berapa, pelajaran apa, dan sampai ke jam berapa.
     */
    fun showLessonAlertNotification(
        context: Context,
        lesson: LessonPeriod
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeParts = lesson.timeRange.split("-")
        val endTimeStr = if (timeParts.size >= 2) timeParts[1].trim() else lesson.timeRange

        val title = "⏰ ${lesson.periodLabel}: ${lesson.subject}"
        val message = "Sekarang ${lesson.periodLabel} (${lesson.timeRange}) mata pelajaran ${lesson.subject} berlangsung sampai pukul $endTimeStr WIB. Pengajar: ${lesson.teacherName} (${lesson.teacherCode})."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_hub)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_LESSON, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Requirement #1:
     * Notifikasi ketika Admin mengunggah postingan baru (gambar, video, dokumen).
     */
    fun showNewPostNotification(
        context: Context,
        post: ClassPost
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "📢 Postingan Baru: ${post.title}"
        val lampiran = if (post.attachmentFileName.isNotEmpty()) " (Lampiran: ${post.attachmentType.displayName} - ${post.attachmentFileName})" else ""
        val message = "${post.authorName} (${post.authorRole}) telah mengunggah informasi baru: \"${post.content.take(80)}...\"$lampiran. Buka aplikasi untuk melihat."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_hub)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_POST, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun scheduleDailyReminder(context: Context, hour: Int = 6, minute: Int = 30) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PicketNotificationReceiver::class.java).apply {
            action = ACTION_PICKET_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerTestNotification(context: Context) {
        val repo = ClassRepository(context)
        val studentName = repo.getActiveStudentName()
        val calendar = Calendar.getInstance()
        val dayEnum = DayOfWeekEnum.fromDayNumber(calendar.get(Calendar.DAY_OF_WEEK))
        val picketMap = repo.getPicketSchedule()
        val dutyList = picketMap[dayEnum] ?: emptyList()
        val isPicket = dutyList.contains(studentName)

        val lessons = ClassRepository.getDefaultLessons().filter { it.day == dayEnum && !it.isBreak }
        val sched = if (lessons.isNotEmpty()) {
            "Mapel hari ini: " + lessons.take(3).joinToString(", ") { "${it.periodLabel} ${it.subject}" }
        } else {
            "Hari ini libur KBM."
        }

        showPicketNotification(
            context = context,
            studentName = studentName,
            isPicketToday = if (dayEnum == DayOfWeekEnum.SABTU || dayEnum == DayOfWeekEnum.MINGGU) true else isPicket,
            friendsList = if (dutyList.isNotEmpty()) dutyList else listOf("Erastus", "Cahaya", "Edsel"),
            scheduleSummary = sched
        )

        // Also trigger lesson alert demonstration
        val demoLesson = lessons.firstOrNull() ?: ClassRepository.getDefaultLessons().first { it.day == DayOfWeekEnum.SENIN && !it.isBreak }
        showLessonAlertNotification(context, demoLesson)
    }
}
