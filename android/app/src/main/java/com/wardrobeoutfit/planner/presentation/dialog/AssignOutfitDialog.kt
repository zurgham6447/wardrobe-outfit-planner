package com.wardrobeoutfit.planner.presentation.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.OutfitStrip
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.Outfit

@Composable
fun AssignOutfitDialog(
    outfits: List<Outfit>,
    itemsById: Map<String, ClothingItem>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WardrobePalette.Surface,
        title = {
            Text(
                text = stringResource(R.string.calendar_assign_title),
                style = MaterialTheme.typography.titleMedium,
                color = WardrobePalette.TextPrimary
            )
        },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                items(outfits, key = { it.id }) { outfit ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, WardrobePalette.Hairline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = WardrobePalette.Surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { onPick(outfit.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = outfit.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = WardrobePalette.TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            OutfitStrip(
                                items = outfit.itemIds.mapNotNull { itemsById[it] },
                                size = 40.dp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
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
