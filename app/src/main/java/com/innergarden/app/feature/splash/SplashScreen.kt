package com.innergarden.app.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.TreeVisual
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.theme.GardenBackground
import com.innergarden.app.ui.theme.MintSurfaceStrong
import androidx.compose.foundation.shape.CircleShape

@Composable
fun SplashScreen(viewModel: SplashViewModel, onFinished: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val reveal = remember { Animatable(0f) }
    LaunchedEffect(Unit) { reveal.animateTo(1f, tween(750)) }
    LaunchedEffect(state.canNavigate) {
        if (state.canNavigate) onFinished()
    }
    Column(
        Modifier.fillMaxSize().background(GardenBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.graphicsLayer {
                alpha = reveal.value
                translationY = (1f - reveal.value) * 18.dp.toPx()
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.background(MintSurfaceStrong, CircleShape), contentAlignment = Alignment.Center) { TreeVisual(Modifier.size(190.dp)) }
            Spacer(Modifier.height(20.dp))
            Text(state.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(state.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.errorMessage?.let {
                Spacer(Modifier.height(20.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                InnerGardenButton("Try again", Modifier.fillMaxWidth(0.6f)) { viewModel.onEvent(SplashUiEvent.Retry) }
            }
        }
    }
}
