package com.wardrobeoutfit.planner.data.repository

import com.wardrobeoutfit.planner.data.local.WardrobePreferences
import com.wardrobeoutfit.planner.data.sample.SampleData
import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.Outfit
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class WardrobeRepositoryImpl(
    private val preferences: WardrobePreferences,
    private val todayProvider: () -> Long
) : WardrobeRepository {

    private val _items = MutableStateFlow<List<ClothingItem>>(emptyList())
    override val items: StateFlow<List<ClothingItem>> = _items.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    override val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _calendar = MutableStateFlow<List<CalendarEntry>>(emptyList())
    override val calendar: StateFlow<List<CalendarEntry>> = _calendar.asStateFlow()

    private val _draft = MutableStateFlow<Map<ClothingType, String>>(emptyMap())
    override val draft: StateFlow<Map<ClothingType, String>> = _draft.asStateFlow()

    private val mutex = Mutex()
    private var loaded = false

    override suspend fun ensureLoaded() {
        mutex.withLock {
            if (loaded) return
            withContext(Dispatchers.IO) {
                if (!preferences.isSeeded()) {
                    val today = todayProvider()
                    preferences.writeItems(SampleData.items(today))
                    preferences.writeOutfits(SampleData.outfits(today))
                    preferences.writeCalendar(SampleData.calendar(today))
                    preferences.markSeeded()
                }
                _items.value = preferences.readItems()
                _outfits.value = preferences.readOutfits()
                _calendar.value = preferences.readCalendar()
                _draft.value = preferences.readDraft()
            }
            loaded = true
        }
    }

    override suspend fun addItem(item: ClothingItem) {
        val updated = _items.value + item
        _items.value = updated
        withContext(Dispatchers.IO) { preferences.writeItems(updated) }
    }

    override suspend fun deleteItem(itemId: String) {
        val updatedItems = _items.value.filterNot { it.id == itemId }
        val updatedOutfits = _outfits.value.filterNot { itemId in it.itemIds }
        val keptIds = updatedOutfits.map { it.id }.toSet()
        val updatedCalendar = _calendar.value.filter { it.outfitId in keptIds }
        _items.value = updatedItems
        _outfits.value = updatedOutfits
        _calendar.value = updatedCalendar
        withContext(Dispatchers.IO) {
            preferences.writeItems(updatedItems)
            preferences.writeOutfits(updatedOutfits)
            preferences.writeCalendar(updatedCalendar)
        }
    }

    override suspend fun storeOutfit(outfit: Outfit, wornDay: Long) {
        val updatedOutfits = _outfits.value + outfit
        val worn = outfit.itemIds.toSet()
        val updatedItems = _items.value.map { item ->
            if (item.id in worn) {
                item.copy(timesWorn = item.timesWorn + 1, lastWornEpochDay = wornDay)
            } else {
                item
            }
        }
        _outfits.value = updatedOutfits
        _items.value = updatedItems
        _draft.value = emptyMap()
        withContext(Dispatchers.IO) {
            preferences.writeOutfits(updatedOutfits)
            preferences.writeItems(updatedItems)
            preferences.writeDraft(emptyMap())
        }
    }

    override suspend fun deleteOutfit(outfitId: String) {
        val updatedOutfits = _outfits.value.filterNot { it.id == outfitId }
        val updatedCalendar = _calendar.value.filterNot { it.outfitId == outfitId }
        _outfits.value = updatedOutfits
        _calendar.value = updatedCalendar
        withContext(Dispatchers.IO) {
            preferences.writeOutfits(updatedOutfits)
            preferences.writeCalendar(updatedCalendar)
        }
    }

    override suspend fun assignDay(dayEpoch: Long, outfitId: String) {
        val updated = _calendar.value.filterNot { it.dayEpoch == dayEpoch } +
            CalendarEntry(dayEpoch, outfitId)
        val sorted = updated.sortedBy { it.dayEpoch }
        _calendar.value = sorted
        withContext(Dispatchers.IO) { preferences.writeCalendar(sorted) }
    }

    override suspend fun clearDay(dayEpoch: Long) {
        val updated = _calendar.value.filterNot { it.dayEpoch == dayEpoch }
        _calendar.value = updated
        withContext(Dispatchers.IO) { preferences.writeCalendar(updated) }
    }

    override suspend fun assignDays(entries: List<CalendarEntry>) {
        if (entries.isEmpty()) return
        val days = entries.map { it.dayEpoch }.toSet()
        val updated = (_calendar.value.filterNot { it.dayEpoch in days } + entries)
            .sortedBy { it.dayEpoch }
        _calendar.value = updated
        withContext(Dispatchers.IO) { preferences.writeCalendar(updated) }
    }

    override suspend fun storeDraft(slots: Map<ClothingType, String>) {
        _draft.value = slots
        withContext(Dispatchers.IO) { preferences.writeDraft(slots) }
    }

    override suspend fun resetSampleWardrobe() {
        val today = todayProvider()
        val freshItems = SampleData.items(today)
        val freshOutfits = SampleData.outfits(today)
        val freshCalendar = SampleData.calendar(today)
        _items.value = freshItems
        _outfits.value = freshOutfits
        _calendar.value = freshCalendar
        _draft.value = emptyMap()
        withContext(Dispatchers.IO) {
            preferences.writeItems(freshItems)
            preferences.writeOutfits(freshOutfits)
            preferences.writeCalendar(freshCalendar)
            preferences.writeDraft(emptyMap())
            preferences.markSeeded()
        }
    }

    override suspend fun clearAllData() {
        _items.value = emptyList()
        _outfits.value = emptyList()
        _calendar.value = emptyList()
        _draft.value = emptyMap()
        withContext(Dispatchers.IO) {
            preferences.writeItems(emptyList())
            preferences.writeOutfits(emptyList())
            preferences.writeCalendar(emptyList())
            preferences.writeDraft(emptyMap())
            preferences.markSeeded()
        }
    }
}
