package com.cktechhub.games

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var adView: AdView
    private var interstitialAd: InterstitialAd? = null
    private var isAdInitialized = false
    private var levelCompleteCount = 0
    private lateinit var loadingIndicator: ProgressBar

    companion object {
        private const val TAG = "MainActivity"

        // Test AdMob IDs — Replace with your production IDs before release
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

        // Show interstitial every N level completions
        private const val INTERSTITIAL_FREQUENCY = 2
    }

    // ──────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Full immersive mode
        enableImmersiveMode()

        // Internet check
        if (!isInternetAvailable()) {
            showOfflineError()
            return
        }

        // Initialize AdMob SDK
        MobileAds.initialize(this) { isAdInitialized = true }

        // Handle back press via OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (::webView.isInitialized && webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // Build the layout: vertical LinearLayout so ad sits BELOW WebView
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor("#000000".toColorInt())
        }

        // WebView (fills remaining space above the ad)
        webView = buildWebView()

        // Loading indicator (centered overlay)
        loadingIndicator = buildLoadingIndicator()

        // Banner ad at the bottom
        adView = buildBannerAd()

        // Wrap WebView + loader in a FrameLayout so the loader overlays the WebView
        val webFrame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // take all remaining space
            )
        }
        webFrame.addView(webView)
        webFrame.addView(loadingIndicator)

        rootLayout.addView(webFrame)
        rootLayout.addView(adView)

        setContentView(rootLayout)

        // Load the game
        webView.loadUrl("file:///android_asset/index.html")

        // Pre -load interstitial ad
        loadInterstitialAd()
    }

    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) adView.resume()
        if (::webView.isInitialized) webView.onResume()
    }

    override fun onPause() {
        if (::webView.isInitialized) webView.onPause()
        if (::adView.isInitialized) adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) webView.destroy()
        if (::adView.isInitialized) adView.destroy()
        interstitialAd = null
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enableImmersiveMode()
    }

    // ──────────────────────────────────────────────
    // Layout builders
    // ──────────────────────────────────────────────

    private fun buildWebView(): WebView {
        return WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            // ── WebView Settings ──
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                javaScriptCanOpenWindowsAutomatically = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                textZoom = 100
            }

            // ── JavaScript Interface for AdMob triggers ──
            addJavascriptInterface(AdBridge(), "AndroidBridge")

            // ── WebViewClient (safe navigation) ──
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return true
                    // Only allow local asset files
                    if (url.startsWith("file:///android_asset/")) {
                        return false
                    }
                    // Block everything else (external links, tel:, mailto:, etc.)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Remove loading indicator
                    loadingIndicator.visibility = View.GONE

                    // Inject JS bridge to notify Android on level complete
                    view?.evaluateJavascript(
                        """
                        (function() {
                            var orig = window.showLevelComplete;
                            window.showLevelComplete = function() {
                                if (orig) orig.apply(this, arguments);
                                if (window.AndroidBridge) {
                                    window.AndroidBridge.onLevelComplete();
                                }
                            };
                        })();
                        """.trimIndent(),
                        null
                    )
                }

                override fun onRenderProcessGone(
                    view: WebView?,
                    detail: android.webkit.RenderProcessGoneDetail?
                ): Boolean {
                    // Crash-proof: destroy and recreate the WebView
                    if (detail != null && !detail.didCrash()) {
                        // System killed the renderer for low memory – reload
                        Log.w(TAG, "WebView renderer killed (OOM), reloading")
                        webView.destroy()
                        return true
                    }
                    Log.e(TAG, "WebView renderer crashed")
                    return false
                }
            }

            // ── WebChromeClient (console logging) ──
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Log.d(
                        "WebView",
                        "${consoleMessage.message()} — ${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}"
                    )
                    return true
                }
            }

            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        }
    }

    private fun buildBannerAd(): AdView {
        return AdView(this).apply {
            adUnitId = BANNER_AD_UNIT_ID
            setAdSize(AdSize.BANNER)
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    private fun buildLoadingIndicator(): ProgressBar {
        return ProgressBar(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            indeterminateTintList = ColorStateList.valueOf("#ffffff".toColorInt())
        }
    }

    // ──────────────────────────────────────────────
    // Internet connectivity
    // ──────────────────────────────────────────────

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun showOfflineError() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 0, 48, 0)
            setBackgroundColor("#1a1a2e".toColorInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Icon
        layout.addView(TextView(this).apply {
            text = "📶"
            textSize = 64f
            gravity = Gravity.CENTER
        })

        // Title
        layout.addView(TextView(this).apply {
            text = getString(R.string.no_internet_title)
            setTextColor("#ffffff".toColorInt())
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 24 }
        })

        // Subtitle
        layout.addView(TextView(this).apply {
            text = getString(R.string.no_internet_subtitle)
            setTextColor("#99AABB".toColorInt())
            textSize = 14f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        })

        // Retry button
        layout.addView(Button(this).apply {
            text = getString(R.string.retry_button)
            setTextColor("#ffffff".toColorInt())
            setBackgroundColor("#7b2ff7".toColorInt())
            setPadding(64, 28, 64, 28)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 40 }
            setOnClickListener { recreate() }
        })

        setContentView(layout)
    }

    // ──────────────────────────────────────────────
    // AdMob — Interstitial
    // ──────────────────────────────────────────────

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            this,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            loadInterstitialAd() // preload next
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            interstitialAd = null
                            loadInterstitialAd()
                        }
                        override fun onAdShowedFullScreenContent() {
                            // Ad is showing
                        }
                    }
                    Log.d(TAG, "Interstitial ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial ad failed to load: ${adError.message}")
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(this@MainActivity)
        } else {
            Log.d(TAG, "Interstitial ad not ready, loading next")
            loadInterstitialAd()
        }
    }

    // ──────────────────────────────────────────────
    // Immersive full-screen mode
    // ──────────────────────────────────────────────

    private fun enableImmersiveMode() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    // ──────────────────────────────────────────────
    // JavaScript Interface (bridge between WebView & Android)
    // ──────────────────────────────────────────────

    @Suppress("unused")
    inner class AdBridge {
        @JavascriptInterface
        fun onLevelComplete() {
            levelCompleteCount++
            Log.d(TAG, "Level complete #$levelCompleteCount")
            // Show interstitial every N completions
            if (levelCompleteCount % INTERSTITIAL_FREQUENCY == 0) {
                runOnUiThread { showInterstitialAd() }
            }
        }
    }
}
