package com.wardrobeoutfit.planner.presentation.game

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.OccasionTag

enum class BuilderStatus { IDLE, BUILDING, INVALID, DUPLICATE, SAVED }

data class GameUiState(
    val loading: Boolean = true,
    val slots: Map<ClothingType, String> = emptyMap(),
    val activeSlot: ClothingType = ClothingType.TOP,
    val slotOrder: List<ClothingType> = emptyList(),
    val pickerItems: List<ClothingItem> = emptyList(),
    val chosenItems: List<ClothingItem> = emptyList(),
    val status: BuilderStatus = BuilderStatus.IDLE,
    val missingSlots: List<ClothingType> = emptyList(),
    val parked: Boolean = false,
    val tags: List<OccasionTag> = emptyList(),
    val newPieces: Int = 0
) {
    val filledCount: Int
        get() = slots.count { it.value.isNotBlank() }

    fun itemFor(type: ClothingType): ClothingItem? {
        val id = slots[type] ?: return null
        return chosenItems.firstOrNull { it.id == id }
    }
}

sealed interface GameEvent {
    data class LookReady(val outfitId: String) : GameEvent
    data object OpenOutfits : GameEvent
}
