package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayOfWeekEnum
import com.example.data.model.LessonPeriod
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClassUiState
import com.example.ui.viewmodel.ClassViewModel

@Composable
fun ScheduleScreen(
    uiState: ClassUiState,
    viewModel: ClassViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Pelajaran, 1: Piket
    var lessonToEdit by remember { mutableStateOf<LessonPeriod?>(null) }
    var picketDayToEdit by remember { mutableStateOf<DayOfWeekEnum?>(null) }
    val days = listOf(
        DayOfWeekEnum.SENIN,
        DayOfWeekEnum.SELASA,
        DayOfWeekEnum.RABU,
        DayOfWeekEnum.KAMIS,
        DayOfWeekEnum.JUMAT
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Slate100,
            contentColor = SmanNavyPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Jadwal Pelajaran", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_jadwal_pelajaran")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Jadwal Piket (36)", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_jadwal_piket")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Day selector chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(days) { day ->
                    val isSelected = uiState.selectedScheduleDay == day
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedScheduleDay(day) },
                        label = {
                            Text(
                                text = day.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SmanNavyPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_day_${day.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val currentDayLessons = uiState.lessons.filter { it.day == uiState.selectedScheduleDay }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Info banner for selected day
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SmanNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Jadwal Resmi Kelas XI-7 SMAN 3 Blitar • Hari ${uiState.selectedScheduleDay.displayName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (uiState.loggedInAdmin != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "✏️ Mode Admin: Tekan ikon pensil pada jadwal untuk mengedit mapel atau guru",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WoodyRed
                                    )
                                }
                            }
                        }
                    }
                }

                items(currentDayLessons) { lesson ->
                    if (lesson.isBreak) {
                        // Rest / Break Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Coffee,
                                        contentDescription = null,
                                        tint = SmanGoldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = lesson.subject,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = lesson.timeRange,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (uiState.loggedInAdmin != null) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { lessonToEdit = lesson },
                                            modifier = Modifier
                                                .size(30.dp)
                                                .testTag("btn_edit_break_${lesson.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Jam Istirahat",
                                                tint = WoodyRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Lesson Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Time badge
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SmanNavyPrimary.copy(alpha = 0.08f),
                                    modifier = Modifier.width(86.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = lesson.periodLabel,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = SmanNavyPrimary
                                        )
                                        Text(
                                            text = lesson.timeRange,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.subject,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = lesson.teacherName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (lesson.teacherCode != "-") {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SmanGoldLight
                                        ) {
                                            Text(
                                                text = lesson.teacherCode,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SmanGoldDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (uiState.loggedInAdmin != null) {
                                    IconButton(
                                        onClick = { lessonToEdit = lesson },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("btn_edit_lesson_${lesson.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Jadwal Pelajaran",
                                            tint = WoodyRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.triggerLessonAlert(lesson) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Ingatkan Jam Ini",
                                        tint = SmanNavyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PICKET SCHEDULE TAB
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SmanGoldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Daftar Piket 36 Siswa Kelas XI-7",
                                fontWeight = FontWeight.Bold,
                                color = SmanGoldDark,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Setiap siswa memiliki jadwal piket mingguan. Centang kehadiran piket untuk konfirmasi.",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }

                items(days) { day ->
                    val studentList = uiState.picketSchedule[day] ?: emptyList()
                    val isToday = day == uiState.todayDayOfWeek

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = if (isToday) SmanNavyPrimary else Slate700,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hari ${day.displayName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isToday) SmanNavyPrimary else Slate900
                                    )
                                    if (isToday) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SmanNavyPrimary
                                        ) {
                                            Text(
                                                text = "HARI INI",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${studentList.size} Petugas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate700
                                    )
                                    if (uiState.loggedInAdmin != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { picketDayToEdit = day },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("btn_edit_piket_${day.name.lowercase()}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Petugas Piket",
                                                tint = WoodyRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Grid/list of student badges
                            studentList.chunked(2).forEach { rowStudents ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowStudents.forEach { student ->
                                        val isPresent = uiState.todayPicketAttendance[student] ?: false
                                        val isMe = student == uiState.selectedStudentForReminder

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isMe) SmanGoldLight else Slate50,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 3.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isPresent) SmanEmerald else SmanNavyPrimary),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = student.take(1),
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = student,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isMe) SmanGoldDark else Slate900
                                                    )
                                                }

                                                if (isToday) {
                                                    Checkbox(
                                                        checked = isPresent,
                                                        onCheckedChange = { checked ->
                                                            viewModel.togglePicketAttendance(student, checked)
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (rowStudents.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (lessonToEdit != null) {
            EditLessonDialog(
                lesson = lessonToEdit!!,
                onDismiss = { lessonToEdit = null },
                onSave = { subject, teacherName, teacherCode, timeRange, isBreak ->
                    viewModel.updateLesson(
                        lessonId = lessonToEdit!!.id,
                        newSubject = subject,
                        newTeacherName = teacherName,
                        newTeacherCode = teacherCode,
                        newTimeRange = timeRange,
                        isBreak = isBreak
                    )
                },
                onResetToDefault = {
                    viewModel.resetLessonToDefault(lessonToEdit!!.id)
                }
            )
        }

        if (picketDayToEdit != null) {
            val day = picketDayToEdit!!
            val currentList = uiState.picketSchedule[day] ?: emptyList()
            EditPicketDialog(
                day = day,
                currentStudents = currentList,
                onDismiss = { picketDayToEdit = null },
                onSave = { newList ->
                    viewModel.updatePicketSchedule(day, newList)
                    picketDayToEdit = null
                }
            )
        }
    }
}

@Composable
fun EditPicketDialog(
    day: DayOfWeekEnum,
    currentStudents: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val allStudents = com.example.data.repository.ClassRepository.ALL_36_STUDENTS
    var selectedStudents by remember { mutableStateOf(currentStudents.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    tint = BuzzGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Edit Petugas Piket",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hari ${day.displayName} (${selectedStudents.size} terpilih)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                Text(
                    text = "Pilih siswa bertugas piket hari ${day.displayName}:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allStudents) { student ->
                        val isSelected = selectedStudents.contains(student)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SmanEmeraldLight else Slate50,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStudents = if (isSelected) {
                                        selectedStudents - student
                                    } else {
                                        selectedStudents + student
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = student,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SmanEmeraldDark else Slate900
                                )
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedStudents = if (checked) {
                                            selectedStudents + student
                                        } else {
                                            selectedStudents - student
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedStudents.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = BuzzGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan Piket", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditLessonDialog(
    lesson: LessonPeriod,
    onDismiss: () -> Unit,
    onSave: (subject: String, teacherName: String, teacherCode: String, timeRange: String, isBreak: Boolean) -> Unit,
    onResetToDefault: () -> Unit
) {
    var subject by remember { mutableStateOf(lesson.subject) }
    var teacherName by remember { mutableStateOf(lesson.teacherName) }
    var teacherCode by remember { mutableStateOf(lesson.teacherCode) }
    var timeRange by remember { mutableStateOf(lesson.timeRange) }
    var isBreak by remember { mutableStateOf(lesson.isBreak) }
    var showResetConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = WoodyRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Edit Jadwal Pelajaran",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${lesson.day.displayName} • ${lesson.periodLabel}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Nama Mata Pelajaran") },
                    placeholder = { Text("misal: Ekonomi, Matematika") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_lesson_subject")
                )

                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("Nama Guru Pengajar") },
                    placeholder = { Text("misal: Naning Eko Noviana, M.Pd") },
                    singleLine = true,
                    enabled = !isBreak,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_lesson_teacher_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = teacherCode,
                        onValueChange = { teacherCode = it },
                        label = { Text("Kode Guru") },
                        placeholder = { Text("Kode 42") },
                        singleLine = true,
                        enabled = !isBreak,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_lesson_teacher_code")
                    )

                    OutlinedTextField(
                        value = timeRange,
                        onValueChange = { timeRange = it },
                        label = { Text("Jam / Waktu") },
                        placeholder = { Text("08.15 - 09.45") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_lesson_time_range")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isBreak = !isBreak }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isBreak,
                        onCheckedChange = { isBreak = it },
                        modifier = Modifier.testTag("checkbox_lesson_is_break")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jam Istirahat / Bebas (Non-KBM)",
                        fontSize = 12.sp,
                        color = TextDark
                    )
                }

                if (showResetConfirm) {
                    Surface(
                        color = WoodyYellow.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Kembalikan jadwal jam ini ke default resmi sekolah?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onResetToDefault()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WoodyRed),
                                    modifier = Modifier.testTag("btn_confirm_reset_lesson")
                                ) {
                                    Text("Ya, Kembalikan", fontSize = 11.sp, color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = { showResetConfirm = false }
                                ) {
                                    Text("Batal", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onSave(subject, teacherName, teacherCode, timeRange, isBreak)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BuzzGreen),
                modifier = Modifier.testTag("btn_save_lesson")
            ) {
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold, color = TextDark)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!showResetConfirm) {
                    TextButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.testTag("btn_reset_lesson_prompt")
                    ) {
                        Text("Reset Default", color = WoodyRed, fontSize = 12.sp)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Batal", color = TextMuted)
                }
            }
        }
    )
}

