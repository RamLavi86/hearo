# Relative calibration for hearo: what an uncalibrated phone + any headphone can and cannot tell you

Prepared 2026-11-07. It builds on `hearo/docs/research.md` (cited below as **R§n**) and `hearo/docs/decisions.md`, and it does not repeat them.

**Tags**
- **[V]**: checked against the named source this session.
- **[V\*]**: checked only through search-engine snippets of that source, because full-text fetching was blocked (see below).
- **[K]**: standard knowledge that I did not re-check.
- **[I]**: my own inference or recommendation.

**Access limits this session.** WebFetch could not resolve most academic and vendor hosts:
- jmir.org / mhealth.jmir.org
- pmc.ncbi.nlm.nih.gov
- pubmed.ncbi.nlm.nih.gov
- frontiersin.org
- arxiv.org
- mimi.io
- source.android.com
- cdn.standards.iteh.ai

GitHub API access to AutoEq was also not enabled. Only developer.android.com could be fetched in full. So most paper numbers below are **[V\*]**: I saw them quoted from the paper's abstract or full text in search results, but did not read the paper. Before you rely on a number for a design decision, re-check it once those hosts are allowed.

---

## 0. Executive summary

1. **Model the error first [I].** Write the measured threshold in app units (dBFS at a fixed volume index) as

   `T_app(ear, f) = HL(ear, f) + RETSPL(f) − G0 − S(f) − B(ear, f) − F(ear, f) + ε`

   where:
   - `G0` = overall chain gain (phone volume, amplifier, headphone sensitivity). Unknown by roughly ±20–30 dB across arbitrary chains.
   - `S(f)` = the headphone model's frequency-response shape relative to an audiometric earphone.
   - `B` = left/right channel imbalance.
   - `F` = fit/seal of this placement.
   - `ε` = psychophysical test-retest error (about 5 dB SD per threshold [K]).

   Relative calibration can cancel `G0` and nothing else.
2. **Only two comparisons also cancel `S(f)` [I]:**
   - **Same frequency, other ear** (the interaural difference, IAD).
   - **Same frequency, same headphone, another day** (longitudinal change).

   Everything else is limited by the unknown headphone shape. That includes audiogram shape relative to your own best frequency, and loudness matching across frequencies. The shape error is plausibly ±5–15 dB at 4–8 kHz for an unknown consumer model [I, supported by [V\*] reports that even a known consumer model needed corrections at 6–8 kHz].
3. **What you can honestly claim without calibration:**
   - **(a) Ear difference**, which is strong. "Left ear is about X dB less sensitive than right at 2–4 kHz", given in ±dB bands. Strengthen it with an **ear-cup swap** step that separates headphone channel imbalance from true ear asymmetry.
   - **(b) Shape**, which is moderate and only for large effects. "High frequencies are clearly reduced relative to your mid frequencies" is safe only when the drop is at least about 20 dB, unless the headphone model's shape is known.
   - **(c) A speech-in-noise (DIN) category.** It is robust to level as long as presentation is above roughly 60 dB SPL [V\*].
   - **(d) Change since last test** on the same setup.
   - **Absolute dB HL, WHO grade and "PTA > 25 dB": cannot be claimed.**
   - **A flat, symmetric loss is invisible to relative tone measures.** Only DIN guards against it.
