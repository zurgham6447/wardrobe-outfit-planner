package com.wardrobeoutfit.planner.domain.model

data class WardrobeStats(
    val itemCount: Int = 0,
    val outfitCount: Int = 0,
    val plannedDays: Int = 0,
    val coveragePercent: Int = 0
)

data class WardrobeSettings(
    val hiddenTypes: Set<ClothingType> = emptySet(),
    val hiddenTags: Set<OccasionTag> = emptySet(),
    val weekStartsMonday: Boolean = true,
    val suggestLeastWorn: Boolean = true
)
