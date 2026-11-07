package com.hearo.core

/**
 * Modified Hughson-Westlake threshold search (ASHA 2005 / ISO 8253-1 style):
 * down 10 dB after a response, up 5 dB after a miss, threshold = lowest level with
 * responses on 2 ascending presentations.
 *
 * Pure state machine: ask [currentLevel], play the tone, call [report], repeat until [result] != null.
 * Catch trials, timing and the audio itself belong to the caller.
 */
class ThresholdSearch(
    startLevel: Int = 40,
    private val minLevel: Int = -10,
    private val maxLevel: Int = 90,
    private val maxTrials: Int = 40,
) {
    var currentLevel: Int = startLevel.coerceIn(minLevel, maxLevel)
        private set
    var result: ThresholdResult? = null
        private set
    var trials: Int = 0
        private set

    private var familiarizing = true
    private var lastWasHeard = false
    private var maxMisses = 0
    private val ascendingHits = mutableMapOf<Int, Int>()

    fun report(heard: Boolean) {
        check(result == null) { "search already finished" }
        trials++

        if (familiarizing) {
            if (heard) {
                familiarizing = false
                lastWasHeard = true
                currentLevel = (currentLevel - 10).coerceAtLeast(minLevel)
            } else {
                if (currentLevel >= maxLevel && ++maxMisses >= 2) {
                    result = ThresholdResult.NoResponse(maxLevel); return
                }
                currentLevel = (currentLevel + 10).coerceAtMost(maxLevel)
            }
        } else if (heard) {
            // A response on an ascending presentation counts; at the floor there is nowhere
            // lower to go, so a response there always counts.
            if (!lastWasHeard || currentLevel == minLevel) {
                val hits = (ascendingHits[currentLevel] ?: 0) + 1
                ascendingHits[currentLevel] = hits
                if (hits >= 2) { result = ThresholdResult.Threshold(currentLevel); return }
            }
            lastWasHeard = true
            currentLevel = (currentLevel - 10).coerceAtLeast(minLevel)
        } else {
            lastWasHeard = false
            if (currentLevel >= maxLevel && ++maxMisses >= 2) {
                result = ThresholdResult.NoResponse(maxLevel); return
            }
            currentLevel = (currentLevel + 5).coerceAtMost(maxLevel)
        }

        if (trials >= maxTrials) {
            result = ThresholdResult.Inconclusive(ascendingHits.keys.minOrNull())
        }
    }
}
