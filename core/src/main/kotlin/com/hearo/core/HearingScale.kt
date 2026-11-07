package com.hearo.core

enum class DegreeOfLoss { NORMAL, SLIGHT, MILD, MODERATE, MODERATELY_SEVERE, SEVERE, PROFOUND, COMPLETE }

/** A classification scale for a pure-tone average. Configurable because clinics differ. */
interface HearingScale {
    val name: String
    fun classify(ptaDb: Double): DegreeOfLoss
}

/** Clark (1981) / ASHA scale. Boundaries from standard references; not re-verified. */
object ClarkScale : HearingScale {
    override val name = "Clark/ASHA"
    override fun classify(ptaDb: Double) = when {
        ptaDb <= 15 -> DegreeOfLoss.NORMAL
        ptaDb <= 25 -> DegreeOfLoss.SLIGHT
        ptaDb <= 40 -> DegreeOfLoss.MILD
        ptaDb <= 55 -> DegreeOfLoss.MODERATE
        ptaDb <= 70 -> DegreeOfLoss.MODERATELY_SEVERE
        ptaDb <= 90 -> DegreeOfLoss.SEVERE
        else -> DegreeOfLoss.PROFOUND
    }
}

/** WHO 2021 grades (better-ear average of 0.5, 1, 2, 4 kHz). Boundaries not re-verified. */
object Who2021Scale : HearingScale {
    override val name = "WHO 2021"
    override fun classify(ptaDb: Double) = when {
        ptaDb < 20 -> DegreeOfLoss.NORMAL
        ptaDb < 35 -> DegreeOfLoss.MILD
        ptaDb < 50 -> DegreeOfLoss.MODERATE
        ptaDb < 65 -> DegreeOfLoss.MODERATELY_SEVERE
        ptaDb < 80 -> DegreeOfLoss.SEVERE
        ptaDb < 95 -> DegreeOfLoss.PROFOUND
        else -> DegreeOfLoss.COMPLETE
    }
}
