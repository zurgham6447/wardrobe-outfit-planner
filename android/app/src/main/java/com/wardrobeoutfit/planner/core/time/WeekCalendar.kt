package com.wardrobeoutfit.planner.core.time

import com.wardrobeoutfit.planner.core.config.GameConfig
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

object WeekCalendar {

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun weekDays(todayEpochDay: Long, startsMonday: Boolean): List<Long> {
        val today = LocalDate.ofEpochDay(todayEpochDay)
        val first = if (startsMonday) DayOfWeek.MONDAY else DayOfWeek.SUNDAY
        val offset = ((today.dayOfWeek.value - first.value) + 7) % 7
        val start = today.minusDays(offset.toLong())
        return (0 until GameConfig.WEEK_DAYS).map { start.plusDays(it.toLong()).toEpochDay() }
    }

    fun dayLetter(epochDay: Long): String =
        LocalDate.ofEpochDay(epochDay).dayOfWeek.name.take(1)

    fun dayShort(epochDay: Long): String =
        LocalDate.ofEpochDay(epochDay).dayOfWeek.name.take(3)

    fun dayNumber(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).dayOfMonth

    fun dayLabel(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.dayOfWeek.name.take(3) + " " + date.dayOfMonth
    }

    fun monthName(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).month.name

    fun weekNumber(epochDay: Long): Int {
        val fields = WeekFields.of(Locale.UK)
        return LocalDate.ofEpochDay(epochDay).get(fields.weekOfWeekBasedYear())
    }
}
