package com.wardrobeoutfit.planner.presentation.additem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.EditorialTopBar
import com.wardrobeoutfit.planner.core.ui.PrimaryButton
import com.wardrobeoutfit.planner.core.ui.SaveConfirmOverlay
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.StepperRow
import com.wardrobeoutfit.planner.core.ui.TagChip
import com.wardrobeoutfit.planner.core.ui.colorLabel
import com.wardrobeoutfit.planner.core.ui.garmentArt
import com.wardrobeoutfit.planner.core.ui.selectionState
import com.wardrobeoutfit.planner.core.ui.tagLabel
import com.wardrobeoutfit.planner.core.ui.typeLabel
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor

@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addItemViewModel: AddItemViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by addItemViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        addItemViewModel.events.collect { onBack() }
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
                title = stringResource(R.string.title_add_item),
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp)
            ) {
                item {
                    FormSection(label = stringResource(R.string.add_section_art)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(ClothingType.entries.toList(), key = { it.name }) { type ->
                                GarmentTile(
                                    type = type,
                                    selected = state.type == type,
                                    onClick = { addItemViewModel.selectType(type) }
                                )
                            }
                        }
                    }
                }

                item {
                    FormSection(label = stringResource(R.string.add_section_name)) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { addItemViewModel.setName(it) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WardrobePalette.Primary,
                            unfocusedBorderColor = WardrobePalette.Hairline,
                            focusedContainerColor = WardrobePalette.Surface,
                            unfocusedContainerColor = WardrobePalette.Surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    }
                }

                item {
                    FormSection(label = stringResource(R.string.add_section_type)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.availableTypes.take(3).forEach { type ->
                            TagChip(
                                text = typeLabel(type),
                                selected = state.type == type,
                                onClick = { addItemViewModel.selectType(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.availableTypes.drop(3).forEach { type ->
                            TagChip(
                                text = typeLabel(type),
                                selected = state.type == type,
                                onClick = { addItemViewModel.selectType(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    }
                }

                item {
                    FormSection(label = stringResource(R.string.add_section_color)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(ItemColor.entries.toList(), key = { it.name }) { swatch ->
                                ColorSwatch(
                                    color = swatch,
                                    selected = state.color == swatch,
                                    onClick = { addItemViewModel.selectColor(swatch) }
                                )
                            }
                        }
                    }
                }

                item {
                    FormSection(label = stringResource(R.string.add_section_tags)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(state.availableTags, key = { it.name }) { tag ->
                                TagChip(
                                    text = tagLabel(tag),
                                    selected = tag in state.tags,
                                    onClick = { addItemViewModel.toggleTag(tag) }
                                )
                            }
                        }
                    }
                }

                item {
                    StepperRow(
                        label = stringResource(R.string.add_section_worn),
                        value = state.timesWorn.toString(),
                        onMinus = { addItemViewModel.decreaseWorn() },
                        onPlus = { addItemViewModel.increaseWorn() }
                    )
                }

                if (state.limitReached) {
                    item {
                        Text(
                            text = stringResource(R.string.add_limit_reached),
                            style = MaterialTheme.typography.labelSmall,
                            color = WardrobePalette.Accent
                        )
                    }
                }
            }

            PrimaryButton(
                text = stringResource(R.string.cta_save_item),
                onClick = { addItemViewModel.submit() },
                enabled = state.canSubmit,
                leadingIcon = Icons.Default.Check,
                height = 56.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }

        SaveConfirmOverlay(visible = state.saving)
    }
}

@Composable
private fun FormSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextTertiary
        )
        content()
    }
}

@Composable
private fun GarmentTile(
    type: ClothingType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val state = selectionState(selected)
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(shape)
            .background(
                if (selected) WardrobePalette.Primary.copy(alpha = 0.08f) else WardrobePalette.Surface
            )
            .border(
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) WardrobePalette.Primary else WardrobePalette.Hairline
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .semantics { stateDescription = state },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(garmentArt(type)),
            contentDescription = typeLabel(type),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
private fun ColorSwatch(
    color: ItemColor,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = colorLabel(color)
    val state = selectionState(selected)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(color.argb))
            .border(
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) WardrobePalette.Primary else WardrobePalette.Hairline
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .semantics { stateDescription = state },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = label,
                tint = WardrobePalette.Primary,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Box(modifier = Modifier.height(0.dp))
        }
    }
}
