package com.wardrobeoutfit.planner.presentation.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemUiState())
    val state: StateFlow<AddItemUiState> = _state.asStateFlow()

    private val _events = Channel<AddItemEvent>(Channel.BUFFERED)
    val events: Flow<AddItemEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            settings.ensureLoaded()
            combine(wardrobe.items, settings.settings) { items, config ->
                items.size to config
            }.collect { pair ->
                _state.update { current ->
                    current.copy(
                        limitReached = pair.first >= GameConfig.MAX_ITEMS,
                        availableTypes = ClothingType.entries.filterNot { it in pair.second.hiddenTypes },
                        availableTags = OccasionTag.entries.filterNot { it in pair.second.hiddenTags }
                    )
                }
            }
        }
    }

    fun setName(value: String) {
        _state.update { it.copy(name = value.take(40)) }
    }

    fun selectType(type: ClothingType) {
        _state.update { it.copy(type = type) }
    }

    fun selectColor(color: ItemColor) {
        _state.update { it.copy(color = color) }
    }

    fun toggleTag(tag: OccasionTag) {
        _state.update { current ->
            val tags = current.tags.toMutableSet()
            if (!tags.add(tag)) tags.remove(tag)
            current.copy(tags = tags)
        }
    }

    fun increaseWorn() {
        _state.update { it.copy(timesWorn = (it.timesWorn + 1).coerceAtMost(99)) }
    }

    fun decreaseWorn() {
        _state.update { it.copy(timesWorn = (it.timesWorn - 1).coerceAtLeast(0)) }
    }

    fun submit() {
        val current = _state.value
        val type = current.type ?: return
        val color = current.color ?: return
        if (!current.canSubmit) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            val today = WeekCalendar.todayEpochDay()
            wardrobe.addItem(
                ClothingItem(
                    id = System.currentTimeMillis().toString(),
                    name = current.name.trim(),
                    type = type,
                    color = color,
                    tags = current.tags,
                    timesWorn = current.timesWorn,
                    lastWornEpochDay = if (current.timesWorn > 0) today else 0L
                )
            )
            delay(GameConfig.SAVE_OVERLAY_MS + GameConfig.SAVE_HOLD_MS)
            _state.update { it.copy(saving = false) }
            _events.send(AddItemEvent.Stored)
        }
    }
}
