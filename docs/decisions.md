# Product decisions (2026-11-07)

Source: product owner, in session. See docs/research.md for background.

## Audience and positioning
- Phase 1: small group of testers. Phase 2: general public.
- Long-term goal: medical providers may adopt it as a pre-screening / triage aid
  ("how urgent is a clinic hearing test"). Positioned as screening, not diagnosis.

## Platform and hardware
- Android only for now. No iOS.
- Any wired or Bluetooth headphones (including ANC models) must be allowed.
- Consequence: no absolute calibration is possible for arbitrary headphones, so
  results from uncalibrated devices must be labelled approximate / lower confidence.
  Device type is detectable via AudioDeviceInfo (wired, USB, BT A2DP/SCO).

## Calibration
- No coupler / sound level meter and no normal-hearing volunteers available now.
- Open: how to get a defensible dB HL mapping later (per-model table, partner clinic,
  volunteer study). Until then DIN (SNR-based) is the most reliable metric.

## Speech material
- Record our own words; generated with a speech model for now.
- Caveat: TTS audio is not clinically validated. Treat as prototype material, flag it
  in results, and replace with a human talker recording before any clinical claim.
- Word lists: English and Hebrew; Hebrew list source/licensing still open.

## Scoring
- Fully automatic: user repeats the word aloud, app decides correct/incorrect (ASR).
- Ambiguous recognitions are scored conservatively and logged for tuning.

## Test length
- Default about 20 minutes, full option about 30 minutes.

## Data
- Local storage only.

## Report
- Output resembling the Clalit audiogram PDF (audiogram chart, speech table,
  summary text, recommendation), bilingual EN/HE, exportable as PDF.
- Classification scale: Clalit's scale is not stated in the sample report; the
  classification words used there are standard. Default to Clark/ASHA and make the
  scale configurable. Confirm with Clalit if possible.
