package com.wardrobeoutfit.planner.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor
import com.wardrobeoutfit.planner.domain.model.OccasionTag

@Composable
fun typeLabel(type: ClothingType): String = stringResource(
    when (type) {
        ClothingType.TOP -> R.string.type_top
        ClothingType.BOTTOM -> R.string.type_bottom
        ClothingType.OUTERWEAR -> R.string.type_outerwear
        ClothingType.SHOES -> R.string.type_shoes
        ClothingType.ACCESSORY -> R.string.type_accessory
    }
)

@Composable
fun typeLabelPlural(type: ClothingType): String = stringResource(
    when (type) {
        ClothingType.TOP -> R.string.type_top_plural
        ClothingType.BOTTOM -> R.string.type_bottom_plural
        ClothingType.OUTERWEAR -> R.string.type_outerwear_plural
        ClothingType.SHOES -> R.string.type_shoes_plural
        ClothingType.ACCESSORY -> R.string.type_accessory_plural
    }
)

@Composable
fun tagLabel(tag: OccasionTag): String = stringResource(
    when (tag) {
        OccasionTag.WARM -> R.string.tag_warm
        OccasionTag.COLD -> R.string.tag_cold
        OccasionTag.RAIN -> R.string.tag_rain
        OccasionTag.WORK -> R.string.tag_work
        OccasionTag.CASUAL -> R.string.tag_casual
        OccasionTag.EVENING -> R.string.tag_evening
    }
)

@Composable
fun colorLabel(color: ItemColor): String = stringResource(
    when (color) {
        ItemColor.NAVY -> R.string.color_navy
        ItemColor.CHARCOAL -> R.string.color_charcoal
        ItemColor.LAVENDER -> R.string.color_lavender
        ItemColor.AMBER -> R.string.color_amber
        ItemColor.CLOUD -> R.string.color_cloud
        ItemColor.IVORY -> R.string.color_ivory
    }
)

@Composable
fun selectionState(selected: Boolean): String =
    stringResource(if (selected) R.string.state_selected else R.string.state_not_selected)
