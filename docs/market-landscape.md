# hearo: competitive landscape and market report

Prepared 2026-11-07. Companion to `hearo/docs/research.md` (technical and regulatory background) and `hearo/docs/decisions.md`. Material covered there (the Apple GUDID/De Novo facts, Masalski biological calibration, the hearZA/Hebrew DIN validation numbers, Play health-app policy, Israeli privacy law) is not repeated here. This report only adds to it.

## How to read this report

- **[V]**: confirmed this session against the linked source. **Caveat:** in this session WebFetch and curl were blocked for every host (apple.com, samsung.com, fda.gov, ncbi, hearingtracker.com and others). So "[V]" here means the claim appeared in **search-engine extracts of the linked page**. I did not open the full page. License data for GitHub repos was read from GitHub search metadata through `gh`. Treat [V] as "sourced, not read in full". Re-open the primary page before you quote it in a regulatory or investor document.
- **[K]**: standard knowledge I did not re-check this session.
- **[I]**: my inference or recommendation.
- **Hosts that need allow-listing to re-verify:** apple.com, news.samsung.com, samsungmobilepress.com, accessdata.fda.gov, ecfr.gov, hearingtracker.com, hearingreview.com, mimi.io, mobihealthnews.com, pmc.ncbi.nlm.nih.gov, plus the support.google.com and pubs.asha.org hosts already flagged in research.md.

---

## 0. Executive summary

1. **The core of hearo is crowded, and on Android specifically.** The parts in question are self-administered pure-tone audiometry plus digits-in-noise on any headphones. Key players:
   - **e-audiologia "Hearing Test" (Masalski):** an Android app with pure-tone audiometry, a DIN test in 17 languages, and preset calibration coefficients for wired and Bluetooth headphones. It reports about 2.5M installs [V].
   - **hearWHO:** a free DIN test on Android and iOS [V].
   - **Sennheiser Hearing Test:** works with any headphones and produces an audiogram [V].
   - **Samsung:** an FDA-cleared OTC Hearing Aid feature, plus a Hearing Test registered as a medical device, for Galaxy Buds3 Pro / Buds4 Pro on Galaxy phones. US rollout is due Q4 2026 (some reports say early 2027), and it also has Korean MFDS approval [V].
   - **Apple:** covers iOS in 100+ countries, and Israel got the Hearing Test in mid-2025 [V].
2. **Hebrew is the only clean gap I found, and it matters only in Israel.**
   - I found no hearing-test app with a Hebrew UI or Hebrew speech material.
   - Hebrew DIN and the 2026 Hebrew CVC lists exist only as research materials.
   - One caveat: e-audiologia's 17-language DIN list was not visible to me, so Hebrew might be in it. Check this first.
   - In the US and Germany, Hebrew gives no advantage. A German launch would need German materials, and those already exist and are licensed (the German digit triplet test; the OLSA/Oldenburg matrix test from HörTech) [V/K].
3. **ASR-scored open-set word recognition is differentiated as a product but not as an idea.**
   - It exists in research: Oldenburg's Alexa "test my hearing" self-test with ASR (AUC 0.95) [V]; the Manchester TTS+ASR DIN (*Trends in Hearing* 2025, n=31) [V]; and the WordRec smartphone app (86% within a clinically acceptable margin, n=37 ears) [V].
   - I found no consumer product that scores clinic-style word lists automatically.
   - It is also the hardest part of hearo to validate, and it is the part closest to "diagnostic" speech audiometry.
4. **Relative calibration on any headphones is not a differentiator.** It is the industry default for free tests (Sennheiser, Signia, Phonak, Soundly, Audicus, hearWHO). Calibrated competitors such as Apple, Samsung and e-audiologia are *more* accurate, so for hearo it is a weakness to disclose rather than a feature.
5. **Triage and a clinic-style PDF give modest differentiation.**
   - Today's tests output a binary "see a professional" message, or an audiogram that funnels into hearing-aid sales.
   - An urgency triage (for example, sudden-onset or asymmetry red flags leading to "urgent ENT") is uncommon, but nothing protects it, and claiming triage pushes the regulatory class *up* [I/K].
6. **The buyer reality.**
   - **US:** online tests are mostly **lead-generation funnels** for payers' hearing benefit administrators (UnitedHealthcare Hearing, TruHearing), employer benefits (TunedCare), and retail kiosks (hearX hearKiosk, MedRx, SHOEBOX) [V]. FDA status is a real gate: the FDA forced UnitedHealth's hi HealthInnovations to pull its online hearing test in 2012 [V].
   - **Germany:** a hearing aid on GKV requires an ENT (HNO) prescription [V], so "triage" means routing people to an HNO or Hörakustiker. Hörakustiker chains run free tests for lead generation. Barmer has promoted the Mimi app [V]. DiGA reimbursement needs CE class I/IIa(/IIb), BSI TR-03161 and a proven positive care effect [V]. That is a poor fit for a screening app.
7. **Recommended positioning [I].** "A Hebrew-first, clinic-style home hearing check that includes speech understanding". Sequence:
   - Israel testers.
   - A published Israeli validation study, with Ariel University, TAU or Sheba.
   - Pilots with Israeli HMOs or dispensers.
   - CE (MDR IIa) and AMAR fast-track.
   - Germany or the US only with local-language materials and a regulatory budget, or as a licensed ASR-scoring component. Do not lead with the US consumer market, where Apple and Samsung give away FDA-regulated tests for free.

---

## 1. Competitor inventory

