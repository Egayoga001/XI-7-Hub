package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AdminAccount
import com.example.data.model.AttachmentType
import com.example.data.model.ClassPost
import com.example.data.repository.ClassRepository
import com.example.ui.components.EditAdminProfileDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClassUiState
import com.example.ui.viewmodel.ClassViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassPostsScreen(
    uiState: ClassUiState,
    viewModel: ClassViewModel
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<ClassPost?>(null) }
    var previewAttachmentPost by remember { mutableStateOf<ClassPost?>(null) }
    var adminToEdit by remember { mutableStateOf<AdminAccount?>(null) }

    val categories = listOf("Semua", "Pengumuman", "Materi", "Tugas", "Kegiatan")

    val filteredPosts = remember(uiState.classPosts, selectedCategory, searchQuery) {
        uiState.classPosts.filter { post ->
            val matchCategory = selectedCategory == "Semua" || post.category.equals(selectedCategory, ignoreCase = true)
            val matchQuery = searchQuery.isEmpty() ||
                    post.title.contains(searchQuery, ignoreCase = true) ||
                    post.content.contains(searchQuery, ignoreCase = true) ||
                    post.authorName.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Admin Status Banner / Callout
        AdminStatusBar(
            loggedInAdmin = uiState.loggedInAdmin,
            registeredAdmins = uiState.registeredAdmins,
            maxAdminCount = ClassRepository.MAX_ADMIN_COUNT,
            onOpenAuth = { showAuthDialog = true },
            onLogout = { viewModel.logoutAdmin() },
            onCreatePost = { showCreatePostDialog = true },
            onEditProfile = { adminToEdit = uiState.loggedInAdmin }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari postingan, materi, pengumuman...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = Slate700,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_post"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SmanNavyPrimary,
                unfocusedBorderColor = Slate200,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SmanNavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_${category.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Post Count Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Menampilkan ${filteredPosts.size} postingan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Slate700
            )
            if (uiState.loggedInAdmin != null) {
                Text(
                    text = "Mode Admin Aktif • Dapat unggah & hapus",
                    fontSize = 11.sp,
                    color = SmanEmerald,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Mode Siswa: Terbuka untuk umum",
                    fontSize = 11.sp,
                    color = SmanNavyPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Post List
        if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Belum ada postingan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Slate800,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Tidak ada postingan yang sesuai kata kunci pencarian."
                        else "Admin kelas dapat mengunggah pengumuman, materi (gambar, video, PDF/dokumen).",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate700,
                            fontSize = 12.sp
                        )
                    )
                    if (uiState.loggedInAdmin == null && uiState.classPosts.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showAuthDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Login Admin Kelas")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    ClassPostCard(
                        post = post,
                        isAdmin = uiState.loggedInAdmin != null,
                        onDeleteClick = { postToDelete = post },
                        onAttachmentClick = { previewAttachmentPost = post }
                    )
                }
            }
        }
    }

    // Modal: Admin Login & Register Dialog
    if (showAuthDialog) {
        AdminAuthDialog(
            registeredAdmins = uiState.registeredAdmins,
            maxAdminCount = ClassRepository.MAX_ADMIN_COUNT,
            onDismiss = { showAuthDialog = false },
            onLogin = { user, pass ->
                val ok = viewModel.loginAdmin(user, pass)
                if (ok) showAuthDialog = false
            },
            onRegister = { user, name, pass, role ->
                val ok = viewModel.registerAdmin(user, name, pass, role)
                if (ok) showAuthDialog = false
            },
            onDeleteAdmin = { adminId ->
                viewModel.deleteAdmin(adminId)
            },
            onResetAdmins = {
                viewModel.resetAdminAccounts()
            },
            onEditAdmin = { admin ->
                showAuthDialog = false
                adminToEdit = admin
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

    // Modal: Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            loggedInAdmin = uiState.loggedInAdmin,
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { title, content, category, attachType, uri, fileName ->
                val ok = viewModel.addPost(title, content, category, attachType, uri, fileName)
                if (ok) showCreatePostDialog = false
            }
        )
    }

    // Confirmation: Delete Post Dialog
    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            icon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Red500) },
            title = { Text("Hapus Postingan?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus postingan \"${post.title}\"? Siswa tidak akan dapat melihat postingan ini lagi.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePost(post.id)
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500)
                ) {
                    Text("Hapus Postingan")
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal: Attachment Viewer / Details
    previewAttachmentPost?.let { post ->
        AttachmentDetailDialog(
            post = post,
            onDismiss = { previewAttachmentPost = null }
        )
    }

    // Floating Action Button for Posting (Always visible at bottom right)
    ExtendedFloatingActionButton(
        onClick = {
            if (uiState.loggedInAdmin != null) {
                showCreatePostDialog = true
            } else {
                showAuthDialog = true
            }
        },
        containerColor = SmanEmerald,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .testTag("fab_create_post"),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (uiState.loggedInAdmin != null) "Buat Postingan" else "Login Admin & Posting",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
  }
}

