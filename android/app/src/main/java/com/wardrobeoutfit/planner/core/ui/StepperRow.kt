package com.wardrobeoutfit.planner.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.theme.WardrobePalette

@Composable
fun StepperRow(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextSecondary,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onMinus,
            modifier = Modifier
                .size(48.dp)
                .background(WardrobePalette.Surface, CircleShape)
                .border(BorderStroke(1.dp, WardrobePalette.Hairline), CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_minus),
                contentDescription = stringResource(R.string.cd_decrease),
                tint = WardrobePalette.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = WardrobePalette.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.6f)
        )
        IconButton(
            onClick = onPlus,
            modifier = Modifier
                .size(48.dp)
                .background(WardrobePalette.Surface, CircleShape)
                .border(BorderStroke(1.dp, WardrobePalette.Hairline), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_increase),
                tint = WardrobePalette.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
