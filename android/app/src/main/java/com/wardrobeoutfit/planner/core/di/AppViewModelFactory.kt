package com.wardrobeoutfit.planner.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wardrobeoutfit.planner.presentation.additem.AddItemViewModel
import com.wardrobeoutfit.planner.presentation.app.AppViewModel
import com.wardrobeoutfit.planner.presentation.calendar.CalendarViewModel
import com.wardrobeoutfit.planner.presentation.closet.ClosetViewModel
import com.wardrobeoutfit.planner.presentation.game.GameViewModel
import com.wardrobeoutfit.planner.presentation.gameover.GameOverViewModel
import com.wardrobeoutfit.planner.presentation.menu.MenuViewModel
import com.wardrobeoutfit.planner.presentation.outfits.OutfitsViewModel
import com.wardrobeoutfit.planner.presentation.settings.SettingsViewModel
import com.wardrobeoutfit.planner.presentation.splash.SplashViewModel

class AppViewModelFactory(private val outfitId: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val wardrobe = ServiceLocator.wardrobeRepository
        val settings = ServiceLocator.settingsRepository
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(wardrobe, settings)
            modelClass.isAssignableFrom(ClosetViewModel::class.java) -> ClosetViewModel(wardrobe, settings)
            modelClass.isAssignableFrom(AddItemViewModel::class.java) -> AddItemViewModel(wardrobe, settings)
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(wardrobe, settings)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(wardrobe, outfitId)
            modelClass.isAssignableFrom(OutfitsViewModel::class.java) -> OutfitsViewModel(wardrobe)
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> CalendarViewModel(wardrobe, settings)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(wardrobe, settings)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
