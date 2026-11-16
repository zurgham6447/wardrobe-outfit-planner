package com.wardrobeoutfit.planner.presentation.outfits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OutfitsViewModel(
    private val wardrobe: WardrobeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OutfitsUiState())
    val state: StateFlow<OutfitsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            combine(
                wardrobe.items,
                wardrobe.outfits,
                wardrobe.calendar
            ) { items, outfits, calendar ->
                val byId = items.associateBy { it.id }
                outfits.sortedByDescending { it.createdEpochDay }.map { outfit ->
                    val picked = outfit.itemIds.mapNotNull { byId[it] }
                    val day = calendar.firstOrNull { it.outfitId == outfit.id }
                    OutfitRow(
                        outfit = outfit,
                        items = picked,
                        tags = picked.flatMap { it.tags }.distinct().take(3),
                        scheduledLabel = day?.let { WeekCalendar.dayLabel(it.dayEpoch) }.orEmpty()
                    )
                }
            }.collect { rows ->
                _state.value = _state.value.copy(loading = false, rows = rows)
            }
        }
    }

    fun askDelete(outfitId: String) {
        _state.value = _state.value.copy(pendingDeleteId = outfitId)
    }

    fun dismissDelete() {
        _state.value = _state.value.copy(pendingDeleteId = "")
    }

    fun confirmDelete() {
        val target = _state.value.pendingDeleteId
        _state.value = _state.value.copy(pendingDeleteId = "")
        if (target.isBlank()) return
        viewModelScope.launch { wardrobe.deleteOutfit(target) }
    }
}
