package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy") : String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat() : String {
    val pattern = if (isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date) : Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND) : Date {
    var time = this.time

    time += when(units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val timeDiff = abs(this.time - date.time)
    val isFuture = this.time > date.time
    val text =  when {
        (timeDiff >= 0L) and (timeDiff <= SECOND) -> "только что"
        (timeDiff > SECOND) and (timeDiff <= 45 * SECOND) -> "несколько секунд"
        (timeDiff > 45 * SECOND) and (timeDiff <= 75 * SECOND) -> "минуту"
        (timeDiff > 75 * SECOND) and (timeDiff <= 45 * MINUTE) -> TimeUnits.MINUTE.plural(timeDiff / MINUTE)
        (timeDiff > 45 * MINUTE) and (timeDiff <= 75 * MINUTE) -> "час"
        (timeDiff > 75 * MINUTE) and (timeDiff <= 22 * HOUR) -> TimeUnits.HOUR.plural(timeDiff / HOUR)
        (timeDiff > 22 * HOUR) and (timeDiff <= 26 * HOUR) -> "день"
        (timeDiff > 26 * HOUR) and (timeDiff <= 360 * DAY) -> TimeUnits.DAY.plural(timeDiff / DAY)
        else -> ""
    }
    return when(text) {
        "только что" -> text
        "" -> if (isFuture) "более чем через год" else "более года назад"
        else -> if (isFuture) "через $text" else "$text назад"
    }
}

private fun humanizeText(amount: Long, type: TimeUnits) : String{
    when (type) {
        TimeUnits.MINUTE -> return when {
            (amount % 10 == 1L) and (amount != 11L) -> "$amount минуту"
            (amount % 10 in 2L..4L) and (amount !in 12L..14L) -> "$amount минуты"
            else -> "$amount минут"
        }
        TimeUnits.HOUR -> return when {
            (amount % 10 == 1L) and (amount != 11L) -> "$amount час"
            (amount % 10 in 2L..4L) and (amount !in 12L..14L) -> "$amount часа"
            else -> "$amount часов"
        }
        else -> return if (amount <= 100)
            when {
                (amount % 10 == 1L) and (amount != 11L) -> "$amount день"
                (amount % 10 in 2L..4L) and (amount !in 12L..14L) -> "$amount дня"
                else -> "$amount дней"
            }
        else
            when {
                (amount % 10 == 1L) and (amount % 100 != 11L) -> "$amount день"
                (amount % 10 in 2L..4L) and (amount % 100 !in 12L..14L) -> "$amount дня"
                else -> "$amount дней"
            }
    }
}

enum class TimeUnits {
    SECOND {
        override fun plural(amount: Long) : String {
            return when {
                    (amount % 10 == 1L) and (amount % 100 != 11L) -> "$amount секунду"
                    (amount % 10 in 2L..4L) and (amount % 100 !in 12L..14L) -> "$amount секунды"
                    else -> "$amount секунд"
                }
        }
    },
    MINUTE {
        override fun plural(amount: Long) : String {
            return when {
                (amount % 10 == 1L) and (amount % 100 != 11L) -> "$amount минуту"
                (amount % 10 in 2L..4L) and (amount % 100 !in 12L..14L) -> "$amount минуты"
                else -> "$amount минут"
            }
        }
    },
    HOUR {
        override fun plural(amount: Long) : String {
            return when {
                (amount % 10 == 1L) and (amount % 100 != 11L) -> "$amount час"
                (amount % 10 in 2L..4L) and (amount % 100 !in 12L..14L) -> "$amount часа"
                else -> "$amount часов"
            }
        }
    },
    DAY {
        override fun plural(amount: Long) : String {
            return when {
                (amount % 10 == 1L) and (amount % 100 != 11L) -> "$amount день"
                (amount % 10 in 2L..4L) and (amount % 100 !in 12L..14L) -> "$amount дня"
                else -> "$amount дней"
            }
        }
    };

    abstract fun plural(amount: Long) : String
}