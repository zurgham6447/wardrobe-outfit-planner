package com.wardrobeoutfit.planner.presentation.closet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.ClothingItemCard
import com.wardrobeoutfit.planner.core.ui.EditorialTopBar
import com.wardrobeoutfit.planner.core.ui.EmptyState
import com.wardrobeoutfit.planner.core.ui.PrimaryButton
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.TagChip
import com.wardrobeoutfit.planner.core.ui.typeLabelPlural
import com.wardrobeoutfit.planner.presentation.dialog.ItemDetailDialog

@Composable
fun ClosetScreen(
    onBack: () -> Unit,
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val closetViewModel: ClosetViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by closetViewModel.state.collectAsStateWithLifecycle()

    val selected = state.selected
    if (selected != null) {
        ItemDetailDialog(
            item = selected,
            onDelete = { closetViewModel.deleteSelected() },
            onDismiss = { closetViewModel.dismissItem() }
        )
    }

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.95f),
            WardrobePalette.PaperMid.copy(alpha = 0.97f),
            WardrobePalette.PaperDeep.copy(alpha = 0.98f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            EditorialTopBar(
                title = stringResource(R.string.title_closet),
                onBack = onBack,
                trailing = {
                    Text(
                        text = stringResource(R.string.closet_count_fmt, state.totalCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.TextTertiary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TagChip(
                        text = stringResource(R.string.filter_all),
                        selected = state.filter == null,
                        onClick = { closetViewModel.setFilter(null) }
                    )
                }
                items(state.visibleTypes, key = { it.name }) { type ->
                    TagChip(
                        text = typeLabelPlural(type),
                        selected = state.filter == type,
                        onClick = { closetViewModel.setFilter(type) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (state.items.isEmpty() && !state.loading) {
                    EmptyState(
                        title = stringResource(R.string.closet_empty_title),
                        hint = stringResource(R.string.closet_empty_hint),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            ClothingItemCard(
                                item = item,
                                onClick = { closetViewModel.openItem(item) }
                            )
                        }
                    }
                }
            }

            PrimaryButton(
                text = stringResource(R.string.cta_add_item),
                onClick = onAddItem,
                leadingIcon = Icons.Default.Add,
                height = 56.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}
