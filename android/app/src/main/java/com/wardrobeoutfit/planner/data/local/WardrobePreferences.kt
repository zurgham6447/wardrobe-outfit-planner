package com.wardrobeoutfit.planner.data.local

import android.content.Context
import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.model.Outfit
import com.wardrobeoutfit.planner.domain.model.WardrobeSettings

class WardrobePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun readItems(): List<ClothingItem> =
        records(prefs.getString(KEY_ITEMS, "").orEmpty()).mapNotNull { decodeItem(it) }

    fun writeItems(items: List<ClothingItem>) {
        prefs.edit().putString(KEY_ITEMS, items.joinToString(RECORD) { encodeItem(it) }).apply()
    }

    fun readOutfits(): List<Outfit> =
        records(prefs.getString(KEY_OUTFITS, "").orEmpty()).mapNotNull { decodeOutfit(it) }

    fun writeOutfits(outfits: List<Outfit>) {
        prefs.edit().putString(KEY_OUTFITS, outfits.joinToString(RECORD) { encodeOutfit(it) }).apply()
    }

    fun readCalendar(): List<CalendarEntry> =
        records(prefs.getString(KEY_CALENDAR, "").orEmpty()).mapNotNull { decodeEntry(it) }

    fun writeCalendar(entries: List<CalendarEntry>) {
        prefs.edit().putString(KEY_CALENDAR, entries.joinToString(RECORD) { encodeEntry(it) }).apply()
    }

    fun readDraft(): Map<ClothingType, String> {
        val raw = prefs.getString(KEY_DRAFT, "").orEmpty()
        val result = LinkedHashMap<ClothingType, String>()
        records(raw).forEach { record ->
            val parts = record.split(FIELD)
            if (parts.size == 2) {
                val type = enumOrNull(ClothingType.entries, parts[0])
                if (type != null && parts[1].isNotBlank()) result[type] = parts[1]
            }
        }
        return result
    }

    fun writeDraft(slots: Map<ClothingType, String>) {
        val raw = slots.entries.joinToString(RECORD) { it.key.name + FIELD + it.value }
        prefs.edit().putString(KEY_DRAFT, raw).apply()
    }

    fun readSettings(): WardrobeSettings = WardrobeSettings(
        hiddenTypes = csv(prefs.getString(KEY_HIDDEN_TYPES, "").orEmpty())
            .mapNotNull { enumOrNull(ClothingType.entries, it) }.toSet(),
        hiddenTags = csv(prefs.getString(KEY_HIDDEN_TAGS, "").orEmpty())
            .mapNotNull { enumOrNull(OccasionTag.entries, it) }.toSet(),
        weekStartsMonday = prefs.getBoolean(KEY_WEEK_START, true),
        suggestLeastWorn = prefs.getBoolean(KEY_SUGGEST, true)
    )

    fun writeSettings(settings: WardrobeSettings) {
        prefs.edit()
            .putString(KEY_HIDDEN_TYPES, settings.hiddenTypes.joinToString(",") { it.name })
            .putString(KEY_HIDDEN_TAGS, settings.hiddenTags.joinToString(",") { it.name })
            .putBoolean(KEY_WEEK_START, settings.weekStartsMonday)
            .putBoolean(KEY_SUGGEST, settings.suggestLeastWorn)
            .apply()
    }

    fun wipe() {
        prefs.edit().clear().apply()
    }

    private fun records(raw: String): List<String> =
        if (raw.isBlank()) emptyList() else raw.split(RECORD).filter { it.isNotBlank() }

    private fun csv(raw: String): List<String> =
        if (raw.isBlank()) emptyList() else raw.split(",").filter { it.isNotBlank() }

    private fun <T : Enum<T>> enumOrNull(values: List<T>, name: String): T? =
        values.firstOrNull { it.name == name }

    private fun encodeItem(item: ClothingItem): String = listOf(
        item.id,
        item.name,
        item.type.name,
        item.color.name,
        item.tags.joinToString(",") { it.name },
        item.timesWorn.toString(),
        item.lastWornEpochDay.toString()
    ).joinToString(FIELD)

    private fun decodeItem(record: String): ClothingItem? {
        val parts = record.split(FIELD)
        if (parts.size < 7) return null
        val type = enumOrNull(ClothingType.entries, parts[2]) ?: return null
        val color = enumOrNull(ItemColor.entries, parts[3]) ?: return null
        return ClothingItem(
            id = parts[0],
            name = parts[1],
            type = type,
            color = color,
            tags = csv(parts[4]).mapNotNull { enumOrNull(OccasionTag.entries, it) }.toSet(),
            timesWorn = parts[5].toIntOrNull() ?: 0,
            lastWornEpochDay = parts[6].toLongOrNull() ?: 0L
        )
    }

    private fun encodeOutfit(outfit: Outfit): String = listOf(
        outfit.id,
        outfit.name,
        outfit.itemIds.joinToString(","),
        outfit.createdEpochDay.toString()
    ).joinToString(FIELD)

    private fun decodeOutfit(record: String): Outfit? {
        val parts = record.split(FIELD)
        if (parts.size < 4) return null
        return Outfit(
            id = parts[0],
            name = parts[1],
            itemIds = csv(parts[2]),
            createdEpochDay = parts[3].toLongOrNull() ?: 0L
        )
    }

    private fun encodeEntry(entry: CalendarEntry): String =
        entry.dayEpoch.toString() + FIELD + entry.outfitId

    private fun decodeEntry(record: String): CalendarEntry? {
        val parts = record.split(FIELD)
        if (parts.size < 2) return null
        val day = parts[0].toLongOrNull() ?: return null
        return CalendarEntry(dayEpoch = day, outfitId = parts[1])
    }

    private companion object {
        const val FILE_NAME = "wardrobe_prefs"
        const val FIELD = "\u001F"
        const val RECORD = "\u001E"
        const val KEY_SEEDED = "seeded"
        const val KEY_ITEMS = "items"
        const val KEY_OUTFITS = "outfits"
        const val KEY_CALENDAR = "calendar"
        const val KEY_DRAFT = "draft_look"
        const val KEY_HIDDEN_TYPES = "settings_hidden_types"
        const val KEY_HIDDEN_TAGS = "settings_hidden_tags"
        const val KEY_WEEK_START = "week_start"
        const val KEY_SUGGEST = "suggest_least_worn"
    }
}
