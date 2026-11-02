package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    onLoadingFinished: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Smooth 2-second loading progress simulation
        val startTime = System.currentTimeMillis()
        val duration = 2000L // 2 seconds

        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val currentProgress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            progress = currentProgress

            if (currentProgress >= 1f) {
                delay(200) // slight pause at 100%
                isLoading = false
                break
            }
            delay(16) // ~60fps smooth update
        }
        onLoadingFinished()
    }

    AnimatedVisibility(
        visible = isLoading,
        exit = fadeOut(animationSpec = tween(600))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEA580C), // Warm Orange #EA580C
                            Color(0xFFF97316)  // Bright Orange #F97316
                        )
                    )
                )
                .testTag("splash_screen_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Logo Icon with Toy Story / Explorer vibe
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(96.dp)
                        .border(3.dp, WoodyYellow, CircleShape),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Exieven Hub Logo",
                            tint = WoodyRed,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Title
                Text(
                    text = "EXIEVEN HUB",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "SMAN 3 BLITAR • XI-7",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WoodyYellow,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                )

                // Thin loading bar (ramah perangkat low-end)
                Column(
                    modifier = Modifier.width(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = WoodyYellow,
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
