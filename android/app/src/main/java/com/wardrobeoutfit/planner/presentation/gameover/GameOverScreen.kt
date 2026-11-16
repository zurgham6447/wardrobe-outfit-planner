package com.wardrobeoutfit.planner.presentation.gameover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.HairlineRule
import com.wardrobeoutfit.planner.core.ui.OutfitStrip
import com.wardrobeoutfit.planner.core.ui.PrimaryButton
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.SecondaryButton
import com.wardrobeoutfit.planner.core.ui.StatRow

@Composable
fun GameOverScreen(
    outfitId: String,
    onBuildAgain: () -> Unit,
    onSchedule: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = outfitId,
        factory = ServiceLocator.factoryFor(outfitId)
    )
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameOverViewModel.events.collect { event ->
            when (event) {
                GameOverEvent.Scheduled -> onSchedule()
            }
        }
    }

    val ringScale by animateFloatAsState(
        targetValue = if (state.loading) 0.55f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 380f),
        label = "ring"
    )

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.Primary.copy(alpha = 0.14f),
            WardrobePalette.PaperBase.copy(alpha = 0.95f),
            WardrobePalette.Accent.copy(alpha = 0.12f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(ringScale)
                    .background(
                        Brush.verticalGradient(
                            listOf(WardrobePalette.Accent, WardrobePalette.AccentLight)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.cd_saved),
                    tint = WardrobePalette.Surface,
                    modifier = Modifier.size(42.dp)
                )
            }

            Text(
                text = stringResource(R.string.gameover_title),
                style = MaterialTheme.typography.displaySmall,
                color = WardrobePalette.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )

            Text(
                text = state.outfitName,
                style = MaterialTheme.typography.headlineSmall,
                color = WardrobePalette.Primary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )

            HairlineRule(modifier = Modifier.padding(top = 14.dp))

            if (state.items.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, WardrobePalette.Hairline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutfitStrip(items = state.items, size = 58.dp)
                        if (state.scheduledLabel.isNotBlank()) {
                            Text(
                                text = stringResource(
                                    R.string.gameover_scheduled_fmt,
                                    state.scheduledLabel
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = WardrobePalette.TextTertiary,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }

            val piecesLabel = stringResource(R.string.stat_pieces)
            val looksLabel = stringResource(R.string.stat_looks_total)
            val coverageLabel = stringResource(R.string.stat_coverage)
            val coverageValue = stringResource(R.string.percent_fmt, state.coveragePercent)
            val cards = mutableListOf<Triple<String, String, Color>>()
            if (state.pieces > 0) {
                cards.add(Triple(piecesLabel, state.pieces.toString(), WardrobePalette.Primary))
            }
            if (state.looksTotal > 0) {
                cards.add(Triple(looksLabel, state.looksTotal.toString(), WardrobePalette.Accent))
            }
            if (state.coveragePercent > 0) {
                cards.add(Triple(coverageLabel, coverageValue, WardrobePalette.Secondary))
            }

            StatRow(
                cards = cards,
                modifier = Modifier.padding(top = 16.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_schedule_it),
                onClick = { gameOverViewModel.scheduleNextFreeDay() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.cta_build_again),
                onClick = onBuildAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            TextButton(
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.cta_menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = WardrobePalette.Primary
                )
            }
        }
    }
}
