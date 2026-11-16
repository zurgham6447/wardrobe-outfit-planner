package com.wardrobeoutfit.planner.data.repository

import com.wardrobeoutfit.planner.data.local.WardrobePreferences
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.model.WardrobeSettings
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val preferences: WardrobePreferences
) : SettingsRepository {

    private val _settings = MutableStateFlow(WardrobeSettings())
    override val settings: StateFlow<WardrobeSettings> = _settings.asStateFlow()

    private val mutex = Mutex()
    private var loaded = false

    override suspend fun ensureLoaded() {
        mutex.withLock {
            if (loaded) return
            _settings.value = withContext(Dispatchers.IO) { preferences.readSettings() }
            loaded = true
        }
    }

    override suspend fun setTypeHidden(type: ClothingType, hidden: Boolean) {
        val current = _settings.value
        val types = current.hiddenTypes.toMutableSet()
        if (hidden) types.add(type) else types.remove(type)
        persist(current.copy(hiddenTypes = types))
    }

    override suspend fun setTagHidden(tag: OccasionTag, hidden: Boolean) {
        val current = _settings.value
        val tags = current.hiddenTags.toMutableSet()
        if (hidden) tags.add(tag) else tags.remove(tag)
        persist(current.copy(hiddenTags = tags))
    }

    override suspend fun setWeekStartsMonday(monday: Boolean) {
        persist(_settings.value.copy(weekStartsMonday = monday))
    }

    override suspend fun setSuggestLeastWorn(enabled: Boolean) {
        persist(_settings.value.copy(suggestLeastWorn = enabled))
    }

    private suspend fun persist(updated: WardrobeSettings) {
        _settings.value = updated
        withContext(Dispatchers.IO) { preferences.writeSettings(updated) }
    }
}
