package com.vicherarr.locgps.presentation.components

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())

fun Instant.formatAsReadable(): String = dateTimeFormatter.format(this)
