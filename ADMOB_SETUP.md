# Google AdMob Setup Guide

This project uses Google AdMob for monetization. Before releasing to production, you **must** replace the test AdMob IDs with your own.

---

## Where to Update AdMob IDs

There are **2 files** that contain AdMob IDs you need to change:

---

### 1. AndroidManifest.xml

**File:** `app/src/main/AndroidManifest.xml`

**Line:** Inside the `<application>` tag

```xml
<!-- AdMob App ID -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-3940256099942544~3347511713" />
```

**What to change:** Replace the `android:value` with your AdMob **App ID**.

| Field           | Current (Test)                        | Your Production Value          |
|-----------------|---------------------------------------|-------------------------------|
| AdMob App ID    | `ca-app-pub-3940256099942544~3347511713` | `ca-app-pub-XXXXXXX~YYYYYYY` |

> The App ID has a `~` (tilde) separator. You can find it in AdMob Console → Apps → App settings.

---

### 2. MainActivity.kt

**File:** `app/src/main/java/com/cktechhub/games/MainActivity.kt`

**Lines:** Inside the `companion object` block

```kotlin
companion object {
    private const val TAG = "MainActivity"

    // Test AdMob IDs — Replace with your production IDs before release
    private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Show interstitial every N level completions
    private const val INTERSTITIAL_FREQUENCY = 2
}
```

**What to change:**

| Constant               | Current (Test)                          | Your Production Value            | Ad Type         |
|------------------------|-----------------------------------------|----------------------------------|-----------------|
| `BANNER_AD_UNIT_ID`    | `ca-app-pub-3940256099942544/6300978111` | `ca-app-pub-XXXXXXX/YYYYYYYYY`  | Banner (bottom) |
| `INTERSTITIAL_AD_UNIT_ID` | `ca-app-pub-3940256099942544/1033173712` | `ca-app-pub-XXXXXXX/ZZZZZZZZZ`  | Full-screen     |

> Ad Unit IDs have a `/` (slash) separator. Create them in AdMob Console → Ad units.

---

## Step-by-Step: Getting Your AdMob IDs

1. Go to [Google AdMob Console](https://apps.admob.com/)
2. **Create an App** (if not already done)
   - Add your app name, platform (Android), and whether it's on Google Play
   - This gives you the **App ID** (`ca-app-pub-XXXXXXX~YYYYYYY`)
3. **Create Ad Units** under your app:
   - Create a **Banner** ad unit → copy the **Ad Unit ID** for `BANNER_AD_UNIT_ID`
   - Create an **Interstitial** ad unit → copy the **Ad Unit ID** for `INTERSTITIAL_AD_UNIT_ID`
4. **Update the 2 files** listed above with your real IDs
5. Rebuild and test

---

## Optional: Interstitial Ad Frequency

In `MainActivity.kt`, you can control how often interstitial ads appear:

```kotlin
private const val INTERSTITIAL_FREQUENCY = 2  // Shows ad every 2 level completions
```

| Value | Behavior                              |
|-------|---------------------------------------|
| `1`   | Show interstitial on every level win  |
| `2`   | Show every 2nd level win (default)    |
| `3`   | Show every 3rd level win              |

---

## Important Notes

- **Never release with test IDs** — Test IDs (`ca-app-pub-3940256099942544/...`) only serve Google test ads and generate no revenue
- **App ID vs Ad Unit ID** — They look similar but are different:
  - App ID = `~` separator → goes in `AndroidManifest.xml`
  - Ad Unit ID = `/` separator → goes in `MainActivity.kt`
- **Test on real devices** — Emulators may not render ads properly
- **It can take up to 15 minutes** after creating ad units in AdMob Console before they start serving
