package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminAccount
import com.example.data.repository.ClassRepository
import com.example.ui.components.EditAdminProfileDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClassUiState
import com.example.ui.viewmodel.ClassViewModel

@Composable
fun SettingsScreen(
    uiState: ClassUiState,
    viewModel: ClassViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showSelectReminderNameDialog by remember { mutableStateOf(false) }
    var adminToEdit by remember { mutableStateOf<AdminAccount?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Class Info Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SmanNavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = SmanGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kelas XI-7 SMAN 3 Blitar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 17.sp
                        )
                    )

                    Text(
                        text = "Total 36 Siswa • Jadwal Pelajaran, Piket & Postingan Kelas",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // Admin Accounts Status Card (Strict 2 Admin Constraint)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (uiState.loggedInAdmin != null) SmanEmerald else SmanNavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Akun Admin Pengurus Kelas",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (uiState.registeredAdmins.size < ClassRepository.MAX_ADMIN_COUNT) SmanNavyPrimary.copy(alpha = 0.1f) else SmanEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${uiState.registeredAdmins.size}/${ClassRepository.MAX_ADMIN_COUNT} Terdaftar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.registeredAdmins.size < ClassRepository.MAX_ADMIN_COUNT) SmanNavyPrimary else SmanEmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sistem dibatasi secara ketat hanya untuk 2 akun admin (Pengurus Kelas). Siswa biasa tidak perlu login atau register, langsung dapat membaca semua jadwal dan materi.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Registered Admin Accounts List
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daftar Akun Admin Terdaftar:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SmanNavyDark
                                )
                                Text(
                                    text = "${uiState.registeredAdmins.size}/2 Slot",
                                    fontSize = 11.sp,
                                    color = Slate700,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (uiState.registeredAdmins.isEmpty()) {
                                Text(
                                    text = "Belum ada admin terdaftar. Anda dapat mendaftarkan 2 akun dari awal.",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            } else {
                                uiState.registeredAdmins.forEachIndexed { index, admin ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${index + 1}. ${admin.fullName} (${admin.roleTitle})",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = SmanNavyPrimary
                                            )
                                            Text(
                                                text = "Username: ${admin.username} • Password: ${if (admin.username == "admin1") "123 / admin123" else "••••••"}",
                                                fontSize = 11.sp,
                                                color = Slate700
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { adminToEdit = admin },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit profil admin",
                                                    tint = SmanNavyPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteAdmin(admin.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.DeleteOutline,
                                                    contentDescription = "Hapus slot admin",
                                                    tint = Red500,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.resetAdminAccounts() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset ke Default (admin1 / 123)", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.loggedInAdmin != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SmanEmerald.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, SmanEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Status: Login sebagai ${uiState.loggedInAdmin.fullName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Jabatan: ${uiState.loggedInAdmin.roleTitle} (@${uiState.loggedInAdmin.username})",
                                        fontSize = 11.sp,
                                        color = SmanEmeraldDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { adminToEdit = uiState.loggedInAdmin },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SmanNavyPrimary)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.logoutAdmin() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Red500),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Keluar", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Sedang dalam Mode Siswa (Melihat)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Login atau daftar admin di tab Postingan.",
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.setActiveTab(2) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Buka Tab Postingan", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Automatic Notification Reminder Settings Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = SmanNavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pengingat Notifikasi Otomatis",
                                fontWeight = FontWeight.Bold,
                                color = SmanNavyDark,
                                fontSize = 15.sp
                            )
                        }

                        Switch(
                            checked = uiState.notificationEnabled,
                            onCheckedChange = { viewModel.setNotificationEnabled(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Aplikasi akan membunyikan notifikasi setiap pagi pukul ${uiState.notificationTime} WIB untuk mengingatkan jam pelajaran keberapa, mata pelajaran apa, sampai jam berapa, serta giliran piket.",
                        fontSize = 12.sp,
                        color = Slate700,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pilihan waktu alarm
                    Text(
                        text = "Waktu Notifikasi Pagi:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SmanNavyDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("06:15", "06:30", "06:45").forEach { time ->
                            val isSel = uiState.notificationTime == time
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SmanNavyPrimary else Slate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setNotificationTime(time) }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$time WIB",
                                        color = if (isSel) Color.White else Slate700,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target name for custom reminder
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelectReminderNameDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Target Pengingat Piket:",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                                Text(
                                    text = uiState.selectedStudentForReminder,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SmanNavyDark
                                )
                            }
                            TextButton(onClick = { showSelectReminderNameDialog = true }) {
                                Text("Ganti", color = SmanNavyPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.testNotificationNow() },
                        colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_test_notification")
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Notifikasi Pengingat Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Database Monitoring & Raw Inspector Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = SmanNavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Database Cloud Firestore",
                                fontWeight = FontWeight.Bold,
                                color = SmanNavyDark,
                                fontSize = 15.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SmanEmerald.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SmanEmerald)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Terhubung Cloud",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SmanEmerald
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Database aplikasi tersinkronisasi langsung dengan Google Cloud Firestore. Anda dapat memantau, memeriksa, mengedit, maupun mengekspor seluruh koleksi data (admins, posts, structure, picket, schedule) langsung dari luar aplikasi via Firebase Console di browser.",
                        fontSize = 12.sp,
                        color = Slate700,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Project ID: gen-lang-client-0386187786",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SmanNavyDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Database: ai-studio-android-xi7hubsm-a9411fa7-5686-43b2-99ae-ef5e6b0d962b",
                                fontSize = 10.sp,
                                color = Slate500
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Koleksi: admins (2 akun), posts, structure, picket, schedule",
                                fontSize = 10.sp,
                                color = SmanEmeraldDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = "https://console.firebase.google.com/project/gen-lang-client-0386187786/firestore"
                                clipboardManager.setText(AnnotatedString(url))
                                Toast.makeText(context, "Link Firebase Console disalin! Buka di browser untuk memantau database.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_copy_firebase_console_url")
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salin Link Firebase Console", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val info = "Firebase Project: gen-lang-client-0386187786\nDatabase ID: ai-studio-android-xi7hubsm-a9411fa7-5686-43b2-99ae-ef5e6b0d962b\nConsole URL: https://console.firebase.google.com/project/gen-lang-client-0386187786/firestore"
                                clipboardManager.setText(AnnotatedString(info))
                                Toast.makeText(context, "Informasi Database Firebase disalin!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SmanNavyPrimary),
                            modifier = Modifier.testTag("btn_copy_firebase_info")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin Info", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // School Information Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tentang XI-7 Hub SMAN 3 Blitar",
                        fontWeight = FontWeight.Bold,
                        color = SmanNavyDark,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Aplikasi resmi jadwal pelajaran, piket harian, pengurus kelas, dan materi/tugas kelas XI-7 SMAN 3 Blitar. Didesain cepat, nyaman, dan langsung dapat diakses oleh semua siswa tanpa hambatan login.",
                        fontSize = 12.sp,
                        color = Slate700,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Wali Kelas: ${uiState.classStructure.homeroomTeacher} • SMAN 3 Blitar",
                        fontSize = 11.sp,
                        color = Slate700,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }


    // Modal Dialog to Select Reminder Target Name (1-Click, No PIN, No password!)
    if (showSelectReminderNameDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val namesList = listOf("Semua Siswa") + ClassRepository.ALL_36_STUDENTS
        val filteredList = namesList.filter { it.contains(searchQuery, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { showSelectReminderNameDialog = false },
            title = { Text("Pilih Nama Pengingat Piket", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Pilih namamu agar HP otomatis membunyikan notifikasi saat giliran piketmu tiba (atau pilih 'Semua Siswa'):",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari nama...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredList) { sName ->
                            val isSel = uiState.selectedStudentForReminder == sName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SmanNavyPrimary else Slate50,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setStudentForReminder(sName)
                                        showSelectReminderNameDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sName,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else Slate900,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSelectReminderNameDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal: Edit Admin Profile Dialog
    adminToEdit?.let { admin ->
        EditAdminProfileDialog(
            admin = admin,
            onDismiss = { adminToEdit = null },
            onSave = { newUsername, newFullName, newPassword, newRoleTitle, syncStructure ->
                val ok = viewModel.updateAdminProfile(
                    id = admin.id,
                    username = newUsername,
                    fullName = newFullName,
                    pass = newPassword,
                    roleTitle = newRoleTitle,
                    syncWithStructure = syncStructure
                )
                if (ok) {
                    adminToEdit = null
                }
            }
        )
    }
}

