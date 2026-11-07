package com.hearo.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ThresholdSearchTest {
    private fun run(trueThreshold: Int, start: Int = 40): ThresholdSearch {
        val s = ThresholdSearch(startLevel = start)
        while (s.result == null) s.report(heard = s.currentLevel >= trueThreshold)
        return s
    }

    @Test fun `finds threshold on the 5 dB grid above the true value`() {
        assertEquals(ThresholdResult.Threshold(30), run(27).result)
        assertEquals(ThresholdResult.Threshold(30), run(30).result)
        assertEquals(ThresholdResult.Threshold(0), run(0).result)
        assertEquals(ThresholdResult.Threshold(60), run(58).result)
    }

    @Test fun `normal hearing converges at the floor`() {
        assertEquals(ThresholdResult.Threshold(-10), run(-20).result)
    }

    @Test fun `no response at max level`() {
        val s = run(200)
        assertEquals(ThresholdResult.NoResponse(90), s.result)
    }

    @Test fun `converges in a reasonable number of trials`() {
        for (t in -10..85 step 5) assert(run(t).trials <= 25) { "threshold $t took ${run(t).trials}" }
    }

    @Test fun `trial cap ends the search as inconclusive`() {
        val s = ThresholdSearch(maxTrials = 5)
        while (s.result == null) s.report(heard = s.currentLevel >= 27)
        assertIs<ThresholdResult.Inconclusive>(s.result)
        assertEquals(5, s.trials)
    }

    @Test fun `result is null before any report`() {
        assertNull(ThresholdSearch().result)
    }
}
