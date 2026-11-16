package com.wardrobeoutfit.planner.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wardrobeoutfit.planner.R
import com.wardrobeoutfit.planner.core.di.ServiceLocator
import com.wardrobeoutfit.planner.core.theme.WardrobePalette
import com.wardrobeoutfit.planner.core.ui.EditorialTopBar
import com.wardrobeoutfit.planner.core.ui.HairlineRule
import com.wardrobeoutfit.planner.core.ui.ScreenScaffold
import com.wardrobeoutfit.planner.core.ui.SecondaryButton
import com.wardrobeoutfit.planner.core.ui.StepperRow
import com.wardrobeoutfit.planner.core.ui.garmentArt
import com.wardrobeoutfit.planner.core.ui.selectionState
import com.wardrobeoutfit.planner.core.ui.tagLabel
import com.wardrobeoutfit.planner.core.ui.typeLabelPlural
import com.wardrobeoutfit.planner.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(
        overlay = listOf(
            WardrobePalette.PaperBase.copy(alpha = 0.95f),
            WardrobePalette.PaperMid.copy(alpha = 0.96f),
            WardrobePalette.PaperDeep.copy(alpha = 0.97f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            EditorialTopBar(
                title = stringResource(R.string.title_settings),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                SectionHeader(title = stringResource(R.string.settings_categories))
                state.categories.forEach { row ->
                    val description = selectionState(row.visible)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(garmentArt(row.type)),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(36.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = typeLabelPlural(row.type),
                                style = MaterialTheme.typography.titleSmall,
                                color = WardrobePalette.TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.settings_items_fmt, row.itemCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = WardrobePalette.TextTertiary
                            )
                        }
                        Switch(
                            checked = row.visible,
                            onCheckedChange = { settingsViewModel.toggleCategory(row.type, it) },
                            colors = switchColors(),
                            modifier = Modifier.semantics { stateDescription = description }
                        )
                    }
                }

                SectionHeader(title = stringResource(R.string.settings_tags))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.tags.take(3).forEach { row ->
                        TagToggle(
                            label = tagLabel(row.tag),
                            visible = row.visible,
                            onToggle = { settingsViewModel.toggleTag(row.tag, !row.visible) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.tags.drop(3).forEach { row ->
                        TagToggle(
                            label = tagLabel(row.tag),
                            visible = row.visible,
                            onToggle = { settingsViewModel.toggleTag(row.tag, !row.visible) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SectionHeader(title = stringResource(R.string.settings_planning))
                StepperRow(
                    label = stringResource(R.string.settings_week_start),
                    value = if (state.weekStartsMonday) {
                        stringResource(R.string.settings_week_monday)
                    } else {
                        stringResource(R.string.settings_week_sunday)
                    },
                    onMinus = { settingsViewModel.setWeekStart(!state.weekStartsMonday) },
                    onPlus = { settingsViewModel.setWeekStart(!state.weekStartsMonday) }
                )
                val suggestDescription = selectionState(state.suggestLeastWorn)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_suggest),
                        style = MaterialTheme.typography.titleSmall,
                        color = WardrobePalette.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.suggestLeastWorn,
                        onCheckedChange = { settingsViewModel.toggleSuggestLeastWorn(it) },
                        colors = switchColors(),
                        modifier = Modifier.semantics { stateDescription = suggestDescription }
                    )
                }

                SectionHeader(title = stringResource(R.string.settings_data))
                SecondaryButton(
                    text = stringResource(R.string.cta_reset_sample),
                    onClick = { settingsViewModel.askReset() },
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_clear_all),
                    onClick = { settingsViewModel.askClear() },
                    enabled = state.itemCount > 0 || state.outfitCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )

                SectionHeader(title = stringResource(R.string.settings_about))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = WardrobePalette.TextPrimary
                )
                Text(
                    text = stringResource(R.string.about_version),
                    style = MaterialTheme.typography.labelSmall,
                    color = WardrobePalette.TextTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = stringResource(R.string.about_offline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WardrobePalette.TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
                )
            }
        }
    }

    if (state.dialog != SettingsDialog.NONE) {
        val reset = state.dialog == SettingsDialog.RESET
        ConfirmDialog(
            title = if (reset) {
                stringResource(R.string.confirm_reset_title)
            } else {
                stringResource(R.string.confirm_clear_title)
            },
            message = if (reset) {
                stringResource(R.string.confirm_reset_message)
            } else {
                stringResource(R.string.confirm_clear_message)
            },
            confirmLabel = if (reset) {
                stringResource(R.string.cta_reset_sample)
            } else {
                stringResource(R.string.cta_clear_all)
            },
            onConfirm = { settingsViewModel.confirmDialog() },
            onDismiss = { settingsViewModel.dismissDialog() }
        )
    }
}

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = WardrobePalette.Surface,
    checkedTrackColor = WardrobePalette.Primary,
    checkedBorderColor = WardrobePalette.Primary,
    uncheckedThumbColor = WardrobePalette.TextTertiary,
    uncheckedTrackColor = WardrobePalette.SurfaceVariant,
    uncheckedBorderColor = WardrobePalette.Hairline
)

@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HairlineRule(modifier = Modifier.padding(top = 20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = WardrobePalette.Primary,
            modifier = Modifier.padding(top = 14.dp, bottom = 10.dp)
        )
    }
}

@Composable
private fun TagToggle(
    label: String,
    visible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = selectionState(visible)
    Row(
        modifier = modifier
            .height(48.dp)
            .background(
                if (visible) WardrobePalette.Surface else WardrobePalette.SurfaceVariant,
                RoundedCornerShape(14.dp)
            )
            .semantics { stateDescription = description }
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (visible) WardrobePalette.TextPrimary else WardrobePalette.TextTertiary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToggle, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = if (visible) WardrobePalette.Accent else WardrobePalette.Hairline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
