package com.hearo.core

class Audiogram(private val results: Map<Pair<Ear, Int>, ThresholdResult> = emptyMap()) {
    fun with(ear: Ear, freqHz: Int, result: ThresholdResult) =
        Audiogram(results + ((ear to freqHz) to result))

    operator fun get(ear: Ear, freqHz: Int): ThresholdResult? = results[ear to freqHz]

    /** Numeric level for averaging; a no-response counts as max level + 5. */
    fun levelOrNull(ear: Ear, freqHz: Int): Double? = when (val r = get(ear, freqHz)) {
        is ThresholdResult.Threshold -> r.level.toDouble()
        is ThresholdResult.NoResponse -> r.maxLevel + 5.0
        is ThresholdResult.Inconclusive -> r.bestGuess?.toDouble()
        null -> null
    }

    /** Pure-tone average; null if any of the frequencies is missing. */
    fun pta(ear: Ear, freqs: List<Int> = listOf(500, 1000, 2000, 4000)): Double? {
        val levels = freqs.map { levelOrNull(ear, it) ?: return null }
        return levels.average()
    }
}
