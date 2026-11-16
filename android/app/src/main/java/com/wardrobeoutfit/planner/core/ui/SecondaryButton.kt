package com.wardrobeoutfit.planner.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wardrobeoutfit.planner.core.theme.WardrobePalette

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 48.dp
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) WardrobePalette.Hairline else WardrobePalette.Disabled
        ),
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = WardrobePalette.Surface,
            contentColor = WardrobePalette.Primary,
            disabledContainerColor = WardrobePalette.Disabled,
            disabledContentColor = WardrobePalette.DisabledText
        ),
        modifier = modifier
            .height(height)
            .defaultMinSize(minHeight = 48.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}
