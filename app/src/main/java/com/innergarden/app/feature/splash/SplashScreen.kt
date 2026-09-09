package com.innergarden.app.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.TreeVisual
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: SplashViewModel, onFinished: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) { delay(1200); viewModel.onEvent(SplashUiEvent.Finished); onFinished() }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TreeVisual()
        Text(state.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(state.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
