package com.wardrobeoutfit.planner.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            settings.ensureLoaded()
            combine(
                wardrobe.items,
                wardrobe.outfits,
                settings.settings
            ) { items, outfits, config ->
                val categories = ClothingType.entries.map { type ->
                    CategoryRow(
                        type = type,
                        itemCount = items.count { it.type == type },
                        visible = type !in config.hiddenTypes
                    )
                }
                val tags = OccasionTag.entries.map { tag ->
                    TagRow(tag = tag, visible = tag !in config.hiddenTags)
                }
                SettingsUiState(
                    loading = false,
                    categories = categories,
                    tags = tags,
                    weekStartsMonday = config.weekStartsMonday,
                    suggestLeastWorn = config.suggestLeastWorn,
                    itemCount = items.size,
                    outfitCount = outfits.size
                )
            }.collect { composed ->
                _state.update { current -> composed.copy(dialog = current.dialog) }
            }
        }
    }

    fun toggleCategory(type: ClothingType, visible: Boolean) {
        viewModelScope.launch { settings.setTypeHidden(type, !visible) }
    }

    fun toggleTag(tag: OccasionTag, visible: Boolean) {
        viewModelScope.launch { settings.setTagHidden(tag, !visible) }
    }

    fun setWeekStart(monday: Boolean) {
        viewModelScope.launch { settings.setWeekStartsMonday(monday) }
    }

    fun toggleSuggestLeastWorn(enabled: Boolean) {
        viewModelScope.launch { settings.setSuggestLeastWorn(enabled) }
    }

    fun askReset() {
        _state.update { it.copy(dialog = SettingsDialog.RESET) }
    }

    fun askClear() {
        _state.update { it.copy(dialog = SettingsDialog.CLEAR) }
    }

    fun dismissDialog() {
        _state.update { it.copy(dialog = SettingsDialog.NONE) }
    }

    fun confirmDialog() {
        val pending = _state.value.dialog
        _state.update { it.copy(dialog = SettingsDialog.NONE) }
        viewModelScope.launch {
            when (pending) {
                SettingsDialog.RESET -> wardrobe.resetSampleWardrobe()
                SettingsDialog.CLEAR -> wardrobe.clearAllData()
                SettingsDialog.NONE -> Unit
            }
        }
    }
}
