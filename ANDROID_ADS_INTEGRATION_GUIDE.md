# Android Ads Integration Guide
## Role Non-Playing Game - Advertisement Strategy & Implementation

---

## Table of Contents
1. [Ad Placement Strategy](#1-ad-placement-strategy)
2. [Technical Implementation Plan](#2-technical-implementation-plan)
3. [Ad Provider Recommendation](#3-ad-provider-recommendation)
4. [Detailed Configuration Guide](#4-detailed-configuration-guide)
5. [Testing & Best Practices](#5-testing--best-practices)
6. [Revenue Optimization Tips](#6-revenue-optimization-tips)

---

## 1. Ad Placement Strategy

### Philosophy: Observation-Friendly Monetization
Since Role Non-Playing Game is about **watching** characters make autonomous decisions, ad placements should leverage natural pause points where players are already passive observers, without interrupting the core experience.

### Recommended Ad Placements

#### A. **App Launch Interstitial** (High Priority)
- **Type**: Interstitial Ad
- **Placement**: After MainActivity loads, before showing main content
- **Frequency**: Once per session (first app open of the day)
- **Impact**: Minimal - users expect initial loading
- **Expected Views**: High (every daily active user)

#### B. **Offline Summary Screen** (High Priority - Unique Opportunity)
- **Type**: Rewarded Ad (optional) + Banner Ad
- **Placement**: On the OfflineSummaryScreen when users return to the app
- **Strategy**:
  - Show summary of what happened while away (free)
  - Offer **rewarded ad** to gain 2x experience/loot from offline period
  - Banner ad at bottom of summary screen
- **Impact**: Low - enhances experience (reward), banner is non-intrusive
- **Expected Views**: Medium-High (every time user returns after being away)

#### C. **Character Transition Interstitial** (Medium Priority)
- **Type**: Interstitial Ad
- **Placement**: When switching between characters on HomeScreen
- **Frequency**: Every 3rd character switch
- **Impact**: Low - brief pause between observations
- **Expected Views**: Medium

#### D. **Game Screen Banner** (Medium Priority)
- **Type**: Banner Ad (Adaptive)
- **Placement**: Bottom of GameScreen (where activity log displays)
- **Strategy**: Non-intrusive, always visible during observation
- **Impact**: Very Low - doesn't interrupt gameplay
- **Expected Views**: Very High (longest user engagement time)

#### E. **Character Creation Completion** (Low Priority)
- **Type**: Interstitial Ad
- **Placement**: After character creation, before navigating to GameScreen
- **Frequency**: Once per character created
- **Impact**: Low - natural transition point
- **Expected Views**: Low (characters created infrequently)

#### F. **Profile Screen Banner** (Low Priority)
- **Type**: Banner Ad
- **Placement**: Bottom of ProfileScreen
- **Impact**: Minimal - settings screen
- **Expected Views**: Low (infrequent visits)

### Ad Frequency Caps
To maintain user experience:
- **Interstitials**: Max 1 per 3 minutes of active usage
- **Rewarded Ads**: No cap (user-initiated)
- **Banners**: Always visible on designated screens

### Priority Implementation Order
1. App Launch Interstitial (quick win)
2. Offline Summary Rewarded Ad (unique value proposition)
3. Game Screen Banner (highest viewtime)
4. Character Transition Interstitial (moderate frequency)
5. Other placements (lower priority)

---

## 2. Technical Implementation Plan

### Required Dependencies

Add to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies...

    // Google Mobile Ads SDK (AdMob)
    implementation("com.google.android.gms:play-services-ads:23.6.0")
}
```

### Code Architecture Changes

#### A. Create Ad Manager Module

**New File**: `app/src/main/java/com/watxaut/rolenonplayinggame/ads/AdManager.kt`

```kotlin
package com.watxaut.rolenonplayinggame.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private val _isInterstitialLoaded = MutableStateFlow(false)
    val isInterstitialLoaded: StateFlow<Boolean> = _isInterstitialLoaded

    private val _isRewardedAdLoaded = MutableStateFlow(false)
    val isRewardedAdLoaded: StateFlow<Boolean> = _isRewardedAdLoaded

    // Track last interstitial show time to enforce frequency cap
    private var lastInterstitialShowTime = 0L
    private val interstitialCooldownMs = 3 * 60 * 1000L // 3 minutes

    init {
        loadInterstitialAd()
        loadRewardedAd()
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AD_UNIT_INTERSTITIAL,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _isInterstitialLoaded.value = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                }
            }
        )
    }

    fun showInterstitialAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {}
    ) {
        val currentTime = System.currentTimeMillis()

        // Enforce cooldown
        if (currentTime - lastInterstitialShowTime < interstitialCooldownMs) {
            onAdDismissed()
            return
        }

        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                    lastInterstitialShowTime = currentTime
                    loadInterstitialAd() // Preload next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } ?: run {
            onAdDismissed()
        }
    }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            AD_UNIT_REWARDED,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _isRewardedAdLoaded.value = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    _isRewardedAdLoaded.value = false
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (Int) -> Unit,
        onAdDismissed: () -> Unit = {}
    ) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    _isRewardedAdLoaded.value = false
                    loadRewardedAd() // Preload next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    _isRewardedAdLoaded.value = false
                    onAdDismissed()
                }
            }

            ad.show(activity) { rewardItem ->
                onRewardEarned(rewardItem.amount)
            }
        } ?: run {
            onAdDismissed()
        }
    }

    companion object {
        // Test ad unit IDs (replace with real IDs in production)
        const val AD_UNIT_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
        const val AD_UNIT_REWARDED = "ca-app-pub-3940256099942544/5224354917"
        const val AD_UNIT_BANNER = "ca-app-pub-3940256099942544/6300978111"
    }
}
```

#### B. Create Banner Ad Composable

**New File**: `app/src/main/java/com/watxaut/rolenonplayinggame/ads/BannerAd.kt`

```kotlin
package com.watxaut.rolenonplayinggame.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.AD_UNIT_BANNER
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun AdaptiveBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.AD_UNIT_BANNER
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                // Adaptive banner automatically adjusts to screen width
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    (context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density).toInt()
                )
                setAdSize(adSize)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
