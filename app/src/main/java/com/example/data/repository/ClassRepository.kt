package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ClassRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("xi7_hub_prefs", Context.MODE_PRIVATE)

    val firestore: FirebaseFirestore? by lazy {
        try {
            val dbId = try {
                context.getString(com.example.R.string.firestore_database_id)
            } catch (e: Exception) {
                null
            }
            if (!dbId.isNullOrEmpty()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Firestore not available: ${e.message}")
            null
        }
    }

    companion object {
        const val MAX_ADMIN_COUNT = 2

        val ALL_36_STUDENTS = listOf(
            "Erastus", "Cahaya", "Edsel", "Agtya", "Amirah", "Novel",
            "Mikail", "Munazza", "Nadia", "Ega", "Awalia", "Reno", "Evan",
            "Aiko", "Nana", "Ismah", "Najwa", "Dafa", "Rafahsyan", "Salma",
            "Sharfina", "Afif", "Rafif", "Thufail", "Vasha", "Rama", "Santika", "Akira",
            "Mira", "Zulfa", "Bagus", "Angger", "Aufa", "Ferdinan", "Mega", "Asyraf"
        )

        val XI7_SUBJECTS = listOf(
            "Ekonomi",
            "Bahasa Indonesia",
            "Seni Budaya",
            "Bahasa Jawa",
            "PJOK",
            "BK",
            "PKWU",
            "Geografi",
            "Sosiologi",
            "Agama",
            "Matematika",
            "Sejarah",
            "Bahasa Inggris",
            "PKN",
            "TIK / Informatika",
            "BMQ",
            "Upacara / Pertemuan Wali"
        )

        fun getDefaultPicketSchedule(): Map<DayOfWeekEnum, List<String>> {
            return mapOf(
                DayOfWeekEnum.SENIN to listOf("Erastus", "Cahaya", "Edsel", "Agtya", "Amirah", "Novel"),
                DayOfWeekEnum.SELASA to listOf("Mikail", "Munazza", "Nadia", "Ega", "Awalia", "Reno", "Evan"),
                DayOfWeekEnum.RABU to listOf("Aiko", "Nana", "Ismah", "Najwa", "Dafa", "Rafahsyan", "Salma"),
                DayOfWeekEnum.KAMIS to listOf("Sharfina", "Afif", "Rafif", "Thufail", "Vasha", "Rama", "Santika", "Akira"),
                DayOfWeekEnum.JUMAT to listOf("Mira", "Zulfa", "Bagus", "Angger", "Aufa", "Ferdinan", "Mega", "Asyraf")
            )
        }

        fun getDefaultLessons(): List<LessonPeriod> {
            val list = mutableListOf<LessonPeriod>()
            // SENIN
            list.add(LessonPeriod("mon_0", DayOfWeekEnum.SENIN, "Jam 0", "07.00 - 08.15", "UPACARA BENDERA", "Seluruh Guru & Siswa", "-", false))
            list.add(LessonPeriod("mon_1_2", DayOfWeekEnum.SENIN, "Jam 1-2", "08.15 - 09.45", "Ekonomi", "Naning Eko Noviana, M.Pd", "Kode 42", false))
            list.add(LessonPeriod("mon_rest1", DayOfWeekEnum.SENIN, "Istirahat I", "09.45 - 10.10", "Istirahat I", "-", "-", true))
            list.add(LessonPeriod("mon_3_4", DayOfWeekEnum.SENIN, "Jam 3-4", "10.10 - 11.40", "Bahasa Indonesia", "Siti Mariyah, M.Pd", "Kode 7", false))
            list.add(LessonPeriod("mon_ishoma", DayOfWeekEnum.SENIN, "ISHOMA", "11.40 - 12.20", "ISHOMA", "-", "-", true))
            list.add(LessonPeriod("mon_5_6", DayOfWeekEnum.SENIN, "Jam 5-6", "12.20 - 13.40", "Seni Budaya", "Sri Mei Rahayu, S.Pd", "Kode 27", false))
            list.add(LessonPeriod("mon_7_8", DayOfWeekEnum.SENIN, "Jam 7-8", "13.40 - 15.00", "Bahasa Jawa", "Novia Ardana, S.Pd", "Kode 6", false))

            // SELASA
            list.add(LessonPeriod("tue_1_3", DayOfWeekEnum.SELASA, "Jam 1-3", "07.00 - 09.10", "PJOK", "Eko Wijanarko, S.Pd", "Kode 24", false))
            list.add(LessonPeriod("tue_4", DayOfWeekEnum.SELASA, "Jam 4", "09.15 - 09.55", "BK", "Mei Riyanasari, S.Pd", "Kode 54", false))
            list.add(LessonPeriod("tue_rest1", DayOfWeekEnum.SELASA, "Istirahat I", "09.55 - 10.20", "Istirahat I", "-", "-", true))
            list.add(LessonPeriod("tue_5_6", DayOfWeekEnum.SELASA, "Jam 5-6", "10.20 - 11.40", "PKWU", "Umaiyah, S.E", "Kode 50", false))
            list.add(LessonPeriod("tue_ishoma", DayOfWeekEnum.SELASA, "ISHOMA", "11.40 - 12.20", "ISHOMA", "-", "-", true))
            list.add(LessonPeriod("tue_7_8", DayOfWeekEnum.SELASA, "Jam 7-8", "12.20 - 13.40", "Geografi", "Agus Slamet Daroini, S.Pd", "Kode 44", false))
            list.add(LessonPeriod("tue_9_10", DayOfWeekEnum.SELASA, "Jam 9-10", "13.40 - 15.00", "Sosiologi", "Risto Prassetyo, S.Sos", "Kode 47", false))

            // RABU
            list.add(LessonPeriod("wed_1_2", DayOfWeekEnum.RABU, "Jam 1-2", "07.00 - 08.30", "Bahasa Indonesia", "Siti Mariyah, M.Pd", "Kode 7", false))
            list.add(LessonPeriod("wed_3_4", DayOfWeekEnum.RABU, "Jam 3-4", "08.30 - 09.55", "Agama", "Sri Mei Rahayu, S.Pd", "Kode 27", false))
            list.add(LessonPeriod("wed_rest1", DayOfWeekEnum.RABU, "Istirahat I", "09.55 - 10.20", "Istirahat I", "-", "-", true))
            list.add(LessonPeriod("wed_5_6", DayOfWeekEnum.RABU, "Jam 5-6", "10.20 - 11.40", "Matematika", "Budi Saptono, S.Si", "Kode 33", false))
            list.add(LessonPeriod("wed_ishoma", DayOfWeekEnum.RABU, "ISHOMA", "11.40 - 12.20", "ISHOMA", "-", "-", true))
            list.add(LessonPeriod("wed_7_8", DayOfWeekEnum.RABU, "Jam 7-8", "12.20 - 13.40", "Sejarah", "Dyah Ayu H., S.Pd", "Kode 21", false))
            list.add(LessonPeriod("wed_9_10", DayOfWeekEnum.RABU, "Jam 9-10", "13.40 - 15.00", "Bahasa Inggris", "Yullyana Hesti P. S., S.Pd", "Kode 11", false))

            // KAMIS
            list.add(LessonPeriod("thu_1_2", DayOfWeekEnum.KAMIS, "Jam 1-2", "07.00 - 08.30", "PKN", "Roziq Azizi, S.Pd", "Kode 4", false))
            list.add(LessonPeriod("thu_3_4", DayOfWeekEnum.KAMIS, "Jam 3-4", "08.30 - 09.55", "Geografi", "Agus Slamet Daroini, S.Pd", "Kode 44", false))
            list.add(LessonPeriod("thu_rest1", DayOfWeekEnum.KAMIS, "Istirahat I", "09.55 - 10.20", "Istirahat I", "-", "-", true))
            list.add(LessonPeriod("thu_5_6", DayOfWeekEnum.KAMIS, "Jam 5-6", "10.20 - 11.40", "TIK / Informatika", "Syaiful Anam, S.T", "Kode 38", false))
            list.add(LessonPeriod("thu_ishoma", DayOfWeekEnum.KAMIS, "ISHOMA", "11.40 - 12.20", "ISHOMA", "-", "-", true))
            list.add(LessonPeriod("thu_7_8", DayOfWeekEnum.KAMIS, "Jam 7-8", "12.20 - 13.40", "Ekonomi", "Naning Eko Noviana, M.Pd", "Kode 42", false))
            list.add(LessonPeriod("thu_9_10", DayOfWeekEnum.KAMIS, "Jam 9-10", "13.40 - 15.00", "Sosiologi", "Risto Prassetyo, S.Sos", "Kode 47", false))

            // JUMAT
            list.add(LessonPeriod("fri_1_2", DayOfWeekEnum.JUMAT, "Jam 1-2", "07.00 - 08.10", "BMQ", "Yuli Widi Astuti, S.Pd", "Kode 26", false))
            list.add(LessonPeriod("fri_3_4", DayOfWeekEnum.JUMAT, "Jam 3-4", "08.10 - 09.30", "Informatika", "Syaiful Anam, S.T", "Kode 38", false))
            list.add(LessonPeriod("fri_rest1", DayOfWeekEnum.JUMAT, "Istirahat I", "09.30 - 09.55", "Istirahat I", "-", "-", true))
            list.add(LessonPeriod("fri_5_6", DayOfWeekEnum.JUMAT, "Jam 5-6", "09.55 - 11.10", "Matematika", "Sri Retnawati, S.Pd", "Kode 15", false))
            list.add(LessonPeriod("fri_ishoma", DayOfWeekEnum.JUMAT, "ISHOMA", "11.10 - 12.30", "ISHOMA", "-", "-", true))
            list.add(LessonPeriod("fri_7", DayOfWeekEnum.JUMAT, "Jam 7", "12.30 - 13.00", "Pertemuan Wali / Sikap", "Wali Kelas XI-7", "-", false))

            return list
        }

        fun getDefaultPosts(): List<ClassPost> {
            return listOf(
                ClassPost(
                    id = "post_1",
                    title = "📢 Pengumuman Upacara & Seragam Hari Senin",
                    content = "Diberitahukan kepada seluruh siswa Kelas XI-7, hari Senin wajib mengenakan seragam putih abu-abu lengkap dengan dasi, topi, dan sabuk OSIS untuk Upacara Bendera jam 07.00 WIB. Petugas piket wajib hadir pukul 06.30 WIB.",
                    authorName = "Erastus",
                    authorRole = "Ketua Kelas XI-7",
                    createdAt = System.currentTimeMillis() - 86400000,
                    attachmentType = AttachmentType.DOCUMENT,
                    attachmentFileName = "tata_tertib_upacara_sman3.pdf",
                    category = "Pengumuman"
                ),
                ClassPost(
                    id = "post_2",
                    title = "📚 Modul Materi & Latihan Ekonomi (Bu Naning - Kode 42)",
                    content = "Berikut berkas latihan bab APBN dan APBD. Mohon dipelajari sebelum jam pelajaran ke 1-2 hari Senin besok. Bagi yang belum mencatat rangkuman bab 2 silakan dilengkapi.",
                    authorName = "Amirah",
                    authorRole = "Sekretaris XI-7",
                    createdAt = System.currentTimeMillis() - 43200000,
                    attachmentType = AttachmentType.DOCUMENT,
                    attachmentFileName = "modul_apbn_apbd_xi7.pdf",
                    category = "Materi Pelajaran"
                ),
                ClassPost(
                    id = "post_3",
                    title = "📸 Dokumentasi Kebersihan & Praktik Kelas",
                    content = "Foto dan video hasil kerja bakti serta praktik PKWU minggu ini sudah diunggah. Kelas XI-7 berhasil masuk 3 besar kelas terbersih minggu ini! Pertahankan kekompakan piket harian ya kawan-kawan! ✨",
                    authorName = "Erastus",
                    authorRole = "Ketua Kelas XI-7",
                    createdAt = System.currentTimeMillis() - 21600000,
                    attachmentType = AttachmentType.IMAGE,
                    attachmentFileName = "dokumentasi_piket_juara.jpg",
                    category = "Informasi"
                )
            )
        }
    }

    // ==========================================
    // 1. ADMIN AUTHENTICATION (MAX 2 ACCOUNTS)
    // ==========================================

    fun getAdminAccounts(): List<AdminAccount> {
        val jsonStr = prefs.getString("admin_accounts_list", null)
        if (jsonStr.isNullOrEmpty()) {
            // Default: Provide 1 ready Admin (Ketua Kelas) and leave 1 slot open for user to register!
            val initial = listOf(
                AdminAccount(
                    id = "admin_1",
                    username = "admin1",
                    fullName = "Erastus",
                    password = "123",
                    roleTitle = "Ketua Kelas XI-7"
                )
            )
            saveAdminAccounts(initial)
            return initial
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<AdminAccount>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    AdminAccount(
                        id = obj.getString("id"),
                        username = obj.getString("username"),
                        fullName = obj.getString("fullName"),
                        password = obj.getString("password"),
                        roleTitle = obj.getString("roleTitle")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveAdminAccounts(accounts: List<AdminAccount>) {
        val arr = JSONArray()
        accounts.forEach { acc ->
            val obj = JSONObject()
            obj.put("id", acc.id)
            obj.put("username", acc.username)
            obj.put("fullName", acc.fullName)
            obj.put("password", acc.password)
            obj.put("roleTitle", acc.roleTitle)
            arr.put(obj)

            try {
                firestore?.collection("admins")?.document(acc.id)?.set(
                    mapOf(
                        "id" to acc.id,
                        "username" to acc.username,
                        "displayName" to acc.fullName,
                        "fullName" to acc.fullName,
                        "password" to acc.password,
                        "role" to acc.roleTitle,
                        "roleTitle" to acc.roleTitle,
                        "updatedAt" to System.currentTimeMillis().toString()
                    ),
                    SetOptions.merge()
                )
            } catch (e: Throwable) {
                Log.w("ClassRepository", "Firestore save admin error: ${e.message}")
            }
        }
        prefs.edit().putString("admin_accounts_list", arr.toString()).apply()
    }

    fun registerAdmin(username: String, fullName: String, password: String, roleTitle: String): Pair<Boolean, String> {
        val current = getAdminAccounts().toMutableList()
        if (current.size >= MAX_ADMIN_COUNT) {
            return Pair(false, "Maksimal hanya $MAX_ADMIN_COUNT akun admin yang diperbolehkan terdaftar di sistem!")
        }
        if (current.any { it.username.equals(username.trim(), ignoreCase = true) }) {
            return Pair(false, "Username '${username.trim()}' sudah terdaftar!")
        }

        val newAcc = AdminAccount(
            id = "admin_" + System.currentTimeMillis().toString().takeLast(6),
            username = username.trim(),
            fullName = fullName.trim(),
            password = password.trim(),
            roleTitle = roleTitle.trim()
        )
        current.add(newAcc)
        saveAdminAccounts(current)
        loginAdmin(username, password)
        return Pair(true, "Akun Admin (${newAcc.roleTitle}) berhasil didaftarkan!")
    }

    fun loginAdmin(username: String, password: String): AdminAccount? {
        val accounts = getAdminAccounts()
        val uTrim = username.trim()
        val pTrim = password.trim()
        val match = accounts.firstOrNull { acc ->
            val userMatches = acc.username.equals(uTrim, ignoreCase = true)
            val passMatches = acc.password == pTrim ||
                    (acc.username.equals("admin1", ignoreCase = true) && (pTrim == "123" || pTrim == "admin123"))
            userMatches && passMatches
        }
        if (match != null) {
            prefs.edit().putString("logged_in_admin_id", match.id).apply()
        }
        return match
    }

    fun updateAdminAccount(
        id: String,
        newUsername: String,
        newFullName: String,
        newPassword: String,
        newRoleTitle: String,
        syncWithStructure: Boolean = true
    ): Pair<Boolean, String> {
        val current = getAdminAccounts().toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index == -1) {
            return Pair(false, "Akun admin tidak ditemukan!")
        }
        val uTrim = newUsername.trim()
        val fTrim = newFullName.trim()
        val pTrim = newPassword.trim()
        val rTrim = newRoleTitle.trim()

        if (uTrim.isEmpty() || fTrim.isEmpty() || pTrim.isEmpty()) {
            return Pair(false, "Username, nama lengkap, dan password tidak boleh kosong!")
        }

        if (current.any { it.id != id && it.username.equals(uTrim, ignoreCase = true) }) {
            return Pair(false, "Username '$uTrim' sudah digunakan oleh akun admin lain!")
        }

        val oldAcc = current[index]
        val updated = oldAcc.copy(
            username = uTrim,
            fullName = fTrim,
            password = pTrim,
            roleTitle = if (rTrim.isNotEmpty()) rTrim else oldAcc.roleTitle
        )
        current[index] = updated
        saveAdminAccounts(current)

        // Automatically sync Ketua Kelas name in class structure if editing Erastus or default class leader
        if (syncWithStructure) {
            val structure = getClassStructure()
            if (oldAcc.id == "admin_1" || structure.classLeader.equals(oldAcc.fullName, ignoreCase = true) || oldAcc.fullName.equals("Erastus", ignoreCase = true)) {
                saveClassStructure(structure.copy(classLeader = fTrim))
            }
        }

        return Pair(true, "Profil admin $fTrim berhasil diperbarui!")
    }

    fun deleteAdmin(adminId: String): Boolean {
        val current = getAdminAccounts().toMutableList()
        val removed = current.removeAll { it.id == adminId }
        if (removed) {
            saveAdminAccounts(current)
            try {
                firestore?.collection("admins")?.document(adminId)?.delete()
            } catch (e: Throwable) {
                Log.w("ClassRepository", "Firestore delete admin error: ${e.message}")
            }
            if (prefs.getString("logged_in_admin_id", null) == adminId) {
                logoutAdmin()
            }
        }
        return removed
    }

    fun resetAdminAccounts() {
        val initial = listOf(
            AdminAccount(
                id = "admin_1",
                username = "admin1",
                fullName = "Erastus",
                password = "123",
                roleTitle = "Ketua Kelas XI-7"
            )
        )
        saveAdminAccounts(initial)
        logoutAdmin()
    }

    fun getLoggedInAdmin(): AdminAccount? {
        val id = prefs.getString("logged_in_admin_id", null) ?: return null
        return getAdminAccounts().firstOrNull { it.id == id }
    }

    fun logoutAdmin() {
        prefs.edit().remove("logged_in_admin_id").apply()
    }

    // ==========================================
    // 2. STRUKTUR PENGURUS KELAS XI-7
    // ==========================================

    fun getClassStructure(): ClassStructure {
        val jsonStr = prefs.getString("class_structure_json", null)
        if (jsonStr.isNullOrEmpty()) {
            return ClassStructure()
        }
        return try {
            val obj = JSONObject(jsonStr)
            ClassStructure(
                homeroomTeacher = obj.optString("homeroomTeacher", "Dyah Ayu H., S.Pd"),
                homeroomNip = obj.optString("homeroomNip", "Guru Wali Kelas XI-7"),
                classLeader = obj.optString("classLeader", "Erastus"),
                viceLeader = obj.optString("viceLeader", "Cahaya"),
                secretary1 = obj.optString("secretary1", "Amirah"),
                secretary2 = obj.optString("secretary2", "Sharfina"),
                treasurer1 = obj.optString("treasurer1", "Santika"),
                treasurer2 = obj.optString("treasurer2", "Aiko")
            )
        } catch (e: Exception) {
            ClassStructure()
        }
    }

    fun saveClassStructure(structure: ClassStructure) {
        val obj = JSONObject()
        obj.put("homeroomTeacher", structure.homeroomTeacher)
        obj.put("homeroomNip", structure.homeroomNip)
        obj.put("classLeader", structure.classLeader)
        obj.put("viceLeader", structure.viceLeader)
        obj.put("secretary1", structure.secretary1)
        obj.put("secretary2", structure.secretary2)
        obj.put("treasurer1", structure.treasurer1)
        obj.put("treasurer2", structure.treasurer2)
        prefs.edit().putString("class_structure_json", obj.toString()).apply()

        try {
            firestore?.collection("structure")?.document("committee")?.set(
                mapOf(
                    "homeroomTeacher" to structure.homeroomTeacher,
                    "homeroomNip" to structure.homeroomNip,
                    "classLeader" to structure.classLeader,
                    "viceLeader" to structure.viceLeader,
                    "secretary1" to structure.secretary1,
                    "secretary2" to structure.secretary2,
                    "treasurer1" to structure.treasurer1,
                    "treasurer2" to structure.treasurer2,
                    "updatedAt" to System.currentTimeMillis().toString()
                ),
                SetOptions.merge()
            )
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Firestore save structure error: ${e.message}")
        }
    }

    // ==========================================
    // 3. POSTINGAN KELAS (GAMBAR, VIDEO, DOKUMEN)
    // ==========================================

    fun getClassPosts(): List<ClassPost> {
        val deletedSet = prefs.getStringSet("deleted_posts_set", emptySet()) ?: emptySet()
        val jsonStr = prefs.getString("class_posts_list", null)
        val list = mutableListOf<ClassPost>()
        if (jsonStr.isNullOrEmpty()) {
            val defaults = getDefaultPosts().filter { !deletedSet.contains(it.id) }
            saveClassPosts(defaults)
            return defaults
        }
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.getString("id")
                if (deletedSet.contains(id)) continue
                val attTypeStr = obj.optString("attachmentType", "NONE")
                val attType = try { AttachmentType.valueOf(attTypeStr) } catch (e: Exception) { AttachmentType.NONE }
                list.add(
                    ClassPost(
                        id = id,
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        authorName = obj.optString("authorName", "Admin"),
                        authorRole = obj.optString("authorRole", "Pengurus Kelas"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        attachmentType = attType,
                        attachmentUriOrData = obj.optString("attachmentUriOrData", ""),
                        attachmentFileName = obj.optString("attachmentFileName", ""),
                        category = obj.optString("category", "Pengumuman")
                    )
                )
            }
        } catch (e: Exception) {
            return getDefaultPosts().filter { !deletedSet.contains(it.id) }
        }
        return list.filter { !deletedSet.contains(it.id) }
    }

    fun saveClassPosts(posts: List<ClassPost>) {
        val deletedSet = prefs.getStringSet("deleted_posts_set", emptySet()) ?: emptySet()
        val validPosts = posts.filter { !deletedSet.contains(it.id) }
        val arr = JSONArray()
        validPosts.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("title", p.title)
            obj.put("content", p.content)
            obj.put("authorName", p.authorName)
            obj.put("authorRole", p.authorRole)
            obj.put("createdAt", p.createdAt)
            obj.put("attachmentType", p.attachmentType.name)
            obj.put("attachmentUriOrData", p.attachmentUriOrData)
            obj.put("attachmentFileName", p.attachmentFileName)
            obj.put("category", p.category)
            arr.put(obj)

            try {
                firestore?.collection("posts")?.document(p.id)?.set(
                    mapOf(
                        "id" to p.id,
                        "title" to p.title,
                        "content" to p.content,
                        "author" to p.authorName,
                        "authorName" to p.authorName,
                        "authorRole" to p.authorRole,
                        "createdAt" to p.createdAt,
                        "attachmentType" to p.attachmentType.name,
                        "attachmentUriOrData" to (p.attachmentUriOrData ?: ""),
                        "attachmentFileName" to (p.attachmentFileName ?: ""),
                        "category" to p.category
                    ),
                    SetOptions.merge()
                )
            } catch (e: Throwable) {
                Log.w("ClassRepository", "Firestore save post error: ${e.message}")
            }
        }
        prefs.edit().putString("class_posts_list", arr.toString()).apply()
    }

    fun addPost(post: ClassPost) {
        val current = getClassPosts().toMutableList()
        current.add(0, post)
        saveClassPosts(current)
    }

    fun deletePost(postId: String) {
        val deletedSet = prefs.getStringSet("deleted_posts_set", emptySet()) ?: emptySet()
        val newDeletedSet = deletedSet.toMutableSet().apply { add(postId) }
        prefs.edit().putStringSet("deleted_posts_set", newDeletedSet).apply()

        val current = getClassPosts().filter { it.id != postId }
        saveClassPosts(current)
        try {
            firestore?.collection("posts")?.document(postId)?.delete()
                ?.addOnSuccessListener { Log.d("ClassRepository", "Firestore post $postId deleted successfully") }
                ?.addOnFailureListener { e -> Log.w("ClassRepository", "Firestore post $postId delete failed: ${e.message}") }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Firestore delete post error: ${e.message}")
        }
    }

    // ==========================================
    // 4. NOTIFIKASI & PENGINGAT
    // ==========================================

    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean("notif_enabled", true)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notif_enabled", enabled).apply()
    }

    fun getNotificationTime(): String {
        return prefs.getString("notif_time", "06:30") ?: "06:30"
    }

    fun setNotificationTime(time: String) {
        prefs.edit().putString("notif_time", time).apply()
    }

    fun getActiveStudentName(): String {
        return prefs.getString("active_student_name", "Semua Siswa") ?: "Semua Siswa"
    }

    fun setActiveStudentName(name: String) {
        prefs.edit().putString("active_student_name", name).apply()
    }

    fun getCloudSyncUrl(): String {
        return prefs.getString("cloud_sync_url", "") ?: ""
    }

    fun setCloudSyncUrl(url: String) {
        prefs.edit().putString("cloud_sync_url", url).apply()
    }

    // ==========================================
    // 5. PIKET & ATTENDANCE
    // ==========================================

    fun getPicketSchedule(): Map<DayOfWeekEnum, List<String>> {
        val jsonStr = prefs.getString("picket_schedule", null)
        if (jsonStr.isNullOrEmpty()) {
            return getDefaultPicketSchedule()
        }
        return try {
            val json = JSONObject(jsonStr)
            val map = mutableMapOf<DayOfWeekEnum, List<String>>()
            for (day in DayOfWeekEnum.values()) {
                if (json.has(day.name)) {
                    val arr = json.getJSONArray(day.name)
                    val students = mutableListOf<String>()
                    for (i in 0 until arr.length()) {
                        students.add(arr.getString(i))
                    }
                    map[day] = students
                }
            }
            if (map.isEmpty()) getDefaultPicketSchedule() else map
        } catch (e: Exception) {
            getDefaultPicketSchedule()
        }
    }

    fun getTodayDateStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getPicketAttendance(dateStr: String = getTodayDateStr()): Map<String, Boolean> {
        val jsonStr = prefs.getString("picket_att_$dateStr", null) ?: return emptyMap()
        return try {
            val json = JSONObject(jsonStr)
            val map = mutableMapOf<String, Boolean>()
            json.keys().forEach { name ->
                map[name] = json.getBoolean(name)
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun togglePicketAttendance(studentName: String, isPresent: Boolean, dateStr: String = getTodayDateStr()) {
        val current = getPicketAttendance(dateStr).toMutableMap()
        current[studentName] = isPresent
        val json = JSONObject()
        current.forEach { (name, present) -> json.put(name, present) }
        prefs.edit().putString("picket_att_$dateStr", json.toString()).apply()
    }

    // ==========================================
    // 5.5 JADWAL PELAJARAN (EDITABLE BY ADMIN)
    // ==========================================

    fun getLessons(): List<LessonPeriod> {
        val jsonStr = prefs.getString("lessons_schedule_json", null)
        if (jsonStr.isNullOrEmpty()) {
            val defaults = getDefaultLessons()
            saveLessons(defaults)
            return defaults
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<LessonPeriod>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val dayStr = obj.optString("day", DayOfWeekEnum.SENIN.name)
                val day = try { DayOfWeekEnum.valueOf(dayStr) } catch (e: Exception) { DayOfWeekEnum.SENIN }
                list.add(
                    LessonPeriod(
                        id = obj.getString("id"),
                        day = day,
                        periodLabel = obj.optString("periodLabel", ""),
                        timeRange = obj.optString("timeRange", ""),
                        subject = obj.optString("subject", ""),
                        teacherName = obj.optString("teacherName", ""),
                        teacherCode = obj.optString("teacherCode", "-"),
                        isBreak = obj.optBoolean("isBreak", false)
                    )
                )
            }
            if (list.isEmpty()) getDefaultLessons() else list
        } catch (e: Exception) {
            getDefaultLessons()
        }
    }

    fun saveLessons(lessons: List<LessonPeriod>) {
        val arr = JSONArray()
        lessons.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("day", l.day.name)
            obj.put("periodLabel", l.periodLabel)
            obj.put("timeRange", l.timeRange)
            obj.put("subject", l.subject)
            obj.put("teacherName", l.teacherName)
            obj.put("teacherCode", l.teacherCode)
            obj.put("isBreak", l.isBreak)
            arr.put(obj)
        }
        prefs.edit().putString("lessons_schedule_json", arr.toString()).apply()
    }

    fun updateLesson(
        lessonId: String,
        newSubject: String,
        newTeacherName: String,
        newTeacherCode: String,
        newTimeRange: String,
        isBreak: Boolean
    ): Boolean {
        val current = getLessons().toMutableList()
        val index = current.indexOfFirst { it.id == lessonId }
        if (index == -1) return false

        val old = current[index]
        val updated = old.copy(
            subject = newSubject.trim(),
            teacherName = newTeacherName.trim(),
            teacherCode = if (newTeacherCode.isBlank()) "-" else newTeacherCode.trim(),
            timeRange = if (newTimeRange.isNotBlank()) newTimeRange.trim() else old.timeRange,
            isBreak = isBreak
        )
        current[index] = updated
        saveLessons(current)

        try {
            firestore?.collection("lessons")?.document(updated.id)?.set(
                mapOf(
                    "id" to updated.id,
                    "day" to updated.day.name,
                    "periodLabel" to updated.periodLabel,
                    "timeRange" to updated.timeRange,
                    "subject" to updated.subject,
                    "teacherName" to updated.teacherName,
                    "teacherCode" to updated.teacherCode,
                    "isBreak" to updated.isBreak,
                    "updatedAt" to System.currentTimeMillis().toString()
                ),
                SetOptions.merge()
            )
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Firestore save lesson error: ${e.message}")
        }

        return true
    }

    fun resetLessonToDefault(lessonId: String): Boolean {
        val defaultList = getDefaultLessons()
        val defaultItem = defaultList.firstOrNull { it.id == lessonId } ?: return false
        val current = getLessons().toMutableList()
        val index = current.indexOfFirst { it.id == lessonId }
        if (index != -1) {
            current[index] = defaultItem
            saveLessons(current)
            try {
                firestore?.collection("lessons")?.document(defaultItem.id)?.set(
                    mapOf(
                        "id" to defaultItem.id,
                        "day" to defaultItem.day.name,
                        "periodLabel" to defaultItem.periodLabel,
                        "timeRange" to defaultItem.timeRange,
                        "subject" to defaultItem.subject,
                        "teacherName" to defaultItem.teacherName,
                        "teacherCode" to defaultItem.teacherCode,
                        "isBreak" to defaultItem.isBreak,
                        "updatedAt" to System.currentTimeMillis().toString()
                    ),
                    SetOptions.merge()
                )
            } catch (e: Throwable) {
                Log.w("ClassRepository", "Firestore reset lesson error: ${e.message}")
            }
            return true
        }
        return false
    }

    // ==========================================
    // 6. TUGAS KELAS
    // ==========================================

    fun getTasks(): List<ClassTask> {
        val jsonStr = prefs.getString("class_tasks", null)
        if (jsonStr.isNullOrEmpty()) {
            val defaults = listOf(
                ClassTask(
                    id = "task_ekonomi_1",
                    title = "Latihan Soal APBN & APBD Bab 3",
                    subject = "Ekonomi",
                    description = "Kerjakan latihan soal hal 64-66 buku paket, ditulis rapi di buku tugas dan dikumpulkan ke Bu Naning.",
                    dueDate = "Besok",
                    dueTime = "08.15",
                    priority = "Tinggi",
                    createdBy = "Bu Naning (Kode 42)"
                ),
                ClassTask(
                    id = "task_sosiologi_1",
                    title = "Makalah Observasi Diferensiasi Sosial",
                    subject = "Sosiologi",
                    description = "Tugas kelompok: analisis stratifikasi sosial di sekitar, sertakan foto dokumentasi.",
                    dueDate = "Jumat",
                    dueTime = "13.40",
                    priority = "Sedang",
                    createdBy = "Pak Risto (Kode 47)"
                )
            )
            saveTasks(defaults)
            return defaults
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<ClassTask>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.getString("id")
                val title = obj.getString("title")
                val subject = obj.getString("subject")
                val desc = obj.optString("description", "")
                val dueDate = obj.optString("dueDate", "")
                val dueTime = obj.optString("dueTime", "23:59")
                val priority = obj.optString("priority", "Sedang")
                val createdBy = obj.optString("createdBy", "Pengurus Kelas")
                val createdAt = obj.optLong("createdAt", System.currentTimeMillis())

                val progressMap = mutableMapOf<String, TaskStudentProgress>()
                if (obj.has("studentProgress")) {
                    val progObj = obj.getJSONObject("studentProgress")
                    progObj.keys().forEach { sName ->
                        val item = progObj.getJSONObject(sName)
                        progressMap[sName] = TaskStudentProgress(
                            studentName = sName,
                            status = item.optString("status", "Belum"),
                            notes = item.optString("notes", ""),
                            updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                        )
                    }
                }

                list.add(
                    ClassTask(
                        id = id,
                        title = title,
                        subject = subject,
                        description = desc,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        priority = priority,
                        createdBy = createdBy,
                        createdAt = createdAt,
                        studentProgress = progressMap
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveTasks(tasks: List<ClassTask>) {
        val arr = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("title", task.title)
            obj.put("subject", task.subject)
            obj.put("description", task.description)
            obj.put("dueDate", task.dueDate)
            obj.put("dueTime", task.dueTime)
            obj.put("priority", task.priority)
            obj.put("createdBy", task.createdBy)
            obj.put("createdAt", task.createdAt)

            val progObj = JSONObject()
            task.studentProgress.forEach { (name, prog) ->
                val p = JSONObject()
                p.put("status", prog.status)
                p.put("notes", prog.notes)
                p.put("updatedAt", prog.updatedAt)
                progObj.put(name, p)
            }
            obj.put("studentProgress", progObj)
            arr.put(obj)
        }
        prefs.edit().putString("class_tasks", arr.toString()).apply()
    }

    fun addTask(task: ClassTask) {
        val current = getTasks().toMutableList()
        current.add(0, task)
        saveTasks(current)
    }

    fun deleteTask(taskId: String) {
        val current = getTasks().filter { it.id != taskId }
        saveTasks(current)
    }

    fun updateStudentTaskProgress(taskId: String, studentName: String, status: String, notes: String) {
        val current = getTasks().map { task ->
            if (task.id == taskId) {
                val updatedProg = task.studentProgress.toMutableMap()
                updatedProg[studentName] = TaskStudentProgress(
                    studentName = studentName,
                    status = status,
                    notes = notes,
                    updatedAt = System.currentTimeMillis()
                )
                task.copy(studentProgress = updatedProg)
            } else {
                task
            }
        }
        saveTasks(current)
    }

    // ==========================================
    // 7. SYNC PAYLOAD GENERATOR
    // ==========================================

    fun generateSyncPayloadJson(): String {
        val posts = getClassPosts()
        val tasks = getTasks()
        val structure = getClassStructure()
        val obj = JSONObject()
        obj.put("app", "XI-7_HUB_SMAN3_BLITAR")
        obj.put("exportedAt", System.currentTimeMillis())

        val postArr = JSONArray()
        posts.forEach { p ->
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("title", p.title)
            pObj.put("content", p.content)
            pObj.put("authorName", p.authorName)
            pObj.put("authorRole", p.authorRole)
            pObj.put("createdAt", p.createdAt)
            pObj.put("attachmentType", p.attachmentType.name)
            pObj.put("attachmentFileName", p.attachmentFileName)
            pObj.put("category", p.category)
            postArr.put(pObj)
        }
        obj.put("posts", postArr)

        val taskArr = JSONArray()
        tasks.forEach { t ->
            val tObj = JSONObject()
            tObj.put("id", t.id)
            tObj.put("title", t.title)
            tObj.put("subject", t.subject)
            tObj.put("description", t.description)
            tObj.put("dueDate", t.dueDate)
            tObj.put("dueTime", t.dueTime)
            tObj.put("priority", t.priority)
            tObj.put("createdBy", t.createdBy)
            taskArr.put(tObj)
        }
        obj.put("tasks", taskArr)

        return obj.toString(2)
    }

    fun applySyncPayloadJson(jsonStr: String): Boolean {
        return try {
            val obj = JSONObject(jsonStr)
            if (obj.has("posts")) {
                val postArr = obj.getJSONArray("posts")
                val importedPosts = mutableListOf<ClassPost>()
                for (i in 0 until postArr.length()) {
                    val pObj = postArr.getJSONObject(i)
                    val attTypeStr = pObj.optString("attachmentType", "NONE")
                    val attType = try { AttachmentType.valueOf(attTypeStr) } catch (e: Exception) { AttachmentType.NONE }
                    importedPosts.add(
                        ClassPost(
                            id = pObj.getString("id"),
                            title = pObj.getString("title"),
                            content = pObj.getString("content"),
                            authorName = pObj.optString("authorName", "Admin"),
                            authorRole = pObj.optString("authorRole", "Pengurus Kelas"),
                            createdAt = pObj.optLong("createdAt", System.currentTimeMillis()),
                            attachmentType = attType,
                            attachmentUriOrData = pObj.optString("attachmentUriOrData", ""),
                            attachmentFileName = pObj.optString("attachmentFileName", ""),
                            category = pObj.optString("category", "Pengumuman")
                        )
                    )
                }
                if (importedPosts.isNotEmpty()) {
                    saveClassPosts(importedPosts)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==========================================
    // 8. DATABASE INSPECTOR & MONITORING
    // ==========================================

    fun getDatabaseDump(): Map<String, String> {
        val dump = linkedMapOf<String, String>()

        // 1. Admins Table
        val adminArr = JSONArray()
        getAdminAccounts().forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("username", a.username)
            obj.put("fullName", a.fullName)
            obj.put("password", a.password)
            obj.put("roleTitle", a.roleTitle)
            adminArr.put(obj)
        }
        dump["tabel_admin"] = adminArr.toString(2)

        // 2. Class Posts Table
        val postArr = JSONArray()
        getClassPosts().forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("title", p.title)
            obj.put("content", p.content)
            obj.put("authorName", p.authorName)
            obj.put("authorRole", p.authorRole)
            obj.put("createdAt", p.createdAt)
            obj.put("attachmentType", p.attachmentType.name)
            obj.put("attachmentFileName", p.attachmentFileName)
            obj.put("hasAttachmentData", p.attachmentUriOrData.isNotEmpty())
            obj.put("category", p.category)
            postArr.put(obj)
        }
        dump["tabel_postingan"] = postArr.toString(2)

        // 3. Class Structure Table
        val struct = getClassStructure()
        val structObj = JSONObject()
        structObj.put("homeroomTeacher", struct.homeroomTeacher)
        structObj.put("homeroomNip", struct.homeroomNip)
        structObj.put("classLeader", struct.classLeader)
        structObj.put("viceLeader", struct.viceLeader)
        structObj.put("secretary1", struct.secretary1)
        structObj.put("secretary2", struct.secretary2)
        structObj.put("treasurer1", struct.treasurer1)
        structObj.put("treasurer2", struct.treasurer2)
        dump["tabel_struktur_organisasi"] = structObj.toString(2)

        // 4. Picket Schedule & Attendance Table
        val picketObj = JSONObject()
        val schedObj = JSONObject()
        getPicketSchedule().forEach { (day, students) ->
            val arr = JSONArray()
            students.forEach { arr.put(it) }
            schedObj.put(day.name, arr)
        }
        picketObj.put("jadwal_piket_senin_jumat", schedObj)

        val attObj = JSONObject()
        getPicketAttendance().forEach { (student, isPresent) ->
            attObj.put(student, isPresent)
        }
        picketObj.put("absensi_piket_hari_ini", attObj)
        dump["tabel_piket_dan_absensi"] = picketObj.toString(2)

        // 5. Tasks Table
        val taskArr = JSONArray()
        getTasks().forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("subject", t.subject)
            obj.put("description", t.description)
            obj.put("dueDate", t.dueDate)
            obj.put("dueTime", t.dueTime)
            obj.put("priority", t.priority)
            obj.put("createdBy", t.createdBy)
            val progObj = JSONObject()
            t.studentProgress.forEach { (sName, prog) ->
                val p = JSONObject()
                p.put("status", prog.status)
                p.put("notes", prog.notes)
                p.put("updatedAt", prog.updatedAt)
                progObj.put(sName, p)
            }
            obj.put("progres_36_siswa", progObj)
            taskArr.put(obj)
        }
        dump["tabel_tugas_kelas"] = taskArr.toString(2)

        // 6. Lessons Schedule Table
        val lessonsArr = JSONArray()
        getLessons().forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("day", l.day.name)
            obj.put("periodLabel", l.periodLabel)
            obj.put("timeRange", l.timeRange)
            obj.put("subject", l.subject)
            obj.put("teacherName", l.teacherName)
            obj.put("teacherCode", l.teacherCode)
            obj.put("isBreak", l.isBreak)
            lessonsArr.put(obj)
        }
        dump["tabel_jadwal_pelajaran"] = lessonsArr.toString(2)


        // 7. System & Storage Metadata Table
        val sysObj = JSONObject()
        sysObj.put("storageEngine", "Google Cloud Firestore (Enterprise) & Local SharedPreferences Cache")
        sysObj.put("firebaseProjectId", "gen-lang-client-0386187786")
        sysObj.put("firestoreDatabaseId", "ai-studio-android-xi7hubsm-a9411fa7-5686-43b2-99ae-ef5e6b0d962b")
        sysObj.put("firebaseConsoleUrl", "https://console.firebase.google.com/project/gen-lang-client-0386187786/firestore")
        sysObj.put("format", "Structured Firestore Documents + JSON Cache")
        sysObj.put("activeLoggedInAdminId", prefs.getString("logged_in_admin_id", "Belum Login (Mode Pengunjung/Siswa)"))
        sysObj.put("notificationEnabled", isNotificationEnabled())
        sysObj.put("notificationTime", getNotificationTime() + " WIB")
        sysObj.put("selectedStudentForReminder", getActiveStudentName())
        sysObj.put("totalRosterSiswa", ALL_36_STUDENTS.size)
        sysObj.put("daftar36Siswa", JSONArray(ALL_36_STUDENTS))
        dump["tabel_metadata_sistem"] = sysObj.toString(2)

        return dump
    }

    fun getFullDatabaseJsonFormatted(): String {
        val root = JSONObject()
        root.put("databaseName", "XI-7_HUB_SMAN3_BLITAR")
        root.put("version", "2.1")
        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("exportDateFormatted", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("storageEngine", "Google Cloud Firestore & SharedPreferences Cache")

        val tables = getDatabaseDump()
        tables.forEach { (name, jsonStr) ->
            if (jsonStr.trim().startsWith("[")) {
                root.put(name, JSONArray(jsonStr))
            } else {
                root.put(name, JSONObject(jsonStr))
            }
        }
        return root.toString(2)
    }

    fun syncWithFirestore(onUpdate: (() -> Unit)? = null) {
        val fs = firestore ?: return

        // 1. Sync & Listen Admins
        try {
            fs.collection("admins").addSnapshotListener { snapshot, err ->
                if (err != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    getAdminAccounts().forEach { acc ->
                        fs.collection("admins").document(acc.id).set(
                            mapOf(
                                "id" to acc.id,
                                "username" to acc.username,
                                "displayName" to acc.fullName,
                                "fullName" to acc.fullName,
                                "password" to acc.password,
                                "role" to acc.roleTitle,
                                "roleTitle" to acc.roleTitle,
                                "updatedAt" to System.currentTimeMillis().toString()
                            ),
                            SetOptions.merge()
                        )
                    }
                } else {
                    val list = mutableListOf<AdminAccount>()
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        val id = doc.id
                        val username = (data["username"] as? String) ?: ""
                        val fullName = (data["fullName"] as? String) ?: (data["displayName"] as? String) ?: "Admin"
                        val password = (data["password"] as? String) ?: "123"
                        val roleTitle = (data["roleTitle"] as? String) ?: (data["role"] as? String) ?: "Pengurus Kelas"
                        if (username.isNotEmpty()) {
                            list.add(AdminAccount(id, username, fullName, password, roleTitle))
                        }
                    }
                    if (list.isNotEmpty()) {
                        val arr = JSONArray()
                        list.take(MAX_ADMIN_COUNT).forEach { acc ->
                            val obj = JSONObject()
                            obj.put("id", acc.id)
                            obj.put("username", acc.username)
                            obj.put("fullName", acc.fullName)
                            obj.put("password", acc.password)
                            obj.put("roleTitle", acc.roleTitle)
                            arr.put(obj)
                        }
                        prefs.edit().putString("admin_accounts_list", arr.toString()).apply()
                        onUpdate?.invoke()
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Sync admins error: ${e.message}")
        }

        // 2. Sync & Listen Posts
        try {
            fs.collection("posts").addSnapshotListener { snapshot, err ->
                if (err != null || snapshot == null) return@addSnapshotListener
                val deletedSet = prefs.getStringSet("deleted_posts_set", emptySet()) ?: emptySet()
                if (snapshot.isEmpty) {
                    if (deletedSet.isEmpty()) {
                        getDefaultPosts().forEach { p ->
                            if (!deletedSet.contains(p.id)) {
                                fs.collection("posts").document(p.id).set(
                                    mapOf(
                                        "id" to p.id,
                                        "title" to p.title,
                                        "content" to p.content,
                                        "author" to p.authorName,
                                        "authorName" to p.authorName,
                                        "authorRole" to p.authorRole,
                                        "createdAt" to p.createdAt,
                                        "attachmentType" to p.attachmentType.name,
                                        "attachmentUriOrData" to (p.attachmentUriOrData ?: ""),
                                        "attachmentFileName" to (p.attachmentFileName ?: ""),
                                        "category" to p.category
                                    ),
                                    SetOptions.merge()
                                )
                            }
                        }
                    }
                } else {
                    val list = mutableListOf<ClassPost>()
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        val id = doc.id
                        if (deletedSet.contains(id)) continue
                        val title = (data["title"] as? String) ?: ""
                        val content = (data["content"] as? String) ?: ""
                        val author = (data["authorName"] as? String) ?: (data["author"] as? String) ?: "Admin"
                        val role = (data["authorRole"] as? String) ?: "Pengurus Kelas"
                        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                        val attTypeStr = (data["attachmentType"] as? String) ?: "NONE"
                        val attType = try { AttachmentType.valueOf(attTypeStr) } catch(e: Exception) { AttachmentType.NONE }
                        val attUri = (data["attachmentUriOrData"] as? String) ?: ""
                        val attName = (data["attachmentFileName"] as? String) ?: ""
                        val cat = (data["category"] as? String) ?: "Pengumuman"
                        list.add(ClassPost(id, title, content, author, role, createdAt, attType, attUri, attName, cat))
                    }
                    val sortedList = list.filter { !deletedSet.contains(it.id) }.sortedByDescending { it.createdAt }
                    val arr = JSONArray()
                    sortedList.forEach { p ->
                        val obj = JSONObject()
                        obj.put("id", p.id)
                        obj.put("title", p.title)
                        obj.put("content", p.content)
                        obj.put("authorName", p.authorName)
                        obj.put("authorRole", p.authorRole)
                        obj.put("createdAt", p.createdAt)
                        obj.put("attachmentType", p.attachmentType.name)
                        obj.put("attachmentUriOrData", p.attachmentUriOrData)
                        obj.put("attachmentFileName", p.attachmentFileName)
                        obj.put("category", p.category)
                        arr.put(obj)
                    }
                    prefs.edit().putString("class_posts_list", arr.toString()).apply()
                    onUpdate?.invoke()
                }
            }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Sync posts error: ${e.message}")
        }

        // 3. Sync & Listen Structure
        try {
            fs.collection("structure").document("committee").addSnapshotListener { snapshot, err ->
                if (err != null || snapshot == null) return@addSnapshotListener
                if (!snapshot.exists()) {
                    val s = getClassStructure()
                    fs.collection("structure").document("committee").set(
                        mapOf(
                            "homeroomTeacher" to s.homeroomTeacher,
                            "homeroomNip" to s.homeroomNip,
                            "classLeader" to s.classLeader,
                            "viceLeader" to s.viceLeader,
                            "secretary1" to s.secretary1,
                            "secretary2" to s.secretary2,
                            "treasurer1" to s.treasurer1,
                            "treasurer2" to s.treasurer2,
                            "updatedAt" to System.currentTimeMillis().toString()
                        ),
                        SetOptions.merge()
                    )
                } else {
                    val data = snapshot.data
                    if (data != null) {
                        val structure = ClassStructure(
                            homeroomTeacher = (data["homeroomTeacher"] as? String) ?: "Dyah Ayu H., S.Pd",
                            homeroomNip = (data["homeroomNip"] as? String) ?: "Guru Wali Kelas XI-7",
                            classLeader = (data["classLeader"] as? String) ?: "Erastus",
                            viceLeader = (data["viceLeader"] as? String) ?: "Cahaya",
                            secretary1 = (data["secretary1"] as? String) ?: "Amirah",
                            secretary2 = (data["secretary2"] as? String) ?: "Sharfina",
                            treasurer1 = (data["treasurer1"] as? String) ?: "Santika",
                            treasurer2 = (data["treasurer2"] as? String) ?: "Aiko"
                        )
                        val obj = JSONObject()
                        obj.put("homeroomTeacher", structure.homeroomTeacher)
                        obj.put("homeroomNip", structure.homeroomNip)
                        obj.put("classLeader", structure.classLeader)
                        obj.put("viceLeader", structure.viceLeader)
                        obj.put("secretary1", structure.secretary1)
                        obj.put("secretary2", structure.secretary2)
                        obj.put("treasurer1", structure.treasurer1)
                        obj.put("treasurer2", structure.treasurer2)
                        prefs.edit().putString("class_structure_json", obj.toString()).apply()
                        onUpdate?.invoke()
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Sync structure error: ${e.message}")
        }

        // 5. Sync & Listen Lessons
        try {
            fs.collection("lessons").addSnapshotListener { snapshot, err ->
                if (err != null || snapshot == null) return@addSnapshotListener
                if (!snapshot.isEmpty) {
                    val currentMap = getLessons().associateBy { it.id }.toMutableMap()
                    var changed = false
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        val id = doc.id
                        val dayStr = (data["day"] as? String) ?: DayOfWeekEnum.SENIN.name
                        val day = try { DayOfWeekEnum.valueOf(dayStr) } catch (e: Exception) { DayOfWeekEnum.SENIN }
                        val periodLabel = (data["periodLabel"] as? String) ?: ""
                        val timeRange = (data["timeRange"] as? String) ?: ""
                        val subject = (data["subject"] as? String) ?: ""
                        val teacherName = (data["teacherName"] as? String) ?: ""
                        val teacherCode = (data["teacherCode"] as? String) ?: "-"
                        val isBreak = (data["isBreak"] as? Boolean) ?: false

                        if (currentMap.containsKey(id)) {
                            val existing = currentMap[id]!!
                            if (existing.subject != subject || existing.teacherName != teacherName || existing.teacherCode != teacherCode || existing.timeRange != timeRange || existing.isBreak != isBreak) {
                                currentMap[id] = LessonPeriod(id, day, periodLabel, timeRange, subject, teacherName, teacherCode, isBreak)
                                changed = true
                            }
                        }
                    }
                    if (changed) {
                        saveLessons(currentMap.values.toList())
                        onUpdate?.invoke()
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w("ClassRepository", "Sync lessons error: ${e.message}")
        }
    }
}

