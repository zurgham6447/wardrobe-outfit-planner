package com.wardrobeoutfit.planner.domain.model

data class Outfit(
    val id: String,
    val name: String,
    val itemIds: List<String>,
    val createdEpochDay: Long
)

data class CalendarEntry(
    val dayEpoch: Long,
    val outfitId: String
)
