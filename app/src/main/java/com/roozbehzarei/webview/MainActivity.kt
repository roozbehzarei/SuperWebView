package com.roozbehzarei.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature
import com.roozbehzarei.webview.databinding.ActivityMainBinding

/**
 * [WEBSITE] the URL of the website to be loaded by [webView]
 */
private const val WEBSITE = ""

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        /**
         * Define and configure [webView]
         */
        webView = binding.webView
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient()
        with(webView.settings) {
            // Tell the WebView to enable JavaScript execution.
            javaScriptEnabled = true
            // Enable DOM storage API.
            domStorageEnabled = false
            // Disable support for zooming using webView's on-screen zoom controls and gestures.
            setSupportZoom(false)
        }
        //
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_ON)
                }

                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_OFF)
                }

                else -> {
                    //
                }
            }
        }

        webView.loadUrl(WEBSITE)

        /**
         * Define Swipe-to-refresh behaviour
         */
        binding.root.setOnRefreshListener {
            if (webView.url == null) {
                webView.loadUrl(WEBSITE)
            } else {
                webView.reload()
            }
        }

        /**
         * Theme Swipe-to-refresh layout
         */
        val spinnerTypedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            spinnerTypedValue,
            true
        )
        val spinnerColor = spinnerTypedValue.resourceId
        binding.root.setColorSchemeResources(spinnerColor)

        val backgroundTypedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            backgroundTypedValue,
            true
        )
        val backgroundColor = backgroundTypedValue.resourceId
        binding.root.setProgressBackgroundColorSchemeResource(backgroundColor)

        /**
         * Disable Swipe-to-refresh if [webView] is scrolling
         */
        webView.viewTreeObserver.addOnScrollChangedListener {
            binding.root.isEnabled = webView.scrollY == 0
        }

        /**
         * If there's no web page history, close the application
         */
        val mCallback = onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }
        mCallback.isEnabled = true

        setContentView(binding.root)
    }

    private inner class MyWebViewClient : WebViewClient() {

        /**
         * Let [webView] load the [WEBSITE]
         * Otherwise, launch another Activity that handles URLs
         */
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url?.contains(WEBSITE) == true) {
                return false
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.webView.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
            binding.progressIndicator.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.root.isRefreshing = false
            binding.progressIndicator.visibility = View.INVISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            binding.webView.visibility = View.GONE
            binding.errorLayout.visibility = View.VISIBLE
            binding.root.isEnabled = false
            binding.retryButton.setOnClickListener {
                if (webView.url == null) {
                    webView.loadUrl(WEBSITE)
                } else {
                    webView.reload()
                }
            }
        }

    }

    /**
     * Update the progress bar when loading a webpage
     */
    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressIndicator.progress = newProgress
        }
    }

}