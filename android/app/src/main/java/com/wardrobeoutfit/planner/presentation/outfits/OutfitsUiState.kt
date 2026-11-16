package com.wardrobeoutfit.planner.presentation.outfits

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.model.Outfit

data class OutfitRow(
    val outfit: Outfit,
    val items: List<ClothingItem>,
    val tags: List<OccasionTag>,
    val scheduledLabel: String
)

data class OutfitsUiState(
    val loading: Boolean = true,
    val rows: List<OutfitRow> = emptyList(),
    val pendingDeleteId: String = ""
) {
    val count: Int
        get() = rows.size
}
