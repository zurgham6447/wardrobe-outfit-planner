package com.wardrobeoutfit.planner.domain.repository

import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.model.WardrobeSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<WardrobeSettings>

    suspend fun ensureLoaded()

    suspend fun setTypeHidden(type: ClothingType, hidden: Boolean)

    suspend fun setTagHidden(tag: OccasionTag, hidden: Boolean)

    suspend fun setWeekStartsMonday(monday: Boolean)

    suspend fun setSuggestLeastWorn(enabled: Boolean)
}
