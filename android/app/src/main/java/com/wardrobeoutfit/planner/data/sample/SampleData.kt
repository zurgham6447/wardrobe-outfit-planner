package com.wardrobeoutfit.planner.data.sample

import com.wardrobeoutfit.planner.domain.model.CalendarEntry
import com.wardrobeoutfit.planner.domain.model.ClothingItem
import com.wardrobeoutfit.planner.domain.model.ClothingType
import com.wardrobeoutfit.planner.domain.model.ItemColor
import com.wardrobeoutfit.planner.domain.model.OccasionTag
import com.wardrobeoutfit.planner.domain.model.Outfit

object SampleData {

    const val OXFORD = "navy_oxford_shirt"
    const val LINEN_TEE = "white_linen_tee"
    const val WOOL_TROUSERS = "charcoal_wool_trousers"
    const val CHINO_SHORTS = "stone_chino_shorts"
    const val CARDIGAN = "lavender_knit_cardigan"
    const val RAIN_SHELL = "navy_rain_shell"
    const val LOAFERS = "tan_leather_loafers"
    const val SNEAKERS = "white_canvas_sneakers"
    const val SCARF = "amber_silk_scarf"
    const val TOTE = "charcoal_canvas_tote"

    fun items(todayEpochDay: Long): List<ClothingItem> = listOf(
        ClothingItem(
            id = OXFORD,
            name = "Navy Oxford Shirt",
            type = ClothingType.TOP,
            color = ItemColor.NAVY,
            tags = setOf(OccasionTag.WORK),
            timesWorn = 6,
            lastWornEpochDay = todayEpochDay - 3
        ),
        ClothingItem(
            id = LINEN_TEE,
            name = "White Linen Tee",
            type = ClothingType.TOP,
            color = ItemColor.CLOUD,
            tags = setOf(OccasionTag.CASUAL, OccasionTag.WARM),
            timesWorn = 2,
            lastWornEpochDay = todayEpochDay - 11
        ),
        ClothingItem(
            id = WOOL_TROUSERS,
            name = "Charcoal Wool Trousers",
            type = ClothingType.BOTTOM,
            color = ItemColor.CHARCOAL,
            tags = setOf(OccasionTag.WORK, OccasionTag.COLD),
            timesWorn = 5,
            lastWornEpochDay = todayEpochDay - 4
        ),
        ClothingItem(
            id = CHINO_SHORTS,
            name = "Stone Chino Shorts",
            type = ClothingType.BOTTOM,
            color = ItemColor.IVORY,
            tags = setOf(OccasionTag.WARM, OccasionTag.CASUAL),
            timesWorn = 1,
            lastWornEpochDay = todayEpochDay - 26
        ),
        ClothingItem(
            id = CARDIGAN,
            name = "Lavender Knit Cardigan",
            type = ClothingType.OUTERWEAR,
            color = ItemColor.LAVENDER,
            tags = setOf(OccasionTag.COLD, OccasionTag.CASUAL),
            timesWorn = 3,
            lastWornEpochDay = todayEpochDay - 8
        ),
        ClothingItem(
            id = RAIN_SHELL,
            name = "Navy Rain Shell",
            type = ClothingType.OUTERWEAR,
            color = ItemColor.NAVY,
            tags = setOf(OccasionTag.RAIN),
            timesWorn = 0,
            lastWornEpochDay = 0L
        ),
        ClothingItem(
            id = LOAFERS,
            name = "Tan Leather Loafers",
            type = ClothingType.SHOES,
            color = ItemColor.AMBER,
            tags = setOf(OccasionTag.WORK, OccasionTag.EVENING),
            timesWorn = 4,
            lastWornEpochDay = todayEpochDay - 3
        ),
        ClothingItem(
            id = SNEAKERS,
            name = "White Canvas Sneakers",
            type = ClothingType.SHOES,
            color = ItemColor.CLOUD,
            tags = setOf(OccasionTag.CASUAL),
            timesWorn = 2,
            lastWornEpochDay = todayEpochDay - 9
        ),
        ClothingItem(
            id = SCARF,
            name = "Amber Silk Scarf",
            type = ClothingType.ACCESSORY,
            color = ItemColor.AMBER,
            tags = setOf(OccasionTag.EVENING),
            timesWorn = 1,
            lastWornEpochDay = todayEpochDay - 17
        ),
        ClothingItem(
            id = TOTE,
            name = "Charcoal Canvas Tote",
            type = ClothingType.ACCESSORY,
            color = ItemColor.CHARCOAL,
            tags = setOf(OccasionTag.WORK),
            timesWorn = 7,
            lastWornEpochDay = todayEpochDay - 2
        )
    )

    fun outfits(todayEpochDay: Long): List<Outfit> = listOf(
        Outfit(
            id = "look_monday_office",
            name = "MONDAY OFFICE",
            itemIds = listOf(OXFORD, WOOL_TROUSERS, LOAFERS, TOTE),
            createdEpochDay = todayEpochDay - 12
        ),
        Outfit(
            id = "look_weekend_light",
            name = "WEEKEND LIGHT",
            itemIds = listOf(LINEN_TEE, CHINO_SHORTS, SNEAKERS),
            createdEpochDay = todayEpochDay - 7
        ),
        Outfit(
            id = "look_evening_out",
            name = "EVENING OUT",
            itemIds = listOf(OXFORD, WOOL_TROUSERS, LOAFERS, SCARF),
            createdEpochDay = todayEpochDay - 2
        )
    )

    fun calendar(todayEpochDay: Long): List<CalendarEntry> = listOf(
        CalendarEntry(todayEpochDay, "look_monday_office"),
        CalendarEntry(todayEpochDay + 1, "look_weekend_light"),
        CalendarEntry(todayEpochDay + 3, "look_evening_out"),
        CalendarEntry(todayEpochDay + 5, "look_monday_office")
    )
}
