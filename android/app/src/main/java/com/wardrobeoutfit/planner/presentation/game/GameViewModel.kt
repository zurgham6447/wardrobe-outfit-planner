package com.wardrobeoutfit.planner.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.Outfit
import com.wardrobeoutfit.planner.domain.model.WardrobeSettings
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository
import com.wardrobeoutfit.planner.domain.usecase.BuildOutfitResult
import com.wardrobeoutfit.planner.domain.usecase.BuildOutfitUseCase
import com.wardrobeoutfit.planner.domain.usecase.SuggestItemUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val wardrobe: WardrobeRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = _events.receiveAsFlow()

    private var allItems: List<ClothingItem> = emptyList()
    private var allOutfits: List<Outfit> = emptyList()
    private var config: WardrobeSettings = WardrobeSettings()
    private var seeded = false

    init {
        viewModelScope.launch {
            wardrobe.ensureLoaded()
            settings.ensureLoaded()
            combine(
                wardrobe.items,
                wardrobe.outfits,
                settings.settings
            ) { items, outfits, current ->
                Triple(items, outfits, current)
            }.collect { snapshot ->
                allItems = snapshot.first
                allOutfits = snapshot.second
                config = snapshot.third
                if (!seeded) {
                    seeded = true
                    val draft = wardrobe.draft.value.filterValues { id ->
                        allItems.any { it.id == id }
                    }
                    val startSlots = draft.ifEmpty { suggestedStart() }
                    mutate { current ->
                        current.copy(
                            loading = false,
                            slots = startSlots,
                            parked = draft.isNotEmpty(),
                            status = if (startSlots.isEmpty()) {
                                BuilderStatus.IDLE
                            } else {
                                BuilderStatus.BUILDING
                            }
                        )
                    }
                } else {
                    mutate { it.copy(loading = false) }
                }
            }
        }
    }

    fun selectSlot(type: ClothingType) {
        mutate { it.copy(activeSlot = type) }
    }

    fun selectItem(itemId: String) {
        mutate { current ->
            current.copy(
                slots = current.slots + (current.activeSlot to itemId),
                status = BuilderStatus.BUILDING,
                missingSlots = emptyList()
            )
        }
    }

    fun suggestForActiveSlot() {
        val current = _state.value
        val suggestion = SuggestItemUseCase(
            items = allItems,
            type = current.activeSlot,
            leastWornFirst = config.suggestLeastWorn,
            excludeIds = current.slots.values.toSet()
        ) ?: return
        selectItem(suggestion.id)
    }

    fun clearSlots() {
        mutate {
            it.copy(
                slots = emptyMap(),
                status = BuilderStatus.IDLE,
                missingSlots = emptyList(),
                parked = false
            )
        }
        viewModelScope.launch { wardrobe.storeDraft(emptyMap()) }
    }

    fun parkDraft() {
        val slots = _state.value.slots
        mutate { it.copy(parked = slots.isNotEmpty()) }
        viewModelScope.launch { wardrobe.storeDraft(slots) }
    }

    fun pause() {
        val current = _state.value
        if (current.status == BuilderStatus.SAVED) {
            mutate {
                it.copy(
                    slots = emptyMap(),
                    status = BuilderStatus.IDLE,
                    missingSlots = emptyList(),
                    parked = false
                )
            }
            viewModelScope.launch { wardrobe.storeDraft(emptyMap()) }
            return
        }
        viewModelScope.launch { wardrobe.storeDraft(current.slots) }
    }

    fun openOutfits() {
        viewModelScope.launch { _events.send(GameEvent.OpenOutfits) }
    }

    fun saveLook() {
        val current = _state.value
        if (current.status == BuilderStatus.SAVED) return
        val today = WeekCalendar.todayEpochDay()
        val result = BuildOutfitUseCase(
            slots = current.slots,
            items = allItems,
            existing = allOutfits,
            todayEpochDay = today,
            createdAtMillis = System.currentTimeMillis()
        )
        when (result) {
            is BuildOutfitResult.Incomplete -> {
                mutate {
                    it.copy(status = BuilderStatus.INVALID, missingSlots = result.missing)
                }
                scheduleFeedbackReset(BuilderStatus.INVALID)
            }

            BuildOutfitResult.Duplicate -> {
                mutate { it.copy(status = BuilderStatus.DUPLICATE, missingSlots = emptyList()) }
                scheduleFeedbackReset(BuilderStatus.DUPLICATE)
            }

            is BuildOutfitResult.Valid -> {
                mutate { it.copy(status = BuilderStatus.SAVED, missingSlots = emptyList()) }
                viewModelScope.launch {
                    wardrobe.storeOutfit(result.outfit, today)
                    delay(GameConfig.SAVE_OVERLAY_MS + GameConfig.SAVE_HOLD_MS)
                    _events.send(GameEvent.LookReady(result.outfit.id))
                }
            }
        }
    }

    private fun scheduleFeedbackReset(from: BuilderStatus) {
        viewModelScope.launch {
            delay(GameConfig.FEEDBACK_HOLD_MS)
            mutate { current ->
                if (current.status != from) {
                    current
                } else {
                    current.copy(
                        status = if (current.slots.isEmpty()) {
                            BuilderStatus.IDLE
                        } else {
                            BuilderStatus.BUILDING
                        },
                        missingSlots = emptyList()
                    )
                }
            }
        }
    }

    private fun suggestedStart(): Map<ClothingType, String> {
        val required = listOf(ClothingType.TOP, ClothingType.BOTTOM, ClothingType.SHOES)
        val picked = LinkedHashMap<ClothingType, String>()
        required.forEach { type ->
            val suggestion = SuggestItemUseCase(
                items = allItems,
                type = type,
                leastWornFirst = config.suggestLeastWorn,
                excludeIds = picked.values.toSet()
            )
            if (suggestion != null) picked[type] = suggestion.id
        }
        return picked
    }

    private fun mutate(block: (GameUiState) -> GameUiState) {
        _state.value = derive(block(_state.value))
    }

    private fun derive(source: GameUiState): GameUiState {
        val visible = ClothingType.entries.filterNot { it in config.hiddenTypes }
        val slots = source.slots.filterKeys { it in visible }
        val active = if (source.activeSlot in visible) {
            source.activeSlot
        } else {
            visible.firstOrNull() ?: ClothingType.TOP
        }
        val chosen = slots.values.mapNotNull { id -> allItems.firstOrNull { it.id == id } }
        return source.copy(
            slots = slots,
            activeSlot = active,
            slotOrder = visible,
            pickerItems = allItems.filter { it.type == active }.sortedBy { it.name },
            chosenItems = chosen,
            tags = chosen.flatMap { it.tags }.distinct().filterNot { it in config.hiddenTags },
            newPieces = chosen.count { it.timesWorn == 0 }
        )
    }
}
