package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ClassStructure
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClassUiState
import com.example.ui.viewmodel.ClassViewModel

@Composable
fun ClassStructureScreen(
    uiState: ClassUiState,
    viewModel: ClassViewModel
) {
    val structure = uiState.classStructure
    val isAdmin = uiState.loggedInAdmin != null
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header info & Admin edit button
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = SmanNavyPrimary,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Struktur Pengurus XI-7",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "SMAN 3 Blitar • Tahun Ajaran 2024/2025",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isAdmin) {
                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_edit_structure")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SmanGoldLight
                    ) {
                        Text(
                            text = "Resmi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmanGoldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Organizational Tree in Scrollable Container
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // TIER 1: WALI KELAS
            item {
                Text(
                    text = "PIMPINAN & PEMBINA KELAS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Slate700,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                HomeroomTeacherCard(
                    name = structure.homeroomTeacher,
                    nip = structure.homeroomNip
                )
            }

            // TIER 2: KETUA & WAKIL KETUA KELAS
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PENGURUS HARIAN KELAS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Slate700,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Ketua Kelas",
                            studentName = structure.classLeader,
                            accentColor = SmanNavyPrimary,
                            icon = Icons.Default.MilitaryTech,
                            dutyDesc = "Memimpin koordinasi kelas, apel pagi, dan perwakilan pertemuan sekolah."
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Wakil Ketua",
                            studentName = structure.viceLeader,
                            accentColor = SmanSkyBlue,
                            icon = Icons.Default.SupervisorAccount,
                            dutyDesc = "Membantu koordinasi piket, ketertiban kelas, dan pengganti saat ketua berhalangan."
                        )
                    }
                }
            }

            // TIER 3: KESEKRETARIATAN
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DIVISI KESEKRETARIATAN (ADMINISTRASI)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Slate700,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Sekretaris 1",
                            studentName = structure.secretary1,
                            accentColor = SmanEmerald,
                            icon = Icons.Default.EditNote,
                            dutyDesc = "Pencatatan absensi harian kelas XI-7 dan notula rapat pengurus."
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Sekretaris 2",
                            studentName = structure.secretary2,
                            accentColor = SmanEmerald,
                            icon = Icons.Default.Assignment,
                            dutyDesc = "Administrasi agenda surat, pengumuman sekolah, dan pembagian tugas."
                        )
                    }
                }
            }

            // TIER 4: KEBENDAHARAAN
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DIVISI KEBENDAHARAAN (KEUANGAN KAS)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Slate700,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Bendahara 1",
                            studentName = structure.treasurer1,
                            accentColor = SmanGoldDark,
                            icon = Icons.Default.AccountBalanceWallet,
                            dutyDesc = "Penarikan uang kas rutin mingguan siswa XI-7 dan buku kas utama."
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OfficerCard(
                            roleTitle = "Bendahara 2",
                            studentName = structure.treasurer2,
                            accentColor = SmanGoldDark,
                            icon = Icons.Default.Savings,
                            dutyDesc = "Pencatatan pengeluaran operasional (spidol, ATK, galon, kebersihan kelas)."
                        )
                    }
                }
            }

            // Summary note
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    border = BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SmanEmerald, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pengurus Resmi Kelas XI-7",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Slate900
                            )
                            Text(
                                text = "Ditetapkan oleh Wali Kelas & disahkan pada musyawarah kelas SMAN 3 Blitar.",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Edit Class Structure Dialog (Admin only)
    if (showEditDialog) {
        EditClassStructureDialog(
            initial = structure,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateClassStructure(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun HomeroomTeacherCard(
    name: String,
    nip: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, SmanNavyPrimary.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_homeroom_teacher")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = SmanNavyPrimary,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SmanNavyPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "WALI KELAS XI-7",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = SmanNavyPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Slate900
                )
                Text(
                    text = "NIP: $nip",
                    fontSize = 12.sp,
                    color = Slate700
                )
            }
            Surface(
                shape = CircleShape,
                color = SmanGold,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = SmanNavyDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OfficerCard(
    roleTitle: String,
    studentName: String,
    accentColor: Color,
    icon: ImageVector,
    dutyDesc: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("officer_${roleTitle.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = roleTitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = studentName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dutyDesc,
                fontSize = 11.sp,
                color = Slate700,
                lineHeight = 15.sp,
                minLines = 3
            )
        }
    }
}

@Composable
private fun EditClassStructureDialog(
    initial: ClassStructure,
    onDismiss: () -> Unit,
    onSave: (ClassStructure) -> Unit
) {
    var homeroom by remember { mutableStateOf(initial.homeroomTeacher) }
    var nip by remember { mutableStateOf(initial.homeroomNip) }
    var leader by remember { mutableStateOf(initial.classLeader) }
    var vice by remember { mutableStateOf(initial.viceLeader) }
    var sec1 by remember { mutableStateOf(initial.secretary1) }
    var sec2 by remember { mutableStateOf(initial.secretary2) }
    var tres1 by remember { mutableStateOf(initial.treasurer1) }
    var tres2 by remember { mutableStateOf(initial.treasurer2) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Pengurus Kelas XI-7",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = homeroom,
                            onValueChange = { homeroom = it },
                            label = { Text("Wali Kelas") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = nip,
                            onValueChange = { nip = it },
                            label = { Text("NIP Wali Kelas") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = leader,
                            onValueChange = { leader = it },
                            label = { Text("Ketua Kelas") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = vice,
                            onValueChange = { vice = it },
                            label = { Text("Wakil Ketua Kelas") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = sec1,
                            onValueChange = { sec1 = it },
                            label = { Text("Sekretaris 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = sec2,
                            onValueChange = { sec2 = it },
                            label = { Text("Sekretaris 2") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = tres1,
                            onValueChange = { tres1 = it },
                            label = { Text("Bendahara 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = tres2,
                            onValueChange = { tres2 = it },
                            label = { Text("Bendahara 2") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = initial.copy(
                            homeroomTeacher = homeroom.trim(),
                            homeroomNip = nip.trim(),
                            classLeader = leader.trim(),
                            viceLeader = vice.trim(),
                            secretary1 = sec1.trim(),
                            secretary2 = sec2.trim(),
                            treasurer1 = tres1.trim(),
                            treasurer2 = tres2.trim()
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan Susunan Pengurus", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
