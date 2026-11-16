package com.wardrobeoutfit.planner.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val WardrobeColorScheme = lightColorScheme(
    primary = WardrobePalette.Primary,
    onPrimary = WardrobePalette.Surface,
    primaryContainer = WardrobePalette.PrimaryLight,
    onPrimaryContainer = WardrobePalette.Surface,
    secondary = WardrobePalette.Secondary,
    onSecondary = WardrobePalette.TextPrimary,
    tertiary = WardrobePalette.Accent,
    onTertiary = WardrobePalette.Surface,
    background = WardrobePalette.PaperBase,
    onBackground = WardrobePalette.TextPrimary,
    surface = WardrobePalette.Surface,
    onSurface = WardrobePalette.TextPrimary,
    surfaceVariant = WardrobePalette.SurfaceVariant,
    onSurfaceVariant = WardrobePalette.TextSecondary,
    outline = WardrobePalette.Hairline,
    outlineVariant = WardrobePalette.Hairline,
    error = WardrobePalette.Accent,
    onError = WardrobePalette.Surface
)

private val WardrobeShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WardrobeColorScheme,
        typography = WardrobeTypography,
        shapes = WardrobeShapes,
        content = content
    )
}
