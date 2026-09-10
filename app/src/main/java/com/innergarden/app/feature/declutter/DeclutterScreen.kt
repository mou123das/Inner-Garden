package com.innergarden.app.feature.declutter

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.innergarden.app.domain.model.WeeklyDeclutter
import com.innergarden.app.ui.components.AnimatedDeclutterLoading
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.GardenTextSecondary
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGardenSurface
import com.innergarden.app.ui.theme.SoftGreen
import com.innergarden.app.ui.theme.WarmSurface

@Composable
fun DeclutterScreen(viewModel: DeclutterViewModel, onBackToGarden: () -> Unit, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    BackHandler(onBack = onBackToGarden)
    LaunchedEffect(Unit) { viewModel.onEvent(DeclutterUiEvent.ScreenShown) }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        BackHeader("Mind Declutter", "A little space for the thoughts you've been carrying this week.", onBackToGarden)
        Spacer(Modifier.height(22.dp))
        Crossfade(targetState = state.status, animationSpec = tween(250), label = "declutter-state") { status ->
            when (status) {
                DeclutterStatus.IDLE -> DeclutterIntro()
                DeclutterStatus.LOADING -> AnimatedDeclutterLoading(Modifier.height(220.dp))
                DeclutterStatus.EMPTY -> DeclutterMessageState(
                    title = "Your space is ready",
                    message = "Add a reflection in a daily check-in, then return here to gather your weekly themes."
                )
                DeclutterStatus.ERROR -> DeclutterMessageState(
                    title = "Let's pause here",
                    message = "Mind Declutter isn't available right now. Please try again later."
                )
                DeclutterStatus.CONTENT -> state.declutter?.let { DeclutterResult(it) }
            }
        }

        if (state.status != DeclutterStatus.CONTENT) {
            Spacer(Modifier.height(14.dp))
            InnerGardenButton(
                text = when (state.status) {
                    DeclutterStatus.LOADING -> "Gathering reflections..."
                    DeclutterStatus.ERROR -> "Try again"
                    DeclutterStatus.EMPTY -> "Back to Garden"
                    else -> "Declutter this week"
                },
                enabled = state.status != DeclutterStatus.LOADING
            ) {
                when (state.status) {
                    DeclutterStatus.EMPTY -> onBackToGarden()
                    DeclutterStatus.ERROR -> viewModel.onEvent(DeclutterUiEvent.Retry)
                    else -> viewModel.onEvent(DeclutterUiEvent.DeclutterWeek)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun DeclutterIntro() {
    Column(
        Modifier.fillMaxWidth().height(220.dp).background(SoftGardenSurface, RoundedCornerShape(24.dp)).padding(horizontal = 24.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ScatteredThoughtsVisual()
        Spacer(Modifier.height(14.dp))
        Text("Clear some space", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your recent reflections have been gathering through the week. Take a quiet moment to bring them together.",
            style = MaterialTheme.typography.bodyMedium,
            color = GardenTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScatteredThoughtsVisual() {
    Canvas(Modifier.size(width = 156.dp, height = 54.dp)) {
        val seeds = listOf(
            Offset(size.width * .08f, size.height * .60f),
            Offset(size.width * .27f, size.height * .25f),
            Offset(size.width * .48f, size.height * .70f),
            Offset(size.width * .68f, size.height * .22f),
            Offset(size.width * .91f, size.height * .55f)
        )
        seeds.forEachIndexed { index, center ->
            if (index % 2 == 0) {
                drawCircle(if (index == 2) PrimaryGreen else SoftGreen, (3.5f + index * .35f).dp.toPx(), center)
            } else {
                drawOval(
                    if (index == 1) FreshGreen else Mint,
                    Offset(center.x - 8.dp.toPx(), center.y - 4.dp.toPx()),
                    Size(16.dp.toPx(), 8.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun DeclutterMessageState(title: String, message: String) {
    GardenCard(Modifier.fillMaxWidth().height(220.dp), SoftGardenSurface) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            ClearedSpaceMark()
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = GardenTextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DeclutterResult(declutter: WeeklyDeclutter) {
    Column(Modifier.fillMaxWidth()) {
        WeeklySummaryHero(declutter.summary)
        Spacer(Modifier.height(28.dp))
        ThemeGarden(declutter.recurringThemes)
        Spacer(Modifier.height(28.dp))
        CarryForwardFocus(declutter.carryForwardReflection)
        Spacer(Modifier.height(30.dp))
        ReflectionQuestion(declutter.reflectionQuestion)
        Spacer(Modifier.height(34.dp))
        DeclutterClosing()
    }
}

@Composable
private fun WeeklySummaryHero(summary: String) {
    GardenCard(
        Modifier.fillMaxWidth(),
        WarmSurface,
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp)
    ) {
        BotanicalLine()
        Spacer(Modifier.height(14.dp))
        Text("This week", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(14.dp))
        Text(summary, style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 27.sp), color = DeepForest)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeGarden(themes: List<String>) {
    Column(Modifier.fillMaxWidth()) {
        Text("What kept showing up", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(14.dp))
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            themes.forEach { theme -> ThemePill(theme) }
        }
    }
}

@Composable
private fun ThemePill(theme: String) {
    Row(
        Modifier.background(MintSurfaceStrong, RoundedCornerShape(50)).padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(FreshGreen, CircleShape))
        Spacer(Modifier.size(8.dp))
        Text(theme, style = MaterialTheme.typography.bodyMedium, color = DeepForest, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CarryForwardFocus(text: String) {
    GardenCard(
        Modifier.fillMaxWidth(),
        MintSurfaceStrong,
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SproutSeal()
            Spacer(Modifier.size(12.dp))
            Text("Carry this forward", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
        }
        Spacer(Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 27.sp), color = DeepForest, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ReflectionQuestion(question: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        BotanicalStem()
        Spacer(Modifier.height(12.dp))
        Text("Something to sit with", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(16.dp))
        Text(
            question,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 29.sp),
            color = DeepForest,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeclutterClosing() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ClearedSpaceMark()
        Spacer(Modifier.height(10.dp))
        Text("A little space, cleared.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(6.dp))
        Text(
            "Come back next week when your garden has more to tell.",
            style = MaterialTheme.typography.bodySmall,
            color = GardenTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BotanicalLine() {
    Canvas(Modifier.fillMaxWidth().height(24.dp)) {
        val y = size.height / 2f
        drawLine(Mint, Offset(0f, y), Offset(size.width * .42f, y), 1.2.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(size.width * .42f, y - 7.dp.toPx()), Size(12.dp.toPx(), 7.dp.toPx()))
        drawOval(PrimaryGreen, Offset(size.width * .48f, y + 1.dp.toPx()), Size(12.dp.toPx(), 7.dp.toPx()))
    }
}

@Composable
private fun SproutSeal() {
    Canvas(Modifier.size(42.dp)) {
        drawCircle(MintSurface, size.minDimension / 2f)
        val x = size.width / 2f
        drawLine(PrimaryGreen, Offset(x, size.height * .74f), Offset(x, size.height * .36f), 2.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(x - 11.dp.toPx(), size.height * .30f), Size(11.dp.toPx(), 7.dp.toPx()))
        drawOval(PrimaryGreen, Offset(x, size.height * .41f), Size(11.dp.toPx(), 7.dp.toPx()))
    }
}

@Composable
private fun BotanicalStem() {
    Canvas(Modifier.size(width = 36.dp, height = 42.dp)) {
        val x = size.width / 2f
        drawLine(Mint, Offset(x, 0f), Offset(x, size.height), 1.4.dp.toPx(), StrokeCap.Round)
        drawOval(FreshGreen, Offset(x, size.height * .44f), Size(10.dp.toPx(), 6.dp.toPx()))
    }
}

@Composable
private fun ClearedSpaceMark() {
    Canvas(Modifier.size(width = 58.dp, height = 34.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(PaleMint, 14.dp.toPx(), center)
        val leaf = Path().apply {
            moveTo(center.x - 11.dp.toPx(), center.y + 6.dp.toPx())
            cubicTo(center.x - 10.dp.toPx(), center.y - 10.dp.toPx(), center.x + 9.dp.toPx(), center.y - 10.dp.toPx(), center.x + 11.dp.toPx(), center.y - 7.dp.toPx())
            cubicTo(center.x + 9.dp.toPx(), center.y + 8.dp.toPx(), center.x - 5.dp.toPx(), center.y + 12.dp.toPx(), center.x - 11.dp.toPx(), center.y + 6.dp.toPx())
            close()
        }
        drawPath(leaf, PrimaryGreen)
    }
}
