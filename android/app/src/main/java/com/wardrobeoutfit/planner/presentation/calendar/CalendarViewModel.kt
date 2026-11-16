package com.wardrobeoutfit.planner.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import com.wardrobeoutfit.planner.domain.usecase.AutoFillWeekUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    private var weekDays: List<Long> = emptyList()

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
                val today = WeekCalendar.todayEpochDay()
                val week = WeekCalendar.weekDays(today, config.weekStartsMonday)
                val byId = items.associateBy { it.id }
                val outfitById = outfits.associateBy { it.id }
                val cells = week.map { day ->
                    val entry = calendar.firstOrNull { it.dayEpoch == day }
                    val outfit = entry?.let { outfitById[it.outfitId] }
                    DayCell(
                        epochDay = day,
                        letter = WeekCalendar.dayLetter(day),
                        number = WeekCalendar.dayNumber(day),
                        outfitName = outfit?.name.orEmpty(),
                        items = outfit?.itemIds.orEmpty().mapNotNull { byId[it] },
                        isToday = day == today
                    )
                }
                weekDays = week
                Triple(cells, outfits, byId)
            }.collect { snapshot ->
                _state.update { current ->
                    val cells = snapshot.first
                    val selected = if (cells.any { it.epochDay == current.selectedEpochDay }) {
                        current.selectedEpochDay
                    } else {
                        cells.firstOrNull { it.isToday }?.epochDay
                            ?: cells.firstOrNull()?.epochDay
                            ?: 0L
                    }
                    current.copy(
                        loading = false,
                        monthLabel = cells.firstOrNull()?.let {
                            WeekCalendar.monthName(it.epochDay)
                        }.orEmpty(),
                        days = cells,
                        selectedEpochDay = selected,
                        outfits = snapshot.second,
                        itemsById = snapshot.third
                    )
                }
            }
        }
    }

    fun selectDay(epochDay: Long) {
        _state.update { it.copy(selectedEpochDay = epochDay) }
    }

    fun openAssign() {
        _state.update { it.copy(assignVisible = true) }
    }

    fun dismissAssign() {
        _state.update { it.copy(assignVisible = false) }
    }

    fun assignOutfit(outfitId: String) {
        val day = _state.value.selectedEpochDay
        _state.update { it.copy(assignVisible = false) }
        viewModelScope.launch { wardrobe.assignDay(day, outfitId) }
    }

    fun clearSelectedDay() {
        val day = _state.value.selectedEpochDay
        viewModelScope.launch { wardrobe.clearDay(day) }
    }

    fun autoFillWeek() {
        viewModelScope.launch {
            val created = AutoFillWeekUseCase(
                weekDays = weekDays,
                outfits = wardrobe.outfits.value,
                calendar = wardrobe.calendar.value
            )
            if (created.isNotEmpty()) wardrobe.assignDays(created)
        }
    }
}
