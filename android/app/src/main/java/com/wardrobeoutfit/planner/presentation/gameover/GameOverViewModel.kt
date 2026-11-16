package com.wardrobeoutfit.planner.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import com.wardrobeoutfit.planner.domain.usecase.WardrobeStatsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GameOverViewModel(
    private val wardrobe: WardrobeRepository,
    private val outfitId: String
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private val _events = Channel<GameOverEvent>(Channel.BUFFERED)
    val events: Flow<GameOverEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            combine(
                wardrobe.items,
                wardrobe.outfits,
                wardrobe.calendar
            ) { items, outfits, calendar ->
                val today = WeekCalendar.todayEpochDay()
                val week = WeekCalendar.weekDays(today, true)
                val outfit = outfits.firstOrNull { it.id == outfitId } ?: outfits.lastOrNull()
                val byId = items.associateBy { it.id }
                val picked = outfit?.itemIds.orEmpty().mapNotNull { byId[it] }
                val stats = WardrobeStatsUseCase(
                    items = items,
                    outfits = outfits,
                    calendar = calendar,
                    weekDays = week,
                    todayEpochDay = today,
                    coverageWindowDays = GameConfig.COVERAGE_WINDOW_DAYS
                )
                val scheduled = calendar.firstOrNull { it.outfitId == outfit?.id }
                GameOverUiState(
                    loading = false,
                    outfitName = outfit?.name.orEmpty(),
                    items = picked,
                    pieces = picked.size,
                    looksTotal = stats.outfitCount,
                    coveragePercent = stats.coveragePercent,
                    scheduledLabel = scheduled?.let { WeekCalendar.dayLabel(it.dayEpoch) }.orEmpty()
                )
            }.collect { composed ->
                _state.value = composed
            }
        }
    }

    fun scheduleNextFreeDay() {
        viewModelScope.launch {
            val today = WeekCalendar.todayEpochDay()
            val week = WeekCalendar.weekDays(today, true)
            val planned = wardrobe.calendar.value.map { it.dayEpoch }.toSet()
            val target = week.firstOrNull { it >= today && it !in planned } ?: (today + 1)
            val outfit = wardrobe.outfits.value.firstOrNull { it.id == outfitId }
            if (outfit != null) wardrobe.assignDay(target, outfit.id)
            _events.send(GameOverEvent.Scheduled)
        }
    }
}
