package com.innergarden.app.feature.reflection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.ui.components.AnimatedReflectionLoading
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.GardenTextSecondary
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGardenSurface
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun ReflectionScreen(
    viewModel: ReflectionViewModel,
    onBackToGarden: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val exitToGarden = {
        viewModel.onEvent(ReflectionUiEvent.BackToGarden)
        onBackToGarden()
    }
    BackHandler(onBack = exitToGarden)

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        BackHeader(
            title = if (state.hasWrittenReflection) "Today's Reflection" else "Check-in complete",
            subtitle = currentDateLabel(),
            onBack = exitToGarden
        )
        Spacer(Modifier.height(24.dp))
        Crossfade(
            targetState = state.isLoading,
            animationSpec = tween(250),
            label = "reflection-result"
        ) { isLoading ->
            if (isLoading) {
                AnimatedReflectionLoading()
            } else if (state.hasWrittenReflection) {
                WrittenReflectionJourney(state.guidance)
            } else {
                BlankReflectionCompletion()
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun WrittenReflectionJourney(guidance: ReflectionGuidance) {
    var revealedStep by remember(guidance) { mutableIntStateOf(0) }
    LaunchedEffect(guidance) {
        repeat(4) { index ->
            delay(if (index == 0) 70 else 110)
            revealedStep = index + 1
        }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        RevealSection(revealedStep >= 1) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp)) {
                BotanicalDetail()
                Spacer(Modifier.height(14.dp))
                Text("Your Reflection", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = DeepForest)
                Spacer(Modifier.height(12.dp))
                Text(
                    guidance.summary,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 27.sp),
                    color = DeepForest
                )
            }
        }

        RevealSection(revealedStep >= 2) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ReflectionFlowConnector()
                GardenCard(
                    Modifier.fillMaxWidth(),
                    MintSurfaceStrong,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 22.dp)
                ) {
                    Text(
                        "A Little Reminder",
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        guidance.affirmation,
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 27.sp),
                        color = DeepForest,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        RevealSection(revealedStep >= 3) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ReflectionFlowConnector()
                Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text("Something to Consider", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        guidance.reflectionQuestion,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                        color = DeepForest,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        RevealSection(revealedStep >= 4) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ReflectionFlowConnector()
                GardenCard(
                    Modifier.fillMaxWidth(),
                    SoftGardenSurface,
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SproutBadge()
                        Spacer(Modifier.size(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Gentle Practice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
                            Spacer(Modifier.height(6.dp))
                            Text(guidance.wellnessActivity, style = MaterialTheme.typography.bodyMedium, color = DeepForest)
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
                SproutMark()
                Spacer(Modifier.height(10.dp))
                Text("See you tomorrow.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Your garden grows every time you show up.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GardenTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BlankReflectionCompletion() {
    val growth = remember { Animatable(.45f) }
    LaunchedEffect(Unit) { growth.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
    GardenCard(
        Modifier.fillMaxWidth(),
        MintSurface,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 34.dp)
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            GrowingSprout(growth.value)
            Spacer(Modifier.height(22.dp))
            Text(
                "You showed up today.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = DeepForest,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Thanks for taking a moment to\ncheck in with yourself today.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 27.sp),
                color = DeepForest,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            Text(
                "Your garden grows through\nthe simple act of showing up.",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 23.sp),
                color = GardenTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            BotanicalDivider()
            Spacer(Modifier.height(18.dp))
            Text("See you tomorrow.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = PrimaryGreen)
        }
    }
}

@Composable
private fun RevealSection(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(260)) + slideInVertically(tween(260)) { it / 10 }
    ) { content() }
}

@Composable
private fun BotanicalDetail() {
    Canvas(Modifier.fillMaxWidth().height(28.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawLine(Mint, Offset(center.x - 34.dp.toPx(), center.y), Offset(center.x + 34.dp.toPx(), center.y), 1.2.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(center.x - 7.dp.toPx(), center.y - 8.dp.toPx()), Size(8.dp.toPx(), 6.dp.toPx()))
        drawOval(PrimaryGreen, Offset(center.x, center.y + 1.dp.toPx()), Size(8.dp.toPx(), 6.dp.toPx()))
    }
}

@Composable
private fun ReflectionFlowConnector() {
    Canvas(Modifier.size(width = 34.dp, height = 42.dp)) {
        val x = size.width / 2f
        drawLine(Mint, Offset(x, 4.dp.toPx()), Offset(x, size.height - 4.dp.toPx()), 1.4.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(x, size.height * .45f), Size(9.dp.toPx(), 6.dp.toPx()))
    }
}

@Composable
private fun SproutBadge() {
    Box(Modifier.size(46.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(MintSurfaceStrong, size.minDimension / 2f)
            val x = size.width / 2f
            drawLine(PrimaryGreen, Offset(x, size.height * .72f), Offset(x, size.height * .36f), 2.dp.toPx(), StrokeCap.Round)
            drawOval(FreshGreen, Offset(x - 12.dp.toPx(), size.height * .30f), Size(12.dp.toPx(), 8.dp.toPx()))
            drawOval(PrimaryGreen, Offset(x, size.height * .40f), Size(12.dp.toPx(), 8.dp.toPx()))
        }
    }
}

@Composable
private fun SproutMark() {
    Canvas(Modifier.size(34.dp)) {
        val x = size.width / 2f
        drawLine(PrimaryGreen, Offset(x, size.height * .84f), Offset(x, size.height * .35f), 2.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(x - 12.dp.toPx(), size.height * .28f), Size(12.dp.toPx(), 8.dp.toPx()))
        drawOval(PrimaryGreen, Offset(x, size.height * .42f), Size(12.dp.toPx(), 8.dp.toPx()))
    }
}

@Composable
private fun GrowingSprout(progress: Float) {
    Canvas(Modifier.size(width = 112.dp, height = 100.dp)) {
        val base = Offset(size.width / 2f, size.height * .84f)
        val top = Offset(size.width / 2f, size.height * (.72f - .38f * progress))
        drawCircle(PaleMint, 38.dp.toPx() * progress, Offset(size.width / 2f, size.height / 2f))
        drawLine(PrimaryGreen, base, top, 3.dp.toPx(), StrokeCap.Round)
        val leafWidth = 24.dp.toPx() * progress
        val leafHeight = 14.dp.toPx() * progress
        drawOval(FreshGreen, Offset(top.x - leafWidth, top.y), Size(leafWidth, leafHeight))
        drawOval(PrimaryGreen, Offset(top.x, top.y + 8.dp.toPx()), Size(leafWidth, leafHeight))
    }
}

@Composable
private fun BotanicalDivider() {
    Canvas(Modifier.size(width = 72.dp, height = 20.dp)) {
        val y = size.height / 2f
        drawLine(Mint, Offset(0f, y), Offset(size.width, y), 1.2.dp.toPx(), StrokeCap.Round)
        drawCircle(PrimaryGreen, 3.dp.toPx(), Offset(size.width / 2f, y))
    }
}

private fun currentDateLabel(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US))