@Composable
private fun AdminStatusBar(
    loggedInAdmin: AdminAccount?,
    registeredAdmins: List<AdminAccount>,
    maxAdminCount: Int,
    onOpenAuth: () -> Unit,
    onLogout: () -> Unit,
    onCreatePost: () -> Unit,
    onEditProfile: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (loggedInAdmin != null) SmanEmerald.copy(alpha = 0.12f) else Slate100
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (loggedInAdmin != null) SmanEmerald.copy(alpha = 0.5f) else Slate200
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (loggedInAdmin != null) SmanEmerald else SmanNavyPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (loggedInAdmin != null) Icons.Default.AdminPanelSettings else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    if (loggedInAdmin != null) {
                        Text(
                            text = "Admin: ${loggedInAdmin.fullName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${loggedInAdmin.roleTitle} • Siap Mengunggah",
                            fontSize = 11.sp,
                            color = SmanEmeraldDark,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Akses Khusus Admin Kelas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Slot Terdaftar: ${registeredAdmins.size}/$maxAdminCount akun",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (loggedInAdmin != null) {
                    if (onEditProfile != null) {
                        IconButton(
                            onClick = onEditProfile,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profil Admin",
                                tint = SmanNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Button(
                        onClick = onCreatePost,
                        colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_create_post")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unggah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Keluar Admin",
                            tint = Red500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onOpenAuth,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SmanNavyPrimary),
                        modifier = Modifier.testTag("btn_open_admin_auth")
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Login / Daftar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ClassPostCard(
    post: ClassPost,
    isAdmin: Boolean,
    onDeleteClick: () -> Unit,
    onAttachmentClick: () -> Unit
) {
    val dateStr = remember(post.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID"))
        sdf.format(Date(post.createdAt))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_post_${post.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Author info & Category badge & Delete icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SmanNavyPrimary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.authorName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SmanGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = post.authorRole,
                                    fontSize = 10.sp,
                                    color = SmanGoldDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (post.category.lowercase()) {
                            "materi" -> SmanSkyBlue.copy(alpha = 0.15f)
                            "tugas" -> Amber500.copy(alpha = 0.15f)
                            "kegiatan" -> SmanEmerald.copy(alpha = 0.15f)
                            else -> SmanNavyPrimary.copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            text = post.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (post.category.lowercase()) {
                                "materi" -> SmanSkyBlue
                                "tugas" -> Amber600
                                "kegiatan" -> SmanEmeraldDark
                                else -> SmanNavyPrimary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Postingan",
                                tint = Red500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Post Content
            Text(
                text = post.content,
                fontSize = 13.sp,
                color = Slate800,
                lineHeight = 19.sp
            )

            // Attachment Banner if present
            if (post.attachmentType != AttachmentType.NONE) {
                Spacer(modifier = Modifier.height(12.dp))
                PostAttachmentView(
                    post = post,
                    onClick = onAttachmentClick
                )
            }
        }
    }
}

@Composable
private fun PostAttachmentView(
    post: ClassPost,
    onClick: () -> Unit
) {
    when (post.attachmentType) {
        AttachmentType.IMAGE -> {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Column {
                    if (post.attachmentUriOrData.isNotEmpty()) {
                        AsyncImage(
                            model = post.attachmentUriOrData,
                            contentDescription = post.attachmentFileName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(SmanSkyBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = SmanSkyBlue,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SmanNavyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.attachmentFileName.ifEmpty { "Lampiran Gambar" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "Ketuk untuk perbesar",
                            fontSize = 11.sp,
                            color = SmanNavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        AttachmentType.VIDEO -> {
            val uriStr = post.attachmentUriOrData
            val isYouTube = uriStr.contains("youtube.com") || uriStr.contains("youtu.be")
            val isDrive = uriStr.contains("drive.google.com")
            val isTikTok = uriStr.contains("tiktok.com")
            val isInstagram = uriStr.contains("instagram.com")
            val platformLabel = when {
                isYouTube -> "YOUTUBE VIDEO"
                isDrive -> "GOOGLE DRIVE VIDEO"
                isTikTok -> "TIKTOK VIDEO"
                isInstagram -> "INSTAGRAM VIDEO"
                else -> "VIDEO PEMBELAJARAN"
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate900,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isYouTube) Red500 else if (isDrive) Amber500 else WoodyRed,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Red500.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = platformLabel,
                                    color = Red500,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = post.attachmentFileName.ifEmpty { "Video Materi / Penjelasan" },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Ketuk untuk memutar video langsung",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        AttachmentType.DOCUMENT -> {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate100,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SmanNavyPrimary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SmanNavyPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "DOKUMEN / PDF",
                                    color = SmanNavyPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = post.attachmentFileName.ifEmpty { "Dokumen Materi / Tugas.pdf" },
                            color = Slate900,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Format file materi siap dibaca siswa",
                            color = Slate700,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Unduh",
                        tint = SmanNavyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        AttachmentType.NONE -> {}
    }
}

private fun handleDownloadAttachment(
    context: android.content.Context,
    uriOrData: String,
    fileName: String,
    mimeType: String = "*/*"
) {
    val cleanUri = uriOrData.trim()
    if (cleanUri.isEmpty()) {
        Toast.makeText(context, "Lampiran tidak tersedia untuk diunduh", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        if (cleanUri.startsWith("http://") || cleanUri.startsWith("https://")) {
            val dm = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as? android.app.DownloadManager
            if (dm != null) {
                val safeName = if (fileName.isNotBlank()) fileName else "Lampiran_XI7_${System.currentTimeMillis()}"
                val req = android.app.DownloadManager.Request(Uri.parse(cleanUri)).apply {
                    setTitle(safeName)
                    setDescription("Mengunduh berkas postingan XI-7 Hub...")
                    setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, safeName)
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(true)
                }
                dm.enqueue(req)
                Toast.makeText(context, "Memulai unduhan file: $safeName...", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUri))
                context.startActivity(intent)
            }
        } else if (cleanUri.startsWith("data:") || cleanUri.contains("base64,")) {
            val base64Data = if (cleanUri.contains(",")) cleanUri.substringAfter(",") else cleanUri
            val decoded = Base64.decode(base64Data, Base64.DEFAULT)
            val safeName = if (fileName.isNotBlank()) fileName else "XI7_File_${System.currentTimeMillis()}"
            val downloadDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) downloadDir.mkdirs()
            val outFile = java.io.File(downloadDir, safeName)
            outFile.writeBytes(decoded)
            Toast.makeText(context, "File tersimpan di folder Unduhan: $safeName", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUri)).apply {
                setDataAndType(Uri.parse(cleanUri), mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Simpan / Unduh File melalui..."))
        }
    } catch (e: Exception) {
        try {
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(if (cleanUri.startsWith("http")) cleanUri else "https://drive.google.com"))
            context.startActivity(fallback)
        } catch (ex: Exception) {
            Toast.makeText(context, "Gagal mengunduh file", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun handleOpenAttachmentWith(
    context: android.content.Context,
    uriOrData: String,
    mimeType: String = "*/*"
) {
    val cleanUri = uriOrData.trim()
    val targetUri = if (cleanUri.startsWith("http://") || cleanUri.startsWith("https://") || cleanUri.startsWith("content://") || cleanUri.startsWith("file://")) {
        Uri.parse(cleanUri)
    } else {
        Uri.parse("https://drive.google.com")
    }

    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            if (cleanUri.startsWith("content://") || cleanUri.startsWith("file://")) {
                setDataAndType(targetUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                data = targetUri
            }
        }
        val chooser = Intent.createChooser(intent, "Buka Melalui...")
        context.startActivity(chooser)
    } catch (e: Exception) {
        try {
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com"))
            context.startActivity(fallback)
        } catch (ex: Exception) {
            Toast.makeText(context, "Aplikasi eksternal tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun AttachmentDetailDialog(
    post: ClassPost,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lampiran Postingan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate900
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (post.attachmentType) {
                    AttachmentType.IMAGE -> {
                        if (post.attachmentUriOrData.isNotEmpty()) {
                            AsyncImage(
                                model = post.attachmentUriOrData,
                                contentDescription = post.attachmentFileName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 260.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        handleOpenAttachmentWith(context, post.attachmentUriOrData, "image/*")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lihat (Preview)", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        handleDownloadAttachment(context, post.attachmentUriOrData, post.attachmentFileName.ifEmpty { "Gambar_XI7.jpg" }, "image/*")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .background(Slate100, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Slate400, modifier = Modifier.size(64.dp))
                            }
                        }
                    }
                    AttachmentType.VIDEO -> {
                        val uriStr = post.attachmentUriOrData
                        val isYouTube = uriStr.contains("youtube.com") || uriStr.contains("youtu.be")
                        val isDrive = uriStr.contains("drive.google.com")
                        val platformLabel = when {
                            isYouTube -> "Putar Video di YouTube"
                            isDrive -> "Putar Video di Google Drive"
                            else -> "Putar Video Pembelajaran"
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate900,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = WoodyYellow,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(platformLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    handleOpenAttachmentWith(context, uriStr, "video/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Putar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    handleDownloadAttachment(context, uriStr, post.attachmentFileName.ifEmpty { "Video_XI7.mp4" }, "video/*")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", fontSize = 12.sp)
                            }
                        }
                    }
                    AttachmentType.DOCUMENT -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Red500, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = post.attachmentFileName.ifEmpty { "Dokumen Kelas XI-7.pdf" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Dokumen resmi materi / tugas",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val mime = if (post.attachmentFileName.endsWith(".pdf", ignoreCase = true)) "application/pdf" else "*/*"
                                    handleOpenAttachmentWith(context, post.attachmentUriOrData, mime)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buka Melalui...", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val mime = if (post.attachmentFileName.endsWith(".pdf", ignoreCase = true)) "application/pdf" else "*/*"
                                    handleDownloadAttachment(context, post.attachmentUriOrData, post.attachmentFileName.ifEmpty { "Dokumen_XI7.pdf" }, mime)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", fontSize = 12.sp)
                            }
                        }
                    }
                    AttachmentType.NONE -> {}
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.attachmentFileName.ifEmpty { "Lampiran Materi" },
                    fontSize = 12.sp,
                    color = Slate700
                )

                if (post.attachmentUriOrData.startsWith("http://") || post.attachmentUriOrData.startsWith("https://")) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachmentUriOrData))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // fallback
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buka Tautan Lampiran Asli")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
private fun AdminAuthDialog(
    registeredAdmins: List<AdminAccount>,
    maxAdminCount: Int,
    onDismiss: () -> Unit,
    onLogin: (username: String, pass: String) -> Unit,
    onRegister: (username: String, fullName: String, pass: String, roleTitle: String) -> Unit,
    onDeleteAdmin: ((String) -> Unit)? = null,
    onResetAdmins: (() -> Unit)? = null,
    onEditAdmin: ((AdminAccount) -> Unit)? = null
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    val canRegisterMore = registeredAdmins.size < maxAdminCount

    // Login state
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Register state
    var regUsername by remember { mutableStateOf("") }
    var regFullName by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRoleTitle by remember { mutableStateOf("Ketua Kelas") }

    val roleOptions = listOf("Ketua Kelas", "Wakil Ketua Kelas", "Sekretaris 1", "Sekretaris 2", "Bendahara 1", "Bendahara 2", "Wali Kelas")

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
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Akses Admin Kelas XI-7",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Khusus 2 akun admin pengurus kelas",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slots indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (canRegisterMore) SmanNavyPrimary.copy(alpha = 0.08f) else Amber500.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (canRegisterMore) Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (canRegisterMore) SmanNavyPrimary else Amber600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Slot Admin: ${registeredAdmins.size} dari $maxAdminCount akun terdaftar" +
                                    if (canRegisterMore) " (Tersedia ${maxAdminCount - registeredAdmins.size} slot)" else " (Penuh)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate800
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: Login vs Register
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = Slate100,
                    contentColor = SmanNavyPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = { selectedAuthTab = 0 },
                        text = { Text("Login Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = { selectedAuthTab = 1 },
                        text = { Text("Daftar Akun (${maxAdminCount - registeredAdmins.size} sisa)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedAuthTab == 0) {
                    // LOGIN FORM
                    if (registeredAdmins.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Akun Terdaftar di Sistem:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                registeredAdmins.forEach { admin ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${admin.fullName} (${admin.roleTitle})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SmanNavyPrimary
                                            )
                                            Text(
                                                text = "Username: ${admin.username} ${if (admin.username == "admin1") "• Pass: 123 / admin123" else ""}",
                                                fontSize = 11.sp,
                                                color = Slate700
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Button(
                                                onClick = {
                                                    loginUsername = admin.username
                                                    loginPassword = if (admin.username == "admin1") "123" else admin.password
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Gunakan", fontSize = 10.sp)
                                            }

                                            if (onEditAdmin != null) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { onEditAdmin(admin) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit profil admin",
                                                        tint = SmanNavyPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            if (onDeleteAdmin != null) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { onDeleteAdmin(admin.id) },
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
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada akun admin terdaftar (2 slot kosong).",
                                    fontSize = 12.sp,
                                    color = Slate700
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                if (onResetAdmins != null) {
                                    OutlinedButton(
                                        onClick = onResetAdmins,
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Muat Akun Bawaan (admin1 / 123)", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = loginUsername,
                        onValueChange = { loginUsername = it },
                        label = { Text("Username Admin") },
                        placeholder = { Text("misal: admin1") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_login_user")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_login_pass")
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "*Akun bawaan sistem: username 'admin1' & pass '123' (Erastus)",
                        fontSize = 11.sp,
                        color = Slate700
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onLogin(loginUsername, loginPassword) },
                        enabled = loginUsername.isNotBlank() && loginPassword.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_submit_admin_login")
                    ) {
                        Text("Masuk Sebagai Admin", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // REGISTER FORM (MAX 2)
                    if (!canRegisterMore) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Red500, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Batas 2 Akun Admin Penuh",
                                fontWeight = FontWeight.Bold,
                                color = Red500,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Aplikasi ini membatasi hanya 2 pengurus kelas sebagai admin. Hapus salah satu akun di tab Login untuk mendaftarkan akun baru.",
                                fontSize = 12.sp,
                                color = Slate700,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it },
                            label = { Text("Username Baru") },
                            placeholder = { Text("misal: admin2 / wakil") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_reg_user")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Nama Lengkap Pengurus") },
                            placeholder = { Text("misal: Cahaya (Wakil Ketua)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_reg_name")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Password (min. 4 karakter)") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_reg_pass")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Jabatan Pengurus:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(roleOptions) { role ->
                                val isSelected = regRoleTitle == role
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) SmanNavyPrimary else Slate100,
                                    modifier = Modifier.clickable { regRoleTitle = role }
                                ) {
                                    Text(
                                        text = role,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Slate800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onRegister(regUsername, regFullName, regPassword, regRoleTitle) },
                            enabled = regUsername.isNotBlank() && regFullName.isNotBlank() && regPassword.length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_admin_reg")
                        ) {
                            Text("Daftarkan Akun Admin ke-2", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun compressImageUriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        if (original == null) return null

        val maxWidth = 800
        val width = original.width
        val height = original.height
        val scaled = if (width > maxWidth) {
            val scaledHeight = (height.toFloat() * maxWidth / width).toInt()
            Bitmap.createScaledBitmap(original, maxWidth, scaledHeight, true)
        } else {
            original
        }

        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, out)
        val bytes = out.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

private fun getFileSize(context: android.content.Context, uri: Uri): Long {
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun readUriAsBase64(context: android.content.Context, uri: Uri, mimeType: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()
        "data:$mimeType;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun CreatePostDialog(
    loggedInAdmin: AdminAccount?,
    onDismiss: () -> Unit,
    onSubmit: (title: String, content: String, category: String, attachType: AttachmentType, uri: String, fileName: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pengumuman") }
    var attachmentType by remember { mutableStateOf(AttachmentType.NONE) }
    var attachmentUriOrData by remember { mutableStateOf("") }
    var attachmentFileName by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            val path = pickedUri.lastPathSegment ?: "file_lampiran"
            attachmentFileName = path.substringAfterLast('/')

            when (attachmentType) {
                AttachmentType.IMAGE -> {
                    val base64Data = readUriAsBase64(context, pickedUri, "image/jpeg")
                    if (base64Data != null) {
                        attachmentUriOrData = base64Data
                        Toast.makeText(context, "Foto berhasil dimuat dan siap dibagikan!", Toast.LENGTH_SHORT).show()
                    } else {
                        attachmentUriOrData = pickedUri.toString()
                    }
                }
                AttachmentType.VIDEO -> {
                    val sizeBytes = getFileSize(context, pickedUri)
                    val sizeMb = sizeBytes.toDouble() / (1024 * 1024)
                    if (sizeMb > 10.0) {
                        Toast.makeText(
                            context,
                            "Ukuran file video (${String.format(Locale.US, "%.1f", sizeMb)}MB) melebihi batas 10MB! Gunakan link YouTube / Google Drive pada Opsi 1.",
                            Toast.LENGTH_LONG
                        ).show()
                        attachmentUriOrData = ""
                        attachmentFileName = ""
                    } else {
                        val base64Data = readUriAsBase64(context, pickedUri, "video/mp4")
                        if (base64Data != null) {
                            attachmentUriOrData = base64Data
                            Toast.makeText(context, "Video siap diputar (${String.format(Locale.US, "%.1f", sizeMb)}MB)!", Toast.LENGTH_SHORT).show()
                        } else {
                            attachmentUriOrData = pickedUri.toString()
                        }
                    }
                }
                AttachmentType.DOCUMENT -> {
                    val sizeBytes = getFileSize(context, pickedUri)
                    val sizeMb = sizeBytes.toDouble() / (1024 * 1024)
                    if (sizeMb > 10.0) {
                        Toast.makeText(
                            context,
                            "Ukuran file dokumen melebihi batas 10MB!",
                            Toast.LENGTH_LONG
                        ).show()
                        attachmentUriOrData = ""
                        attachmentFileName = ""
                    } else {
                        val base64Data = readUriAsBase64(context, pickedUri, "application/octet-stream")
                        if (base64Data != null) {
                            attachmentUriOrData = base64Data
                            Toast.makeText(context, "Dokumen siap diunggah!", Toast.LENGTH_SHORT).show()
                        } else {
                            attachmentUriOrData = pickedUri.toString()
                        }
                    }
                }
                else -> {
                    attachmentUriOrData = pickedUri.toString()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            when (attachmentType) {
                AttachmentType.IMAGE -> filePickerLauncher.launch("image/*")
                AttachmentType.VIDEO -> filePickerLauncher.launch("video/*")
                AttachmentType.DOCUMENT -> filePickerLauncher.launch("*/*")
                else -> filePickerLauncher.launch("*/*")
            }
        } else {
            Toast.makeText(context, "Izin akses penyimpanan ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    val categoryList = listOf("Pengumuman", "Materi", "Tugas", "Kegiatan")

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Unggah Postingan Baru",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Penulis: ${loggedInAdmin?.fullName ?: "Admin"} (${loggedInAdmin?.roleTitle})",
                            fontSize = 11.sp,
                            color = SmanEmeraldDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Judul
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Postingan") },
                    placeholder = { Text("misal: Jadwal Pengumpulan Tugas Ekonomi") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_post_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Kategori Selector
                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate700)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categoryList.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SmanNavyPrimary else Slate100,
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Slate800,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Isi Konten
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Pesan / Penjelasan") },
                    placeholder = { Text("Tuliskan informasi lengkap untuk seluruh siswa...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_post_content")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Jenis Lampiran
                Text("Lampirkan File (Gambar / Video / Dokumen):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate700)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val types = listOf(
                        Triple(AttachmentType.NONE, "Tanpa", Icons.Default.Cancel),
                        Triple(AttachmentType.IMAGE, "Gambar", Icons.Default.Image),
                        Triple(AttachmentType.VIDEO, "Video", Icons.Default.Videocam),
                        Triple(AttachmentType.DOCUMENT, "Dokumen", Icons.Default.Description)
                    )
                    types.forEach { (type, label, icon) ->
                        val isSelected = attachmentType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SmanNavyPrimary else Slate100,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    attachmentType = type
                                    if (type == AttachmentType.NONE) {
                                        attachmentUriOrData = ""
                                        attachmentFileName = ""
                                    } else if (attachmentFileName.isEmpty()) {
                                        when (type) {
                                            AttachmentType.IMAGE -> attachmentFileName = "catatan_xi7.jpg"
                                            AttachmentType.VIDEO -> attachmentFileName = "penjelasan_materi.mp4"
                                            AttachmentType.DOCUMENT -> attachmentFileName = "modul_pelajaran.pdf"
                                            else -> {}
                                        }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) Color.White else Slate700
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Slate800
                                )
                            }
                        }
                    }
                }

                if (attachmentType != AttachmentType.NONE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Pilih Cara Lampiran Berkas:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Spacer(modifier = Modifier.height(6.dp))

                    // OPSI 2: Tombol Upload File Perangkat (Minta Izin & Buka File Manager/Galeri)
                    Button(
                        onClick = {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (attachmentType) {
                                    AttachmentType.IMAGE -> Manifest.permission.READ_MEDIA_IMAGES
                                    AttachmentType.VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
                                    else -> Manifest.permission.READ_MEDIA_IMAGES
                                }
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }

                            val hasPerm = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                            if (hasPerm) {
                                when (attachmentType) {
                                    AttachmentType.IMAGE -> filePickerLauncher.launch("image/*")
                                    AttachmentType.VIDEO -> filePickerLauncher.launch("video/*")
                                    AttachmentType.DOCUMENT -> filePickerLauncher.launch("*/*")
                                    else -> filePickerLauncher.launch("*/*")
                                }
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmanNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_pick_device_file")
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📁 Opsi 2: Upload File dari Penyimpanan / Galeri HP", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // OPSI 1: Kolom Input URL / Link Apapun
                    OutlinedTextField(
                        value = attachmentUriOrData,
                        onValueChange = { attachmentUriOrData = it },
                        label = { Text("Opsi 1: Salin & Tempel Link / URL (Opsional)") },
                        placeholder = { Text("https://... (Drive, YouTube, atau Link Web)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_attachment_url")
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = attachmentFileName,
                        onValueChange = { attachmentFileName = it },
                        label = { Text("Nama File / Keterangan Lampiran") },
                        placeholder = { Text("misal: modul_matematika.pdf / foto_kegiatan.jpg") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_attachment_filename")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Simpan & Kirim
                Button(
                    onClick = {
                        onSubmit(title, content, category, attachmentType, attachmentUriOrData, attachmentFileName)
                    },
                    enabled = title.isNotBlank() && content.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_submit_post")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unggah & Kirim Notifikasi ke Siswa", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
