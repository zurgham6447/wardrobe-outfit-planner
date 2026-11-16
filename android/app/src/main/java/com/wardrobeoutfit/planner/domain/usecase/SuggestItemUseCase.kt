package com.wardrobeoutfit.planner.domain.usecase

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType

object SuggestItemUseCase {

    operator fun invoke(
        items: List<ClothingItem>,
        type: ClothingType,
        leastWornFirst: Boolean,
        excludeIds: Set<String>
    ): ClothingItem? {
        val candidates = items.filter { it.type == type && it.id !in excludeIds }
        if (candidates.isEmpty()) return null
        return if (leastWornFirst) {
            candidates.sortedWith(compareBy({ it.timesWorn }, { it.lastWornEpochDay }, { it.name })).first()
        } else {
            candidates.sortedWith(compareByDescending<ClothingItem> { it.lastWornEpochDay }.thenBy { it.name }).first()
        }
    }
}
