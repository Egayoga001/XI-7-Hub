package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ClassViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                // Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        viewModel.setNotificationEnabled(true)
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasPerm) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Show feedback message in Snackbar
                LaunchedEffect(uiState.feedbackMessage) {
                    uiState.feedbackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearFeedback()
                    }
                }

                // BackHandler: navigate back to Home tab if on another tab
                BackHandler(enabled = uiState.activeTab != 0) {
                    viewModel.setActiveTab(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val subtitleText = when (uiState.activeTab) {
                            0 -> "Beranda Kelas XI-7"
                            1 -> "Jadwal Pelajaran & Piket"
                            2 -> "Postingan & Materi Kelas"
                            3 -> "Struktur Pengurus Kelas"
                            else -> "Pengaturan & Info Notifikasi"
                        }
                        AppTopBar(
                            title = "XI-7 Hub • SMAN 3 Blitar",
                            subtitle = subtitleText,
                            loggedInAdmin = uiState.loggedInAdmin,
                            onAdminBadgeClick = { viewModel.setActiveTab(2) },
                            onInfoClick = { viewModel.setActiveTab(4) }
                        )
                    },
                    bottomBar = {
                        AppBottomNavigation(
                            selectedTab = uiState.activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (uiState.activeTab) {
                            0 -> HomeScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateToSchedule = { viewModel.setActiveTab(1) },
                                onNavigateToPosts = { viewModel.setActiveTab(2) },
                                onNavigateToStructure = { viewModel.setActiveTab(3) },
                                onNavigateToSettings = { viewModel.setActiveTab(4) }
                            )
                            1 -> ScheduleScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                            2 -> ClassPostsScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                            3 -> ClassStructureScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                            4 -> SettingsScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
