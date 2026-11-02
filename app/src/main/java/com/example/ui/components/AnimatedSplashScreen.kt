package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    onLoadingFinished: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Tampil persis seperti yang diminta: Logo exieven di tengah dengan background oranye polos selama 2 detik
        delay(2000L)
        isLoading = false
        onLoadingFinished()
    }

    AnimatedVisibility(
        visible = isLoading,
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF7A00)) // Background oranye polos persis seperti logo
                .testTag("splash_screen_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Logo persis di tengah layar
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_hub),
                contentDescription = "Logo Exieven Hub",
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(36.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}
