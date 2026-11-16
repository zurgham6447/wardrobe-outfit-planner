package com.wardrobeoutfit.planner.domain.usecase

import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.Outfit

sealed interface BuildOutfitResult {
    data class Valid(val outfit: Outfit) : BuildOutfitResult
    data object Duplicate : BuildOutfitResult
    data class Incomplete(val missing: List<ClothingType>) : BuildOutfitResult
}

object BuildOutfitUseCase {

    private val required = listOf(ClothingType.TOP, ClothingType.BOTTOM, ClothingType.SHOES)

    private val order = listOf(
        ClothingType.OUTERWEAR,
        ClothingType.TOP,
        ClothingType.BOTTOM,
        ClothingType.SHOES,
        ClothingType.ACCESSORY
    )

    operator fun invoke(
        slots: Map<ClothingType, String>,
        items: List<ClothingItem>,
        existing: List<Outfit>,
        todayEpochDay: Long,
        createdAtMillis: Long
    ): BuildOutfitResult {
        val missing = required.filter { slots[it].isNullOrEmpty() }
        if (missing.isNotEmpty()) return BuildOutfitResult.Incomplete(missing)

        val chosen = order.mapNotNull { type -> slots[type] }
        val signature = chosen.toSet()
        if (existing.any { it.itemIds.toSet() == signature }) return BuildOutfitResult.Duplicate

        val byId = items.associateBy { it.id }
        val top = byId[slots[ClothingType.TOP]]
        val name = buildName(top, chosen.mapNotNull { byId[it] })
        return BuildOutfitResult.Valid(
            Outfit(
                id = createdAtMillis.toString(),
                name = name,
                itemIds = chosen,
                createdEpochDay = todayEpochDay
            )
        )
    }

    private fun buildName(top: ClothingItem?, picked: List<ClothingItem>): String {
        val dominant = picked.groupingBy { it.color }.eachCount()
            .maxByOrNull { it.value }?.key?.name
            ?: top?.color?.name.orEmpty()
        val words = top?.name.orEmpty().trim().split(" ").filter { it.isNotBlank() }
        val accent = when {
            words.size >= 2 -> words[1]
            words.isNotEmpty() -> words[0]
            else -> ""
        }.uppercase()
        return listOf(dominant, accent, "LOOK").filter { it.isNotBlank() }.joinToString(" ")
    }
}
