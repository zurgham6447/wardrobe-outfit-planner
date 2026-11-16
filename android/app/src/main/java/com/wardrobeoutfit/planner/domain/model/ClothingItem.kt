package com.wardrobeoutfit.planner.domain.model

enum class ClothingType { TOP, BOTTOM, OUTERWEAR, SHOES, ACCESSORY }

enum class ItemColor(val argb: Long) {
    NAVY(0xFF2A65AB),
    CHARCOAL(0xFF181E24),
    LAVENDER(0xFFB1B0DD),
    AMBER(0xFFE08C28),
    CLOUD(0xFFEDF1F6),
    IVORY(0xFFF3EEE2)
}

enum class OccasionTag { WARM, COLD, RAIN, WORK, CASUAL, EVENING }

data class ClothingItem(
    val id: String,
    val name: String,
    val type: ClothingType,
    val color: ItemColor,
    val tags: Set<OccasionTag> = emptySet(),
    val timesWorn: Int = 0,
    val lastWornEpochDay: Long = 0L
)
