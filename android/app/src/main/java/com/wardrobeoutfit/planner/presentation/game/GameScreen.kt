package com.wardrobeoutfit.planner.presentation.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.HairlineRule
import com.wardrobeoutfit.planner.core.ui.PrimaryButton
import com.wardrobeoutfit.planner.core.ui.SaveConfirmOverlay
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.SecondaryButton
import com.wardrobeoutfit.planner.core.ui.SlotPlaceholder
import com.wardrobeoutfit.planner.core.ui.TagChip
import com.wardrobeoutfit.planner.core.ui.garmentArt
import com.wardrobeoutfit.planner.core.ui.tagLabel
import com.wardrobeoutfit.planner.core.ui.typeLabel
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType

@Composable
fun GameScreen(
    onBack: () -> Unit,
    onLookReady: (String) -> Unit,
    onOpenOutfits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.LookReady -> onLookReady(event.outfitId)
                GameEvent.OpenOutfits -> onOpenOutfits()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.90f),
            WardrobePalette.PaperMid.copy(alpha = 0.95f),
            WardrobePalette.Secondary.copy(alpha = 0.30f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            BuilderHeader(
                onBack = onBack,
                onHold = { gameViewModel.parkDraft() }
            )

            PreviewPanel(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )

            StatStrip(state = state)

            ControlsPanel(
                state = state,
                onSelectSlot = { gameViewModel.selectSlot(it) },
                onSelectItem = { gameViewModel.selectItem(it) },
                onSuggest = { gameViewModel.suggestForActiveSlot() },
                onSave = { gameViewModel.saveLook() },
                onClear = { gameViewModel.clearSlots() },
                onOpenOutfits = { gameViewModel.openOutfits() }
            )
        }

        SaveConfirmOverlay(visible = state.status == BuilderStatus.SAVED)
    }
}

@Composable
private fun BuilderHeader(
    onBack: () -> Unit,
    onHold: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = WardrobePalette.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = stringResource(R.string.title_builder),
                style = MaterialTheme.typography.titleMedium,
                color = WardrobePalette.TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onHold, modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_hold),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        HairlineRule()
    }
}

@Composable
private fun PreviewPanel(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, WardrobePalette.Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SlotLane(
                    state = state,
                    type = ClothingType.OUTERWEAR,
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .weight(0.9f)
                )
                SlotLane(
                    state = state,
                    type = ClothingType.TOP,
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .weight(1f)
                )
                SlotLane(
                    state = state,
                    type = ClothingType.BOTTOM,
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .weight(1.1f)
                )
                SlotLane(
                    state = state,
                    type = ClothingType.SHOES,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .weight(0.75f)
                )
                Text(
                    text = stringResource(
                        R.string.builder_slots_fmt,
                        state.filledCount,
                        GameConfig.SLOT_COUNT
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = WardrobePalette.TextSecondary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            val accessory = state.itemFor(ClothingType.ACCESSORY)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (accessory != null) {
                    Image(
                        painter = painterResource(garmentArt(accessory.type)),
                        contentDescription = accessory.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                } else {
                    SlotPlaceholder(
                        label = typeLabel(ClothingType.ACCESSORY).take(3),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotLane(
    state: GameUiState,
    type: ClothingType,
    modifier: Modifier = Modifier
) {
    val item = state.itemFor(type)
    AnimatedContent(
        targetState = item,
        transitionSpec = {
            (fadeIn(tween(160)) + scaleIn(initialScale = 0.94f)) togetherWith fadeOut(tween(120))
        },
        modifier = modifier
    ) { current ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (current != null) {
                Image(
                    painter = painterResource(garmentArt(current.type)),
                    contentDescription = current.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxHeight()
                )
            } else {
                SlotPlaceholder(
                    label = typeLabel(type),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StripCell(
            label = stringResource(R.string.builder_stat_slots),
            value = stringResource(
                R.string.builder_slots_value_fmt,
                state.filledCount,
                GameConfig.SLOT_COUNT
            ),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(22.dp)
                .background(WardrobePalette.Hairline)
        )
        val tagNames = ArrayList<String>(2)
        for (tag in state.tags.take(2)) {
            tagNames.add(tagLabel(tag))
        }
        StripCell(
            label = stringResource(R.string.builder_stat_tags),
            value = tagNames.joinToString(" "),
            modifier = Modifier.weight(1.4f)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(22.dp)
                .background(WardrobePalette.Hairline)
        )
        StripCell(
            label = stringResource(R.string.builder_stat_new),
            value = state.newPieces.toString(),
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun StripCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ControlsPanel(
    state: GameUiState,
    onSelectSlot: (ClothingType) -> Unit,
    onSelectItem: (String) -> Unit,
    onSuggest: () -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onOpenOutfits: () -> Unit
) {
    val pulse = rememberInfiniteTransition()
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(320),
            repeatMode = RepeatMode.Reverse
        )
    )
    Surface(
        color = WardrobePalette.Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, WardrobePalette.Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.slotOrder, key = { it.name }) { type ->
                    val pulsing = state.status == BuilderStatus.INVALID && type in state.missingSlots
                    TagChip(
                        text = typeLabel(type),
                        selected = state.activeSlot == type,
                        onClick = { onSelectSlot(type) },
                        modifier = Modifier.graphicsLayer {
                            val factor = if (pulsing) pulseScale else 1f
                            scaleX = factor
                            scaleY = factor
                        }
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                items(state.pickerItems, key = { it.id }) { item ->
                    PickerTile(
                        item = item,
                        selected = state.slots[state.activeSlot] == item.id,
                        onClick = { onSelectItem(item.id) }
                    )
                }
                item {
                    SuggestTile(onClick = onSuggest)
                }
            }

            StatusBar(
                state = state,
                onOpenOutfits = onOpenOutfits,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cta_clear),
                    onClick = onClear,
                    enabled = state.slots.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = stringResource(R.string.cta_save_look),
                    onClick = onSave,
                    enabled = state.slots.isNotEmpty() && state.status != BuilderStatus.SAVED,
                    leadingIcon = Icons.Default.Check,
                    height = 56.dp,
                    modifier = Modifier.weight(1.8f)
                )
            }
        }
    }
}

@Composable
private fun StatusBar(
    state: GameUiState,
    onOpenOutfits: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state.status) {
        BuilderStatus.INVALID -> NoticeBar(text = stringResource(R.string.builder_invalid), modifier = modifier)
        BuilderStatus.DUPLICATE -> Column(modifier = modifier) {
            NoticeBar(text = stringResource(R.string.builder_duplicate))
            SecondaryButton(
                text = stringResource(R.string.cta_open_outfits),
                onClick = onOpenOutfits,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        else -> if (state.parked) {
            NoticeBar(text = stringResource(R.string.builder_parked), modifier = modifier)
        } else {
            Box(modifier = modifier.height(4.dp))
        }
    }
}

@Composable
private fun NoticeBar(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WardrobePalette.Accent.copy(alpha = 0.12f))
            .border(
                border = BorderStroke(1.dp, WardrobePalette.Accent),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PickerTile(
    item: ClothingItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(shape)
                .background(WardrobePalette.SurfaceVariant)
                .border(
                    border = BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) WardrobePalette.Primary else WardrobePalette.Hairline
                    ),
                    shape = shape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(garmentArt(item.type)),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
        }
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SuggestTile(onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(shape)
                .background(WardrobePalette.Primary.copy(alpha = 0.08f))
                .border(
                    border = BorderStroke(1.5.dp, WardrobePalette.Primary),
                    shape = shape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = WardrobePalette.Primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = stringResource(R.string.cta_suggest),
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.Primary,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
