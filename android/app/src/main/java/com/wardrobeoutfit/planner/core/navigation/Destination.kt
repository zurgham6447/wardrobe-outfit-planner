package com.wardrobeoutfit.planner.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Closet : Screen
    data object AddItem : Screen
    data object Game : Screen
    data class GameOver(val outfitId: String) : Screen
    data object Outfits : Screen
    data object Calendar : Screen
    data object Settings : Screen
}
