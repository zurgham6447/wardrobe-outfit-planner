package com.wardrobeoutfit.planner.presentation.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClosetViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClosetUiState())
    val state: StateFlow<ClosetUiState> = _state.asStateFlow()

    private val filter = MutableStateFlow<ClothingType?>(null)

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            settings.ensureLoaded()
            combine(wardrobe.items, settings.settings, filter) { items, config, active ->
                val visible = ClothingType.entries.filterNot { it in config.hiddenTypes }
                val shown = items
                    .filter { it.type in visible }
                    .filter { active == null || it.type == active }
                    .sortedBy { it.name }
                ClosetUiState(
                    loading = false,
                    items = shown,
                    visibleTypes = visible,
                    filter = active,
                    totalCount = items.count { it.type in visible },
                    selected = null
                )
            }.collect { composed ->
                _state.update { current -> composed.copy(selected = current.selected) }
            }
        }
    }

    fun setFilter(type: ClothingType?) {
        filter.value = type
    }

    fun openItem(item: ClothingItem) {
        _state.update { it.copy(selected = item) }
    }

    fun dismissItem() {
        _state.update { it.copy(selected = null) }
    }

    fun deleteSelected() {
        val target = _state.value.selected ?: return
        _state.update { it.copy(selected = null) }
        viewModelScope.launch { wardrobe.deleteItem(target.id) }
    }
}
