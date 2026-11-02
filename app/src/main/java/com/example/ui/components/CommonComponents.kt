package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.model.AdminAccount
import com.example.ui.theme.*

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    loggedInAdmin: AdminAccount? = null,
    onAdminBadgeClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null,
    syncStatusText: String = "🟢 Sinkron ke Cloud"
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(SmanGold, SmanGoldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Logo SMAN 3 Blitar",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (subtitle != null) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "• $syncStatusText",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (loggedInAdmin != null) SmanEmerald else SmanGold,
                    modifier = Modifier
                        .clickable { onAdminBadgeClick?.invoke() }
                        .testTag("top_bar_class_badge")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = if (loggedInAdmin != null) Icons.Default.AdminPanelSettings else Icons.Default.Group,
                            contentDescription = null,
                            tint = if (loggedInAdmin != null) Color.White else SmanNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (loggedInAdmin != null) "ADMIN: ${loggedInAdmin.fullName}" else "KELAS XI-7",
                            color = if (loggedInAdmin != null) Color.White else SmanNavyDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun AppBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Beranda", Icons.Default.Home, Icons.Outlined.Home, "nav_beranda"),
        NavItem("Jadwal", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_jadwal"),
        NavItem("Postingan", Icons.Default.Campaign, Icons.Outlined.Campaign, "nav_postingan"),
        NavItem("Struktur", Icons.Default.AccountTree, Icons.Outlined.AccountTree, "nav_struktur"),
        NavItem("Pengaturan", Icons.Default.Tune, Icons.Outlined.Tune, "nav_settings")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SmanNavyPrimary,
                    selectedTextColor = SmanNavyPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = Slate700,
                    unselectedTextColor = Slate700
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val (bgColor, textColor) = when (category) {
        "Pengumuman" -> Pair(SmanRoseLight, SmanRose)
        "Materi Pelajaran" -> Pair(SmanGoldLight, SmanGoldDark)
        "Tugas" -> Pair(Color(0xFFE0E7FF), SmanNavyPrimary)
        else -> Pair(SmanEmeraldLight, SmanEmerald)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = category,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
