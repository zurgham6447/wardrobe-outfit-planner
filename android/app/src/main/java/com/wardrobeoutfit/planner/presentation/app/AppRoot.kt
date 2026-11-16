package com.wardrobeoutfit.planner.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.core.config.GameConfig
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.navigation.Screen
import com.wardrobeoutfit.planner.presentation.additem.AddItemScreen
import com.wardrobeoutfit.planner.presentation.calendar.CalendarScreen
import com.wardrobeoutfit.planner.presentation.closet.ClosetScreen
import com.wardrobeoutfit.planner.presentation.game.GameScreen
import com.wardrobeoutfit.planner.presentation.gameover.GameOverScreen
import com.wardrobeoutfit.planner.presentation.menu.MenuScreen
import com.wardrobeoutfit.planner.presentation.outfits.OutfitsScreen
import com.wardrobeoutfit.planner.presentation.settings.SettingsScreen
import com.wardrobeoutfit.planner.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen != Screen.Menu && screen != Screen.Splash) {
        appViewModel.back()
    }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            val enter = fadeIn(tween(GameConfig.TRANSITION_MS)) +
                slideInVertically(tween(GameConfig.TRANSITION_MS)) { full -> full / 14 }
            enter togetherWith fadeOut(tween(GameConfig.TRANSITION_MS))
        }
    ) { current ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (current) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.finishSplash() }
                )

                Screen.Menu -> MenuScreen(
                    onStartPlanning = { appViewModel.navigate(Screen.Game) },
                    onOpenCloset = { appViewModel.navigate(Screen.Closet) },
                    onOpenOutfits = { appViewModel.navigate(Screen.Outfits) },
                    onOpenCalendar = { appViewModel.navigate(Screen.Calendar) },
                    onOpenSettings = { appViewModel.navigate(Screen.Settings) }
                )

                Screen.Closet -> ClosetScreen(
                    onBack = { appViewModel.back() },
                    onAddItem = { appViewModel.navigate(Screen.AddItem) }
                )

                Screen.AddItem -> AddItemScreen(
                    onBack = { appViewModel.back() }
                )

                Screen.Game -> GameScreen(
                    onBack = { appViewModel.back() },
                    onLookReady = { id -> appViewModel.replaceCurrent(Screen.GameOver(id)) },
                    onOpenOutfits = { appViewModel.navigate(Screen.Outfits) }
                )

                is Screen.GameOver -> GameOverScreen(
                    outfitId = current.outfitId,
                    onBuildAgain = { appViewModel.replaceCurrent(Screen.Game) },
                    onSchedule = { appViewModel.navigate(Screen.Calendar) },
                    onMenu = { appViewModel.goHome() }
                )

                Screen.Outfits -> OutfitsScreen(
                    onBack = { appViewModel.back() },
                    onStartPlanning = { appViewModel.navigate(Screen.Game) },
                    onSchedule = { appViewModel.navigate(Screen.Calendar) }
                )

                Screen.Calendar -> CalendarScreen(
                    onBack = { appViewModel.back() }
                )

                Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
