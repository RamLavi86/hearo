# Research report: a smartphone hearing-test app (Android first, English and Hebrew)

Prepared 2026-11-07. Written in English for a Hebrew-speaking product owner.

**How to read this report.**
- **[V]** means I checked it against a source during this research session. The source is linked inline.
- **[K]** means it is standard audiology or Android knowledge that I did not re-fetch in this session. It is believed correct, but you should check it against the primary standard or documentation before you rely on it for engineering.
- **[I]** means it is my own inference or recommendation.
- **Two primary sites could not be reached.** This session's network blocked `support.google.com` and `pubs.asha.org`. Play Console rules were therefore checked only through secondary sources. The 2026 Hebrew word-list paper was checked only through search snippets. Both are flagged where they come up.

---

## 0. Executive summary

1. **What is feasible.** A phone can reasonably produce an air-conduction pure-tone audiogram per ear (250–8000 Hz) and a speech-in-noise score such as digits-in-noise (DIN). With more caveats, it can also produce an approximate SRT and word-recognition score. It **cannot** produce tympanometry, real bone conduction, or reliable clinical masking. So it cannot tell conductive from sensorineural loss.
2. **Calibration decides accuracy.** Accuracy depends almost entirely on calibration. Without per-model calibration, absolute thresholds can be off by 10–20+ dB. With biological or model-specific calibration, published smartphone systems get a standard error of about 5 dB per frequency [V, Masalski 2016/2018, below]. Tier your claims to match. Support a short list of calibrated headphone models (wired preferred). Fall back to "relative / uncalibrated" mode otherwise.
3. **DIN is the most robust self-test.** Digits-in-noise is SNR-based and largely insensitive to absolute level. It is validated in English and in Hebrew (Taitelbaum-Swead & Fostick 2024; normal-hearing mean about −9.7 dB SNR [V]). WHO's hearWHO uses it. It should be in the MVP.
4. **Product positioning.** Ship it as a "hearing self-check / screening, not a medical device". Wording alone does not set regulatory status: intended use does. Apple's own hearing test is regulated as a Class II audiometer (product code EWO) [V]. In the EU, software that informs a diagnosis is at least Class IIa under MDR Rule 11 [K]. Google Play requires a Health apps declaration and a "not a medical device" disclaimer for unregulated health apps [V]. For a private or friends-and-family tool this is manageable. A public "audiogram" app aimed at Israel, the EU or the US needs a regulatory decision first.
5. **Tech stack.** Native Kotlin + Jetpack Compose, with audio in `AudioTrack` (float PCM) or Oboe. Put the test logic in a Kotlin Multiplatform-ready pure module so that iOS can later reuse it behind an `AVAudioEngine` layer.
6. **Time budget.** 15–20 minutes is realistic for: a 6-frequency audiogram per ear with retest (about 8–10 min), DIN (about 3–4 min), and one 25-word list per ear (about 4–5 min). Adding 3k/6k Hz, an SRT and a second list pushes it to 25–30 minutes.

---

## 1. Clinical protocol (pure-tone air conduction)

