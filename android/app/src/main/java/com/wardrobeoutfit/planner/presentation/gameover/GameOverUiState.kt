package com.wardrobeoutfit.planner.presentation.gameover

import com.wardrobeoutfit.planner.domain.model.ClothingItem

data class GameOverUiState(
    val loading: Boolean = true,
    val outfitName: String = "",
    val items: List<ClothingItem> = emptyList(),
    val pieces: Int = 0,
    val looksTotal: Int = 0,
    val coveragePercent: Int = 0,
    val scheduledLabel: String = ""
)

sealed interface GameOverEvent {
    data object Scheduled : GameOverEvent
}
