package com.wardrobeoutfit.planner.core.ui

import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.domain.model.ClothingType

fun garmentArt(type: ClothingType): Int = when (type) {
    ClothingType.TOP -> R.drawable.sprite_1
    ClothingType.BOTTOM -> R.drawable.sprite_2
    ClothingType.SHOES -> R.drawable.sprite_3
    ClothingType.OUTERWEAR -> R.drawable.sprite_4
    ClothingType.ACCESSORY -> R.drawable.sprite_5
}
