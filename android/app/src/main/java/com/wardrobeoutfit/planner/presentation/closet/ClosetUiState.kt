package com.wardrobeoutfit.planner.presentation.closet

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType

data class ClosetUiState(
    val loading: Boolean = true,
    val items: List<ClothingItem> = emptyList(),
    val visibleTypes: List<ClothingType> = emptyList(),
    val filter: ClothingType? = null,
    val totalCount: Int = 0,
    val selected: ClothingItem? = null
)