### 1.1 Standards
- **ISO 8253-1:2010.** *Acoustics — Audiometric test methods — Part 1: Pure-tone air and bone conduction audiometry* [K]. It defines familiarization, the ascending method, and the maximum permissible ambient noise levels.
- **ASHA (2005), *Guidelines for Manual Pure-Tone Threshold Audiometry*.** This is the modified Hughson-Westlake procedure [K].
- **RETSPL standards (dB SPL corresponding to 0 dB HL)** [K]:
  - ISO 389-1: supra-aural earphones.
  - ISO 389-2: insert earphones.
  - ISO 389-5: extended high frequency, 8–16 kHz.
  - ISO 389-8: circumaural earphones.
  - ISO 389-4: masking noise.
  - ISO 389-7: sound field.

  ISO 389-1 Addendum 1 supports applying values across a *class* of earphone measured on an IEC 60318-1 ear simulator, within stated uncertainty [V, [ISO 389-1 preview](https://webstore.ansi.org/preview-pages/ISO/preview_ISO+389-1-2017.pdf)]. This is the theoretical basis for "generic per-class" calibration of consumer supra-aural or circumaural headphones. It is only an approximation.

### 1.2 Procedure to implement (modified Hughson-Westlake, ASHA 2005 / ISO 8253-1) [K]
- **Tone.** Pulsed or continuous pure tone, 1–2 s, with 20–50 ms raised-cosine on/off ramps. Pulsed tones (for example 3 × 200 ms on / 200 ms off) improve detection for people with tinnitus. Recommend pulsed. [I]
- **Familiarization.** Start at 1000 Hz, 30–40 dB HL, in the better ear by self-report. If there is no response, go up in 20 dB steps, and from 50 dB HL in 10 dB steps.
- **Bracketing.** After each response go down 10 dB. After each miss go up 5 dB.
- **Threshold.** The lowest level heard on at least 50% of ascending runs, with at least 2 responses at that level (ASHA: 2 of 2, 2 of 3, or 3 of 4). ISO 8253-1 uses a similar rule of 2 of 3 ascents [K].
- **Frequency order.** 1000, 2000, 3000*, 4000, 6000*, 8000, then retest 1000, then 500 and 250. Then the other ear.
  - *A retest at 1 kHz that differs by more than 5 dB (ASHA says retest when they differ by >5 dB) flags unreliability [K].
  - Test the inter-octaves 3k and 6k when adjacent octaves differ by 20 dB or more (common clinical practice [K]). Your Clalit report's axis shows those positions. In an app, always testing 3k and 6k costs about 1.5 min per ear. Test them adaptively instead. [I]
- **Catch trials (an app-specific addition).** Silent trials, about 10–15% of trials, measure the false-positive rate. Abort or flag if false positives exceed about 20%. Automated audiometers use this, for example AMTAS and the Apple test [K/I].
- **Response windows.** Use a randomized inter-stimulus interval of 1–3 s, so the user cannot predict the rhythm. A response counts if it falls within about 1.5 s of tone onset. Use `AudioTrack.getTimestamp()` to align the real playback time with the tap, not the time the buffer was written [K].
- **Speed-ups (optional).** Bayesian or machine-learning methods (Song/Barbour's ML audiometry, or QUEST/ZEST per frequency) need fewer trials than Hughson-Westlake [V, [scoping review](https://pubmed.ncbi.nlm.nih.gov/34919056/)]. Start with Hughson-Westlake for comparability with clinic reports. Add a Bayesian variant later. [I]

### 1.3 Ambient noise
- Clinical audiometry is done in a booth. The ISO 8253-1 / ANSI S3.1 maximum permissible ambient levels are very low: roughly 20–30 dB SPL in low octave bands for supra-aural testing down to 0 dB HL [K].
- Consumer headphones give little attenuation below 1 kHz, except closed-back or ANC models. So the 250 and 500 Hz thresholds are the most likely to be elevated by room noise. The 2021 meta-analysis found accuracy depended on whether a soundproof booth was used [V, [Chen et al. 2021 meta-analysis, JMIR mHealth](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8477297/)].
- **Recommendation [I]:**
  - Measure ambient noise with the phone mic before and during the test. Use `AudioRecord` with `MediaRecorder.AudioSource.UNPROCESSED` (API 24+, if supported) so that AGC and noise suppression are off.
  - Compute octave-band levels from those readings.
  - Block or flag the test above thresholds. The phone mic is uncalibrated, so use conservative relative thresholds plus a per-phone-model offset where you have one.
  - Mark low-frequency thresholds as "may be noise-limited" when the room is loud.

### 1.4 How existing apps work and their validity
- **Meta-analysis.** 25 studies, 4,470 patients. Pooled sensitivity was 89% and specificity 93% for detecting hearing loss against conventional pure-tone audiometry. Accuracy depended on age, the equipment used, and the booth [V, [PMC8477297](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8477297/)].
- **2016 review.** Only 6 apps had peer-reviewed validity data, from 11 studies [V, [Bright & Pallawela 2016, JMIR Rehab](https://rehab.jmir.org/2016/2/e13)].
- **uHear** (iOS screening): the most-validated iOS app in one review, with 8 papers [V, same search].
- **Hearing Test by e-audiologia (Masalski, Android).** Uses **biological calibration**: model-specific reference levels derived from normal-hearing listeners using bundled headphones. Results were "highly compatible" with booth audiometry, 250 Hz–8 kHz. The calibration method's mean standard error was about **4.9 dB** [V, [Masalski 2018 JMIR mHealth](https://mhealth.jmir.org/2018/1/e10/PDF); [Masalski 2016 JMIR](https://www.jmir.org/2016/5/e130)]. Their 2014 web study found a calibration-error SD of 6.19 dB at 1 kHz and concluded the test is "screening, not a replacement" [V, [JMIR 2014](https://jmir.org/2014/1/e11/PDF)]. A [2024 review in Medical Devices: Evidence and Research](https://dovepress.com/the-hearing-test-app-for-android-devices-distinctive-features-of-pure--peer-reviewed-fulltext-article-MDER) describes the app's features. **This is the closest prior art to what you want to build. Read it in full.**
- **hearWHO (WHO, built with hearX).** A **digits-in-noise** test, not pure-tone [K]. hearX's hearZA DIN had sensitivity and specificity of 0.88 / 0.88 against abnormal pure-tone results, and a correlation of rs = 0.81 with the better-ear 4-frequency PTA [V, [Potgieter et al. 2018](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5968873/)].
- **Apple Hearing Test (AirPods Pro 2 / Pro 3).** A tone test of about 5 minutes. It is listed in FDA GUDID as product code **EWO, "Audiometer"**, GMDN "Self-testing tone audiometer" [V, [GUDID](https://accessgudid.nlm.nih.gov/devices/00195949149948)]. It was validated against professional audiograms (n≈202 cited in coverage of the De Novo DEN230081 for the Hearing Aid Feature) [V, [Innolitics DEN230081](https://fda.innolitics.com/device/DEN230081); [MedTech Dive](https://www.medtechdive.com/news/apple-fda-hearing-aid-software-airpods/726933/)]. It works because Apple controls both the transducer and the phone: an end-to-end known calibration. **There is no Android equivalent unless you restrict supported hardware.**
- **Mimi, Shoebox, Sennheiser, Hearing Scale.**
  - Mimi and Shoebox use model-calibrated pure-tone tests [K].
  - Shoebox (tablet with calibrated audiometric headphones) is marketed as a clinical-grade audiometer [K; regulatory status not re-verified].
  - I did not verify validation numbers for Mimi, Sennheiser or Hearing Scale in this session.

### 1.5 Time budget (realistic) [I, based on about 25–35 s per threshold for an experienced Hughson-Westlake run]

| Block | Content | Time |
|---|---|---|
| Setup | Headphone check, L/R check, ambient noise, volume lock, instructions | 2 min |
| Pure-tone, right ear | 1k, 2k, 4k, 8k, 1k retest, 500, 250 (+3k/6k if needed) | 4–5 min |
| Pure-tone, left ear | Same | 4–5 min |
| DIN | Diotic or antiphasic, 24 triplets | 3–4 min |
| Word recognition | 25 words per ear at a fixed level | 4–6 min |
| **Total** | | **17–22 min** |

- Adding an SRT per ear (about 2 min each) and 50-word lists gets you to **about 30 min**.
- **Recommendation.** Make the 15–20 min "standard" profile the default. Add a "full" profile (about 30 min). Add a "quick" profile (DIN only, about 4 min).

---

## 2. Calibration (the critical part)

### 2.1 The chain you have to calibrate
The signal passes through these stages:

```
digital dBFS → Android mixer / effects → volume index → DAC / amp (phone or USB-C dongle or BT earbud) → transducer → coupling to the ear → dB SPL at the eardrum
```

- **Target value.** dB HL = dB SPL (measured in the standard coupler) − RETSPL(f, earphone class) [K].
- **Phone and dongle output varies.** It varies by model and by the volume curve. For example, Apple's USB-C-to-3.5 mm adapter has different maximum output in EU and US SKUs [K]. Bluetooth earbuds add their own DSP: EQ, ANC, transparency, "adaptive" modes. Their absolute volume (AVRCP) maps the Android volume index onto earbud gain in a vendor-specific way [K].

### 2.2 Options, ranked
1. **Acoustic calibration per supported (phone-family, output path, headphone model).** This is the best option.
   - Measure dB SPL on an ear simulator or coupler: IEC 60318-1 for supra- and circumaural, IEC 60318-4 / 711 for inserts and earbuds.
   - Use a Class 1 sound level meter, at each test frequency, at a fixed volume index.
   - Store a table: `{headphoneModel, outputPathType, phoneModel?} → dBSPL_at_0dBFS[f]` per ear.
   - Combine it with RETSPL for that earphone class.
   - A university audiology lab (Tel Aviv University, Ariel, Haifa, Hadassah Academic College) or a hearing-aid dispenser with a coupler could do this in an afternoon. [I]
2. **Biological calibration (Masalski).**
   - Collect thresholds from roughly 10–20 young, otologically normal listeners per headphone model.
   - Set "0 dB HL" to the median threshold.
   - The standard error is about 5 dB [V].
   - Needs no lab. You need recruits and an exclusion screen: age 18–30, no complaints, ideally a clinic audiogram.
3. **Generic per-class RETSPL plus a manufacturer sensitivity spec (dB SPL/mW and impedance).** This gives ±10 dB or worse, because consumer frequency responses are not flat (bass boost, a treble peak around 3–6 kHz). Use it only as a fallback that is labelled "approximate".
4. **Uncalibrated / relative mode.** Report only:
   - interaural asymmetry (left minus right per frequency — the same headphone serves both ears, so this is fairly robust if the channels are matched),
   - configuration shape relative to a population normal curve, and
   - the DIN SRT.
   Do not draw the dB HL axis, or show it greyed out with a warning. [I]
5. **Personal anchoring.** You already have a Clalit audiogram. Anchoring the app to it (offsets = clinic thresholds − app thresholds) is valid **for you and your headphones only**. It also makes a good first validation dataset. [I]

**Recommended headphone choice [I].**
- Pick one or two wired, closed-back models for "calibrated mode". Wired avoids Bluetooth DSP and pass-through variability. Closed-back gives some ambient attenuation.
- **Sennheiser HD 280 Pro** is attractive: it is the consumer sibling of the audiometric **HDA 280**, whose RETSPL table is published (for example 25.0 dB at 250 Hz and 13.0 dB at 500 Hz per the manual) [V, [HDA 280 manual](https://manualsonline.com/manuals/mfg/sennheiser/hda_280.html?p=3)]. The HDA 280-CL equivalent threshold levels were also published in IJA 2019 [V, [UWO record](https://ir.lib.uwo.ca/scsdpub/84)]. The HD 280 Pro is **not** guaranteed to have the same response. You still need at least one coupler measurement or a biological calibration.
- A cheap, consistent USB-C DAC dongle (one specific SKU) fixes the electrical chain across phones. Specify one, so that calibration does not depend on the phone's own headphone amp. Most current phones lack a 3.5 mm jack.

### 2.3 Android audio implementation for level accuracy [K unless noted]
- **Signal generation.**
  - Generate sine tones yourself in float32 at 48 kHz, the native rate on most devices. That avoids resampling.
  - Use `AudioTrack` with `AudioFormat.ENCODING_PCM_FLOAT`, or Oboe (`AAudio` / `OpenSL ES`) in `PerformanceMode::LowLatency`, `SharingMode::Exclusive` where available.
  - Latency does not matter much for thresholds, but exclusive mode can bypass some mixer processing.
  - Use 20–50 ms raised-cosine ramps (cos²). Rectangular gating causes spectral splatter that is audible as clicks, and at high frequencies it creates off-frequency energy.
  - Send per-ear signals as separate L/R channels (`CHANNEL_OUT_STEREO`, writing zeros to the other channel). Do not use the pan API.
- **Dynamic range.**
  - Fix the system volume at a calibrated index, typically one that leaves headroom for the maximum level you need, and attenuate digitally.
  - A threshold of about 0 dB HL on sensitive headphones may need about −90 to −100 dBFS.
  - 16-bit PCM has about 96 dB of range, so use float. Even so, the DAC noise floor may limit the lowest levels.
  - Verify the lowest usable level per calibrated chain. Report a "floor" if thresholds hit it, as in "better than X dB HL".
- **Volume control.**
  - `AudioManager.getStreamVolume / setStreamVolume(STREAM_MUSIC, idx, 0)`.
  - On EU/"safe media volume" devices, Android caps headphone volume at a "safe" index until the user confirms the warning. You cannot bypass that programmatically.
  - Detect it by reading back the index after setting it.
  - Re-check the volume before every block. Use `AudioManager` volume-change broadcasts or `ContentObserver` on settings, and abort if the user changes volume mid-test.
- **Effects and processing to defeat.**
  - Disable spatial audio for your stream: `AudioAttributes.Builder().setSpatializationBehavior(AudioAttributes.SPATIALIZATION_BEHAVIOR_NEVER)` (API 32+).
  - Check `Spatializer.isEnabled()` and head tracking.
  - Use `USAGE_MEDIA` / `CONTENT_TYPE_MUSIC`, or `USAGE_ASSISTANCE_ACCESSIBILITY`. Test which avoids OEM "voice clarity" processing on target phones.
  - OEM effects (Samsung Dolby Atmos / Adapt Sound / SoundAssistant, Xiaomi / Oppo "headphone effects", system EQ apps such as Wavelet) cannot be reliably detected or disabled by a normal app.
  - Mitigation: a setup checklist, and a calibration verification check (see 2.5).
  - Android accessibility "Mono audio" and "Left/right balance" would invalidate ear-specific testing. They are not readable through public API. Guard against them with an **in-app L/R identification check**: play a tone in one ear and ask which side.
- **Android 14+ Computed Sound Dose (CSD)** is implemented in the framework / HAL per IEC 62368-1 and EN 50332-3. It may warn and then lower volume to RS1 after cumulative exposure [V, [AOSP Sound dose](https://source.android.com/docs/core/audio/sound-dose)]. A 20-minute test at mostly low levels will not trigger it. But a forced volume reduction mid-test would corrupt calibration, so detect index changes as above.
- **Headphone detection.**
  - `AudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)` returns `AudioDeviceInfo` objects.
  - Register `AudioDeviceCallback` to watch for devices being added or removed.
  - Types:
    - `TYPE_WIRED_HEADPHONES` (4) and `TYPE_WIRED_HEADSET` (3)
    - `TYPE_USB_HEADSET` (22) and `TYPE_USB_DEVICE`
    - `TYPE_BLUETOOTH_A2DP` (8)
    - `TYPE_BLE_HEADSET` (26, API 31, LE Audio) [V, [AudioDeviceInfo](https://developer.android.com/reference/android/media/AudioDeviceInfo)]
    - `TYPE_HEARING_AID`
  - `getProductName()` gives a human-readable name [V].
    - USB devices often give the real product string.
    - Wired 3.5 mm headphones give no identity at all, so ask the user to pick the model.
    - Bluetooth gives the device's friendly name. `BluetoothDevice.getName()` needs `BLUETOOTH_CONNECT` permission on API 31+, and users can rename devices, so treat it as a hint.
  - Route explicitly with `AudioTrack.setPreferredDevice(info)`.
  - Abort the test on `ACTION_AUDIO_BECOMING_NOISY` (unplug).
- **Bluetooth caveats.**
  - Codec choice (SBC / AAC / LDAC / aptX / LC3) adds roughly 100–300 ms latency. That is fine for thresholds when timestamps are used.
  - Low-level signals may be gated by earbud noise gates or "silence detection" auto-off. Some earbuds cut audio after silence and fade in at the start of the next sound, which would eat a short tone. Mitigation [I]: keep the stream running with continuous digital silence or −120 dBFS dither between tones.
  - ANC / transparency mode changes the effective level and ambient leakage. Require a fixed mode that is calibrated per model.
  - **Opening the Bluetooth headset microphone switches A2DP to HFP / SCO** (narrowband 8 / 16 kHz, a different gain path). That would destroy playback calibration during the speech test. Always record with the phone's built-in mic (`AudioRecord.setPreferredDevice(TYPE_BUILTIN_MIC)`). Check whether LE Audio devices behave differently.
  - **Recommendation:** in v0.x, allow Bluetooth only in "relative / uncalibrated" mode, or for specific calibrated models, for example Pixel Buds or Galaxy Buds in a fixed mode.

### 2.4 Safety limits [I, with K]
- Clinical audiometers reach 110–120 dB HL. Do not go there.
- Cap at about **90 dB HL at 500 Hz–4 kHz** and about 80 at 250 and 8 kHz, or lower if the calibrated chain cannot do it cleanly (check for distortion).
- Short tonal exposures at these levels are well below damage-risk daily doses. The WHO safe-listening reference is 80 dBA for 40 h/week [K].
- Mark "no response at max" with the standard down-arrow symbol.
- Ramp the level up from comfortable. Never start high.
- Speech presentation: cap at about 80 dB HL.
- Hard-limit the digital output so that no bug can exceed the computed safe dBFS for the selected chain.

### 2.5 Runtime calibration verification [I]
- **Loudness-match check.** At setup, ask the user to compare a 1 kHz tone in the L and R channels. A strong imbalance implies a hardware or balance problem.
- **Repeat check.** Retest 1 kHz and compare.
- **Normal-hearing sanity check.** For users under about 30 with no complaints, thresholds much worse than 25 dB HL at all frequencies suggest a calibration or noise problem rather than hearing loss. Show "check setup".

### 2.6 What to claim
- **With a calibrated, supported headphone:** "Estimated hearing thresholds (dB HL), comparable to screening audiometry. Accuracy roughly ±5–10 dB." Masalski's standard error of about 5 dB in controlled studies supports ±10 dB as an honest real-world statement. [I]
- **Without one:** "Relative results only. Not comparable with a clinical audiogram."
- **Always:** "Not a diagnosis. Cannot detect the type of hearing loss (conductive vs. sensorineural). See an ENT / audiologist if …"

### 2.7 Regulation
- **US.**
  - Audiometers are FDA devices (21 CFR 874.1050, Class II, product code EWO [K]).
  - Apple's tone test is listed under EWO [V, GUDID].
  - Apple's Hearing Aid Feature was granted De Novo DEN230081 (product code SCR, 21 CFR 874.3335) [V, Innolitics].
  - FDA's *General Wellness* and *Policy for Device Software Functions* guidance allows enforcement discretion for some low-risk wellness functions [K]. An app that outputs dB HL thresholds and classifies hearing loss is plausibly an audiometer function.
  - Conclusion: no US public release with diagnostic claims without a regulatory review.
- **EU (MDR 2017/745).** Rule 11: software that provides information used to make diagnostic decisions is Class IIa or higher [K]. "Screening, informational" wording does not avoid Rule 11 if the output is an audiogram plus a loss grade. Hearing-test apps such as Mimi carry CE marks as medical devices [K; not re-verified].
- **Israel (AMAR, Ministry of Health).**
  - The Medical Devices Law 2012 governs.
  - Since 1 January 2025 there are three registration routes: Declaration (Class I), Fast-Track (via a recognized FDA/CE approval), and Normal [V, [Emergo by UL](https://www.emergobyul.com/news/updates-israels-declaration-and-fast-track-routes-medical-devices); [Compliance & Risks](https://www.complianceandrisks.com/blog/registration-of-medical-devices-in-israel-recent-updates/)].
  - I found **no AMAR software-specific (SaMD) guideline** in this session. That needs checking with AMAR or a regulatory consultant.
  - In practice, Israel usually leans on FDA / CE status (Fast-Track). [I]
- **Google Play.**
  - Every app must complete the **Health apps declaration**, including apps on closed and open tracks.
  - Apps regulated as medical devices must provide proof on request and are labelled "Medical Device".
  - Other health apps must state in the store description that the app is "not a medical device and does not diagnose, treat, cure, or prevent any medical condition".
  - The policy scope widened on 2025-08-28 [V, via search summary of [Play Console Help 16679511](https://support.google.com/googleplay/android-developer/answer/16679511?hl=en), [13996823](https://support.google.com/googleplay/android-developer/answer/13996823), [NatLawReview](https://natlawreview.com/article/googles-new-policies-health-apps). `support.google.com` was not fetchable here, so check the live text].
- **Recommendation [I].**
  - Phase 1 (personal or tester use) is low risk. Position it as a self-check, add disclaimers, and avoid words like "diagnosis" and "audiogram equivalent to clinic".
  - Before any public listing, decide between two paths. (a) Stay a wellness / screening app: DIN plus "see a professional" outcomes, and possibly a relative tone test. (b) Pursue CE IIa / FDA 510(k) / AMAR, which is a multi-month project with a QMS (ISO 13485, IEC 62304, ISO 14971) and a clinical validation study.

---

## 3. Bone conduction, masking, tympanometry

- **Bone conduction: not feasible.**
  - It needs a calibrated bone vibrator (B-71 / B-81) on the mastoid with a fixed static force (5.4 N), plus RETFL values (ISO 389-3) [K].
  - Consumer bone-conduction headsets (Shokz etc.) have unknown force and coupling, and output in the low-frequency region that also radiates airborne sound.
  - **Show the bone conduction rows as "Not tested (requires clinic)".** [I]
- **Consequence: the type of loss cannot be determined.** Without bone conduction you cannot separate conductive, sensorineural, and mixed loss.
  - The report should say "type undetermined".
  - Use red-flag rules to recommend ENT, for example asymmetry, sudden onset, ear pain or discharge, low-frequency-only loss (which may be conductive or Ménière's), and a normal pattern with a poor word score. [I]
- **Masking for air conduction (cross-hearing).**
  - Interaural attenuation (IA): about **40 dB** (conservative) for supra-aural / circumaural, and about **55–60 dB** for deep inserts [K].
  - Rule: masking is needed if AC(test ear) − BC(non-test ear) ≥ IA. Without bone conduction, use AC(non-test ear) as a lower bound. That under-calls the need in conductive cases.
  - **Can the app present contralateral narrowband masking?** Technically yes: 1/3-octave-ish narrowband noise centred on the test frequency, calibrated in effective masking level (ISO 389-4). In practice it is risky in an unsupervised test, for four reasons:
    - Plateau-seeking needs clinical judgement.
    - Over-masking happens.
    - Users confuse the noise and the tone.
    - Calibration of noise in dB EM adds error.
  - **Recommendation [I]:**
    - Do **not** implement plateau masking in the MVP.
    - Detect "possible cross-hearing": the test-ear threshold is at least 40 dB (circumaural) or 55 dB (insert) worse than the other ear's.
    - Label that point "unmasked, may be cross-heard (shadow curve)".
    - Recommend a clinic test.
    - Optional later: a single fixed contralateral masking level, for example the non-test-ear AC threshold + 10 dB + 10 dB safety, to *confirm* a profound unilateral loss. It must be clearly labelled.
- **Tympanometry: impossible on a phone.**
  - It needs a hermetic probe seal, an air pump (+200 to −400 daPa), and a 226 Hz probe-tone admittance measurement. That is how ear-canal volume, compliance, and peak pressure are obtained [K].
  - A research system used the phone speaker and mic plus a paper funnel to detect middle-ear fluid acoustically (Chan et al., *Sci Transl Med* 2019, University of Washington) [K, not re-verified]. It is not tympanometry, it was validated in children for effusion only, and it is not suitable to ship.
  - **Substitute [I]:** a short questionnaire (ear fullness, pain, discharge, recent cold, pressure changes, previous ear surgery). A conductive red flag means "see ENT, tympanometry recommended".
  - Also omitted: acoustic reflexes and OAEs. OAEs would need a probe mic and sealed insert, which is research-grade.

---

## 4. Speech audiometry

### 4.1 Standard English materials [K]

- **SRT (spondees).**
  - Material: CID W-1 two-syllable words with equal stress (baseball, hotdog …).
  - ASHA 1988 SRT guideline: descending method with 5 dB steps, or 2 dB steps (Tillman-Olsen / Chaiklin).
  - Use: the SRT should agree with PTA(0.5, 1, 2 kHz) within about ±6–10 dB. That makes it a cross-check of the tone results.
- **Word recognition (open set).**
  - NU-6: 4 lists × 50 words, recorded by Auditec / VA, copyrighted recordings.
  - CID W-22.
  - PB-50.
- **AB lists (Boothroyd).** 15 lists of 10 CVC words, scored by **phoneme** (30 phonemes per list). That gives better reliability per minute. These are UK / Australian-standard.
- **Digits-in-noise (DIN).**
  - Smits et al. (Dutch, 2004/2013 antiphasic).
  - US version by Watson et al. 2012.
  - Many languages exist.
  - Antiphasic presentation (digits inverted in one ear) increases sensitivity to unilateral and conductive loss. In hearZA data, dichotic presentation markedly improved sensitivity [V, [UP repository](https://repository.up.ac.za/handle/2263/70505)].

**Reliability limits.** Thornton & Raffin (1978) binomial critical differences show that a 25-word score has wide 95% intervals. For example, a true 96% could plausibly be measured anywhere from about 84% to 100%. 50 words narrows this [K]. Show confidence intervals in the report. [I]

### 4.2 Hebrew materials
- **Hebrew DIN.**
  - Validated by Fostick, Berlin & Taitelbaum-Swead (conference 2020).
  - Used in Taitelbaum-Swead & Fostick 2024 (CI users and normal-hearing listeners).
  - Format: 25 triplets in speech-shaped noise. Normal young-adult mean −9.7 dB SNR (SD 0.9) [V, [Audiology Research 2024, PMC11130868](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC11130868/); [PMC11172305](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC11172305/)].
  - The authors are at **Ariel University**. Recordings are not public as far as I found. Contact the authors about licensing. [I]
- **Hebrew CVC word recognition test (2026).**
  - JSLHR, published 9 July 2026, open access: *"A Novel Computerized Approach to Constructing Speech Audiometry Materials: Development of a Perceptually Balanced Hebrew Word Recognition Test"*, DOI 10.1044/2026_JSLHR-25-00933.
  - Method: 275 CVC words with difficulty measured in noise (60 normal-hearing listeners), then 200 words assigned to **8 × 25-word lists and 4 × 50-word sets**. No significant differences between lists in quiet.
  - The paper states that standardized Hebrew materials "remain limited" [V via search snippets. The page was not fetchable here (`pubs.asha.org` blocked), so **authors, talker and whether recordings or word lists are published are unverified**].
  - **This is the best candidate source for the Hebrew word test.** Open access may mean the word lists are in the paper or its supplement. Recordings likely need author permission.
- **Older Hebrew CVC lists.** Tel Aviv University's Kishon-Rabin group used 15 lists × 10 Hebrew CVC words in noise (2012 pilot) [V, [De Gruyter JBCPP 2012](https://www.degruyterbrill.com/document/doi/10.1515/jbcpp-2012-0036/pdf)]. Israeli clinics (including Clalit) use in-house Hebrew monosyllable and bisyllable lists, often live voice or legacy recordings [K / I, not verified]. I could **not** find a published, recorded Hebrew spondee / SRT list, Hebrew AB ("HAB") lists, or a Hebrew matrix sentence test.
- **Recommendation [I].** Contact (a) the 2026 JSLHR authors, (b) Taitelbaum-Swead / Fostick (Ariel University) for Hebrew DIN, and (c) Kishon-Rabin (TAU) for clinical Hebrew lists and SRT bisyllables. Ask for permission or licensing for research and non-commercial use. Ask a Clalit audiologist which lists produced your "%DISC 96% @ 55 dB".

### 4.3 Presentation levels and procedure [K]
- **SRT.** Familiarize with the spondee list, start about 30 dB above the estimated PTA, descend in 5 dB steps (or 2 dB), and the threshold is 50% correct.
  - In-app SRT shortcut [I]: start at PTA + 30, use a 2-down-1-up or a Bayesian 50% estimate on bisyllables.
- **Word recognition level.**
  - Classic: **SRT + 30–40 dB SL**, or at MCL.
  - Guthrie & Mackersie (2009) found a level based on the 2 kHz threshold (2 kHz threshold + 25 dB SL for losses below 50 dB HL) maximizes the score better than fixed SL [K].
  - Your report's 55 dB with an SRT of 20 dB corresponds to SRT + 35.
  - App rule [I]: level = max(SRT + 35, 2 kHz threshold + 25), capped at 80 dB HL and at the user's comfort.
  - A full performance-intensity function (several levels) goes beyond the 20 minutes. Offer an optional extra level when the score is below 80% (rollover screen).
- **Masking for speech.** Contralateral speech-shaped noise when the presentation level − IA ≥ the non-test-ear threshold. For the MVP the same advice as in §3 applies: flag it, don't mask.
- **Level normalization.**
  - Calibrate speech by the RMS of a 1 kHz calibration tone equal to the long-term speech RMS (standard clinical practice), or by equalizing each word's active-speech level (ITU-T P.56).
  - 0 dB HL for speech ≈ 20 dB SPL for supra-aural (ANSI S3.6 speech RETSPL: about 12.5 dB above the 1 kHz tone RETSPL) [K, verify the exact value].
- **Recording specification.**
  - 48 kHz / 24-bit WAV masters. Ship as FLAC or 16-bit WAV, never lossy-encoded.
  - Anechoic or very dry room.
  - One native talker per language. A female talker is common in newer materials, but match the norms of the list you license.
  - Carrier phrase optional. For Hebrew, a common one is "תגיד/תגידי ..." ("Say …"), which helps level setting.
  - A 1 kHz calibration tone at the speech RMS.

### 4.4 TTS vs. recorded talker [I]
- **Do not use TTS for validated tests.** Norms such as list equivalence, the 50% thresholds, and DIN norms belong to the specific recordings.
- Neural TTS has unnatural micro-prosody and different spectral properties. It also tends to be more intelligible in noise, which shifts thresholds.
- TTS is fine for instructions.
- If no licensed Hebrew recordings are available:
  - record your own with a native talker, with a signed release and a professional mic, and
  - run a normalization study (about 20–30 normal-hearing listeners) to equalize word difficulty, as the 2026 paper did.
- Until then, label Hebrew word scores "not normed".

### 4.5 Response mode: repeat aloud with ASR vs. alternatives [I]

| Mode | Pros | Cons |
|---|---|---|
| **Repeat aloud + ASR** | Closest to the clinic | ASR errors on isolated words (no context); accents; speech impairment; mic issues; the user may say a near-miss |
| **Repeat aloud + helper scores** | Valid, clinic-like | Needs a second person |
| **Typed** | Precise | Spelling errors (Hebrew homophones: ט/ת, כ/ח, ק/כ, א/ע); slow; elderly users |
| **Closed set (4–6 choices)** | Robust, no ASR, scoring exact | Not the same test as an open-set WRS. Guessing floor (25% for 4-AFC). Needs specially designed foils (rhyme-test style) |
| **DIN with keypad** | Fully validated self-test, closed set 0–9 | Measures speech-in-noise, not the clinic's %DISC |

**Recommendation.**
- DIN uses the keypad (standard).
- The word test uses open-set repeat aloud with ASR. Show the top hypotheses after each word, or batch-review at the end, with a "correct / wrong" toggle that the user or a helper can override. Log both the ASR-only and the confirmed scores.
- Offer a closed-set fallback when ASR is unavailable.

---

## 5. Speech recognition for scoring

### Options
- **Android `SpeechRecognizer` (platform).**
  - `SpeechRecognizer.createOnDeviceSpeechRecognizer()` (API 31+).
  - Check support with `checkRecognitionSupport(intent, executor, callback)` (API 33+). It returns installed, pending, supported and online languages [V, [SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer); [RecognitionSupport](https://developer.android.com/reference/android/speech/RecognitionSupport)].
  - Download a model with `triggerModelDownload(intent)` [V].
  - Use `EXTRA_PREFER_OFFLINE`, `EXTRA_MAX_RESULTS` (n-best), and `EXTRA_BIASING_STRINGS` (API 33+, bias toward the current word list) [K].
  - **On-device he-IL availability is device- and Google-app-dependent and was not verified.** Test at runtime and fall back gracefully.
  - Results come from Google's recognizer. Network recognizers send audio to Google.
- **Google Cloud Speech-to-Text v2.** he-IL is supported [K]. Supports phrase hints / adaptation. Requires network, API cost, and a privacy disclosure (health-adjacent voice data).
- **Whisper.**
  - Stock small or base Whisper is poor in Hebrew.
  - **ivrit.ai** Hebrew fine-tunes of large-v3(-turbo) reach about 5–6% WER on Hebrew sentence benchmarks [V, [Ozen-v1 card](https://huggingface.co/itayinbar/Ozen-v1); [Interspeech 2025 ivrit.ai](https://www.isca-archive.org/interspeech_2025/marmor25_interspeech.pdf)]. Distilled small models reach about 9–16%.
  - On a phone, run via **whisper.cpp** (JNI) with a quantized model. Turbo is roughly 500–800 MB quantized [K/I]. Expect 1–3 s per short clip on a recent flagship, slower on mid-range.
  - Sentence WER does not equal isolated-word accuracy. Isolated words lose the language-model context. Expect worse.
  - Whisper's `initial_prompt` can bias toward the list. Constrained decoding (restricting the output to the vocabulary) is possible in whisper.cpp via grammar (GBNF) support [K, verify].
- **Vosk (Kaldi).** Small offline models. Supports a **restricted grammar / vocabulary** via the `KaldiRecognizer(model, rate, grammarJson)` form, which is ideal for closed-vocabulary scoring [K]. I am not aware of an official Hebrew Vosk model. Check [alphacephei.com/vosk/models](https://alphacephei.com/vosk/models) [unverified]. English is fine.

### The scoring problem is better framed as verification, not transcription [I]
- You know the target word. Score "did the user say word W?".
- Generic transcription plus string-match is weaker.

### Scoring algorithm [I]
1. Run ASR with n-best (5–10), biased to the whole list, or to the target plus its phonetic neighbours, so that bias doesn't simply force the target.
2. Convert hypotheses and the target to a **phonemic** representation.
   - English: CMUdict / g2p.
   - Hebrew: unpointed spelling is ambiguous. Store each list word with its phonemic transcription (for example `/kaf/`) and a list of accepted spellings and homophones (for example כף / קף-type errors).
   - Strip niqqud and normalize final letters (ך→כ, ם→מ, ן→נ, ף→פ, ץ→צ) on ASR output.
3. Classify as correct if any hypothesis has phoneme edit distance 0 to the target or to an accepted variant.
   - Optionally use AB-style phoneme scoring: count matching phonemes out of 3 per CVC word.
4. Treat low confidence (or empty) results as "uncertain". Send them to user or helper review, or replay.
5. Validate the ASR scorer against human scoring on recorded responses. Aim for at least 95% agreement before trusting ASR-only scores.

### Microphone and audio routing [K/I]
- Record **after** the word finishes (half-duplex), during a 3–4 s response window. This avoids capturing playback leaking from the headphones.
- Use the phone's **built-in mic**, never the Bluetooth headset mic (A2DP→HFP switch, see 2.3).
- With wired headsets that have a mic (`TYPE_WIRED_HEADSET` / USB), the inline mic is fine and closer to the mouth. Check that it doesn't change the playback path.
- Use `AudioSource.VOICE_RECOGNITION`. It is meant to be unprocessed (no AGC), whereas `VOICE_COMMUNICATION` applies AEC/NS.

**Recommended approach [I].**
- English: Android on-device recognizer with biasing as the default, plus a Vosk grammar mode as the offline fallback.
- Hebrew: Android on-device he-IL if available. Otherwise whisper.cpp with an ivrit.ai distilled model as an optional download.
- Always keep the human-review override.

---

## 6. Results and report

### Audiogram rendering (ASHA 1990 *Guidelines for Audiometric Symbols*) [K]
- **Axes.**
  - X axis: log frequency with octaves equally spaced, 125/250–8000 Hz (the Clalit chart goes to 12 kHz; keep 250–8k).
  - Y axis: dB HL **increasing downward**, −10 to 120, gridlines every 10 dB.
  - Aspect ratio: **one octave equals 20 dB**.
- **Symbols.**
  - Right ear AC unmasked **O (red)**. Left ear AC unmasked **X (blue)**.
  - Masked AC: right **△**, left **□**.
  - BC unmasked: right **<**, left **>**. BC masked: right **[**, left **]**. These are not used in the app.
  - No response: the symbol with a downward arrow, angled outward (right ear down-left, left ear down-right).
  - Connect AC symbols per ear with a solid line. Do not connect through no-response points.
- **RTL.** **Do not mirror the chart in RTL (Hebrew) layout.** The frequency axis always runs low to high, left to right. Only the labels are translated. In Compose, wrap the chart in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)`. [I]
- **Shading.** Optionally shade the normal range (≤20 or ≤25 dB HL).
- **Uncalibrated mode.** Optionally show a "speech banana", but only in calibrated mode.

### Classification [V / K]
- **WHO 2021.**
  - Based on the better-ear PTA of 0.5, 1, 2 and 4 kHz.
  - Grades: normal <20; mild 20–<35; moderate 35–<50; moderately severe 50–<65; severe 65–<80 (inferred from contiguous bands, check against the WHO report); profound 80–<95; complete ≥95. Unilateral loss: <20 in the better ear and ≥35 in the worse [V partial, [table](https://www.jhhearingaids.com/hearing-loss/grades-of-hearing-aids)].
- **Clark (1981) / ASHA**, per ear, PTA of 0.5/1/2 kHz (or including 4 kHz): normal −10 to 15, slight 16–25, mild 26–40, moderate 41–55, moderately severe 56–70, severe 71–90, profound 91+ [K].
- **Israeli clinics.** The scale and PTA definition in use are unknown to me. Ask the Clalit audiologist.
- **Recommendation.** Show the WHO grade for the person and a per-ear Clark/ASHA descriptor. Make the scale a setting.

### Pattern and red-flag rules (summary text) [I, with K]
- **Configuration**: flat, sloping (high-frequency), rising (low-frequency), notch at 3–6 kHz (noise-induced pattern), cookie-bite.
- **Asymmetry**: an interaural difference of at least 15 dB at 2 or more adjacent frequencies, or at least 20 dB at one frequency. AAO-HNS and other criteria vary, so pick one and cite it. Text: "Asymmetry: an ENT evaluation is recommended."
- **Sudden hearing loss** (onset within 72 hours, by questionnaire): "urgent ENT / emergency evaluation". Corticosteroid treatment is time-sensitive (AAO-HNS 2019 SSNHL guideline) [K].
- **Speech vs. tones**: a word score disproportionately poor for the PTA, or a DIN SRT inconsistent with the PTA, means "refer".
- **Catch-trial false positives too high**: "results unreliable".
- **Fixed phrase**: "Type of hearing loss (conductive / sensorineural) cannot be determined without bone conduction and tympanometry, which require a clinic visit."
- **Generation.** Use **templated** sentences, not an LLM, so the text is deterministic and translatable. Use ICU MessageFormat plurals and gender in `strings.xml` for Hebrew, which has gendered imperatives (הקש/הקישי). Use either gender-neutral phrasing or a user-selected form.

### PDF export [K]
- `android.graphics.pdf.PdfDocument` with Canvas drawing. Draw text through `StaticLayout`, which handles bidi and shaping for Hebrew. Embed a font with Hebrew glyphs (Noto Sans Hebrew / Rubik / Assistant, all OFL).
- Alternative: render the Compose UI to a bitmap. Text is then not selectable, so it is worse.
- Content: patient-entered name or age, date, device and headphone model, calibration mode, ambient noise, the chart, a threshold table, speech results with confidence intervals, disclaimers, and the app version.
- Share through `FileProvider` with an `ACTION_SEND` intent.

### Storage and privacy [V / K / I]
- **Storage.** Local only by default: Room DB with SQLCipher, or EncryptedFile / Jetpack Security (deprecated, but Tink-based alternatives exist). No account, no cloud. Export is user-initiated. Store audio responses only temporarily, or opt in for ASR validation.
- **Israel.** Privacy Protection Law **Amendment 13 in force since 14 August 2025**. Health data is "data of special sensitivity". The Privacy Protection Authority now has administrative fining powers. Duties include a DPO for some controllers and database registration in some cases [V, [Pearl Cohen](https://www.pearlcohen.com/israel-significant-amendment-to-the-privacy-law-takes-effect/); [Baker McKenzie](https://resourcehub.bakermckenzie.com/en/resources/global-data-and-cyber-handbook/emea/israel/topics/key-definitions)]. Local-only storage minimizes your obligations.
- **GDPR.** Health data falls under Art. 9 (special category). It needs explicit consent if you process it. On-device-only processing with no transfer to you largely keeps you out of the controller role [K/I, get legal advice before cloud features].
- **HIPAA.** Applies only to covered entities and their business associates. A direct-to-consumer app is generally not covered, but the **FTC Health Breach Notification Rule** (amended 2024) does apply to health apps [K].
- **Google Play.** Complete the Data safety form and the Health apps declaration. Requesting `RECORD_AUDIO` needs a clear in-app rationale.

---

## 7. Tech stack recommendation

### Recommendation: native Android (Kotlin + Jetpack Compose) with a pure-Kotlin core module that is ready for Kotlin Multiplatform [I]

**Reasons.**
1. **Audio precision and control.**
   - Direct `AudioTrack` (float, `setPreferredDevice`, `getTimestamp`) or Oboe / AAudio.
   - `AudioDeviceCallback`, spatializer control, `AudioRecord` source selection.
   - Flutter and React Native would need platform channels or native modules for every one of these anyway, so you would write the hard part natively regardless. Their audio plugins (just_audio, expo-av) give no sample-accurate, calibrated, per-channel float control.
2. **Speech recognition.** `SpeechRecognizer` APIs (on-device, biasing, support checks) are first-class in Kotlin. whisper.cpp and Vosk have Android JNI bindings.
3. **Localization and RTL.**
   - Android resources (`values-iw`/`values-he`; Android historically maps `he`→`iw`, so provide both or use `he` with AGP resource handling) [K].
   - `android:supportsRtl="true"`, so Compose mirrors automatically.
   - Per-app language via `AppCompatDelegate.setApplicationLocales` / `LocaleManager` (API 33).
4. **Path to iOS.**
   - Put the procedure state machines (Hughson-Westlake, DIN adaptive track, SRT), scoring, classification, report model and calibration math in a `:core` module with no Android imports. Promote it later to KMP `commonMain`.
   - On iOS, write a thin native audio layer: `AVAudioEngine` + `AVAudioSourceNode`, `AVAudioSession` route change notifications for headphone detection, and `SFSpeechRecognizer` (he-IL supported by Apple [K]).
   - UI: Compose Multiplatform or SwiftUI.
   - Flutter is the main alternative if you want one UI codebase. But audio, ASR and device detection would still be about 40% of the app in per-platform native code.

**Suggested modules.**
- `:core`: pure Kotlin. Procedures, psychometric simulation, scoring, classification, i18n keys.
- `:audio`: Android. Tone and noise generator, player, recorder, device monitor, calibration store.
- `:asr`: interface plus platform, Vosk and whisper implementations.
- `:app`: Compose UI, the audiogram Canvas, PDF.

**Signal details.**
- Generate the sine with a phase accumulator in double precision, writing float frames.
- Raised-cosine ramp of 25 ms.
- Level: `amp = 10^((dBHL + RETSPL[f] − SPLat0dBFS[f])/20)` (the formula assumes the calibration table is per the user's headphone chain).
- Noise for DIN and masking: precomputed speech-shaped noise matched to the long-term spectrum of the digits; narrowband noise via FFT filtering.
- Pre-render each stimulus to a buffer to avoid real-time glitches. Use `MODE_STATIC` `AudioTrack` for short tones, or streaming with continuous silence (better for Bluetooth gating; see 2.3).

**Testing strategy [I].**
1. **Unit tests** for the procedures, using a **simulated listener**: a psychometric function with a true threshold, slope about 5–10%/dB, plus lapse and false-alarm rates. Monte Carlo (1,000 runs) checks bias and SD of the estimated threshold, the number of trials and the time. Run in CI on the JVM, which is fast.
2. **Golden-signal tests.** Render stimuli offline and assert, in dBFS:
   - RMS level,
   - frequency (FFT peak),
   - ramp shape,
   - absence of energy in the wrong channel (below −90 dB),
   - spectral splatter.
3. **Electrical loopback on device.** Feed the phone's USB-C / 3.5 mm output into a USB audio interface on a PC. Verify actual output levels across volume indices, channel separation, effects being applied (compare with the digital signal), and latency.
4. **Acoustic calibration and verification.**
   - A Class 1 or 2 sound level meter plus a coupler or artificial ear. At minimum, a flat-plate coupler with a calibrated measurement mic (for example a UMIK-1 with its calibration file) and a 94 dB / 1 kHz calibrator.
   - Phone SLM apps are not accurate without a calibrated external mic. NIOSH SLM (iOS) with an external mic is the best-known exception [K].
   - Re-verify per release.
5. **Human validation.** Test about 10 people, ideally including people with known loss and a recent clinic audiogram (yourself first). Compare per-frequency differences. Report mean, SD, and % within ±10 dB.

**CI.** GitHub Actions: `./gradlew lint test assembleRelease` plus ktlint/detekt. Instrumented tests on Firebase Test Lab or a Gradle managed device for UI and RTL screenshots (Paparazzi or Roborazzi screenshot tests in LTR and RTL). Sign release builds through repository secrets.

**SDK targets.**
- Play requires `targetSdk` **36 (Android 16)** for new apps and updates from 31 August 2026 [V, [developer.android.com target-sdk](https://developer.android.com/google/play/requirements/target-sdk)].
- `minSdk` 26–29 is reasonable. On-device ASR support checks need API 33, so degrade gracefully below that.

---

## 8. Distribution for a first-time publisher

1. **Sideloaded APK (fastest, free).**
   - Build a signed APK, share the file, and the tester enables "install unknown apps".
   - **Caveat:** Google's **developer verification** requirement for apps on certified devices started enforcement on 30 September 2026 in Brazil, Indonesia, Singapore and Thailand. It expands **globally in 2027**. Unverified apps then require an "advanced flow" with a restart and a 24-hour wait. ADB installs are exempt [V, [Android Authority](https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/); [The Hacker News](https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html); [Android developer verification guide](https://developer.android.com/developer-verification/guides)].
   - Israel is not in the first wave. Register in the Android Developer Console / Play Console anyway, so sideloads keep working in 2027.
2. **Firebase App Distribution.** Free. Upload an APK or AAB, invite testers by email, and they install via the App Tester app. Good for 5–50 testers with no Play review [K].
3. **Google Play Console.**
   - **One-time $25 registration** with identity verification (ID, and possibly a device verification step) [K].
   - **Tracks:** Internal testing (up to 100 testers, near-instant, no full review), Closed testing (email lists or Google Groups), Open testing, Production.
   - **New personal accounts** (created after 13 November 2023) must run a **closed test with at least 12 testers opted in continuously for 14 days** before applying for production access. It was 20 testers until about December 2024. Internal testing does not count. After the 14 days you answer a production-access questionnaire about testing feedback and changes [V via secondary sources: [testerscommunity](https://www.testerscommunity.com/blog/google-play-closed-testing-requirements-2026), [Choicely](https://www.choicely.com/blog/google-play-12-tester-rule). The official page [answer/14151465](https://support.google.com/googleplay/android-developer/answer/14151465) was not fetchable in this session, so confirm in Play Console]. Organization accounts (requiring a D-U-N-S number) are exempt.
   - **For production you need:**
     - a privacy policy URL,
     - the Data safety form,
     - the **Health apps declaration** and medical disclaimer (see 2.7),
     - a content rating questionnaire,
     - target audience (do not target children without the Families policy),
     - a `RECORD_AUDIO` justification,
     - store listing assets in English and Hebrew,
     - an AAB signed with Play App Signing,
     - target SDK 36.
4. **Apple, later.**
   - Apple Developer Program costs **$99/year** [K].
   - TestFlight: up to 100 internal and 10,000 external testers. The first external build goes through Beta App Review.
   - App Store Review Guideline 1.4.1 (Physical Harm) requires apps that make health measurements to disclose methodology and possibly show regulatory clearance [K]. Expect scrutiny for anything called a "hearing test".

---

## 9. Risks and scope recommendation

### Main risks
1. **Calibration validity.** Without supported-hardware calibration the dB HL numbers are not meaningful. This is the biggest risk to the product.
2. **Regulatory.** An audiogram with a grade is a medical-device function in the US and EU, and probably in Israel.
3. **Hebrew materials.** There is no public recorded Hebrew SRT or word list. Licensing is needed, or your own recordings plus a norming study.
4. **ASR on isolated Hebrew words.** Accuracy is unproven. The human-review fallback is needed.
5. **Ambient noise and unsupervised behaviour.** False positives and noise-limited low frequencies.
6. **OEM audio effects and Bluetooth DSP**, which break calibration silently.

### Proposed roadmap [I]

**v0.1 (about 4–6 weeks).**
- Android, English UI with the Hebrew UI strings scaffolded and RTL from day one.
- Headphone detection, L/R check, volume lock, ambient check.
- Air-conduction audiogram (Hughson-Westlake, catch trials, retest).
- One calibrated wired headphone plus one USB-C dongle, with biological or coupler calibration. "Relative" mode for everything else.
- Audiogram chart, local storage, PDF.
- Validate on yourself against your Clalit audiogram.

**v0.2.**
- Digits-in-noise: diotic, then antiphasic.
- English uses existing norms. Recording your own English digits needs norming. Alternatively, license the Smits / Watson / hearX materials.
- Hebrew DIN: license from Ariel University, or record and norm.
- Keypad response.

**v0.3.**
- English word recognition: an AB-style or NU-6-style list with licensed recordings.
- ASR scoring plus human review, with confidence intervals.
- Optional SRT.

**v0.4.**
- Hebrew word recognition: the 2026 JSLHR lists if available, the Hebrew ASR pipeline, and the full Hebrew report text.

**v0.5.**
- More calibrated headphone models including one or two Bluetooth models in fixed modes.
- Cross-hearing flags, red-flag questionnaire, closed testing with 12+ testers.

**Later.**
- Regulatory decision. iOS via KMP. Bayesian fast audiometry.

### What not to claim
- "Diagnosis", "replaces audiologist", or "clinical audiogram".
- Type of loss.
- Middle-ear status.
- Hearing-aid fitting.
- Accuracy numbers you have not measured yourself.

---

## 10. Prioritized plan (summary)
1. Decide the supported hardware and the calibration route. Buy the headphone and dongle, and arrange coupler access or a group of 10–20 normal-hearing young listeners.
2. Build `:core` procedures with simulated-listener tests, then `:audio` with loopback verification.
3. Build the audiogram UI, report and PDF (LTR chart in both locales).
4. Validate against your own and a few others' clinic audiograms.
5. Add DIN. Pursue Hebrew DIN licensing in parallel; start the emails now, since they are the long lead time.
6. Add word recognition with ASR plus review, English first.
7. Regulatory and positioning decision before any public listing. Meanwhile distribute through Firebase App Distribution or Play internal testing, and start the 12-tester × 14-day closed test early.

## 11. Questions for you
1. **Audience.** Personal / family tool, a small tester group, or a public app? This sets the regulatory and calibration effort.
2. **Hardware.** Are you willing to require one specific wired headphone model plus a USB-C dongle for "calibrated" results? Which phone(s) will you test on?
3. **Calibration access.** Can you get time with a sound level meter and coupler (an audiology department or a hearing-aid lab)? If not, can you recruit 10–20 normal-hearing young adults?
4. **Hebrew materials.** May I draft outreach emails to the 2026 JSLHR authors, Ariel University (Hebrew DIN) and TAU (Kishon-Rabin)? Or would you rather record your own talker and run a norming study?
5. **Response mode.** Is a "repeat aloud" test with a review screen acceptable, or must it be fully automatic? Is a helper allowed?
6. **Duration.** Is about 20 minutes acceptable as the default, with a roughly 30-minute "full" option that adds 3k/6k, SRT and 50-word lists?
7. **Data.** Is local-only storage acceptable? Is there any need for cloud sync or sharing with a clinician?
8. **Classification and report.** Do you want the WHO 2021 grade, the Clark/ASHA scale, or whatever Clalit uses? Could you share an anonymized copy of the Clalit report so its layout and wording can be mirrored?
9. **iOS timing.** If iOS is likely within about 12 months, start KMP from day one rather than "KMP-ready".
