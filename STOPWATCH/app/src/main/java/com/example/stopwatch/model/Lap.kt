package com.example.stopwatch.model

data class Lap(
    val id: Int,
    val lapNumber: Int,
    val lapTimeMs: Long,
    val totalTimeMs: Long
)
