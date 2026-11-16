package com.wardrobeoutfit.planner.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.Outfit
import com.wardrobeoutfit.planner.domain.model.WardrobeSettings
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import com.wardrobeoutfit.planner.domain.usecase.WardrobeStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            settings.ensureLoaded()
            combine(
                wardrobe.items,
                wardrobe.outfits,
                wardrobe.calendar,
                settings.settings
            ) { items, outfits, calendar, config ->
                compose(items, outfits, calendar, config)
            }.collect { composed ->
                _state.value = composed
            }
        }
    }

    private fun compose(
        items: List<ClothingItem>,
        outfits: List<Outfit>,
        calendar: List<CalendarEntry>,
        config: WardrobeSettings
    ): MenuUiState {
        val today = WeekCalendar.todayEpochDay()
        val week = WeekCalendar.weekDays(today, config.weekStartsMonday)
        val stats = WardrobeStatsUseCase(
            items = items,
            outfits = outfits,
            calendar = calendar,
            weekDays = week,
            todayEpochDay = today,
            coverageWindowDays = GameConfig.COVERAGE_WINDOW_DAYS
        )
        val entry = calendar.firstOrNull { it.dayEpoch == today }
        val planned = outfits.firstOrNull { it.id == entry?.outfitId }
        val byId = items.associateBy { it.id }
        return MenuUiState(
            loading = false,
            weekNumber = WeekCalendar.weekNumber(today),
            todayLabel = WeekCalendar.dayLabel(today),
            todayOutfitName = planned?.name.orEmpty(),
            todayItems = planned?.itemIds.orEmpty().mapNotNull { byId[it] }.take(3),
            stats = stats
        )
    }
}
