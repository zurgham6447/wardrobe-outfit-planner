package com.wardrobeoutfit.planner.domain.usecase

import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.Outfit

object AutoFillWeekUseCase {

    operator fun invoke(
        weekDays: List<Long>,
        outfits: List<Outfit>,
        calendar: List<CalendarEntry>
    ): List<CalendarEntry> {
        if (outfits.isEmpty()) return emptyList()
        val taken = calendar.filter { it.dayEpoch in weekDays }
        val used = taken.map { it.outfitId }.toMutableSet()
        val plannedDays = taken.map { it.dayEpoch }.toSet()
        val pool = outfits.sortedWith(compareBy({ it.createdEpochDay }, { it.name }))
        val created = mutableListOf<CalendarEntry>()
        for (day in weekDays) {
            if (day in plannedDays) continue
            val next = pool.firstOrNull { it.id !in used } ?: break
            used.add(next.id)
            created.add(CalendarEntry(dayEpoch = day, outfitId = next.id))
        }
        return created
    }
}
