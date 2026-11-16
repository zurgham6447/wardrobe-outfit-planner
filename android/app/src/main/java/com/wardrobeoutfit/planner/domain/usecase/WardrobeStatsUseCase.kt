package com.wardrobeoutfit.planner.domain.usecase

import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.Outfit
import com.wardrobeoutfit.planner.domain.model.WardrobeStats

object WardrobeStatsUseCase {

    operator fun invoke(
        items: List<ClothingItem>,
        outfits: List<Outfit>,
        calendar: List<CalendarEntry>,
        weekDays: List<Long>,
        todayEpochDay: Long,
        coverageWindowDays: Long
    ): WardrobeStats {
        val planned = calendar.count { it.dayEpoch in weekDays }
        val recentlyWorn = items.count {
            it.timesWorn > 0 && todayEpochDay - it.lastWornEpochDay <= coverageWindowDays
        }
        val coverage = if (items.isEmpty()) 0 else (recentlyWorn * 100) / items.size
        return WardrobeStats(
            itemCount = items.size,
            outfitCount = outfits.size,
            plannedDays = planned,
            coveragePercent = coverage
        )
    }
}
