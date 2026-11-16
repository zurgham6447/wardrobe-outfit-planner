package com.wardrobeoutfit.planner.presentation.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.EditorialTopBar
import com.wardrobeoutfit.planner.core.ui.EmptyState
import com.wardrobeoutfit.planner.core.ui.HairlineRule
import com.wardrobeoutfit.planner.core.ui.OutfitStrip
import com.wardrobeoutfit.planner.core.ui.PrimaryButton
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.SecondaryButton
import com.wardrobeoutfit.planner.core.ui.garmentArt
import com.wardrobeoutfit.planner.core.ui.selectionState
import com.wardrobeoutfit.planner.presentation.dialog.AssignOutfitDialog

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calendarViewModel: CalendarViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by calendarViewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.94f),
            WardrobePalette.PaperMid.copy(alpha = 0.96f),
            WardrobePalette.Secondary.copy(alpha = 0.26f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            EditorialTopBar(
                title = stringResource(R.string.title_calendar),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 12.dp),
                trailing = {
                    Text(
                        text = state.monthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.TextTertiary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                state.days.forEach { day ->
                    DayColumn(
                        day = day,
                        selected = day.epochDay == state.selectedEpochDay,
                        onClick = { calendarViewModel.selectDay(day.epochDay) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HairlineRule(modifier = Modifier.padding(horizontal = 20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, WardrobePalette.Hairline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selected = state.selected
                    Column(modifier = Modifier.padding(18.dp)) {
                        if (selected != null && selected.planned) {
                            Text(
                                text = stringResource(R.string.calendar_planned_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = WardrobePalette.TextTertiary
                            )
                            Text(
                                text = selected.outfitName,
                                style = MaterialTheme.typography.titleMedium,
                                color = WardrobePalette.TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            OutfitStrip(
                                items = selected.items,
                                size = 54.dp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        } else {
                            EmptyState(
                                title = stringResource(R.string.calendar_empty_title),
                                hint = stringResource(R.string.calendar_empty_hint),
                                artSize = 64.dp
                            )
                        }
                    }
                }

                PrimaryButton(
                    text = stringResource(R.string.cta_assign_look),
                    onClick = { calendarViewModel.openAssign() },
                    enabled = state.outfits.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.selected?.planned == true) {
                        SecondaryButton(
                            text = stringResource(R.string.cta_clear_day),
                            onClick = { calendarViewModel.clearSelectedDay() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SecondaryButton(
                        text = stringResource(R.string.cta_autofill_week),
                        onClick = { calendarViewModel.autoFillWeek() },
                        enabled = state.outfits.isNotEmpty() && state.emptyDays > 0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (state.assignVisible) {
        AssignOutfitDialog(
            outfits = state.outfits,
            itemsById = state.itemsById,
            onPick = { calendarViewModel.assignOutfit(it) },
            onDismiss = { calendarViewModel.dismissAssign() }
        )
    }
}

@Composable
private fun DayColumn(
    day: DayCell,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = selectionState(selected)
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics { stateDescription = state }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.letter,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextTertiary,
            textAlign = TextAlign.Center
        )
        Text(
            text = day.number.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = if (day.isToday) WardrobePalette.Accent else WardrobePalette.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            val first = day.items.firstOrNull()
            if (first != null) {
                Image(
                    painter = painterResource(garmentArt(first.type)),
                    contentDescription = first.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(38.dp)
                        .background(WardrobePalette.SurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(5.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.dp, WardrobePalette.Secondary, RoundedCornerShape(8.dp))
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (selected) WardrobePalette.Primary else WardrobePalette.Hairline
                )
        )
    }
}
