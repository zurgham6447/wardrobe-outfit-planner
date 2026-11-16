package com.wardrobeoutfit.planner.presentation.app

import androidx.lifecycle.ViewModel
import com.wardrobeoutfit.planner.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun replaceCurrent(target: Screen) {
        _screen.value = target
    }

    fun finishSplash() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun goHome() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back() {
        if (_screen.value == Screen.Menu || _screen.value == Screen.Splash) return
        val previous = backStack.removeLastOrNull()
        _screen.value = previous ?: Screen.Menu
    }
}
