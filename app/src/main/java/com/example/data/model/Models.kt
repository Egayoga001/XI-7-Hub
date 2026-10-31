package com.example.data.model

enum class AttachmentType(val displayName: String) {
    NONE("Tanpa Lampiran"),
    IMAGE("Gambar / Foto"),
    VIDEO("Video"),
    DOCUMENT("Dokumen / File")
}

data class AdminAccount(
    val id: String,
    val username: String,
    val fullName: String,
    val password: String,
    val roleTitle: String
)

data class ClassPost(
    val id: String,
    val title: String,
    val content: String,
    val authorName: String,
    val authorRole: String,
    val createdAt: Long = System.currentTimeMillis(),
    val attachmentType: AttachmentType = AttachmentType.NONE,
    val attachmentUriOrData: String = "",
    val attachmentFileName: String = "",
    val category: String = "Pengumuman" // Pengumuman, Tugas, Materi, Informasi
)

data class ClassStructure(
    val homeroomTeacher: String = "Dyah Ayu H., S.Pd",
    val homeroomNip: String = "Guru Wali Kelas XI-7",
    val classLeader: String = "Erastus",
    val viceLeader: String = "Cahaya",
    val secretary1: String = "Amirah",
    val secretary2: String = "Sharfina",
    val treasurer1: String = "Santika",
    val treasurer2: String = "Aiko"
)

enum class DayOfWeekEnum(val displayName: String, val index: Int) {
    SENIN("Senin", 1),
    SELASA("Selasa", 2),
    RABU("Rabu", 3),
    KAMIS("Kamis", 4),
    JUMAT("Jumat", 5),
    SABTU("Sabtu", 6),
    MINGGU("Minggu", 7);

    companion object {
        fun fromDayNumber(dayOfWeek: Int): DayOfWeekEnum {
            return when (dayOfWeek) {
                java.util.Calendar.MONDAY -> SENIN
                java.util.Calendar.TUESDAY -> SELASA
                java.util.Calendar.WEDNESDAY -> RABU
                java.util.Calendar.THURSDAY -> KAMIS
                java.util.Calendar.FRIDAY -> JUMAT
                java.util.Calendar.SATURDAY -> SABTU
                else -> MINGGU
            }
        }
    }
}

data class LessonPeriod(
    val id: String,
    val day: DayOfWeekEnum,
    val periodLabel: String, // e.g. "Jam 0", "Jam 1-2", "Jam 3-4"
    val timeRange: String,   // e.g. "07.00 - 08.15", "08.15 - 09.45"
    val subject: String,
    val teacherName: String,
    val teacherCode: String,
    val isBreak: Boolean = false
)

data class ClassTask(
    val id: String,
    val title: String,
    val subject: String,
    val description: String,
    val dueDate: String,
    val dueTime: String = "23:59",
    val priority: String = "Sedang", // Tinggi, Sedang, Rendah
    val createdBy: String = "Pengurus Kelas",
    val createdAt: Long = System.currentTimeMillis(),
    val studentProgress: Map<String, TaskStudentProgress> = emptyMap()
)

data class TaskStudentProgress(
    val studentName: String,
    val status: String = "Belum",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

data class ClassMemory(
    val id: String,
    val caption: String,
    val dateStr: String,
    val uploaderName: String,
    val imageBase64OrUri: String,
    val isPreset: Boolean = false
)