### 1.1 Big tech (bundled with earbuds)

| Product | What it does | Regulatory | Markets / languages | Notes |
|---|---|---|---|---|
| **Apple Hearing Test + Hearing Aid** (AirPods Pro 2/3, iOS 18+) | About 5 min pure-tone test; OTC hearing-aid mode built on it | US: see research.md (EWO / De Novo). EU: the hearing test reached France at launch but the hearing-aid mode was held back for regulatory reasons [V, [AppleInsider](https://appleinsider.com/articles/24/11/21/apple-brings-hearing-tests-to-airpods-pro-2-in-france-but-holds-back-key-feature)]. The UK needed government OTC approval [V, [Apple UK newsroom](https://www.apple.com/uk/newsroom/2024/12/hearing-test-feature-available-today-with-airpods-pro-2)] | "More than 100 countries and regions" (Dec 2025) [V, [Apple newsroom](https://www.apple.com/bh/newsroom/2025/12/hearing-test-and-hearing-aid-features-available-today-with-airpods-pro)]. **Israel got the Hearing Test in July 2025** [V, [MacRumors](https://www.macrumors.com/2025/07/15/airpods-pro-2-hearing-aid-expansion/)]. Israeli hearing-aid mode not found; in Nov 2024 neither feature worked in Israel [V, [Apple Community](https://discussions.apple.com/thread/255821684)]. Germany listed on Apple's availability pages [V, [Apple UK availability](https://www.apple.com/uk/airpods-pro/feature-availability)]. Hebrew UI for the test: unknown | Validation: Kruger, Manchaiah & Swanepoel (*Otolaryngol–HNS* 2026): **86.5% of thresholds within 10 dB** of audiologist PTA across 400 comparisons [V via [UP repository](https://repository.up.ac.za/items/29b0be23-c977-42ac-8b8a-87c1679b7102)]. A small AJA 2025 pilot (n=25) [V, [AJA](https://pubs.asha.org/doi/10.1044/2025_AJA-25-00192)]. Apple white paper [V, [PDF](https://images.apple.com/health/pdf/Hearing_Health_Features_on_AirPods_Pro_2_October_2024.pdf)]. iOS only, so not a direct Android rival. |
| **Samsung Galaxy Buds Hearing Test + Hearing Aid** (Buds3 Pro, Buds4 Pro; Galaxy phone with One UI 8+) | About 5 min pure-tone air-conduction test per ear. Classifies each ear as normal or one of four loss stages. Results go to Samsung Health. Built in the Buds settings, with no separate app [V] | **FDA-cleared** OTC Hearing Aid feature (announced around 11 Aug 2026). The test is "registered as SaMD" [V, [Samsung newsroom](https://news.samsung.com/global/samsungs-hearing-aid-feature-on-galaxy-buds-cleared-by-fda); [Droid-Life](https://www.droid-life.com/2026/08/11/galaxy-buds-get-hearing-aid-clearance-from-fda/); [Medical Device Network](https://www.medicaldevice-network.com/news/samsung-enters-otc-hearing-aid-space-with-fda-clearance-for-galaxy-earbud-feature/)]. **Korean MFDS** approval as digital medical device software [V, [SamMobile](https://sammobile.com/news/galaxy-buds-3-4-pro-hearing-aid-feature-release)]. 510(k) number not seen | US and "select approved markets" in Q4 2026; SamMobile says early 2027 [V]. Developed with Australia's NAL and Samsung Medical Center [V] | **The most important Android competitor.** It applies only to the Galaxy phone + Galaxy Buds Pro combination, and is calibrated end-to-end. |
| **Samsung Adapt Sound** (all Galaxy phones, for years) | 1–2 min beep test in each ear. Builds a sound-personalization profile. Works with wired or Bluetooth headphones [V, [SamMobile](https://www.sammobile.com/news/one-ui-tip-get-the-best-audio-quality-adapt-sound/)] | Not medical | Every Galaxy phone | Shows that "tone test on any headphone on Android" is a free OS feature. |
| **Google / Pixel** | **No hearing test found** for Pixel or Pixel Buds as of Nov 2026. Related features: "Hearing Wellness" volume protection, LE Audio hearing-aid support in Android 16 [V, [Android Authority](https://www.androidauthority.com/android-16-hearing-aids-3520978/)] | — | — | A gap today, but Google could close it at any time [I]. |
| **Sennheiser Hearing Test** (Sonova Consumer Hearing) | Under 5 min, works with "any pair of headphones", wheel-based response, produces a personal audiogram, history and reminders [V, [Sennheiser](https://sennheiser-hearing.com/sennheiser-hearing-test-app)] | Not found | iOS and Android, free. Android update Oct 2024 [V]. Languages not found | The closest "any-headphone audiogram" consumer rival. Conversation Clear Plus is reported as discontinued [V, [HearingTracker DE](https://www.hearingtracker.com/de/hearing-aids/sennheiser-conversation-clear-plus)]. |
| **Jabra Enhance (GN)** | In-app "self-tuning" test for Enhance Select hearing aids, 8–10 min; you can also upload a clinic audiogram [V, [Jabra help](https://help.jabraenhance.com/personalization/what-is-the-self-tuning-test)] | Part of the hearing-aid product | US | Bound to the hearing aid. Jabra Sound+ "MySound" not verified. |
| **Bose / Lexie (LXE Hearing)** | Lexie B2 Plus had an optional in-app hearing test [V, [Hearing Review](https://hearingreview.com/hearing-products/hearing-aids/otc/lexie-hearings-new-otc-hearing-aid-includes-in-app-hearing-test)] | OTC hearing aid | **LXE (Eargo + Lexie) began winding down US operations in July 2026.** Support guaranteed only to 15 Sept 2026. GN wrote its 9% stake down to zero. hearX's professional business continues outside the US [V, [HearingTracker](https://hearingtracker.com/news/eargo-and-lexie-parent-lxe-hearing-begins-u-s-wind-down); [Hearing Review](https://hearingreview.com/hearing-products/hearing-aids/otc/lxe-hearing-announces-wind-down-of-us-operations-affecting-eargo-lexie-and-go-brands)] | **Instructive.** Even $100M of capital (Patient Square, 2025 merger [V, [HLTH](https://hlth.com/insights/news/eargo-and-hearx-merge-to-form-lxe-hearing-with-100-million-investment-2025-04-02)]) did not make DTC hearing care work in the US. |
| **Sony CRE-C10/E10** | Self-fitting OTC hearing aids with in-app customization [V, thin, [Digital Trends](https://www.digitaltrends.com/mobile/best-over-the-counter-hearing-aids/)] | FDA OTC | US | Test details not verified. |

### 1.2 Consumer and clinical apps (non-big-tech)

| Product | Platform | Tests | Calibration | Regulatory | Languages | Model / price | Evidence |
|---|---|---|---|---|---|---|---|
| **e-audiologia "Hearing Test"** (Masalski, Poland) | **Android** (and others) | Pure tone + **DIN** (DIN "in 17 languages" built as an extension of this app) [V, [multilingual DIN paper record](https://katalog.lib.cas.cz/KNAV/EdsRecord/cmedm,39207918)] | Biological calibration. **Predefined coefficients for wired and Bluetooth headphones** [V, [AppBrain](https://www.appbrain.com/app/hearing-test/mobile.eaudiologia)] | CE status not found | 17 DIN languages (Hebrew **unverified**) | Freemium. Pro adds offline mode and cloud sync [V]. About 2.5M installs, 15.2K ratings [V] | Strongest of the group: JMIR 2016/2018 (research.md) and the 2024 MDER review [V, [Dovepress](https://dovepress.com/the-hearing-test-app-for-android-devices-distinctive-features-of-pure--peer-reviewed-fulltext-article-MDER)] |
| **hearWHO** (WHO + hearX) | iOS, Android | DIN, under 2 min | SNR-based, so it needs no calibration | Not marketed as a device; disclaims liability [V] | WHO lists Chinese, Dutch, English, Russian, Spanish [V, [WHO](https://www.who.int/teams/noncommunicable-diseases/sensory-functions-disability-and-rehabilitation/hearwho)]. No Hebrew, although the app is listed in the Israeli store [V, [apptail](https://apptail.io/app/hearwho-check-your-hearing-TUlb/israel)] | Free | A 2024 study reports 82% sensitivity and 95% specificity (secondary source) [V] |
| **hearZA / hearDigits** (hearX) | iOS, Android, web widget | DIN | SNR | — | South African languages; widget offers "all languages" [V, [hearX blog](https://billing.hearxgroup.com/blog/hearDigits–hearX-Groups-hearing-health-self-screening-test.html)] | Free / B2B embed | See research.md |
| **Mimi Hearing Test** (Berlin) | **iOS only now. Android discontinued** [V] | Pure-tone thresholds. The masked-threshold test was removed [V] | Model calibration. RTINGS partnership (Feb 2025) for headphone coverage [V, [Mimi](https://mimi.io/press/mimi-hearing-technologies-and-rtings-announce-strategic-collaboration)] | **Lost its CE class I (MDD) status in May 2024.** Now follows the consumer standard ANSI/CTA-2118 [V, [Mimi Health update](https://mimi.io/mimi-health); [FAQ](https://mimi.io/faq/hearing-test-app)] | Many | B2B: sound personalization and a **hearing-test SDK** for partners, presented as a way for them to "avoid certification" [V, [Mimi SDK](https://mimi.io/mimi-health-hearing-test-new-sdk)]. Barmer (a German insurer) promoted the Mimi app [V, [Barmer PDF](https://www.barmer.de/resource/blob/1028572/4e072f7c73f8aaac47c533b79a3fa7cf/pm-tag-des-laerms-data.pdf)] | **Instructive.** A well-funded German company walked *away* from a CE medical-device hearing test under MDR. |
| **Jacoti Hearing Center** (Belgium) | iOS. **Removed from Google Play in March 2025** [V] | DuoTone automated pure-tone test | Supports AirPods | CE 2797. US "FDA listed, EWO". Intended for informational screening when self-used [V, [appfollow](https://apps.appfollow.io/ios/jacoti-hearing-center/1001095418?country=us)] | — | Free / B2B | AJA 2022, n=13 [V, [AJA](https://pubs.asha.org/doi/10.1044/2022_AJA-21-00191)] |
| **hEARtest** (woheller69, open source) | Android (F-Droid) | Pure tone | Calibration with 18–35-year-old normal-hearing listeners, or at 1 kHz only [V, [F-Droid](https://f-droid.org/en/packages/org.woheller69.audiometry)] | None | — | Free, **GPL-3.0** | None |
| **Tunefork** (Israel) | Mobile SDK | 5 min earphone test producing a "hearing profile" for sound personalization | — | — | — | B2B to device and app makers. Sheba Medical Center trial in 2021, results not found [V, [JPost](https://www.jpost.com/opinion/hillels-tech-corner-tunefork-ensuring-you-hear-the-sounds-being-made-663876)] | Status after 2021 unknown |
| **Tuned Ltd** (Israel) | Mobile app | Calibrated pure-tone self-test feeding NAL-NL2 fitting | Calibrated | **FDA 510(k) K251208** (26 Sept 2025), OTC self-fitting software [V, [Innolitics](https://510k.innolitics.com/device/K251208)] | — | Licensed to hardware (Intricon "Tuned L155") [V] | An Israeli team got 510(k) for a self-test plus fitting. That makes them a possible partner or acquirer [I] |
| **Koalys (ex-Audyx)** (Jerusalem) | Kit + app | "Koalys Contact" smartphone screening audiometer with calibrated headset; also diagnostics and tele-audiology [V, [startupim](https://startupim.com/company/koalys)] | Calibrated headset | Listed in FDA GUDID (Audyx) [V, [GUDID](https://accessgudid.nlm.nih.gov/devices/07290017820019)] | Hebrew UI unknown | B2B SaaS: occupational, school, humanitarian | The Israeli B2B incumbent to check before approaching HMOs [I] |
| **Soundly, Audicus** (US) | Web, iOS | Tone test with audiogram-style chart. Audicus takes 10–15 min [V, [Soundly](https://soundly.com/hearing-test); [Audicus app](https://apps.apple.com/app/id6446603673)] | Uncalibrated | "Not a certified hearing test" (Soundly ToS) [V] | English | Free funnel; email required for results [V] | — |
| **Phonak Hearing Screener / Signia / Starkey online tests** | Web, apps | About 5 min frequency or speech tests; results shared with a local hearing-care professional [V, [Phonak](https://supportapp.phonak.com/us/en/online-hearing-test.html); [Signia](https://signia.net/en-AU/service/hearing-test)] | Uncalibrated | Not devices | Regional | **Lead generation for dispensers** | — |
| **uHear** (Unitron/Sonova) | iOS | Tone + speech-in-noise questionnaire | — | — | — | — | Most-validated iOS app (research.md). Current availability **unclear** [V, thin] |

### 1.3 National, charity and public tests (DIN dominates)

| Country | Test | Facts |
|---|---|---|
| **UK** | **RNID online hearing check** | Free, adults 18+, about 3 min digits-in-noise (the voice gets quieter against constant noise). It says "not a full hearing test" and "can't determine degree/type" [V, [RNID](https://rnid.org.uk/information-and-support/take-online-hearing-check/)]. Specsavers and Boots online tests were not verified. |
| **Netherlands** | **Nationale Hoortest** (Smits DIN, Hoorstichting) | The original telephone DIN (65,000 calls in 4 months). Online Oorcheck had more than 300,000 tests between 2010 and 2014. A broadband DIN version is still being validated in 2025 [V, [RUG](https://research.rug.nl/en/publications/digits-in-noise-test-implementations-for-onsite-and-online-testin/); [onderzoekmetmensen](https://www.onderzoekmetmensen.nl/en/node/41316)] |
| **Germany** | Ziffern-Tripel-Test (HörTech / Hörzentrum Oldenburg); Oldenburg **Alexa "Hörtest"** (OLSA with ASR) | Telephone and headphone digit triplet test [V, [scoping review](https://pubmed.ncbi.nlm.nih.gov/33840339/)]. The Alexa skill is described as "not a medical diagnosis" with about 1 dB deviation [V, [Uni Oldenburg](https://uol.de/en/communication-acoustics/alexa-testmyhearing)]. Retail chains (KIND, GEERS, Fielmann, Amplifon, audibene/hear.com) offer free online or in-store tests as an entry point to fitting. KIND says its online test is orientation only and recommends a store visit [V, [KIND](https://kind.com/en-sg/consultation-and-service/online-hearing-test)]. hear.com requires contact data [V]. Germany-specific online-test pages for Fielmann and Amplifon were **not verified**. FGH 10-question self-test [V]. |
| **Sweden** | **Hörseltestaren** (HRF / Hörselbron) | Free speech-in-noise app (since 2012) and web test [V, [HRF PDF](https://hrf.se/app/uploads/2016/05/press_horseltestaren_2012.pdf)]. Audika and AudioNova online tests [V]. |
| **Denmark, Norway** | — | Not verified (no results). Home of GN, Demant/Oticon and WS Audiology [K]. |
| **Israel** | — | **No Hebrew hearing-test app or HMO digital screening found** [V, negative result from Hebrew and English searches]. Phonak Israel has Hebrew informational pages [V]. |
| **South Africa** | hearZA (national) | See research.md. |

### 1.4 Clinical, occupational and kiosk tools (calibrated hardware)

| Product | What | Regulatory | Model |
|---|---|---|---|
| **SHOEBOX** (Canada) | iPad audiometer with calibrated transducers; PureTest is a Type 4 (screening) audiometer per ANSI S3.6-2018; boothless; "Hearing Screener" with an unattended kiosk mode and contact capture [V, [datasheet](https://www.shoebox.md/products/shoebox-puretest/ext-19-2-0-puretest-datasheetv1-1/); [webinar](https://www.shoebox.md/hearing-care-and-healthcare-resources/webinars/2026-shoebox-hearing-services-portfolio-overview/)] | Vendor says "Class II listed" with FDA under 21 CFR 874.1050, Health Canada, and CE [V]. "Listed" is not "cleared" | B2B: occupational (OSHA), clinics |
| **hearX hearTest / hearScreen / hearKiosk** | Android-smartphone or tablet audiometer with calibrated headphones (bundles use the Sennheiser HD 280 Pro); DIN kiosk for pharmacies [V, [hearX](https://hearxgroup.com/hearscreen); [Oaktree](https://www.oaktreeproducts.com/hearx-hearscreen-smartphone-screening-audiometer-hearscreen)] | "FDA registered", CE, ISO 13485, HIPAA/GDPR/POPIA claims [V] | Subscription with unlimited test credits; kiosk providers sign up for 24 months [V] |
| **KUDUwave** (eMoyo) | Integrated headset with ambient monitoring; boothless [V, [MedicalExpo](https://medicalexpo.com/prod/emoyo/product-120025-835181.html)] | — | Occupational |
| **MedRx kiosk, InnerScope / Hearing Wellness Network kiosks** | Pharmacy screening kiosks (Albertsons, Giant Eagle) [V, [Supermarket News](https://www.supermarketnews.com/health-wellness/giant-eagle-to-deploy-hearing-screening-kiosks); [e3](https://e3diagnostics.com/blog/the-new-medrx-kiosk-is-here)] | — | Lead generation for practices |

**Occupational context.** OSHA 29 CFR 1910.95 Appendix D sets maximum ambient noise levels for audiometric test rooms. Boothless testing must apply stricter limits [V, [SHOEBOX blog](https://www.shoebox.md/occupational-hearing-screening-service-provider-solution/)]. The US DoD ran more than 40,000 boothless tests between 2021 and May 2025 [V, [AJA commentary](https://pubs.asha.org/doi/abs/10.1044/2025_AJA-25-00184)]. **Occupational audiometry needs calibrated audiometers [K], so hearo's any-headphone approach is excluded from this segment** [I].

---

## 2. Comparison table: hearo vs. the closest rivals

| | hearo (planned) | Samsung Buds test | Apple AirPods test | e-audiologia | Sennheiser | hearWHO | RNID / national DIN | hearX / SHOEBOX (B2B) |
|---|---|---|---|---|---|---|---|---|
| Android | Yes, all | Galaxy only | No | Yes | Yes | Yes | Web | Yes (hearX) / iPad |
| Any headphones | Yes | No (Buds Pro only) | No (AirPods Pro) | Yes, with preset coefficients | Yes | Yes | Yes | No (calibrated) |
| Pure tone per ear | Yes (relative) | Yes (calibrated) | Yes (calibrated) | Yes (biological calibration) | Yes | — | — | Yes (calibrated) |
| DIN | Yes | — | — | Yes (17 languages) | — | Yes | Yes | Yes (kiosk) |
| Open-set word recognition, ASR-scored | **Yes** | — | — | — | — | — | — | — |
| Hebrew | **Yes** | ? | ? | ? (check) | ? | No | No | ? |
| Triage / urgency output | Yes | Grade per ear | Grade + hearing aid | ? | Explanation | Score | "Get a test" | Pass/refer |
| Clinic-style PDF | Yes | Samsung Health | Health app | ? | Audiogram | — | — | Yes (professional) |
| Regulatory | None (planned wellness) | FDA OTC + MFDS SaMD | FDA De Novo / Class II | ? | — | — | — | Class II listed, CE |
| Price | ? | Free with buds | Free with buds | Freemium | Free | Free | Free | B2B subscription |

`?` = not verified this session.

---

## 3. Buyers and partners in the medical and insurance pipeline

### 3.1 United States
- **Medicare Advantage hearing-benefit administrators.**
  - UnitedHealthcare Hearing: members take an online test first, then an in-person audiogram [V, [UHC PDF](https://retiree.uhc.com/content/dam/retiree/pdf/pg-maplan/2023/UnitedHealthcare-Hearing.pdf)].
  - TruHearing: "check your hearing online in five minutes", then a consultant books an exam [V, [UCare](https://media.ucare.org/health-wellness/member/truhearing)].
  - Also Amplifon Hearing Health Care and NationsHearing [K].
  - These buyers use screening as a **funnel to in-network fitting**. They do not need a triage engine; they need conversion, and they need no FDA problem.
- **The FDA precedent to remember.** On 28 March 2012 the FDA sent a warning letter to hi HealthInnovations (owned by UnitedHealth). It said the company's online hearing test needed 510(k) clearance, and the test was pulled and never revived [V, [Hearing Health Matters](https://hearinghealthmatters.org/hearing-news-watch/2012/hi-healthinnovations-drops-online-hearing-test-after-fda-warns-that-its-illegal/); [ITEM Coalition](https://itemcoalition.org/2012/05/01/050112-fda-warns-hi-healthinnovations-of-illegal-online-hearing-test/)]. **Any US payer will ask about FDA status first.**
- **Employers.** TunedCare is an employer hearing benefit with an online "Tuned 360 Hearing Profile" screening plus virtual audiologists, billed per use. It claims 1.4M people with access (2023) [V, [PR Newswire](https://prnewswire.com/news-releases/tuned-launches-to-bring-hearing-health-mainstream-301603858.html); [MedCity](https://medcitynews.com/2022/08/how-one-company-is-trying-to-make-hearing-care-a-mainstream-benefit/)]. It is not affiliated with Israel's Tuned Ltd [V].
- **Primary care.** USPSTF 2021: "I" statement (insufficient evidence) for screening asymptomatic adults over 50 [V, [JAMA](https://jamanetwork.com/journals/jama/fullarticle/2777773)]. So there is no screening mandate that would pull a tool into primary care. The Medicare Annual Wellness Visit includes a hearing question [K]. Medicare covers audiology only diagnostically, and hearing aids are excluded by statute [V, [PMC9200460](https://pmc.ncbi.nlm.nih.gov/articles/PMC9200460)]. Audiologist direct access was **not verified**.
- **Retail and pharmacy.** Kiosks such as hearX, MedRx and SHOEBOX are positioned as referral engines; pharmacies are pushed as "community hubs" ahead of OTC hearing-aid sales [V, [Hearing Health Matters 2025](https://hearinghealthmatters.org/hearing-news-watch/2025/pharmacy-hearing-health-care/)].
- **What US buyers require [K/I]:**
  - FDA status: a 510(k) or De Novo for anything that outputs thresholds or loss categories, or a defensible "general wellness" position.
  - HIPAA BAA and security attestation (SOC 2) once the buyer is a covered entity.
  - The FTC Health Breach Notification Rule for direct-to-consumer apps (research.md).
  - Integration: SMART on FHIR / HL7 FHIR R4 Observation per ear and frequency. LOINC has per-ear, per-frequency threshold codes, for example **89024-4** "Hearing threshold Ear (left) 500 Hz" [V, [HL7 AU Core example](https://hl7.org.au/fhir/core/Observation-hearing-threshold-1.html)].
  - I found **no international HL7 audiology IG**. Only example resources exist [V, negative].
  - A peer-reviewed validation study.

### 3.2 Germany
- **The hearing-aid pathway is gated by the ENT (HNO).**
  - GKV reimbursement requires an HNO examination and a prescription on Muster 15, redeemed within 28 days, under the G-BA Hilfsmittel-Richtlinie §§18–31 [V, [betanet](https://www.betanet.de/hoerhilfen.html)].
  - With that prescription the patient chooses among the Kasse's contracted Hörakustiker. A "verkürzter Versorgungsweg" (fitting via the HNO, §128 SGB V) exists, and the hearing-loss associations criticise it [V, [DAK](https://www.dak.de/dak/verkuerzter-versorgungsweg-2266578.html); [Deutsche Handwerks Zeitung](https://www.deutsche-handwerks-zeitung.de/?p=90359)].
  - Hearing aids are excluded from the e-prescription (eVerordnung) rollout [V, [IKK classic](https://cdn.ikk-classic.de/exporter/19946-pm-egesundheit-deutschland-08-20-2026-e-rezept-83-prozent-wollen-elektronische-verordnungen-auch-fuer-heil-und-hilfsmittel.pdf)].
  - **So the triage output that matters in Germany is "see an HNO", with "how soon".**
- **Hörakustiker chains** (Fielmann, KIND, GEERS/Sonova, Amplifon, audibene/hear.com/WSA) are the **most likely paying B2B buyers**. They pay for **leads**: free tests that capture contact details and book appointments [V for KIND / hear.com; I for the rest]. Stiftung Warentest compares the chains on service and price [V, [test.de](https://www.test.de/Ergebnisse-Umfrage-Hoerakustiker-Ein-grosser-Anbieter-vorn-bei-Service-und-Preis-4756794-0/)].
- **Krankenkassen** promote third-party apps; Barmer has promoted Mimi [V]. **DiGA** ("app on prescription") requirements [V, [Johner Institute](https://blog.johner-institute.com/health-it/7-steps-to-the-diga-directory/); [Covington 2026](https://www.covingtonblogs.com/2026/02/03/germany-changes-rules-for-digital-health-applications/); [Promon](https://promon.io/resources/knowledge-center/bsi-digas-compliance-germany)]:
  - an MDR CE mark in class I or IIa (some sources now say IIb),
  - a proven "positiver Versorgungseffekt",
  - a **BSI TR-03161** security certificate (mandatory for new applications by 2025), and
  - BfArM data-protection criteria.
  - No hearing-screening DiGA was found [V, negative]. A screening-only app would struggle to prove a care effect [I].
- **Requirements [K/I]:** MDR CE (Rule 11, so IIa for triage claims), GDPR Art. 9 explicit consent with EU hosting, BSI TR-03161 if DiGA, German-language validated materials, and an interface into the clinic's audiometry software (often the Noah/HIMSA format for Hörakustiker [K]) rather than FHIR.

### 3.3 Israel (the most plausible first B2B market for hearo) [I]
- The buyers are HMOs (Clalit, Maccabi, Meuhedet, Leumit), hospital digital-health units (for example Sheba ARC, which wanted Tunefork for remote hearing tests [V, [Israel21c](https://archive.israel21c.org/new-hearing-tech-will-adjust-your-device-to-fit-your-earprint/)]), and hearing-aid dispensers.
- No HMO digital hearing-screening deployment was found [V, negative].
- Local B2B incumbents to map first: **Koalys** (calibrated kit) and **Tuned Ltd** (510(k) self-fitting) [V].

---

## 4. Gap analysis: differentiated or crowded?

| hearo feature | Verdict | Why |
|---|---|---|
| **Hebrew / RTL UI + Hebrew speech material** | **Real gap (Israel only)** | No Hebrew hearing-test app found. Hebrew DIN and the 2026 Hebrew word lists are research-only (research.md). Risks: e-audiologia's 17-language DIN may include Hebrew (**check first**); Apple's test is live in Israel and may be localized; the market is small (about 10M people) [I]. |
| **Open-set word recognition scored by ASR** | **Differentiated product, unproven science** | Research precedents: Oldenburg OLSA via ASR (test-retest 0.5 dB, as good as human-supervised) [V, [Interspeech 2015](https://www.isca-archive.org/interspeech_2015/meyer15_interspeech.pdf)]; Alexa at home (AUC 0.95) [V, [PubMed 33272109](https://www.pubmed.gov/33272109/)]; VoIP automated OLSA (r=.93) [V, [AJA 2022](https://pubs.asha.org/doi/10.1044/2022_AJA-21-00217)]; Manchester ASR+TTS DIN [V, [Manchester](https://www.manchester.ac.uk/about/news/ai-powered-automated-hearing-test-okayed-by-scientists/)]; WordRec app [V, [Pacific](https://scholarlycommons.pacific.edu/shs-all/100)]. A review says ASR-based home tests "do not reach clinical standards" yet [V, [PMC11851792](https://pmc.ncbi.nlm.nih.gov/articles/PMC11851792)]. **No product combines this with clinic-style %WRS.** It is a moat only if hearo validates it, especially for isolated Hebrew words. |
| **Speech-in-noise (DIN)** | **Crowded** | hearWHO, RNID, the Dutch national test, hearZA, e-audiologia, hearX kiosks, the Swedish HRF test. It is table stakes, not a differentiator. |
| **TTS-generated speech material** | **Has a research precedent, which supports the plan** | The "Aladdin" method builds DIN tests from synthetic speech with ASR-based level equalization. Sensitivity and specificity were 100% / 84%, against 100% / 87% for the reference DIN, in Dutch and English [V, [Sci Rep 2025](https://doaj.org/article/ce2ea97d136f44a6a3a1603b6174502c)]. This gives a citable method for norming hearo's TTS Hebrew material, but norms are still needed. |
| **Any-headphone relative calibration** | **Crowded, and a weakness** | It is the default for free tests (Sennheiser, Signia, Soundly, Samsung Adapt Sound). Calibrated rivals (Apple, Samsung, e-audiologia coefficients) beat it on accuracy. |
| **Android-first** | **Partly differentiated** | Mimi left Android, Apple is not on it, and Samsung covers only Galaxy + Buds Pro. But e-audiologia (2.5M installs), Sennheiser, hearWHO and hEARtest are already on Android. |
| **Triage / urgency output** | **Modestly differentiated, raises regulatory risk** | Competitors output pass/refer or grades plus a hearing-aid funnel. A red-flag urgency output ("urgent ENT within 72 h" for sudden loss) is rare. Under MDR Rule 11 and FDA rules it is plausibly a diagnostic or triage function [K]. |
| **Clinic-style PDF report** | **Modest** | Many tests give audiograms (Soundly, Sennheiser, Jacoti, Apple via Health). A bilingual, Clalit-layout PDF with a speech table is a nice touch, not a moat. |

**Verdict.**
- **For the US and Germany: crowded. There is no clean gap.** Big tech gives away calibrated, regulated tests (Apple everywhere; Samsung on Android from Q4 2026 or early 2027). Retail and payers use free tests as lead-generation funnels. The one genuinely new element, ASR-scored word recognition, would need German or English validated materials and regulatory clearance to sell there.
- **For Israel: a real but narrow gap.** It consists of a Hebrew clinic-style home check that includes speech understanding, which no one offers today. Defending it depends on Hebrew materials licensing and a published validation study, not on code.

---

## 5. Reusable open-source components and materials

| Component | License | Use | Notes |
|---|---|---|---|
| [google/oboe](https://github.com/google/oboe) | Apache-2.0 [V] | Low-latency Android audio | Safe for a proprietary app. |
| [ggml-org/whisper.cpp](https://github.com/ggml-org/whisper.cpp) | MIT [V] | On-device ASR | |
| [ivrit-ai](https://github.com/ivrit-ai/ivrit.ai) code; [ivrit-ai/whisper-large-v3-turbo-ct2](https://huggingface.co/ivrit-ai/whisper-large-v3-turbo-ct2) model | Code MIT [V]; the model repo is tagged Apache-2.0 [V] | Hebrew ASR | **The license of the training dataset (crowd-transcribe-v5 + 93 h professional data) is unverified.** Confirm before commercial use. |
| [woheller69/whisperIME](https://github.com/woheller69/whisperIME) | MIT [V] | Example of Whisper running on Android | Reference for the JNI and model download path. |
| [alphacep/vosk-api](https://github.com/alphacep/vosk-api) | Apache-2.0 [V] | Grammar-constrained ASR (English) | **No Hebrew Vosk model found** [V, negative]. |
| [k2-fsa/sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) | Apache-2.0 [V] | ONNX on-device ASR, VAD, TTS | Hebrew model availability not checked. |
| [woheller69/audiometry](https://github.com/woheller69/audiometry) (hEARtest) | **GPL-3.0** [V] | Android pure-tone reference implementation | Copyleft. Read it for ideas only, unless hearo is GPL. |
| [HoerTech-gGmbH/openMHA](https://github.com/HoerTech-gGmbH/openMHA) | **AGPL-3.0** [V] | Hearing-aid DSP platform | Not needed for screening. AGPL is incompatible with a closed app. |
| [saivittalb/audiogram](https://github.com/saivittalb/audiogram) | Apache-2.0 [V] | Small Android audiogram app | Low maturity (★4). |
| [GreenCUBIC/AudiogramDigitization](https://github.com/GreenCUBIC/AudiogramDigitization) | MIT [V] | Digitize clinic audiogram images | Could import a user's Clalit PDF for personal anchoring (research.md §2.2 item 5) [I]. |
| GitHub DIN repos (for example DharLab/digits-in-noise, erik392/digit-in-noise) | **No license** [V] | — | Not reusable (all rights reserved by default). |
| **Aladdin method** (Polspoel et al., Sci Rep 2025, [DOI 10.1038/s41598-025-96312-z](https://doaj.org/article/ce2ea97d136f44a6a3a1603b6174502c)) | Open-access paper | Recipe for TTS-based DIN with ASR equalization | Directly applicable to Hebrew DIN built from hearo's own TTS. |
| **Hebrew DIN (Ariel University) and the 2026 Hebrew CVC lists** | Ask the authors (research.md) | Normed Hebrew material | Still the long-lead item. |
| HL7 FHIR R4 + LOINC per-ear threshold codes | Free | Export | Example: [AU Core hearing threshold](https://hl7.org.au/fhir/core/Observation-hearing-threshold-1.html). |
| Mimi hearing-test SDK | Commercial | Buy rather than build the tone test | Mimi presents it as a way for partners to avoid certification, but Mimi itself dropped CE [V]. |

---

## 6. Risks and recommendation

### 6.1 Key risks
1. **Big-tech bundling.**
   - Samsung brings a free, FDA/MFDS-regulated test to the largest Android OEM in Q4 2026 or 2027.
   - Apple is already live in Israel and Germany.
   - Google could add one to Pixel at any time [I].
   - For consumers who own those earbuds, a relative-calibration app cannot compete on accuracy.
2. **Regulation.**
   - US: the hi HealthInnovations precedent shows that an online test with diagnostic output can draw an FDA warning letter.
   - EU: MDR made Mimi drop its CE class I, and a triage output is likely class IIa.
   - Israel: AMAR fast-track depends on CE or FDA.
   - "Triage for providers" is the riskiest claim in the plan.
3. **Commercial fragility of direct-to-consumer hearing.** LXE (Eargo + Lexie + hearX US) wound down its US business in July 2026 despite $100M of funding. Consumer screening is monetized almost only as a hearing-aid lead funnel, which is low-margin and controlled by the chains.
4. **Validation burden.** Buyers want peer-reviewed accuracy. Apple is publishing studies (86.5% within 10 dB). hearo's ASR word score on isolated Hebrew words with TTS stimuli has no norms yet.
5. **Data privacy.** GDPR Art. 9, HIPAA BAAs, BSI TR-03161 for DiGA, and Israeli Amendment 13 (research.md). Local-only storage helps for consumers, but B2B use will require transfer and integration.
6. **Materials licensing.** Hebrew DIN and word-list rights, and OLSA/German materials rights if you go into Germany (HörTech licenses them commercially [K]).

### 6.2 Recommended positioning [I]
"**hearo: a Hebrew/English home hearing check that also measures speech understanding, the way a clinic does, and tells you how soon to book a hearing test.**" It is not an audiogram replacement. Lead with speech-in-noise and word recognition, the areas where relative calibration matters least. Present pure-tone results as relative or "approximate".

### 6.3 Go-to-market ordering [I]
1. **Now to 6 months: Israel, closed testers.**
   - Wellness positioning with Play "not a medical device" wording.
   - DIN, relative tones, and the ASR word test (marked "experimental").
   - Log ASR-vs-human agreement.
   - Check e-audiologia for Hebrew before claiming "first in Hebrew".
2. **6–18 months: a validation study** with an Israeli academic audiology partner (Ariel, TAU, Haifa or Sheba). Compare hearo against booth audiometry and clinic %WRS, norm the TTS Hebrew material (Aladdin-style), and publish it. This study is the moat.
3. **B2B pilots in Israel.**
   - HMO digital-health units and hearing-aid dispensers, as a pre-visit triage and lead tool, with FHIR or PDF export.
   - Consider partnering with or licensing to Tuned Ltd or Koalys rather than competing with them.
4. **Regulatory step:** ISO 13485 QMS, then **CE MDR class IIa**, then AMAR fast-track. Do this only once a paying B2B customer needs a medical claim.
5. **Germany, optional, after CE.** Add German materials (licensed digit-triplet test or OLSA) and sell to Hörakustiker chains or Krankenkassen as a white-label lead and triage tool. Skip DiGA unless an outcome study is funded.
6. **US last, or never as B2C.** Consider it only as a licensed **ASR speech-audiometry scoring component**, or with a 510(k)/De Novo plan. Do not fight Apple and Samsung on tone tests.

---

## 7. Unverified items to check next
- e-audiologia: the full 17-language list (Hebrew?), its CE status, and Pro pricing.
- Apple Hearing Test: Hebrew localization and the Israeli hearing-aid status (apple.com/il).
- Samsung: the 510(k) number, the full market list, and whether the test runs on non-Galaxy phones.
- Germany-specific online-test pages for Fielmann, Amplifon and audibene; current Krankenkassen hearing apps.
- Specsavers and Boots (UK); Danish and Norwegian national tests.
- SHOEBOX and hearX: FDA "listed" vs "cleared" status (accessdata.fda.gov was blocked).
- uHear availability; Jabra Sound+ "MySound"; Sony's test details.
- License of the ivrit.ai datasets; whether sherpa-onnx has a Hebrew model.
- Medicare audiologist direct-access rules.
