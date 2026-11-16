package com.wardrobeoutfit.planner.presentation.calendar

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.Outfit

data class DayCell(
    val epochDay: Long,
    val letter: String,
    val number: Int,
    val outfitName: String,
    val items: List<ClothingItem>,
    val isToday: Boolean
) {
    val planned: Boolean
        get() = outfitName.isNotBlank()
}

data class CalendarUiState(
    val loading: Boolean = true,
    val monthLabel: String = "",
    val days: List<DayCell> = emptyList(),
    val selectedEpochDay: Long = 0L,
    val outfits: List<Outfit> = emptyList(),
    val itemsById: Map<String, ClothingItem> = emptyMap(),
    val assignVisible: Boolean = false
) {
    val selected: DayCell?
        get() = days.firstOrNull { it.epochDay == selectedEpochDay }

    val emptyDays: Int
        get() = days.count { !it.planned }
}
