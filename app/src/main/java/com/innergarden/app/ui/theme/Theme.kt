package com.innergarden.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GardenColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = GardenSurface,
    primaryContainer = PaleMint,
    onPrimaryContainer = DeepForest,
    secondary = FreshGreen,
    onSecondary = GardenSurface,
    secondaryContainer = Mint,
    onSecondaryContainer = DeepForest,
    tertiary = SoftGreen,
    background = GardenBackground,
    onBackground = GardenText,
    surface = GardenSurface,
    onSurface = GardenText,
    surfaceVariant = PaleMint,
    onSurfaceVariant = GardenTextSecondary,
    outline = SoftGreen
)

@Composable
fun InnerGardenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GardenColorScheme,
        typography = Typography,
        content = content
    )
}
