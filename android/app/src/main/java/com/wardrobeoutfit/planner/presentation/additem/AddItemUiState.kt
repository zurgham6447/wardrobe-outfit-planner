package com.wardrobeoutfit.planner.presentation.additem

import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor
import com.wardrobeoutfit.planner.domain.model.OccasionTag

data class AddItemUiState(
    val name: String = "",
    val type: ClothingType? = null,
    val color: ItemColor? = null,
    val tags: Set<OccasionTag> = emptySet(),
    val timesWorn: Int = 0,
    val saving: Boolean = false,
    val limitReached: Boolean = false,
    val availableTypes: List<ClothingType> = emptyList(),
    val availableTags: List<OccasionTag> = emptyList()
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() && type != null && color != null && !limitReached && !saving
}

sealed interface AddItemEvent {
    data object Stored : AddItemEvent
}
