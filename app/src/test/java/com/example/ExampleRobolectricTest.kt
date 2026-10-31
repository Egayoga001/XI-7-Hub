package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AttachmentType
import com.example.data.model.ClassPost
import com.example.data.model.ClassStructure
import com.example.data.model.DayOfWeekEnum
import com.example.data.repository.ClassRepository
import com.example.reminder.NotificationHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: ClassRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = ClassRepository(context)
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("XI-7 Hub", appName)
    }

    @Test
    fun `verify 36 students roster`() {
        val students = ClassRepository.ALL_36_STUDENTS
        assertEquals(36, students.size)
        assertTrue(students.contains("Erastus"))
        assertTrue(students.contains("Cahaya"))
        assertTrue(students.contains("Amirah"))
        assertTrue(students.contains("Sharfina"))
        assertTrue(students.contains("Santika"))
        assertTrue(students.contains("Aiko"))
        assertTrue(students.contains("Asyraf"))
    }

    @Test
    fun `strict max 2 admin accounts limit`() {
        val admins = repository.getAdminAccounts()
        assertTrue("Admin slots should not exceed MAX_ADMIN_COUNT", admins.size <= ClassRepository.MAX_ADMIN_COUNT)

        // Try registering beyond max 2
        if (admins.size < ClassRepository.MAX_ADMIN_COUNT) {
            val (ok1, _) = repository.registerAdmin("admin_test_2", "Admin Dua", "pass123", "Wakil Ketua")
            assertTrue("2nd admin registration should succeed", ok1)
        }

        val (okExcess, msg) = repository.registerAdmin("admin_test_3", "Admin Tiga", "pass123", "Sekretaris")
        assertFalse("3rd admin registration MUST be blocked", okExcess)
        assertTrue("Message should mention max 2 admins limit", msg.contains("Maksimal") && msg.contains("2 akun"))
    }

    @Test
    fun `class structure contains homeroom teacher and leaders`() {
        val structure = repository.getClassStructure()
        assertEquals("Dyah Ayu H., S.Pd", structure.homeroomTeacher)
        assertEquals("Erastus", structure.classLeader)
        assertEquals("Cahaya", structure.viceLeader)
        assertEquals("Amirah", structure.secretary1)
        assertEquals("Sharfina", structure.secretary2)
        assertEquals("Santika", structure.treasurer1)
        assertEquals("Aiko", structure.treasurer2)

        // Update structure
        val updated = structure.copy(classLeader = "Erastus M.")
        repository.saveClassStructure(updated)
        assertEquals("Erastus M.", repository.getClassStructure().classLeader)
    }

    @Test
    fun `class posts support image video document attachments`() {
        val post = ClassPost(
            id = "test_post_1",
            title = "Materi Bab 3 Matematika",
            content = "Silakan pelajari dokumen terlampir",
            authorName = "Erastus",
            authorRole = "Ketua Kelas",
            createdAt = System.currentTimeMillis(),
            attachmentType = AttachmentType.DOCUMENT,
            attachmentUriOrData = "https://example.com/matematika_bab3.pdf",
            attachmentFileName = "matematika_bab3.pdf",
            category = "Materi"
        )

        repository.addPost(post)
        val posts = repository.getClassPosts()
        assertTrue(posts.any { it.id == "test_post_1" && it.attachmentType == AttachmentType.DOCUMENT })

        repository.deletePost("test_post_1")
        assertFalse(repository.getClassPosts().any { it.id == "test_post_1" })
    }

    @Test
    fun `lesson periods schedule coverage`() {
        val lessons = ClassRepository.getDefaultLessons()
        assertTrue("Lessons schedule should not be empty", lessons.isNotEmpty())
        val seninLessons = lessons.filter { it.day == DayOfWeekEnum.SENIN }
        assertTrue("Monday should have multiple lesson periods", seninLessons.size >= 4)
    }

    @Test
    fun `database inspector dump and export format`() {
        val dump = repository.getDatabaseDump()
        assertTrue("Dump should have multiple tables", dump.size >= 6)
        assertTrue("Should have admin table", dump.containsKey("tabel_admin"))
        assertTrue("Should have posts table", dump.containsKey("tabel_postingan"))
        assertTrue("Should have structure table", dump.containsKey("tabel_struktur_organisasi"))
        assertTrue("Should have picket table", dump.containsKey("tabel_piket_dan_absensi"))
        assertTrue("Should have system metadata table", dump.containsKey("tabel_metadata_sistem"))

        val fullJson = repository.getFullDatabaseJsonFormatted()
        assertTrue("Full json should contain database name", fullJson.contains("XI-7_HUB_SMAN3_BLITAR"))
        assertTrue("Full json should contain admin accounts", fullJson.contains("tabel_admin"))
        assertTrue("Full json should contain storage engine info", fullJson.contains("SharedPreferences"))
    }
}

