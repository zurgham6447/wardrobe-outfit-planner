package com.wardrobeoutfit.planner.presentation.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun MenuScreen(
    onStartPlanning: () -> Unit,
    onOpenCloset: () -> Unit,
    onOpenOutfits: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onStartPlanning = onStartPlanning,
        onOpenCloset = onOpenCloset,
        onOpenOutfits = onOpenOutfits,
        onOpenCalendar = onOpenCalendar,
        onOpenSettings = onOpenSettings,
        modifier = modifier
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStartPlanning: () -> Unit,
    onOpenCloset: () -> Unit,
    onOpenOutfits: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.93f),
            WardrobePalette.PaperMid.copy(alpha = 0.96f),
            WardrobePalette.PaperDeep.copy(alpha = 0.98f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = stringResource(R.string.menu_brand),
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.TextSecondary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(WardrobePalette.Surface)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.menu_week_fmt, state.weekNumber),
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.Primary
                    )
                }
            }

            HairlineRule(modifier = Modifier.padding(top = 10.dp))

            Text(
                text = stringResource(R.string.menu_hero_top),
                style = MaterialTheme.typography.displaySmall,
                color = WardrobePalette.TextPrimary,
                modifier = Modifier.padding(top = 18.dp)
            )
            Text(
                text = stringResource(R.string.menu_hero_bottom),
                style = MaterialTheme.typography.displaySmall,
                color = WardrobePalette.Primary
            )

            Box(modifier = Modifier.weight(1f)) {
                TodayCard(
                    state = state,
                    onPlanToday = onOpenCalendar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(top = 14.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = (-8).dp)
                        .size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(WardrobePalette.Secondary.copy(alpha = 0.22f))
                    )
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        alpha = 0.95f,
                        modifier = Modifier.size(124.dp)
                    )
                }
            }

            StatRow(
                cards = menuStatCards(state),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.menu_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = WardrobePalette.TextSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_start_planning),
                onClick = onStartPlanning,
                leadingIcon = Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth(0.86f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DestinationTile(
                    label = stringResource(R.string.nav_closet),
                    onClick = onOpenCloset,
                    modifier = Modifier.weight(1f),
                    art = R.drawable.icon_home
                )
                DestinationTile(
                    label = stringResource(R.string.nav_outfits),
                    onClick = onOpenOutfits,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Star
                )
                DestinationTile(
                    label = stringResource(R.string.nav_calendar),
                    onClick = onOpenCalendar,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DateRange
                )
                DestinationTile(
                    label = stringResource(R.string.nav_settings),
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Settings
                )
            }
        }
    }
}

@Composable
private fun menuStatCards(state: MenuUiState): List<Triple<String, String, Color>> {
    val cards = mutableListOf<Triple<String, String, Color>>()
    if (state.stats.itemCount > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_items),
                state.stats.itemCount.toString(),
                WardrobePalette.Primary
            )
        )
    }
    if (state.stats.outfitCount > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_looks),
                state.stats.outfitCount.toString(),
                WardrobePalette.Accent
            )
        )
    }
    if (state.stats.plannedDays > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_planned),
                state.stats.plannedDays.toString(),
                WardrobePalette.Secondary
            )
        )
    }
    if (state.stats.coveragePercent > 0) {
        cards.add(
            Triple(
                stringResource(R.string.stat_coverage),
                stringResource(R.string.percent_fmt, state.stats.coveragePercent),
                WardrobePalette.TextPrimary
            )
        )
    }
    return cards
}

@Composable
private fun TodayCard(
    state: MenuUiState,
    onPlanToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, WardrobePalette.Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = state.todayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = WardrobePalette.TextTertiary
            )
            if (state.todayItems.isNotEmpty()) {
                OutfitStrip(
                    items = state.todayItems,
                    size = 56.dp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = state.todayOutfitName,
                    style = MaterialTheme.typography.titleMedium,
                    color = WardrobePalette.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 10.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.menu_nothing_planned),
                    style = MaterialTheme.typography.titleMedium,
                    color = WardrobePalette.TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_plan_today),
                    onClick = onPlanToday,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun DestinationTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    art: Int? = null
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(WardrobePalette.Surface)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (art != null) {
            Image(
                painter = painterResource(art),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(22.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WardrobePalette.Primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
