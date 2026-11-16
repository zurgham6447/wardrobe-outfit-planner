package com.wardrobeoutfit.planner.presentation.menu

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.WardrobeStats

data class MenuUiState(
    val loading: Boolean = true,
    val weekNumber: Int = 0,
    val todayLabel: String = "",
    val todayOutfitName: String = "",
    val todayItems: List<ClothingItem> = emptyList(),
    val stats: WardrobeStats = WardrobeStats()
)
