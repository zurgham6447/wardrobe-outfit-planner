package com.wardrobeoutfit.planner.core.di

import android.content.Context
import com.wardrobeoutfit.planner.core.time.WeekCalendar
import com.wardrobeoutfit.planner.data.local.WardrobePreferences
import com.wardrobeoutfit.planner.data.repository.SettingsRepositoryImpl
import com.wardrobeoutfit.planner.data.repository.WardrobeRepositoryImpl
import com.wardrobeoutfit.planner.domain.repository.SettingsRepository
import com.wardrobeoutfit.planner.domain.repository.WardrobeRepository

object ServiceLocator {

    private var wardrobe: WardrobeRepository? = null
    private var preferences: SettingsRepository? = null

    val wardrobeRepository: WardrobeRepository
        get() = requireNotNull(wardrobe)

    val settingsRepository: SettingsRepository
        get() = requireNotNull(preferences)

    val viewModelFactory: AppViewModelFactory = AppViewModelFactory("")

    fun init(context: Context) {
        if (wardrobe != null) return
        val store = WardrobePreferences(context)
        wardrobe = WardrobeRepositoryImpl(store) { WeekCalendar.todayEpochDay() }
        preferences = SettingsRepositoryImpl(store)
    }

    fun factoryFor(outfitId: String): AppViewModelFactory = AppViewModelFactory(outfitId)
}