```

#### C. Update Dependency Injection

**File**: `app/src/main/java/com/watxaut/rolenonplayinggame/di/AppModule.kt`

Add AdManager to the module (if it doesn't already provide it automatically via @Singleton and @Inject):

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Existing code...

    // AdManager is already injectable via @Singleton @Inject constructor
    // No need to provide it manually unless custom initialization is needed
}
```

#### D. Initialize Mobile Ads SDK

**File**: `app/src/main/java/com/watxaut/rolenonplayinggame/RoleNonPlayingGameApplication.kt`

```kotlin
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class RoleNonPlayingGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Mobile Ads SDK on background thread
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@RoleNonPlayingGameApplication)
        }
    }
}
```

#### E. Update AndroidManifest.xml

Add AdMob App ID metadata:

```xml
<application>
    <!-- Existing content... -->

    <!-- AdMob App ID -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/> <!-- Replace with your actual App ID -->
</application>
```

#### F. Integration Points

##### 1. MainActivity - App Launch Interstitial
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Show interstitial on first launch
        adManager.showInterstitialAd(this)

        setContent {
            RoleNonPlayingGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
```

##### 2. GameScreen - Banner Ad
```kotlin
@Composable
fun GameScreen(
    characterId: String,
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Existing game content...

        Spacer(modifier = Modifier.weight(1f))

        // Banner ad at bottom
        AdaptiveBannerAd(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

##### 3. OfflineSummaryScreen - Rewarded Ad
```kotlin
@Composable
fun OfflineSummaryScreen(
    summary: OfflineSimulationSummary,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val adManager = hiltViewModel<NavigationViewModel>().adManager // Inject via ViewModel
    val isRewardedAdLoaded by adManager.isRewardedAdLoaded.collectAsState()

    Column {
        // Summary content...

        if (isRewardedAdLoaded) {
            Button(
                onClick = {
                    activity?.let {
                        adManager.showRewardedAd(
                            activity = it,
                            onRewardEarned = { amount ->
                                // Double the XP/loot earned
                                viewModel.applyOfflineReward(multiplier = 2)
                            }
                        )
                    }
                }
            ) {
                Text("Watch Ad for 2x Rewards!")
            }
        }

        Button(onClick = onContinue) {
            Text("Continue")
        }

        // Banner at bottom
        BannerAd()
    }
}
```

##### 4. HomeScreen - Character Switch Interstitial
```kotlin
@Composable
fun HomeScreen(
    onNavigateToCharacterCreation: () -> Unit,
    onNavigateToGame: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val adManager = hiltViewModel<NavigationViewModel>().adManager
    var characterSwitchCount by remember { mutableStateOf(0) }

    // Existing UI...

    LazyColumn {
        items(characters) { character ->
            CharacterCard(
                character = character,
                onClick = {
                    characterSwitchCount++

                    if (characterSwitchCount % 3 == 0) {
                        activity?.let {
                            adManager.showInterstitialAd(it) {
                                onNavigateToGame(character.id)
                            }
                        }
                    } else {
                        onNavigateToGame(character.id)
                    }
                }
            )
        }
    }
}
```

---

## 3. Ad Provider Recommendation

### Recommended: **Google AdMob**

#### Why AdMob?

✅ **Best for Android Apps**
- Native Google integration
- Highest fill rates for Android
- Seamless Google Play Services integration

✅ **Ease of Integration**
- Well-documented SDK
- Jetpack Compose support via AndroidView
- Test ad units for development

✅ **Revenue Potential**
- Highest eCPM (effective cost per thousand impressions) for mobile apps
- Access to Google's advertiser network
- Mediation support (can add other networks later)

✅ **Features**
- Multiple ad formats (banner, interstitial, rewarded, native)
- Adaptive banners (automatically fit screen sizes)
- AdMob Mediation (optimize revenue by competing ad networks)
- Real-time analytics dashboard

✅ **Compliance & Safety**
- GDPR/CCPA compliance built-in
- Family-friendly ad filtering
- Brand safety controls

#### Alternatives Considered

**Unity Ads**
- Good for game apps
- ❌ Requires Unity integration or separate SDK
- ❌ Lower fill rates for non-Unity apps

**Facebook Audience Network**
- ❌ Being deprecated (Meta is sunsetting it)
- ❌ Privacy concerns

**AppLovin**
- Good mediation platform
- ❌ More complex setup
- ❌ Better as secondary network via AdMob mediation

**Verdict**: AdMob is the clear winner for initial implementation. You can add mediation later to increase revenue.

---

## 4. Detailed Configuration Guide

### Step 1: Create AdMob Account

1. Go to https://admob.google.com/
2. Sign in with your Google account
3. Click **"Get Started"**
4. Accept the terms and conditions
5. Select your country and timezone
6. Choose "I want to monetize apps" → Continue

### Step 2: Add Your App to AdMob

1. In AdMob dashboard, click **"Apps"** in left sidebar
2. Click **"Add App"**
3. Select **"Android"**
4. Choose **"No"** for "Is your app published on Google Play?" (if not yet published)
   - Enter app name: **"Role Non-Playing Game"**
   - OR choose **"Yes"** if already published and search for it
5. Click **"Add"**
6. **Copy the App ID** (format: `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY`)
   - You'll need this for AndroidManifest.xml

### Step 3: Create Ad Units

AdMob uses "Ad Units" to serve different types of ads. Create these:

#### A. Interstitial Ad Unit

1. In AdMob, go to **Apps** → Select your app
2. Click **"Ad units"** → **"Add ad unit"**
3. Select **"Interstitial"**
4. Enter ad unit name: `"App Launch Interstitial"`
5. Click **"Create ad unit"**
6. **Copy the Ad unit ID** (format: `ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ`)
7. Repeat for other interstitials:
   - `"Character Switch Interstitial"`
   - `"Character Creation Interstitial"`

#### B. Rewarded Ad Unit

1. Click **"Add ad unit"** → **"Rewarded"**
2. Name: `"Offline Summary Reward"`
3. **Reward settings**:
   - Reward amount: `1`
   - Reward item: `"bonus"`
4. Click **"Create ad unit"**
5. **Copy the Ad unit ID**

#### C. Banner Ad Unit

1. Click **"Add ad unit"** → **"Banner"**
2. Name: `"Game Screen Banner"`
3. Click **"Create ad unit"**
4. **Copy the Ad unit ID**
5. Repeat for:
   - `"Profile Banner"`
   - `"Summary Banner"`

### Step 4: Update Code with Real Ad Unit IDs

Replace test IDs in `AdManager.kt`:

```kotlin
companion object {
    // PRODUCTION Ad Unit IDs
    const val AD_UNIT_INTERSTITIAL = "ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ"
    const val AD_UNIT_REWARDED = "ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ"
    const val AD_UNIT_BANNER = "ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ"
}
```

**IMPORTANT**: Use test IDs during development to avoid policy violations!

### Step 5: Update AndroidManifest.xml

```xml
<manifest>
    <application>
        <!-- Replace with YOUR App ID from Step 2 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>
    </application>
</manifest>
```

### Step 6: Add Dependency to build.gradle.kts

```kotlin
dependencies {
    implementation("com.google.android.gms:play-services-ads:23.6.0")
}
```

Sync Gradle files.

### Step 7: Initialize Mobile Ads SDK

Already covered in implementation section (RoleNonPlayingGameApplication.kt).

### Step 8: Test with Test Ad Units

Before going live, test with Google's official test ad unit IDs:

```kotlin
object TestAdUnits {
    const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED = "ca-app-pub-3940256099942544/5224354917"
    const val BANNER = "ca-app-pub-3940256099942544/6300978111"
}
```

Run the app and verify ads load and display correctly.

### Step 9: Enable App for Review (Before Publishing)

1. In AdMob dashboard, go to **Apps** → Your app
2. Click **"App settings"**
3. Enable **"App is ready to serve ads"**
4. AdMob will review your app (usually takes 24-48 hours)

**Note**: You can test with test IDs immediately, but real ads won't show until:
- App is published on Google Play OR
- You add your device as a test device in AdMob

### Step 10: Add Test Devices (for Testing with Real Ad Units)

1. In AdMob, go to **Settings** → **Test devices**
2. Click **"Add test device"**
3. Select **"Android"**
4. Get your device's Advertising ID:
   ```kotlin
   // Log advertising ID for testing (REMOVE in production)
   AdvertisingIdClient.getAdvertisingIdInfo(context).id
   ```
   OR use ADB:
   ```bash
   adb shell settings get secure advertising_id
   ```
5. Enter the ID in AdMob → Save

Now you can test with real ad units without violating policies.

### Step 11: Configure GDPR/CCPA Compliance

AdMob handles most compliance, but you should use Google's UMP SDK:

Add dependency:
```kotlin
implementation("com.google.android.ump:user-messaging-platform:3.1.0")
```

Initialize in MainActivity:
```kotlin
private fun requestConsentInformation() {
    val params = ConsentRequestParameters.Builder()
        .setTagForUnderAgeOfConsent(false)
        .build()

    val consentInformation = UserMessagingPlatform.getConsentInformation(this)
    consentInformation.requestConsentInfoUpdate(
        this,
        params,
        {
            // Consent info updated
            if (consentInformation.isConsentFormAvailable) {
                loadConsentForm()
            }
        },
        { error ->
            // Handle error
        }
    )
}

private fun loadConsentForm() {
    UserMessagingPlatform.loadConsentForm(
        this,
        { consentForm ->
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                consentForm.show(this) { error ->
                    // Form dismissed
                }
            }
        },
        { error ->
            // Handle error
        }
    )
}
```

Call `requestConsentInformation()` in `onCreate()` before initializing ads.

### Step 12: Monitor Performance

1. Go to AdMob dashboard → **Reports**
2. Monitor key metrics:
   - **Impressions**: How many ads shown
   - **eCPM**: Earnings per 1000 impressions
   - **Click-through rate (CTR)**
   - **Fill rate**: % of ad requests filled
3. Use **Mediation** to add more ad networks if fill rate < 95%

---

## 5. Testing & Best Practices

### Testing Checklist

- [ ] Test ads load successfully with test ad units
- [ ] Test on multiple screen sizes (small phone, tablet)
- [ ] Test ad dismissal callbacks work correctly
- [ ] Test with airplane mode (ads should fail gracefully)
- [ ] Test frequency caps work (interstitials don't spam)
- [ ] Test rewarded ad grants rewards correctly
- [ ] Test banners don't overlap UI elements
- [ ] Test app doesn't crash if ads fail to load
- [ ] Test navigation after interstitial dismissal
- [ ] Verify no ads shown with real ad unit IDs without test device registration

### AdMob Policy Compliance

❌ **Do NOT**:
- Click your own ads (instant ban)
- Ask users to click ads
- Place ads behind misleading buttons
- Use more than 3 banner ads on one screen
- Refresh banner ads faster than 60 seconds
- Show ads during prohibited activities (app crashes, loading screens)

✅ **Do**:
- Use test ads during development
- Clearly distinguish ads from content
- Provide value (rewarded ads should truly provide rewards)
- Respect user experience (don't spam interstitials)
- Implement proper error handling

### Error Handling

Always handle ad load failures gracefully:

```kotlin
fun showInterstitialAd(activity: Activity, onComplete: () -> Unit) {
    if (interstitialAd != null) {
        interstitialAd?.show(activity)
    } else {
        // Ad not ready, continue without showing
        onComplete()
    }
}
```

Never block user flow waiting for ads to load.

### Performance Optimization

1. **Preload ads**: Load next ad immediately after one is shown
2. **Use adaptive banners**: Better fill rates and user experience
3. **Lazy load**: Don't load all ad units at once, load on-demand
4. **Background loading**: Initialize MobileAds on IO dispatcher

---

## 6. Revenue Optimization Tips

### Short Term (Week 1-4)

1. **Start with test ads** to ensure everything works
2. **Implement priority placements first**:
   - App launch interstitial
   - Offline summary rewarded ad
   - Game screen banner
3. **Monitor fill rates** in AdMob dashboard
4. **A/B test ad frequencies** (e.g., show interstitial every 2 switches vs every 3)

### Medium Term (Month 2-3)

1. **Enable AdMob Mediation**:
   - Add networks: AppLovin, Unity Ads, ironSource
   - Let AdMob automatically optimize which network serves ads
2. **Experiment with ad placements**:
   - Test different interstitial frequencies
   - Try native ads (blend into UI) vs banners
3. **Add more rewarded ad opportunities**:
   - "Watch ad to instantly revive character" (if character dies)
   - "Watch ad to unlock rare item from loot"
   - "Watch ad for 3 hours of offline simulation bonus"

### Long Term (Month 4+)

1. **Analyze user segments**:
   - High-engagement users: Show more rewarded ads (they're willing)
   - Low-engagement users: Show fewer interstitials (reduce churn)
2. **Implement IAP (In-App Purchases)**:
   - Offer "Remove Ads" for $2.99
   - Hybrid model: Free with ads, or one-time purchase to remove
3. **Use AdMob's Smart Segmentation**:
   - Automatically shows more ads to users less likely to pay for IAP
4. **Seasonal campaigns**: Higher eCPMs during holidays (Nov-Dec)

### Expected Revenue Benchmarks

Based on typical RPG game metrics:

- **eCPM (Banner)**: $0.50 - $2.00
- **eCPM (Interstitial)**: $3.00 - $10.00
- **eCPM (Rewarded)**: $8.00 - $20.00

**Example calculation** (1000 daily active users):
- 1000 users × 1 app launch interstitial = 1000 impressions/day
- 1000 users × 30 min avg session × banner impressions = 2000 impressions/day
- 500 users × 1 rewarded ad = 500 impressions/day

**Estimated daily revenue**:
- Interstitials: (1000 / 1000) × $5 = $5
- Banners: (2000 / 1000) × $1 = $2
- Rewarded: (500 / 1000) × $12 = $6
- **Total: ~$13/day = $390/month** for 1000 DAU

Scale accordingly for your user base.

---

## Appendix: Quick Reference

### Test Ad Unit IDs (Development)

```kotlin
const val TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
const val TEST_REWARDED = "ca-app-pub-3940256099942544/5224354917"
const val TEST_BANNER = "ca-app-pub-3940256099942544/6300978111"
```

### AdMob Dashboard Links

- Main Dashboard: https://admob.google.com/
- Reports: https://admob.google.com/home/reports
- Mediation: https://admob.google.com/home/mediation
- Policy Center: https://admob.google.com/home/policy-center

### Useful Commands

```bash
# Get device advertising ID for test device registration
adb shell settings get secure advertising_id

# Clear app data (reset ad state for testing)
adb shell pm clear com.watxaut.rolenonplayinggame

# View logcat for ad events
adb logcat | grep -i "ads"
```

---

## Next Steps

1. Review this document thoroughly
2. Create AdMob account and add your app
3. Implement `AdManager.kt` and `BannerAd.kt`
4. Test with test ad units
5. Replace with real ad unit IDs
6. Submit app to AdMob for review
7. Monitor performance and iterate

**Questions? Issues?**
- AdMob Help: https://support.google.com/admob
- AdMob Community: https://groups.google.com/g/google-admob-ads-sdk

---

**Document Version**: 1.0
**Last Updated**: 2025-11-14
**Author**: Android Development Team