4. **Calibration procedures, ranked for an MVP [I]:**
   1. A self-referenced within-ear baseline (the person's best mid frequency), plus interaural difference, plus a swap check.
   2. DIN as an absolute-level-independent anchor and a gross-miscalibration catch.
   3. Volume set to a reproducible state (maximum, read back, `getStreamVolumeDb` logged) with a digital ceiling.
   4. The phone mic for ambient noise only.
   5. ISO 7029 age norms for plausibility flags only, never to adjust results.

   Later: per-model shape corrections, from crowd data or published measurement databases.
   Research-only: categorical loudness scaling. Xu & Kollmeier report a calibration-offset error of about 5 dB median absolute error, but in simulation [V\*].
   Not worth it: equal-loudness matching across frequencies, which is confounded by recruitment, and mic-loopback absolute calibration of the headphone.
5. **Triage still works in a relative mode:**
   - **History-based red flags** (sudden loss, unilateral loss with neurological signs).
   - **Interaural asymmetry rules**, with a stricter app threshold.
   - **Longitudinal change.**
   - **DIN categories.**

   Rules that need absolute levels (PTA > 25 dB HL, WHO grade, AAO-HNS's 30 dB in 3 frequencies) must be replaced by these. Sudden unilateral loss is shown as an emergency regardless of test results. NICE: refer to be seen within 24 h if the loss developed within the past 30 days [V\*].
6. **Regulation.** Being "uncalibrated / relative" does **not** reduce regulatory class. Intended purpose does. A referral-urgency output is a medical purpose in the EU (MDR Rule 11 → at least IIa) and probably in the US. Relative status only shrinks what you can truthfully claim.

---

## 1. What the literature says about uncalibrated or loosely calibrated self-testing

### 1.1 Quantified results (excluding those already in R§1.4 / R§2.2)

| Study / system | Setup | Key number | Tag |
|---|---|---|---|
| Masalski & Kręcicki 2013, web PC test at home | Home PCs, uncalibrated, then biologically calibrated | Home-test error about **7.28 dB**; "will not substitute" clinical PTA | [V\*] [JMIR 2013 e71](https://jmir.org/2013/4/e71) |
| Masalski 2014, 7 biological-calibration methods, n=25 | Normal-hearing reference listener | Simplest is self-adjusting to "barely audible". **Békésy per-frequency** has the lowest error | [V\*] [PMC3906690](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3906690/) |
| Masalski 2016, reference level per phone model | Android phone + bundled earphones | **Within-model SD 4.03 dB** (95% CI 3.93–4.11). Models differ significantly | [V\*] [PMC4906240](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4906240/) |
| Masalski 2018, n=70 | Model-specific biological calibration | Mobile minus PTA: **mean 2.6 dB, SD 8.3 dB** | [V\*] [mhealth.jmir.org 2018/1/e10](https://mhealth.jmir.org/2018/1/e10) |
| Review snippet (uncalibrated apps) | Various | Average difference **> 8 dB HL in a booth, > 14 dB HL in a waiting room** | [V\*] via [JMIR 2013 e71 context](https://jmir.org/2013/4/e71). The primary paper was not identified |
| Manchester iPad app + Sennheiser HD 300 (consumer) | Calibrated consumer headphone | Good agreement **except 6 and 8 kHz**, which needed adjusted calibration values | [V\*] [Manchester record](https://research.manchester.ac.uk/en/publications/validity-and-reliability-of-an-automatic-audiometry-application-f/) |
| DigiBel (web app), 30 adults, one ear plugged | iPad + Sennheiser HD 400S, lab + biologically calibrated | **73.1% within 10 dB**. Significant overestimation at 4 and 8 kHz. Sensitivity 100%, specificity 72.7% | [V\*] [JMIR Formative 2024 e51770](https://formative.jmir.org/2024/1/e51770) |
| Apple Hearing Test, AirPods Pro 2 (Kruger, Manchaiah, Swanepoel 2026) | Closed, end-to-end calibrated | **86.5% of 400 thresholds within 10 dB** of PTA | [V\*] [UP repository](https://repository.up.ac.za/items/29b0be23-c977-42ac-8b8a-87c1679b7102) |
| Extended-HF smartphone audiometry (2019, n=61) | Audiometric vs non-standard supra-aural | About 83–84% within 10 dB | [V\*] [PubMed 30461416](https://pubmed.ncbi.nlm.nih.gov/30461416/) |
| Booth-less audiometry (Swanepoel 2015) | Attenuating earphones + real-time noise monitoring | > 90% of thresholds within 5 dB of in-booth thresholds | [V\*] via [UP repository](https://repository.up.ac.za/bitstreams/3d848498-bf25-4806-bdfc-9f7deb554a3a/download) |
| hearWHO (DIN) | User's own headphones | Score bands **≥75 good / 50–74 "hearing loss risk" / <50 likely problem**. WHO: sensitivity and specificity > 85%. Independent study: 82% / 95%. WHO says accuracy depends on headphone type and ambient noise | [V\*] [WHO FAQ](https://www.who.int/news-room/questions-and-answers/item/frequently-asked-questions-about-hearing-checks-and-the-hearwho-app) |
| hearZA smartphone DIN (Potgieter 2016) | **5 headphone types** | Reliable on standard Android earphones or clinical headphones | [V\*] [UP 2016 thesis](https://repository.up.ac.za/bitstream/handle/2263/55987/Potgieter_Development_2016.pdf?sequence=1) |
| DIN presentation level | Normal hearing | Diotic and antiphasic DIN SRTs are **independent of presentation level above about 60 dB SPL**. Audibility matters for hearing loss; the effect of conductive loss is largely compensated by raising the level | [V\*] [IJA 2022, 10.1080/14992027.2022.2119611](https://informahealthcare.com/doi/full/10.1080/14992027.2022.2119611) |
| DIN vs PTA | Various | r ≈ 0.7–0.8. PTA explains about 54% of SRT variance. Antiphasic DIN tracks the **poorer** ear, diotic the **better** ear. High-pass vs low-pass filtered DIN is only weakly related to slope (R² 0.19–0.24) | [V\*] [PMC9010337 et al.](https://pmc.ncbi.nlm.nih.gov/articles/PMC9010337) |
| Bravard, Demany & Pressnitzer 2023 | 100 online participants, uncalibrated | Audibility in quiet "highly variable as overall level was uncalibrated". **Threshold-equalizing noise (TEN)** equalized thresholds from 125 Hz to 4 kHz | [V\*] [JASA-EL 10.1121/10.0019807](https://www.bordeaux-neurocampus.fr/?p=160866) |
| Xu & Kollmeier 2025/2026, calibration offset from categorical loudness scaling (CLS) | Simulation on the Oldenburg database | Bayesian regression: **MAE ≈ 5 dB** for the offset, r up to 0.81. About 2.2 dB better with 2 frequencies. "Validation with real uncalibrated devices still needed" | [V\*] [arXiv 2508.14824](https://arxiv.org/abs/2508.14824), [IJA 2026](https://www.tandfonline.com/doi/full/10.1080/14992027.2026.2663342) |
| Xu, Schell-Majoor, Kollmeier (rACALOS + GRaBr threshold), home smartphone | 15 normal-hearing listeners, home vs lab | ICC > 0.75, bias < 1 dB (v1 preprint) | [V\*] [Southampton eprint 509790](https://eprints.soton.ac.uk/509790) |
| Audiogram from **uncalibrated** ACALOS | ML classification | "Very imprecise" | [V\*] [arXiv 2512.04616](https://arxiv.org/pdf/2512.04616) |
| Mimi | Commercial | Calibrates by "objective measurements of headphone output levels, and validation through subjective tests", per headphone **and device** combination. Partnership with RTINGS for measurement data. FAQ says the CE certification may have lapsed | [V\*] [mimi.io FAQ](https://mimi.io/faq/hearing-test-app), [RTINGS press](https://mimi.io/press/mimi-hearing-technologies-and-rtings-announce-strategic-collaboration) |
| Headphone placement variability | Circumaural, 6 heads × 20 placements | SD **< 2.5 dB up to 10 kHz**, up to 9 dB above 10 kHz. Supra-aural thresholds variable around 6 kHz | [V\*] [HAL Brest](https://hal.univ-brest.fr/hal-00742225/document) |
| Insert/earbud seal leak | Measured earphones | A 0.5 mm leak cuts output by about **15 dB at 50 Hz**. Output at **500 Hz is largely independent of seal** | [V\*] [AES e-lib 18445](https://www.aes.org/e-lib/browse.cfm?elib=18445) |
| Left/right driver matching | Consumer | No primary data found. Forum and vendor claims are ±1 to ±3 dB. The audiocheck.net test advises **swapping the earpieces** to tell driver imbalance from ear imbalance | [V\*] low quality: [head-fi](https://www.head-fi.org/threads/how-closely-does-the-left-and-right-sides-of-headphones-need-to-be-matched.843431/post-13405917), [audiocheck](https://audiocheck.net/soundtests_headphones.php) |

**I found no peer-reviewed study that measures interaural-difference accuracy with uncalibrated consumer headphones.** This is a genuine evidence gap. The numbers in 1.2 are derived, not measured.

### 1.2 Which quantities are robust to an unknown headphone, and how robust [I]

These figures are built from the error model in §0, with ε ≈ 5 dB SD per threshold [K]. The SD of a difference of two thresholds is therefore about 7 dB.

| Quantity | Cancels | Remaining error sources | Expected 1-SD error (my estimate) |
|---|---|---|---|
| **Absolute threshold** (dB HL) | nothing | G0 (±20–30 dB), S(f), B, F, ε | Useless without calibration. Biological per-model calibration gives about 4–8 dB [V\*, Masalski] |
| **Interaural difference** at one frequency | G0, S(f) | B(f) (≈1–3 dB?), fit difference L vs R (over-ear < 2.5 dB; earbuds ≫ at ≤250 Hz), 2× ε | **About 7–8 dB** per frequency. **About 4–5 dB** averaged over 3–4 frequencies. Better again after the swap check |
| **Longitudinal change**, same phone, headphone, volume index | G0, S(f), B | refit F, 2× ε, OEM effects or firmware changes | About 7 dB per frequency. About 4–5 dB averaged |
| **Shape within an ear** (threshold at f minus own reference) | G0, B | S(f) − S(ref) for an unknown model, F, 2× ε | **About 8–15 dB at 4–8 kHz and at ≤250 Hz** (earbuds). About 7–9 dB at 0.5–2 kHz |
| Shape with known per-model S(f) | G0, B, most of S | unit-to-unit spread (≈4 dB [V\*, Masalski within-model]), F, ε | About 7–9 dB |
| **DIN SRT** | G0, S(f) (mostly) | audibility if presented too quietly, ambient noise, the talker or material not being normed (TTS!), learning | About 1–1.5 dB for repeated tests [K]. Maps to PTA only roughly (r 0.7–0.8) |

**Consequences [I]:**
- The interaural difference can support statements in **10 dB bands**: "about the same", "about 10–20 dB worse", "more than 20 dB worse".
- Shape can support **"clearly reduced high frequencies"** only if the relative drop is at least about 20–25 dB. Otherwise say "no clear pattern".
- In the owner's audiogram, the left minus right difference at 3–6 kHz is about 25–40 dB. **That is well within what relative mode should detect.** It is a good first validation case.

---

## 2. Relative-calibration procedures

### 2a. Self-referenced baseline (own reference frequency = 0)
- **Method [I].** Define the reference per ear as `R_e = min(T(e,500), T(e,1000), T(e,2000))`, or the median of these three. Do not use 1 kHz alone: one noisy point would shift the whole curve. Then report `Rel(e,f) = T(e,f) − R_e`.
  - Also define a **global** reference `R = min(R_L, R_R)`, so that the better ear's best region is "0". This keeps the interaural information on the same scale.
- **What it cancels:** G0 only.
- **Failure modes:**
  1. **Flat loss (one or both ears):** it looks normal. Relative tone mode cannot detect it [I]. DIN must cover this.
  2. **Loss at the reference** (low-frequency or cookie-bite loss, Ménière's): the high frequencies look *better* than the reference. Detect this when `Rel` is negative by more than 10 dB at some frequency. Then show "pattern atypical, see clinic", not a curve.
  3. **Low-frequency ambient noise** raises T(500) and therefore the reference. Use the mic check (§2d) and drop 500 Hz from the reference when the room is noisy.
- **Feasibility:** zero extra time, because it reuses the threshold run. **Accuracy:** see 1.2. **Burden:** none. **Verdict: MVP.**

### 2b. Loudness-based references (loudness balance, MCL/UCL, categorical loudness scaling)
- **Interaural loudness balance (ABLB-style).** Cancels G0 and S(f), but **recruitment** in sensorineural loss makes suprathreshold balance underestimate threshold asymmetry. That is the classic ABLB recruitment effect [K] ([Interacoustics ABLB](https://www.interacoustics.com/academy/audiometry-training/advanced-tests/alternate-binaural-loudness-balance-ablb-test) [V\*]). It also cannot tell headphone imbalance from ear asymmetry. **Use thresholds instead, plus the swap step (below).**
- **Equal-loudness matching across frequencies** (to estimate S(f) against ISO 226 contours).
  - For a normal-hearing listener it estimates S(f) − S(1k) plus individual loudness variability. Studies report the matching direction alone shifts results by more than 4 dB [V\*, [Forum Acusticum 2025](https://dael.euracoustics.org/confs/fa2025/data/articles/000756.pdf)].
  - **For a listener with hearing loss, the loss contaminates the estimate, so the method is circular** [I].
  - DIY tools (HearCal) say explicitly that the result is not an audiogram [V\*, [github.com/bzeiss/hearcal](https://github.com/bzeiss/hearcal)].
  - **Verdict: do not use it for calibration.**
- **MCL / "loud but OK" level.** Useful for **safety and headroom**, not for calibration.
  - Ask the user to set a comfortable level at 1 kHz, and a "loud but OK" level at 1 kHz and 4 kHz.
  - Their dBFS values bound the stimulus range (no tone above the "loud" level + 5 dB) and show whether the volume is too low (MCL near the digital ceiling).
  - About 1 min. **Verdict: MVP, for safety and headroom only.**
- **Categorical loudness scaling (ACALOS / ISO 16832 family).**
  - The best published route to estimating an unknown calibration offset (MAE ≈ 5 dB) [V\*]. However, the estimate is **simulation-only**, depends on a large reference database, and takes about 3–5 min per frequency per ear [K].
  - Uncalibrated ACALOS predicts the audiogram "very imprecisely" [V\*].
  - **Verdict: later or research. Watch for the Oldenburg group's real-device validation.**

### 2c. DIN as the absolute-level-independent anchor
- **Role 1: an independent outcome.** The DIN SRT does not depend on G0 or S(f), provided the presentation is loud enough. That is above about 60 dB SPL for normal hearing [V\*], and higher for people with hearing loss.
  - **Check audibility relatively [I]:** set DIN at the user's own comfortable level. Require the noise level (digital) to be at least about 30–35 dB above the user's measured tone thresholds at 0.5–2 kHz (digital). Otherwise warn "DIN may be audibility-limited".
- **Role 2: anchoring G0 [I].**
  - Predict better-ear PTA from the diotic DIN SRT with a published regression. Compare it with the better ear's relative PTA. The difference is an estimate of G0.
  - With r ≈ 0.7–0.8, the PTA prediction error is probably about 8–10 dB SD [I, from 54% variance explained, [V\*]]. **That is too coarse to display dB HL.** It is good enough to **catch gross miscalibration**, for example:
    - DIN normal but tones imply that everything is at the ceiling, which means volume or routing is broken; or
    - DIN clearly abnormal but tone thresholds sit at the digital floor, which means the volume is very high or the DIN materials or noise are wrong.
- **Caveat.** Decisions.md says digits and words are TTS for now. **A TTS DIN has no norms.** Both its category boundaries and any PTA regression would need your own norming. Until then, DIN can be used only for **within-person** comparisons (ears via antiphasic DIN, sessions over time), not as a category [I].
- **Duration:** 3–4 min per run. **Verdict: MVP** once a normed DIN exists. Until then, use it as a relative check only.
- **TEN idea (Bravard 2023).** Threshold-equalizing noise equalizes audibility across frequencies for **normal-hearing** listeners regardless of headphone gain [V\*]. However, the masked threshold of an impaired ear is compared against a noise whose real SPL still contains G0 + S(f). So TEN **does not** remove S(f) from loss estimates [I]. It is useful for suprathreshold tasks, not as a threshold calibration.

### 2d. Phone microphone as a measurement device
- **What Android guarantees.** If a device supports `UNPROCESSED`, CDD §5.11 requires:
  - an approximately flat response (**±10 dB from 100 Hz to 7 kHz**),
  - input sensitivity such that **94 dB SPL at 1 kHz gives −36 dBFS** (float),
  - SNR > 60 dB, and THD < 1%.

  Only a fixed gain is allowed (no AGC, no high-pass filter, no echo cancellation) [V\*, [CDD 5.11 mirror](https://android.googlesource.com/platform/compatibility/cdd/+/refs/tags/android-9.0.0_r9/5_multimedia/5_11_unprocessed-audio.md); check the current CDD]. The API doc only says "tuned for unprocessed (raw) sound **if available**, behaves like DEFAULT otherwise" [V, [AudioSource](https://developer.android.com/reference/android/media/MediaRecorder.AudioSource)]. Query `AudioManager.getProperty(PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED)` [K].
  - **So a supporting phone's mic is a ±several-dB sound level meter at 1 kHz and a ±10 dB one across 0.1–7 kHz** [I]. Built-in MEMS mics remain the weak point of smartphone sound level meters [V\*, [NIOSH](https://cdc.gov/niosh/blogs/2014/sound-app.html)].
  - Coarse field calibration exists: whistling over **standardized glass bottles** gives about 2–3 dB uncertainty [V\*, Scharf/Kollmeier, IJA 2024, [bohrium record](https://www.bohrium.com/paper-details/microphone-calibration-estimation-for-mobile-audiological-tests-with-resonating-bottles/1041591444994785283-9391)].
- **Headphone-on-mic "loopback" for absolute headphone level: not viable [I].**
  - The pressure in an ear cup pressed on a phone, or in an earbud held to a mic port, depends on the coupling volume and the leak. It is nothing like an ear simulator.
  - The error relative to eardrum SPL is likely more than 10 dB and frequency-dependent, which is worse than the unknown we are trying to remove. ANC adds another unknown: feedback ANC acts on the coupler cavity.
  - Patents describe this idea, but I found no validated product [V\*, [US 10368154](https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/10368154)].
- **Where the mic *is* useful:**
  1. **Ambient noise** (R§1.3), with an approximately absolute level from the CDD sensitivity when `UNPROCESSED` is supported.
  2. **Left/right channel imbalance**, over-ear only: the same mic and placement, recorded for the left cup then the right cup. It is only a cross-check for the swap test, because placement repeatability is uncertain.
  3. **Bluetooth silence-gating detection:** play a 1 kHz tone into the cup at moderate level after 3 s of silence and check whether the onset is truncated.
  4. **Routing sanity:** detect sound from the phone speaker when it should be in the headphones.
- **Verdict:**
  - MVP: ambient noise + routing check.
  - Later: gating check, mic L/R cross-check, optional bottle calibration.
  - Never: absolute headphone calibration.

### 2e. Volume and Bluetooth state
- **APIs [V, [AudioManager](https://developer.android.com/reference/android/media/AudioManager)]:**
  - `getStreamVolumeDb(STREAM_MUSIC, index, deviceType)` (API 28) returns the framework attenuation in dB at that index. The typical value at the maximum index is 0 dB.
  - `isVolumeFixed()` means `setStreamVolume` has no effect.
  - `setStreamVolume` can throw `SecurityException` if the change touches Do Not Disturb.
  - `ACTION_AUDIO_BECOMING_NOISY` fires on unplug or A2DP disconnect.
- **Volume curves are vendor tables.** For example, a Pixel 2 headset curve runs −49.5 dB at index 1 to 0 dB at index 100, with interpolated points [V\*, [taimen default_volume_tables.xml](https://android.googlesource.com/device/google/taimen/+/473d4e0/default_volume_tables.xml)]. **Only "maximum" is a reproducible state across devices** [I]. On wired, USB and non-absolute-volume Bluetooth, any index is reproducible on the *same* device if you read it back.
- **Bluetooth absolute volume (AVRCP).**
  - The phone index maps onto 0–127 and is sent to the headset, which applies its own (unknown) curve. Integer mapping makes several remote values collapse onto one index [V\*, [AOSP Bluetooth commits](https://android.googlesource.com/platform/packages/apps/Bluetooth/+/android-6.0.1_r40%5E%21/)].
  - New devices may start at half of maximum [V\*, [AOSP commit](https://android.googlesource.com/platform/packages/apps/Bluetooth/+/2d41d72%5E%21/)].
  - Users can turn it off in Developer options. It then becomes two independent volumes, phone and headset [V\*, [Android Authority](https://androidauthority.com/android-disable-absolute-bluetooth-volume-3391110)]. **There is no public API to read whether absolute volume is active** [K/I], and with it on, `getStreamVolumeDb` probably does not describe the real gain [I].
- **Recommendation [I]:**
  1. Ask the user to set **phone volume to maximum** and, on Bluetooth, also to **press the headset's volume-up until it stops**. Read back the index and require `index == max`.
  2. Accept the safe-media-volume dialog if it appears (R§2.3).
  3. Log `getStreamVolumeDb` and device type.
  4. Do all attenuation digitally in float.
  5. Enforce a **digital ceiling** = min(hard cap, user's "loud but OK" level + 5 dB).
  6. Start every track at or below the comfortable level and descend. (Rationale: "max" is reproducible, maximizes headroom for poor ears, and the user-anchored ceiling keeps it safe even though SPL is unknown.)
  7. **Floor check:** if the best threshold falls below about −95 dBFS, the volume is too high for clean low-level tones (DAC noise, codec quantization). Ask the user to lower the volume by N steps and retest 1 kHz.
- **Codec and DSP [I/K]:**
  - SBC, AAC and LC3 are perceptual codecs. Isolated **very low-level pure tones may be quantized away or distorted** by bit allocation. I found no measurement of this.
  - **The §4 digital-attenuation experiment measures it directly.** If low-level linearity fails, Bluetooth thresholds near the floor are artefacts.
  - Earbud **silence gating / power save** clips the start of sounds after silence. Some devices need about 2 s of signal to wake [V\*, [Sony community](https://community.sony.no/t5/portable-audio/wf-1000f-audio-turns-of-and-i-lose-half-of-the-sounds/td-p/2514504), [HP forum](https://h30434.www3.hp.com/t5/Bluetooth-Headsets-Earbuds/Headset-battery-saving-prevents-me-from-hearing-beginning-of/m-p/9488446)].
    - A continuous stream of digital silence may *not* be enough if the earbud gates on signal level.
    - Mitigation: keep the stream open for the whole block. Use longer pulsed tones (3 × 250 ms). Add a "gating probe": the same level presented as a short tone and as a 2 s tone. If the long one is heard and the short one is not, flag gating.
  - **HFP/SCO when the mic opens** (R§2.3): always use the built-in mic. Run ambient checks *before* the Bluetooth test block, or with the built-in mic only.
  - **Latency:** 100–300 ms on A2DP [K]. Use response windows of at least 2 s after the *estimated* acoustic onset and never score reaction time.
  - **ANC / transparency:** ask the user to pick and keep one mode: ANC on if available, transparency/ambient off, "adaptive", "personalized" and "spatial" EQ off where the app allows. Record the mode, because consistency matters more than which mode.
  - Per-ear adaptive EQ on some earbuds, which use an inner mic, may partly equalize fit differences. That is fine for the interaural difference, but unknown [I].

### 2f. Biological plausibility check (ISO 7029)
- **The model.** The median age-related shift relative to age 18 is `ΔH_md = α(f)·(Y−18)²`, valid 18–70 in the 2000 edition [V\*]. Confirmed coefficients: 125 Hz 0.0030 and 1 kHz 0.0040 (both sexes) [V\*].
- **Remaining coefficients from memory, to verify [K].** These are the ISO 7029:2000 values as I remember them. ISO 7029:2017 extends the range to 80 and was amended in 2024 ("correction of parameter values") [V\*, [ISO](https://committee.iso.org/standard/42916.html), [iteh Amd 1](https://www.iteh.ai/catalog/standards/iso/9f0f7f30-654c-40ae-a576-a747a4c9ebd0/iso-7029-2017-amd-1)], so **buy the 2017 + Amd 1 text before coding**.

  | Frequency | Male | Female |
  |---|---|---|
  | 500 Hz | .0035 | .0035 |
  | 2 kHz | .0070 | .0060 |
  | 3 kHz | .0115 | .0075 |
  | 4 kHz | .0160 | .0090 |
  | 6 kHz | .0180 | .0120 |
  | 8 kHz | .0220 | .0150 |

  - Example: a 60-year-old man at 4 kHz has a median of about 28 dB re age 18, and 1 kHz about 7 dB. So the median relative drop from 1 to 4 kHz is about 21 dB.
- **Use [I].**
  - Compute the *expected relative shape* (for example ΔH(4k) − ΔH(1k)) and its 10th–90th percentile spread for the user's age and sex.
  - Use it **only** to:
    - (i) flag implausible patterns, such as "rising" high frequencies in a 70-year-old, which suggests the headphone or effects or reference loss;
    - (ii) phrase results, for example "high-frequency reduction is typical / more than typical for your age"; and
    - (iii) catch gross miscalibration with DIN.
  - **Never use it as a Bayesian prior that pulls the displayed thresholds toward the norm.** That would hide exactly the losses you want to find.
- **Verdict:** MVP, as flags and phrasing.

### 2g. Crowd-sourced, per-model calibration
- **Identifiers:**
  - `AudioDeviceInfo.getProductName()`: real for USB, the friendly name for Bluetooth (user-renamable), nothing for 3.5 mm (R§2.3).
  - So add a **model picker** with autocomplete, and store `{type, productName, user-picked model, phone model, volume index, volume dB, ANC mode}` [I].
- **What others did.**
  - Masalski: per-model biological calibration from normal listeners, within-model SD about 4 dB [V\*].
  - Mimi: objective measurements plus subjective validation per headphone and device combination, now with RTINGS measurement data [V\*].
  - Public frequency-response databases exist. AutoEq aggregates oratory1990, crinacle, RTINGS and others [V\*, [AutoEq](https://github.com/jaakkopasanen/AutoEq)]. They give a **shape S(f)** on measurement rigs, normalised in level, and not sensitivity at your volume. They could supply S(f) for popular models.
  - Mapping a rig measurement onto an audiometric RETSPL shape needs a reference transducer measured on the same rig type [I]. Unverified, but promising for "later".
- **Hierarchical model [I]:**

  `T_app(i, e, f, m) = θ(i, e, f) − g_m(f) − b_m,i(e, f) + ε`

  - θ is the person's threshold, g_m the model's offset per frequency, b the per-session fit and imbalance.
  - Priors: θ ~ ISO 7029(age, sex) with inflated spread for self-selection; g_m ~ N(g_class(f), τ²), with classes such as over-ear wired, TWS, and so on.
  - **Identifiability:** g_m is identified only up to a constant unless some data are absolute. It needs **(1) people who test on ≥2 models**, which links models into a connected graph and gives relative offsets, and **(2) an anchor**: clinic audiograms for some users (the owner's Clalit audiogram is the first), or a coupler-calibrated chain later.
  - Population-prior estimates converge as SE ≈ σ_pop/√N. With σ_pop ≈ 12–15 dB [I], **about 25–40 users per model gives ±2–3 dB** for the offset, but it is **biased** by who uses a hearing app. Hence the anchor.
- **Conflict.** Decisions.md says "local storage only". Crowd calibration needs an opt-in upload of de-identified results plus device metadata. That is health data under GDPR Art. 9 and Israel's Amendment 13 (R§6). **This needs an owner decision.**
- **Verdict:** design the data schema now (MVP, stored locally). The upload and model are for later.

### 2h. The ear-cup swap check (my recommendation; prior art is only the audiocheck tip [V\*])
- **For over-ear and on-ear headphones only.** Normal orientation measures `D1 = A + C`, where A is the true ear asymmetry (left minus right) and C is the channel imbalance. Reversed (left cup on the right ear, with channel routing unchanged) measures `D2 = A − C`.
- **Then `A = (D1 + D2)/2` and `C = (D1 − D2)/2`.** A also averages two fittings, which reduces the fit error.
- **Cost:** retest 1 kHz and 4 kHz in both ears reversed. That is 4 thresholds, about 2 min.
- **Interpreting C:**
  - If |C| ≤ 3 dB, do not correct.
  - If 3 < |C| ≤ 10 dB, correct and log it.
  - If |C| > 10 dB, flag "headphone or balance problem". The cause could be a defect or an accessibility balance setting (R§2.3).
- **Not possible** with shaped TWS earbuds or some IEMs. For those, label the interaural difference "not verified for channel balance". A software L/R swap does **not** help, because the imbalance belongs to the physical driver.

---

## 3. Recommended protocol

### 3.1 MVP flow (about 20 min, within decisions.md)

| # | Step | Time | Output |
|---|---|---|---|
| 0 | **Red-flag questionnaire first:** sudden change (when?), one ear only, facial weakness or numbness, vertigo, ear pain or discharge, pulsatile or one-sided tinnitus, recent noise exposure | 1 min | If sudden loss in the last 30 days or neurological signs: **stop and show the emergency message (§5)** before any testing |
| 1 | Device detection, headphone type, model picker, ANC mode question. Block speakers, bone-conduction headsets and hearing-aid profiles | 0.5 min | `setupTier` (table 3.3) |
| 2 | Volume: maximum (phone + headset), read back, `isVolumeFixed`, log `getStreamVolumeDb`. Spatializer off | 0.5 min | volume state |
| 3 | Ambient noise, 20–30 s, built-in mic (`UNPROCESSED` if supported), octave bands | 0.5 min | quiet / marginal / too loud (per type) |
| 4 | L/R identification (2 trials per side) and a mono/balance check | 0.5 min | abort if wrong |
| 5 | Comfortable and "loud but OK" levels at 1 kHz and 4 kHz | 1 min | ceiling, headroom estimate |
| 6 | Bluetooth only: gating probe (short vs 2 s tone at a moderate-low level) | 0.5 min | gating flag |
| 7 | Thresholds per ear: 1k, 2k, 4k, 8k, 1k retest, 500, 250. Adaptive 3k/6k. Catch trials. **Final step 2–2.5 dB** (Békésy-like refinement had the lowest error in Masalski 2014 [V\*]) | 8–10 min | `T(e,f)` in dBFS, censored flags |
| 8 | Swap check (over-ear/on-ear): 1k and 4k, both ears | 2 min | A, C |
| 9 | DIN: diotic (or antiphasic), at comfortable level, with an audibility check | 3–4 min | SRT (relative unless normed) |
| | **Total** | **about 18–21 min** | |

Word recognition from R§4 comes after step 9 in the full profile.

### 3.2 Quantities computed [I]
- **Censoring.** Any threshold at the ceiling becomes "≥ ceiling" (right-censored). Use the standard no-response symbol. Never interpolate through it.
- **Interaural difference.** `IAD(f) = T_L(f) − T_R(f) − C(f)`. C comes from the swap: measured at 1k and 4k, interpolated linearly in log-frequency, and held flat outside.
  - Summaries: `IAD_mid = mean over 0.5, 1, 2k` and `IAD_high = mean over 3/4, 6, 8k`.
  - Count adjacent-frequency pairs with |IAD| ≥ 15 and ≥ 20 dB.
  - With censoring, IAD is a lower bound ("≥").
- **Shape.**
  - Reference `R_e` = min of 0.5/1/2k (drop 500 Hz if the room was marginal).
  - `Rel(e,f) = T(e,f) − R_e`.
  - `HF_drop(e) = mean(Rel at 4k, 8k)`. `Notch(e) = Rel(4k) − mean(Rel(2k), Rel(8k))`.
  - Compare HF_drop with the ISO 7029 expected relative drop and its percentile range.
- **Reliability:**
  - |1k test − retest| (flag if > 5 dB [K, R§1.2] and mark unreliable if > 10).
  - Catch-trial false positives (flag if > 20%).
  - Swap consistency.
  - Ambient flag.
  - Gating flag.
- **Headroom.** `HR(e,f) = ceiling − T(e,f)`. If the comfortable level at 1 kHz is within 15 dB of the hard cap, warn "volume may be too low".
- **Floor.** If the best T is below −95 dBFS, warn "volume too high".
- **Gross-miscalibration catch (DIN vs tones):**
  - DIN "normal" while ≥ 3 frequencies per ear are censored at the ceiling → "setup problem, results not shown".
  - DIN clearly poor while all tones sit near the floor → "check headphones / noise / materials".
- **Longitudinal.** Compare with the last session that had the *same* `{phone model, headphone id, volume index, ANC mode}`. Report per-frequency change. A shift of ≥ 10 dB averaged over 2–4 kHz, or ≥ 15 dB at 2 adjacent frequencies, is a flagged change [I, modelled on NIOSH STS (10 dB avg 2/3/4k) [K]].

### 3.3 Display and allowed claims, by confidence tier [I]

| Setup | Interaural difference | Shape | Absolute | Notes |
|---|---|---|---|---|
| Wired/USB over-ear or on-ear, swap done | **High** (±5 dB band) | Medium (claim only drops ≥ 20 dB) | none | best relative setup |
| Wired/USB over-ear, no swap | Medium-high | Medium | none | |
| Wired earbuds / IEM | Medium (≤ 250 Hz unreliable: seal) | Low-medium | none | ask for a good seal; 250 Hz greyed |
| Bluetooth over-ear (ANC or not), fixed mode | Medium | Low | none | gating probe required |
| Bluetooth TWS earbuds | Medium-low (independent buds, no swap) | Low | none | 250 Hz greyed |
| Any setup + known model correction (later) | same | +1 level | still none until anchored | |
| Speaker / bone conduction / hearing aid | blocked | blocked | — | |

**Allowed wording (EN, templated):**
- "Compared with your right ear, your left ear needed about **20–30 dB** more sound at **3–6 kHz**." Give 10 dB bands, not single values.
- "Your ears respond **about the same** (within about 10 dB) across the tested range."
- "High pitches are **clearly reduced** compared with your mid pitches, **more than is typical for your age**." Only when HF_drop ≥ 20 dB and above the ISO 7029 90th percentile.
- "We **could not reliably measure** [frequency]: [reason: room noise / ceiling / fit]."
- "Speech-in-noise result: [category]." Only once norms exist; otherwise "compared with your previous test".

**Forbidden in relative mode:**
- a dB HL axis or numbers;
- "normal hearing";
- WHO or Clark grades;
- "mild/moderate loss";
- PTA.

The chart can show an **"ear-comparison" view** (left minus right per frequency, with error bars) and a **"relative shape" view** (0 at the best mid frequency, downward, no HL label). Do not draw the standard audiogram grid.

**Mandatory footer:**
- "This setup is not calibrated. A normal-looking result does not rule out hearing loss in both ears."
- The type-of-loss sentence from R§6.

### 3.4 Handling likely miscalibration [I]
- **Ceiling hits at ≥ 1 frequency.** Retry once after asking for maximum volume. If still at the ceiling, show "no response at the maximum this setup can play". If ≥ 3 frequencies in one ear, add "possible significant loss in this ear **or** a setup problem: see a clinic".
- **Both ears censored at the same high frequencies, with an otherwise normal DIN.** Suspect headphone roll-off or Bluetooth codec/DSP. Show "high-frequency result not available with these headphones".
- **Ambient noise too loud.** Block the test, or test with 250/500 Hz marked "may be noise-limited".
- **Swap |C| > 10 dB, failed L/R identification, retest > 10 dB, or false positives > 20%.** Show the reliability summary only, no curve.
- **The user changes volume mid-test, or the route changes.** Invalidate the block and restart.
- **Gating flag (Bluetooth).** Retest with long tones. If it persists: "These earbuds may mute very quiet sounds; results may underestimate your hearing."

---

## 4. Validation experiments that need no lab equipment

### E1. Simulation (software only, first)
- Simulate listeners with a psychometric function, lapses and false alarms (R§7).
- Inject random G0 (±25 dB), S(f) drawn from a spread you assume (for example ±8 dB at 4–8 kHz), B (±3 dB), F (±2 dB over-ear, ±10 dB at 250 Hz for earbuds), and the ceiling and floor.
- Check:
  - bias and SD of IAD, A and C (with swap), HF_drop and Notch;
  - censoring behaviour;
  - the false-positive and false-negative rates of each display rule and referral rule.
- **Tune the display thresholds so that the false-positive rate for "asymmetry" is ≤ 5% in symmetric simulated ears.**

### E2. Known digital modifications on real listeners (blinded)
- **Design.** In a hidden "validation mode", the app applies a random, logged digital gain pattern before the test. Examples:
  - left ear −15 dB at 2–4 kHz;
  - both ears a −20 dB tilt above 2 kHz;
  - −10 dB on one ear across all frequencies;
  - none (sham).

  The tester does not know which. Each tester does sham + 2 patterns, on 2+ headphones.
- **Recovered effect** = threshold(pattern) − threshold(sham) at each frequency. **The truth is known exactly.**
- **What it validates:** procedure precision, the relative pipeline, and **low-level linearity of the real chain**, including Bluetooth codec, gating and DAC floor. A −20 dB pattern pushes tones 20 dB lower in dBFS.
- **What it does not validate:** the transducer shape S(f). Only cross-headphone comparison (E3) and clinic anchors (E5) do that.

### E3. Same person, several headphones
- **Panel:** the owner plus 5–10 friends or family. Each tests on **3–5 setups** (wired over-ear, wired earbuds, Bluetooth over-ear ANC, TWS ANC, plus a cheap earbud). Two sessions on different days, with randomized order.
- **Analysis:** a linear mixed model `T ~ ear*freq + (1|person:ear:freq) + headphone:freq + session`, giving variance components for between-headphone variance of IAD, of HF_drop, and test-retest.
  - **If the between-headphone SD of IAD is ≤ 5 dB, the IAD claims stand.**
- **Owner as ground truth.** Expected IAD at 3–6 kHz is about +25 to +40 dB (left worse). Right ear shows a mild 2–4 kHz dip, left a sloping loss. Check that every setup reports "left worse at high frequencies" and the size band.
  - **Cross-hearing caveat:** with a 40 dB difference on over-ear headphones, left-ear points near 60 dB HL may be heard by the right ear. Interaural attenuation is about 40 dB circumaural / 55 dB insert [K, R§3]. This is a nice test of the "possible cross-hearing" flag, because IAD ≥ about 35 dB is relative-valid.

### E4. Earplug simulated conductive asymmetry
- A foam plug in one ear under **over-ear** headphones (not with earbuds).
- Attenuation varies with insertion [V\*, earplug-simulated conductive loss used as a model: [PMC5581305](https://pmc.ncbi.nlm.nih.gov/articles/PMC5581305)]. DigiBel used exactly this design (30 plugged and 30 unplugged ears) [V\*].
- **Use it for detection rates** of the asymmetry rule, not for dB accuracy. Optionally estimate each insertion's true attenuation by testing plugged vs unplugged on the *same* headphone, which is relative-valid.

### E5. Clinic anchors (opportunistic)
- Recruit people who already have a recent clinic audiogram (≤ 6 months, no change since): parents, older relatives, the owner.
- Aim for **≥ 10 people with asymmetric loss** and ≥ 10 with symmetric sloping loss.
- These are the only data that test shape claims and that anchor crowd offsets.

### Statistics and sample size [K / I]
- **Accuracy:** report MAE, mean bias, and % within 10 dB (comparable to Apple 86.5% [V\*] and DigiBel 73% [V\*]).
- **Bland–Altman** with multiple observations per subject (Bland & Altman 2007 method [K]) for IAD and HF_drop, against E2 truth and E5 clinic values. Count ears or differences, but model the person as the cluster.
- **ICC(2,1)** for IAD across headphones (agreement) and across sessions.
- **Precision of an SD estimate:** relative SE ≈ 1/√(2(n−1)). **n ≈ 50 paired differences gives the SD within about ±20%.** For limits of agreement, SE ≈ 1.7·s/√n, so n = 50 gives ±0.24 s.
- **Screening rule:** sensitivity of the asymmetry flag with a 95% CI half-width of about ±15% needs about 30 positive cases. Combine E4 plug cases and E5.
- **Practical plan:**
  - Phase A: E1 + E2 + E3, owner + 5 people × 4 setups × 2 sessions. That gives about 40 setup-sessions and about 300 frequency-ear points: enough for variance components.
  - Phase B: E4 with 20 people, 2 insertions each.
  - Phase C: E5, rolling.

---

## 5. Triage output: which rules survive in relative mode

| Rule (source) | Needs absolute dB HL? | Relative-mode version | App urgency |
|---|---|---|---|
| **Sudden loss (≤ 3 days) that developed within the past 30 days** → seen by a specialist **within 24 h** (NICE QS185 / NG98) [V\*, [NICE QS185](https://www.nice.org.uk/guidance/QS185/chapter/quality-statement-2-sudden-onset-of-hearing-loss)] | No (history) | Questionnaire. Also a **new interaural difference ≥ 20 dB vs the previous same-setup session** | **Emergency, today** |
| Sudden loss > 30 days ago → within 2 weeks (NICE) [V\*] | No | Questionnaire | **Urgent, ≤ 2 weeks** |
| Unilateral loss + same-side facial droop or altered sensation → immediate, or stroke pathway (NG98 1.1.2) [V\*] | No | Questionnaire | **Emergency: call emergency services** |
| SSNHL definition, ≥ 30 dB in 3 consecutive frequencies within 72 h (AAO-HNS 2019) [V\*, [summary](https://speciality.medicaldialogues.in/amp/sudden-hearing-loss-guideline-update-by-aao-hnsf)] | Yes, against a baseline | Only as a longitudinal change on the same setup, or as interaural difference when the other ear is known to be stable | Emergency if it fits the history |
| **Asymmetry ≥ 15 dB at 2 adjacent frequencies** (0.5, 1, 2, 4, 8 kHz), suggested MRI threshold in NICE-based protocols [V\*, [BAA MRI guidance](https://www.baaudiology.org/app/uploads/2020/04/Guidance-on-Referral-for-MRI-by-Audiologists.pdf), [Salisbury protocol](https://www.salisbury.nhs.uk/FOIdocs/FOI_7229/QuestionNo1/Protocol%20for%20direct%20Audiology%20referrals%20to%20MRI%20FOI.pdf)]. AAO-HNS ≥ 15 dB averaged 0.5–3 kHz: sensitivity about 87%, specificity about 65% [V\*, [PMC6354499 table](https://pmc.ncbi.nlm.nih.gov/articles/PMC6354499)]. Charing Cross: 15 or 20 dB at 2 frequencies, depending on the better ear [V\*] | **No**: IAD is relative-valid | Use **≥ 20 dB at 2 adjacent frequencies, or IAD_mid/high ≥ 15 dB**, swap-verified where possible, to absorb the ±7 dB error. 15–20 dB → "possible difference, retest" | **Soon**: audiology/ENT within weeks. Not an emergency unless sudden |
| Unilateral or pulsatile tinnitus → ENT (BSA-derived local protocols) [V\*] | No | Questionnaire | Soon (pulsatile: soon, ENT) |
| Ear pain, discharge, fullness after a cold | No | Questionnaire | GP/ENT, routine–soon |
| Better-ear PTA ≥ 20 / > 25 dB HL; WHO 2021 grades | **Yes** | **Not available.** Replace with the DIN category (hearWHO-style ≥ 75 / 50–74 / < 50 [V\*], once your DIN is normed) and with "high-frequency reduction more than typical for age" | DIN "likely problem" → **routine** hearing test (weeks–months) |
| Configuration (sloping, 4 kHz notch) | Partly | Relative shape, large effects only | Routine. A notch plus a noise history gives a hearing-protection message |
| Possible cross-hearing (IAD ≥ about 35–40 dB) | No | Flag "the poorer ear may be worse than shown" | Soon |
| Results unreliable or setup problem | — | Show reliability flags | "Repeat test" / "see clinic if concerned" |

**Medical-safety wording (templated, deterministic) [I]:**
- **Emergency:** "Sudden hearing loss in one ear can be a medical emergency. Treatment works best when started early. **Contact a doctor or emergency department today. Do not wait to retest.**" (In Israel: suggest ER/ENT on-call or Magen David Adom 101 if there is facial weakness. Verify the wording with a clinician.)
- **Never let a reassuring test override a red-flag history.** Order the summary so that the history-based urgency appears first.
- **"Normal" outcome:** "No clear difference between your ears and no clear high-frequency drop was found **with this setup**. This does not rule out hearing loss. If you have trouble hearing, see an audiologist."
- **Always say:** "This is a self-check, not a diagnosis."

---

## 6. Regulatory and claims note (short; uncertain)
- **EU.**
  - MDCG 2019-11 rev. 1 was published in June 2025. Software that informs diagnosis or screening defaults to Class IIa under Rule 11 [V\*, [Emergo](https://www.emergobyul.com/news/european-revision-primary-software-guidance-mdcg-2019-11-revision-1-small-changes-meaningful)]. MDCG 2025-4 asks app stores to label medical-device apps [V\*].
  - **"Uncalibrated / relative" does not change qualification.** An output of "how urgent is a hearing referral" is a medical purpose [I].
  - Avoiding device status requires removing the medical purpose itself, not just softening the precision claims. In practice that means no referral advice beyond generic "see a professional if concerned", and no condition-specific outputs [I; get a regulatory opinion].
- **US.**
  - FDA's revised General Wellness and CDS guidances were issued 6 January 2026. The two-factor wellness test is kept, with new wearable-sensor and "out-of-range notification" language [V\*, [Greenberg Traurig](https://www.gtlaw.com/en/insights/2026/01/fda-updates-two-digital-health-final-guidance-documents)].
  - A hearing test that estimates loss or triages referral is plausibly an audiometer function (21 CFR 874.1050, EWO; Apple's test is listed there, R§2.7). **I found nothing hearing-specific in the 2026 guidance. Unverified.**
- **Israel.** No SaMD-specific AMAR guidance was found (R§2.7). Fast-Track leans on CE/FDA [V in R]. Treat as unknown.
- **Practical [I].**
  - Phase 1 testers: frame it as a research or self-check prototype with informed consent.
  - Relative mode actually *helps* honesty: you never show dB HL.
  - **Phase 2 public release with triage wording needs a regulatory decision first.** The provider-adoption goal in decisions.md is clearly a medical device.

---

## 7. Honest limits
1. **Relative tone testing cannot detect symmetric loss.** Its absolute-level blind spot is the most common kind of loss (age-related, bilateral). DIN is the only guard, and **your DIN is currently TTS and un-normed**.
2. **Shape claims depend on the unknown headphone response.** That includes high-frequency drop and notch. For unknown models, only large effects (≥ 20 dB) are reportable, and 8 kHz and 250 Hz are the weakest points.
3. **Bluetooth low-level behaviour is unmeasured.** Codec, gating and DSP are untested; E2 is the test. Until then, Bluetooth thresholds are lower-confidence.
4. **The interaural-difference error numbers are derived, not published.** I found no study of interaural accuracy with uncalibrated consumer headphones.
5. **Type of loss cannot be determined, and masking is not used** (R§3). Cross-hearing can mask the true size of large asymmetries.
6. **Key papers were read only through search snippets** (Masalski, Xu & Kollmeier, DigiBel, Bravard, NICE, ISO 7029). ISO 7029 coefficients beyond 125 Hz and 1 kHz are from memory.
7. **The proposals are untested.** The swap method and the DIN-anchoring heuristic are my proposals, not published procedures.

## 8. Prioritized recommendations

**MVP (relative mode)**
1. Red-flag questionnaire first, with emergency routing.
2. Setup: type tier, maximum volume with read-back, ceiling and headroom from comfortable/loud levels, ambient mic check, L/R check.
3. Thresholds with 2–2.5 dB final steps, censoring, catch trials, and retest.
4. Swap check for over-ear and on-ear headphones.
5. IAD and relative-shape outputs in bands, with tiered confidence. No dB HL anywhere.
6. Longitudinal comparison on an identical setup.
7. ISO 7029 plausibility phrasing.
8. DIN as a relative check now; as a category once normed.
9. Log the full device metadata schema locally.
10. Run E1 and E2 before showing results to testers.

**Next**
- Bluetooth gating probe.
- Normed DIN (license, or norm your own).
- E3 and E4.
- Per-model shape corrections for the 5–10 most common models, from crowd data and/or public frequency-response databases.
- Owner-anchored offsets for the owner's own headphones.

**Later**
- Opt-in crowd upload with a hierarchical model.
- CLS-based offset estimation, if the Oldenburg real-device validation appears.
- Bottle-calibrated mic.
- Coupler or biological calibration to unlock dB HL for selected models (R§2.2).

## 9. Decisions needed from the owner
1. **Is relative mode acceptable for Phase 1?** That means no dB HL, no grades, and an "ear comparison + relative shape" chart instead of a Clalit-style audiogram for uncalibrated setups.
2. **Will you allow an opt-in upload** of de-identified results and device metadata, for crowd calibration? This conflicts with "local only".
3. **Do you accept a 2-minute ear-cup swap step** for over-ear users?
4. **Should the user be required to set maximum volume** (phone and headset), and how strict should blocking be? The options are to block Bluetooth TWS from shape claims or to only label it.
5. **DIN material:** license a normed DIN (English, Hebrew/Ariel), or norm your TTS or recorded digits. Until then DIN is relative-only.
6. **Triage thresholds and wording:**
   - adopt the app-level asymmetry rule (≥ 20 dB at 2 adjacent frequencies);
   - adopt NICE-style emergency timing;
   - have an Israeli ENT or audiologist review the emergency text.
7. **Validation:** who are the E3/E5 volunteers (family with clinic audiograms)? Can the owner do the 4-headphone × 2-session protocol?
8. **Phase 2 regulatory path:** wellness-only with no triage output, or medical device (CE IIa / FDA). The triage goal implies a medical device.
