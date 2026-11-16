package com.wardrobeoutfit.planner.presentation.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.EditorialTopBar
import com.wardrobeoutfit.planner.core.ui.EmptyState
import com.wardrobeoutfit.planner.core.ui.OutfitStrip
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.SecondaryButton
import com.wardrobeoutfit.planner.core.ui.tagLabel
import com.wardrobeoutfit.planner.presentation.dialog.ConfirmDialog

@Composable
fun OutfitsScreen(
    onBack: () -> Unit,
    onStartPlanning: () -> Unit,
    onSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outfitsViewModel: OutfitsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by outfitsViewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.94f),
            WardrobePalette.PaperMid.copy(alpha = 0.96f),
            WardrobePalette.PaperDeep.copy(alpha = 0.97f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            EditorialTopBar(
                title = stringResource(R.string.title_outfits),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 12.dp),
                trailing = {
                    Text(
                        text = stringResource(R.string.outfits_count_fmt, state.count),
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.TextTertiary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            if (state.rows.isEmpty() && !state.loading) {
                EmptyState(
                    title = stringResource(R.string.outfits_empty_title),
                    hint = stringResource(R.string.outfits_empty_hint),
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    action = {
                        SecondaryButton(
                            text = stringResource(R.string.cta_start_planning),
                            onClick = onStartPlanning
                        )
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.rows, key = { it.outfit.id }) { row ->
                        OutfitRowCard(
                            row = row,
                            onSchedule = onSchedule,
                            onDelete = { outfitsViewModel.askDelete(row.outfit.id) }
                        )
                    }
                }
            }
        }
    }

    if (state.pendingDeleteId.isNotBlank()) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_outfit_title),
            message = stringResource(R.string.confirm_delete_outfit_message),
            confirmLabel = stringResource(R.string.cta_delete),
            onConfirm = { outfitsViewModel.confirmDelete() },
            onDismiss = { outfitsViewModel.dismissDelete() }
        )
    }
}

@Composable
private fun OutfitRowCard(
    row: OutfitRow,
    onSchedule: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, WardrobePalette.Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.outfit.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = WardrobePalette.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (row.scheduledLabel.isBlank()) {
                            stringResource(R.string.outfit_unscheduled)
                        } else {
                            row.scheduledLabel
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (row.scheduledLabel.isBlank()) {
                            WardrobePalette.TextTertiary
                        } else {
                            WardrobePalette.Primary
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = onSchedule, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.cd_schedule),
                        tint = WardrobePalette.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_delete),
                        tint = WardrobePalette.TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            OutfitStrip(
                items = row.items,
                size = 52.dp,
                modifier = Modifier.padding(top = 12.dp)
            )

            if (row.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.tags.forEach { tag ->
                        Text(
                            text = tagLabel(tag),
                            style = MaterialTheme.typography.labelSmall,
                            color = WardrobePalette.TextSecondary,
                            modifier = Modifier
                                .background(
                                    WardrobePalette.SurfaceVariant,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
