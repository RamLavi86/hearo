package com.hearo.core

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScaleAndAudiogramTest {
    @Test fun `clark boundaries`() {
        assertEquals(DegreeOfLoss.NORMAL, ClarkScale.classify(15.0))
        assertEquals(DegreeOfLoss.SLIGHT, ClarkScale.classify(16.0))
        assertEquals(DegreeOfLoss.MODERATE, ClarkScale.classify(55.0))
        assertEquals(DegreeOfLoss.PROFOUND, ClarkScale.classify(91.0))
    }

    @Test fun `who boundaries`() {
        assertEquals(DegreeOfLoss.NORMAL, Who2021Scale.classify(19.9))
        assertEquals(DegreeOfLoss.MILD, Who2021Scale.classify(20.0))
        assertEquals(DegreeOfLoss.COMPLETE, Who2021Scale.classify(95.0))
    }

    @Test fun `pta needs all four frequencies`() {
        var a = Audiogram()
        for (f in listOf(500, 1000, 2000)) a = a.with(Ear.LEFT, f, ThresholdResult.Threshold(20))
        assertNull(a.pta(Ear.LEFT))
        a = a.with(Ear.LEFT, 4000, ThresholdResult.Threshold(60))
        assertEquals(30.0, a.pta(Ear.LEFT))
    }

    @Test fun `no response counts as max plus 5 in pta`() {
        var a = Audiogram()
        for (f in listOf(500, 1000, 2000)) a = a.with(Ear.RIGHT, f, ThresholdResult.Threshold(0))
        a = a.with(Ear.RIGHT, 4000, ThresholdResult.NoResponse(90))
        assertEquals(23.75, a.pta(Ear.RIGHT))
    }

    @Test fun `tone has requested rms and silent ramps`() {
        val tone = ToneGenerator.generate(1000, 1000, levelDbFs = -20.0)
        val peak = tone.maxOf { abs(it) }
        assertTrue(abs(peak - 0.1f) < 0.005f, "peak=$peak")
        val rms = sqrt(tone.sumOf { (it * it).toDouble() } / tone.size)
        assertTrue(abs(rms - 0.1 / sqrt(2.0)) < 0.003, "rms=$rms")
        assertEquals(0f, tone[0])
        assertTrue(abs(tone.last()) < 0.001f)
    }

    @Test fun `level mapper never exceeds the safety ceiling`() {
        val m = RelativeLevelMapper(dbFsAtZero = -80.0, maxDbFs = -10.0)
        assertEquals(-40.0, m.dbFs(Ear.LEFT, 1000, 40))
        assertEquals(-10.0, m.dbFs(Ear.LEFT, 1000, 120))
    }
}
