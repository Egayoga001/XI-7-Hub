package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.*
import com.example.data.repository.ClassRepository
import com.example.reminder.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

data class ClassUiState(
    val loggedInAdmin: AdminAccount? = null,
    val registeredAdmins: List<AdminAccount> = emptyList(),
    val activeTab: Int = 0,
    val selectedScheduleDay: DayOfWeekEnum = DayOfWeekEnum.SENIN,
    val todayDayOfWeek: DayOfWeekEnum = DayOfWeekEnum.SENIN,
    val picketSchedule: Map<DayOfWeekEnum, List<String>> = emptyMap(),
    val todayPicketAttendance: Map<String, Boolean> = emptyMap(),
    val classPosts: List<ClassPost> = emptyList(),
    val classStructure: ClassStructure = ClassStructure(),
    val lessons: List<LessonPeriod> = emptyList(),
    val activeLesson: LessonPeriod? = null,
    val nextLesson: LessonPeriod? = null,
    val notificationEnabled: Boolean = true,
    val notificationTime: String = "06:30",
    val selectedStudentForReminder: String = "Semua Siswa",
    val isDarkMode: Boolean = false,
    val feedbackMessage: String? = null,
    val showNotificationPermissionDialog: Boolean = false
)

class ClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ClassRepository(application)

    private val _uiState = MutableStateFlow(ClassUiState())
    val uiState: StateFlow<ClassUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        repo.syncWithFirestore {
            loadInitialData()
        }
    }

    fun loadInitialData() {
        val calendar = Calendar.getInstance()
        val today = DayOfWeekEnum.fromDayNumber(calendar.get(Calendar.DAY_OF_WEEK))
        val currentAdmin = repo.getLoggedInAdmin()
        val adminsList = repo.getAdminAccounts()
        val picketMap = repo.getPicketSchedule()
        val att = repo.getPicketAttendance()
        val postsList = repo.getClassPosts()
        val structure = repo.getClassStructure()
        val lessonsList = repo.getLessons()

        val initialDay = if (today == DayOfWeekEnum.SABTU || today == DayOfWeekEnum.MINGGU) {
            DayOfWeekEnum.SENIN
        } else {
            today
        }

        val (curLesson, nxtLesson) = calculateCurrentAndNextLessons(today, lessonsList)

        val shouldShowPermissionDialog = !repo.hasPromptedInitialNotification()

        _uiState.update {
            it.copy(
                loggedInAdmin = currentAdmin,
                registeredAdmins = adminsList,
                todayDayOfWeek = today,
                selectedScheduleDay = initialDay,
                picketSchedule = picketMap,
                todayPicketAttendance = att,
                classPosts = postsList,
                classStructure = structure,
                lessons = lessonsList,
                activeLesson = curLesson,
                nextLesson = nxtLesson,
                notificationEnabled = repo.isNotificationEnabled(),
                notificationTime = repo.getNotificationTime(),
                selectedStudentForReminder = repo.getActiveStudentName(),
                isDarkMode = repo.isDarkMode(),
                showNotificationPermissionDialog = shouldShowPermissionDialog
            )
        }
    }

    private fun calculateCurrentAndNextLessons(
        today: DayOfWeekEnum,
        allLessons: List<LessonPeriod>
    ): Pair<LessonPeriod?, LessonPeriod?> {
        if (today == DayOfWeekEnum.SABTU || today == DayOfWeekEnum.MINGGU) {
            return Pair(null, null)
        }
        val lessons = allLessons.filter { it.day == today }
        val nowCal = Calendar.getInstance()
        val nowMinutes = nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)

        var cur: LessonPeriod? = null
        var nxt: LessonPeriod? = null

        for (item in lessons) {
            val parts = item.timeRange.split("-")
            if (parts.size == 2) {
                val startM = parseTimeToMinutes(parts[0].trim())
                val endM = parseTimeToMinutes(parts[1].trim())
                if (startM != null && endM != null && nowMinutes in startM..endM) {
                    cur = item
                    break
                }
            }
        }

        for (item in lessons) {
            val parts = item.timeRange.split("-")
            if (parts.size == 2) {
                val startM = parseTimeToMinutes(parts[0].trim())
                if (startM != null && startM > nowMinutes) {
                    nxt = item
                    break
                }
            }
        }

        return Pair(cur, nxt)
    }

    private fun parseTimeToMinutes(timeStr: String): Int? {
        val clean = timeStr.replace(".", ":")
        val parts = clean.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            return h * 60 + m
        }
        return null
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun setSelectedScheduleDay(day: DayOfWeekEnum) {
        _uiState.update { it.copy(selectedScheduleDay = day) }
    }

    // ==========================================
    // ADMIN AUTHENTICATION (MAX 2 ACCOUNTS)
    // ==========================================

    fun loginAdmin(username: String, pass: String): Boolean {
        val logged = repo.loginAdmin(username, pass)
        return if (logged != null) {
            _uiState.update {
                it.copy(
                    loggedInAdmin = logged,
                    feedbackMessage = "Selamat datang, ${logged.fullName} (${logged.roleTitle})!"
                )
            }
            true
        } else {
            _uiState.update {
                it.copy(feedbackMessage = "Username atau password admin salah!")
            }
            false
        }
    }

    fun registerAdmin(username: String, fullName: String, pass: String, roleTitle: String): Boolean {
        val (success, msg) = repo.registerAdmin(username, fullName, pass, roleTitle)
        if (success) {
            _uiState.update {
                it.copy(
                    registeredAdmins = repo.getAdminAccounts(),
                    loggedInAdmin = repo.getLoggedInAdmin(),
                    feedbackMessage = msg
                )
            }
        } else {
            _uiState.update { it.copy(feedbackMessage = msg) }
        }
        return success
    }

    fun logoutAdmin() {
        repo.logoutAdmin()
        _uiState.update {
            it.copy(
                loggedInAdmin = null,
                feedbackMessage = "Berhasil keluar dari mode Admin."
            )
        }
    }

    fun deleteAdmin(adminId: String) {
        repo.deleteAdmin(adminId)
        _uiState.update {
            it.copy(
                registeredAdmins = repo.getAdminAccounts(),
                loggedInAdmin = repo.getLoggedInAdmin(),
                feedbackMessage = "Akun admin berhasil dihapus. Slot kembali tersedia!"
            )
        }
    }

    fun updateAdminProfile(
        id: String,
        username: String,
        fullName: String,
        pass: String,
        roleTitle: String,
        syncWithStructure: Boolean = true
    ): Boolean {
        val (success, msg) = repo.updateAdminAccount(id, username, fullName, pass, roleTitle, syncWithStructure)
        if (success) {
            _uiState.update {
                it.copy(
                    registeredAdmins = repo.getAdminAccounts(),
                    loggedInAdmin = repo.getLoggedInAdmin(),
                    classStructure = repo.getClassStructure(),
                    feedbackMessage = msg
                )
            }
        } else {
            _uiState.update { it.copy(feedbackMessage = msg) }
        }
        return success
    }

    fun resetAdminAccounts() {
        repo.resetAdminAccounts()
        _uiState.update {
            it.copy(
                registeredAdmins = repo.getAdminAccounts(),
                loggedInAdmin = repo.getLoggedInAdmin(),
                classStructure = repo.getClassStructure(),
                feedbackMessage = "Akun admin di-reset ke default awal (admin1 / 123)."
            )
        }
    }

    // ==========================================
    // CLASS POSTS (GAMBAR, VIDEO, DOKUMEN)
    // ==========================================

    fun addPost(
        title: String,
        content: String,
        category: String,
        attachmentType: AttachmentType,
        attachmentUriOrData: String,
        attachmentFileName: String
    ): Boolean {
        val admin = _uiState.value.loggedInAdmin
        if (admin == null) {
            _uiState.update { it.copy(feedbackMessage = "Hanya Admin yang dapat mengunggah postingan!") }
            return false
        }

        val newPost = ClassPost(
            id = "post_${System.currentTimeMillis()}_" + UUID.randomUUID().toString().take(8),
            title = title.trim(),
            content = content.trim(),
            authorName = admin.fullName,
            authorRole = admin.roleTitle,
            createdAt = System.currentTimeMillis(),
            attachmentType = attachmentType,
            attachmentUriOrData = attachmentUriOrData,
            attachmentFileName = attachmentFileName,
            category = category
        )
        repo.addPost(newPost)
        _uiState.update {
            it.copy(
                classPosts = repo.getClassPosts(),
                feedbackMessage = "Postingan berhasil diunggah dan dibagikan ke seluruh siswa!"
            )
        }

        // Broadcast notification to all students
        NotificationHelper.showNewPostNotification(getApplication(), newPost)
        return true
    }

    fun deletePost(postId: String) {
        val admin = _uiState.value.loggedInAdmin
        if (admin == null) {
            _uiState.update { it.copy(feedbackMessage = "Hanya Admin yang dapat menghapus postingan!") }
            return
        }
        repo.deletePost(postId)
        _uiState.update {
            it.copy(
                classPosts = repo.getClassPosts(),
                feedbackMessage = "Postingan telah dihapus."
            )
        }
    }

    // ==========================================
    // CLASS STRUCTURE (STRUKTUR ORGANISASI KELAS)
    // ==========================================

    fun updateClassStructure(structure: ClassStructure) {
        val admin = _uiState.value.loggedInAdmin
        if (admin == null) {
            _uiState.update { it.copy(feedbackMessage = "Hanya Admin yang dapat mengubah susunan pengurus kelas!") }
            return
        }
        repo.saveClassStructure(structure)
        _uiState.update {
            it.copy(
                classStructure = structure,
                feedbackMessage = "Struktur pengurus kelas berhasil diperbarui!"
            )
        }
        if (_uiState.value.notificationEnabled) {
            NotificationHelper.showStructureUpdateNotification(getApplication(), structure)
        }
    }

    // ==========================================
    // NOTIFICATIONS & REMINDERS
    // ==========================================

    fun dismissNotificationPermissionDialog(granted: Boolean) {
        repo.setPromptedInitialNotification(true)
        if (granted) {
            setNotificationEnabled(true)
            NotificationHelper.showWelcomeNotification(getApplication())
        }
        _uiState.update { it.copy(showNotificationPermissionDialog = false) }
    }

    fun openNotificationPermissionDialog() {
        _uiState.update { it.copy(showNotificationPermissionDialog = true) }
    }

    fun triggerLessonAlert(lesson: LessonPeriod) {
        NotificationHelper.showLessonAlertNotification(getApplication(), lesson)
        _uiState.update {
            it.copy(feedbackMessage = "Notifikasi jam ke ${lesson.periodLabel} (${lesson.subject}) dikirim!")
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        repo.setNotificationEnabled(enabled)
        _uiState.update { it.copy(notificationEnabled = enabled) }
        val parts = repo.getNotificationTime().split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 6
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 30
        if (enabled) {
            NotificationHelper.scheduleDailyReminder(getApplication(), h, m)
            _uiState.update { it.copy(feedbackMessage = "Pengingat otomatis diaktifkan pukul $h:${m.toString().padStart(2, '0')}") }
        } else {
            _uiState.update { it.copy(feedbackMessage = "Pengingat otomatis dinonaktifkan") }
        }
    }

    fun setNotificationTime(time: String) {
        repo.setNotificationTime(time)
        _uiState.update { it.copy(notificationTime = time) }
        val parts = time.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 6
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 30
        if (repo.isNotificationEnabled()) {
            NotificationHelper.scheduleDailyReminder(getApplication(), h, m)
        }
        _uiState.update { it.copy(feedbackMessage = "Waktu notifikasi diatur ke $time") }
    }

    fun setStudentForReminder(name: String) {
        repo.setActiveStudentName(name)
        _uiState.update {
            it.copy(
                selectedStudentForReminder = name,
                feedbackMessage = if (name == "Semua Siswa") "Pengingat diatur untuk semua jadwal piket" else "Pengingat piket diatur untuk $name"
            )
        }
    }

    fun togglePicketAttendance(studentName: String, isPresent: Boolean) {
        val updated = _uiState.value.todayPicketAttendance.toMutableMap()
        updated[studentName] = isPresent
        _uiState.update { it.copy(todayPicketAttendance = updated) }
        repo.togglePicketAttendance(studentName, isPresent)
    }

    fun updatePicketSchedule(day: DayOfWeekEnum, studentList: List<String>) {
        val admin = _uiState.value.loggedInAdmin
        if (admin == null) {
            _uiState.update { it.copy(feedbackMessage = "Hanya Admin yang dapat mengubah jadwal piket!") }
            return
        }
        val currentMap = _uiState.value.picketSchedule.toMutableMap()
        currentMap[day] = studentList
        repo.savePicketSchedule(currentMap)
        _uiState.update {
            it.copy(
                picketSchedule = currentMap,
                feedbackMessage = "Jadwal piket hari ${day.displayName} berhasil diperbarui!"
            )
        }
        if (_uiState.value.notificationEnabled) {
            NotificationHelper.showPicketUpdateNotification(getApplication(), day.displayName, studentList)
        }
    }

    fun testNotificationNow() {
        NotificationHelper.triggerTestNotification(getApplication())
        _uiState.update { it.copy(feedbackMessage = "Notifikasi pengingat jam pelajaran & piket dikirim ke bilah status!") }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun updateLesson(
        lessonId: String,
        newSubject: String,
        newTeacherName: String,
        newTeacherCode: String,
        newTimeRange: String,
        isBreak: Boolean
    ) {
        if (_uiState.value.loggedInAdmin == null) {
            _uiState.update { it.copy(feedbackMessage = "Akses ditolak: Hanya admin yang dapat mengedit jadwal pelajaran!") }
            return
        }

        val success = repo.updateLesson(
            lessonId = lessonId,
            newSubject = newSubject,
            newTeacherName = newTeacherName,
            newTeacherCode = newTeacherCode,
            newTimeRange = newTimeRange,
            isBreak = isBreak
        )

        if (success) {
            val periodLabel = _uiState.value.lessons.firstOrNull { it.id == lessonId }?.periodLabel ?: ""
            loadInitialData()
            _uiState.update { it.copy(feedbackMessage = "Jadwal '$newSubject' berhasil diperbarui!") }
            if (_uiState.value.notificationEnabled) {
                NotificationHelper.showScheduleUpdateNotification(
                    getApplication(),
                    newSubject,
                    periodLabel,
                    newTeacherName
                )
            }
        } else {
            _uiState.update { it.copy(feedbackMessage = "Gagal memperbarui jadwal pelajaran.") }
        }
    }

    fun resetLessonToDefault(lessonId: String) {
        if (_uiState.value.loggedInAdmin == null) {
            _uiState.update { it.copy(feedbackMessage = "Akses ditolak: Hanya admin yang dapat mereset jadwal pelajaran!") }
            return
        }

        val defaultItem = ClassRepository.getDefaultLessons().firstOrNull { it.id == lessonId }
        val success = repo.resetLessonToDefault(lessonId)
        if (success) {
            loadInitialData()
            _uiState.update { it.copy(feedbackMessage = "Jadwal berhasil dikembalikan ke jadwal resmi!") }
            if (_uiState.value.notificationEnabled && defaultItem != null) {
                NotificationHelper.showScheduleUpdateNotification(
                    getApplication(),
                    defaultItem.subject,
                    defaultItem.periodLabel,
                    defaultItem.teacherName
                )
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        repo.setDarkMode(enabled)
        _uiState.update { it.copy(isDarkMode = enabled, feedbackMessage = if (enabled) "Mode Gelap diaktifkan" else "Mode Terang diaktifkan") }
    }

    fun getDatabaseDump(): Map<String, String> {
        return repo.getDatabaseDump()
    }

    fun getFullDatabaseJson(): String {
        return repo.getFullDatabaseJsonFormatted()
    }
}

