package com.wardrobeoutfit.planner.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.colorLabel
import com.wardrobeoutfit.planner.core.ui.garmentArt
import com.wardrobeoutfit.planner.core.ui.tagLabel
import com.wardrobeoutfit.planner.core.ui.typeLabel
import com.wardrobeoutfit.planner.domain.model.ClothingItem

@Composable
fun ItemDetailDialog(
    item: ClothingItem,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WardrobePalette.Surface,
        title = {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = WardrobePalette.TextPrimary
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(garmentArt(item.type)),
                        contentDescription = item.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(72.dp)
                    )
                    Column {
                        DetailLine(
                            label = stringResource(R.string.item_detail_type),
                            value = typeLabel(item.type)
                        )
                        DetailLine(
                            label = stringResource(R.string.item_detail_color),
                            value = colorLabel(item.color)
                        )
                        DetailLine(
                            label = stringResource(R.string.item_detail_worn),
                            value = if (item.timesWorn > 0) {
                                item.timesWorn.toString()
                            } else {
                                stringResource(R.string.item_never_worn)
                            }
                        )
                    }
                }
                if (item.tags.isNotEmpty()) {
                    val tagNames = ArrayList<String>(item.tags.size)
                    for (tag in item.tags) {
                        tagNames.add(tagLabel(tag))
                    }
                    Text(
                        text = tagNames.joinToString(" "),
                        style = MaterialTheme.typography.labelSmall,
                        color = WardrobePalette.TextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    text = stringResource(R.string.cta_delete),
                    style = MaterialTheme.typography.labelSmall,
                    color = WardrobePalette.Accent
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_close),
                    style = MaterialTheme.typography.labelSmall,
                    color = WardrobePalette.Primary
                )
            }
        }
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.TextPrimary
        )
    }
}
