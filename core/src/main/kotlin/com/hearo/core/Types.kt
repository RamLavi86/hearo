package com.hearo.core

enum class Ear { LEFT, RIGHT }

/** Test frequencies in Hz, in the order they appear on the audiogram. */
val STANDARD_FREQUENCIES: List<Int> = listOf(250, 500, 1000, 2000, 3000, 4000, 6000, 8000)

/**
 * Result of a threshold search at one ear/frequency.
 * Levels are in dB relative to the app's reference (see [LevelMapper]); they are only
 * dB HL if the playback chain is calibrated.
 */
sealed interface ThresholdResult {
    data class Threshold(val level: Int) : ThresholdResult
    /** No response even at the maximum level (twice). */
    data class NoResponse(val maxLevel: Int) : ThresholdResult
    /** Search did not converge (inconsistent responses); [bestGuess] is the lowest level heard, if any. */
    data class Inconclusive(val bestGuess: Int?) : ThresholdResult
}
