package com.hearo.core

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

/**
 * Maps a test level to digital level. The mapping is a placeholder until the calibration
 * research is implemented: [dbFsAtZero] is dBFS corresponding to level 0 and is only
 * meaningful per headphone/device.
 */
fun interface LevelMapper {
    fun dbFs(ear: Ear, freqHz: Int, level: Int): Double
}

class RelativeLevelMapper(
    private val dbFsAtZero: Double = -80.0,
    /** Hard safety ceiling so a bug can never output a loud tone. */
    private val maxDbFs: Double = -10.0,
) : LevelMapper {
    override fun dbFs(ear: Ear, freqHz: Int, level: Int) = (dbFsAtZero + level).coerceAtMost(maxDbFs)
}

object ToneGenerator {
    /** Mono float PCM in [-1, 1]: a sine at [levelDbFs] (peak) with raised-cosine on/off ramps. */
    fun generate(
        freqHz: Int,
        durationMs: Int,
        levelDbFs: Double,
        sampleRate: Int = 48_000,
        rampMs: Int = 20,
    ): FloatArray {
        val n = sampleRate * durationMs / 1000
        val ramp = (sampleRate * rampMs / 1000).coerceAtMost(n / 2)
        val amp = 10.0.pow(levelDbFs / 20.0)
        return FloatArray(n) { i ->
            val env = when {
                i < ramp -> 0.5 * (1 - kotlin.math.cos(PI * i / ramp))
                i >= n - ramp -> 0.5 * (1 - kotlin.math.cos(PI * (n - 1 - i) / ramp))
                else -> 1.0
            }
            (amp * env * sin(2 * PI * freqHz * i / sampleRate)).toFloat()
        }
    }
}
