package com.wardrobeoutfit.planner.domain.repository

import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.Outfit
import kotlinx.coroutines.flow.StateFlow

interface WardrobeRepository {

    val items: StateFlow<List<ClothingItem>>

    val outfits: StateFlow<List<Outfit>>

    val calendar: StateFlow<List<CalendarEntry>>

    val draft: StateFlow<Map<ClothingType, String>>

    suspend fun ensureLoaded()

    suspend fun addItem(item: ClothingItem)

    suspend fun deleteItem(itemId: String)

    suspend fun storeOutfit(outfit: Outfit, wornDay: Long)

    suspend fun deleteOutfit(outfitId: String)

    suspend fun assignDay(dayEpoch: Long, outfitId: String)

    suspend fun clearDay(dayEpoch: Long)

    suspend fun assignDays(entries: List<CalendarEntry>)

    suspend fun storeDraft(slots: Map<ClothingType, String>)

    suspend fun resetSampleWardrobe()

    suspend fun clearAllData()
}
