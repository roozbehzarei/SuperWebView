package com.roozbehzarei.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.roozbehzarei.webview.databinding.ActivityMainBinding

/**
 * [WEBSITE] the URL of the website to be loaded by [webView]
 */
private const val WEBSITE = "https://sarahzarei.com/"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        /**
         * Define and configure [webView]
         */
        webView = binding.webView
        webView.webViewClient = MyWebViewClient()
        with(webView.settings) {
            // Tell the WebView to enable JavaScript execution.
            javaScriptEnabled = true
            // Enable DOM storage API.
            domStorageEnabled = true
            // Disable support for zooming using webView's on-screen zoom controls and gestures.
            setSupportZoom(false)
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
         * Disable Swipe-to-refresh if [webView] is scrolling
         */
        webView.viewTreeObserver.addOnScrollChangedListener {
            binding.root.isEnabled = webView.scrollY == 0
        }

        binding.buttonRetry.setOnClickListener {
            if (webView.url == null) {
                webView.loadUrl(WEBSITE)
            } else {
                webView.reload()
            }
        }

        setContentView(binding.root)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
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
            binding.layoutError.visibility = View.GONE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.root.isRefreshing = false
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            binding.webView.visibility = View.GONE
            binding.layoutError.visibility = View.VISIBLE
            binding.root.isEnabled = false
        }

    }

}