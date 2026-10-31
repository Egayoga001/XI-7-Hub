package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AdminAccount
import com.example.ui.theme.*

@Composable
fun EditAdminProfileDialog(
    admin: AdminAccount,
    onDismiss: () -> Unit,
    onSave: (newUsername: String, newFullName: String, newPassword: String, newRoleTitle: String, syncStructure: Boolean) -> Unit
) {
    var fullName by remember { mutableStateOf(admin.fullName) }
    var username by remember { mutableStateOf(admin.username) }
    var password by remember { mutableStateOf(admin.password) }
    var passwordVisible by remember { mutableStateOf(false) }
    var roleTitle by remember { mutableStateOf(admin.roleTitle) }
    var syncStructure by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val commonRoles = listOf(
        "Ketua Kelas XI-7",
        "Wakil Ketua Kelas XI-7",
        "Sekretaris 1 XI-7",
        "Sekretaris 2 XI-7",
        "Bendahara 1 XI-7",
        "Bendahara 2 XI-7",
        "Wali Kelas XI-7",
        "Pengurus Kelas XI-7"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("dialog_edit_admin_profile")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SmanNavyPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ManageAccounts,
                                    contentDescription = null,
                                    tint = SmanNavyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Edit Profil Akun Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Slate900
                            )
                            Text(
                                text = "ID: ${admin.id} (${admin.username})",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Slate700)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error alert if any
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Red500.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Red500.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Red500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                color = Red500,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 1. Nama Lengkap (e.g. Erastus -> User's name)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = null
                    },
                    label = { Text("Nama Lengkap Admin") },
                    placeholder = { Text("misal: Nama Kamu / Ketua Baru") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = SmanNavyPrimary, modifier = Modifier.size(20.dp))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_admin_fullname"),
                    shape = RoundedCornerShape(10.dp)
                )
                Text(
                    text = "Ganti nama akun ini (sebelumnya: \"${admin.fullName}\")",
                    fontSize = 11.sp,
                    color = Slate700,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
                )

                // 2. Username Admin
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Username Login") },
                    placeholder = { Text("misal: ketua11_7") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = SmanNavyPrimary, modifier = Modifier.size(20.dp))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_admin_username"),
                    shape = RoundedCornerShape(10.dp)
                )
                Text(
                    text = "Username untuk masuk ke mode admin (sebelumnya: \"${admin.username}\")",
                    fontSize = 11.sp,
                    color = Slate700,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
                )

                // 3. Password Admin
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password Admin") },
                    placeholder = { Text("Masukkan password baru") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = SmanNavyPrimary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Sembunyikan password" else "Lihat password",
                                tint = Slate700,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_admin_password"),
                    shape = RoundedCornerShape(10.dp)
                )
                Text(
                    text = "Password untuk keamanan akun admin",
                    fontSize = 11.sp,
                    color = Slate700,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
                )

                // 4. Jabatan / Peran
                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = {
                        roleTitle = it
                        errorMessage = null
                    },
                    label = { Text("Jabatan / Peran di Kelas") },
                    placeholder = { Text("misal: Ketua Kelas XI-7") },
                    leadingIcon = {
                        Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = SmanNavyPrimary, modifier = Modifier.size(20.dp))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_admin_role"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pilih Cepat Jabatan:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate800
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(commonRoles) { role ->
                        val isSelected = roleTitle == role
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) SmanNavyPrimary else Slate100,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SmanNavyPrimary else Slate200
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { roleTitle = role }
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

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Sync with Struktur Pengurus Kelas checkbox
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { syncStructure = !syncStructure }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = syncStructure,
                            onCheckedChange = { syncStructure = it },
                            colors = CheckboxDefaults.colors(checkedColor = SmanNavyPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Sinkronkan ke Struktur Kelas",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate900
                            )
                            Text(
                                text = "Otomatis ganti nama Ketua Kelas di bagan struktur kelas XI-7",
                                fontSize = 10.sp,
                                color = Slate700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (fullName.isBlank() || username.isBlank() || password.isBlank()) {
                                errorMessage = "Nama, username, dan password wajib diisi!"
                            } else {
                                onSave(username.trim(), fullName.trim(), password.trim(), roleTitle.trim(), syncStructure)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmanEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("btn_save_admin_profile")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Profil", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
