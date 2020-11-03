package de.f0x.lokales

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.*

class LokaleFormatter(locale: Locale) {
    private val numFormat = NumberFormat.getNumberInstance(locale)
    private val intFormat = NumberFormat.getIntegerInstance(locale)
    private val percentFormat = NumberFormat.getPercentInstance(locale)
    private val currencyFormat = NumberFormat.getCurrencyInstance(locale)

    private val dateFormats: List<DateTimeFormatter> = FormatStyle.values().map {
        DateTimeFormatter.ofLocalizedDate(it).withLocale(locale)
    }
    private val timeFormats: List<DateTimeFormatter> = FormatStyle.values().map {
        DateTimeFormatter.ofLocalizedTime(it).withLocale(locale)
    }
    private val dateTimeFormats: List<DateTimeFormatter> = FormatStyle.values().map {
        DateTimeFormatter.ofLocalizedDateTime(it).withLocale(locale)
    }

    fun num(value: Number): String =
        synchronized(numFormat) {
            numFormat.format(value)
        }

    fun int(value: Number): String =
        synchronized(intFormat) {
            intFormat.format(value)
        }

    fun percent(value: Number): String =
        synchronized(percentFormat) {
            percentFormat.format(value)
        }

    fun currency(value: Number): String =
        synchronized(currencyFormat) {
            currencyFormat.format(value)
        }

    fun date(value: TemporalAccessor, style: FormatStyle = FormatStyle.SHORT): String =
        dateFormats[style.ordinal].format(value)

    fun time(value: TemporalAccessor, style: FormatStyle = FormatStyle.SHORT): String =
        timeFormats[style.ordinal].format(value)

    fun dateTime(value: TemporalAccessor, style: FormatStyle = FormatStyle.SHORT): String =
        dateTimeFormats[style.ordinal].format(value)
}