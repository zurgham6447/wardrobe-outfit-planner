package com.wardrobeoutfit.planner.presentation.settings

import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.OccasionTag

enum class SettingsDialog { NONE, RESET, CLEAR }

data class CategoryRow(
    val type: ClothingType,
    val itemCount: Int,
    val visible: Boolean
)

data class TagRow(
    val tag: OccasionTag,
    val visible: Boolean
)

data class SettingsUiState(
    val loading: Boolean = true,
    val categories: List<CategoryRow> = emptyList(),
    val tags: List<TagRow> = emptyList(),
    val weekStartsMonday: Boolean = true,
    val suggestLeastWorn: Boolean = true,
    val itemCount: Int = 0,
    val outfitCount: Int = 0,
    val dialog: SettingsDialog = SettingsDialog.NONE
)
