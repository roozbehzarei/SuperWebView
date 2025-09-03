package com.roozbehzarei.superwebview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.roozbehzarei.superwebview.ui.theme.SuperWebViewTheme

private const val WEBSITE = "https://roozbehzarei.com"

class MainActivity : ComponentActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            SuperWebViewTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainScreen() {
    var progress by rememberSaveable { mutableIntStateOf(0) }
    var fullScreenView: View? by rememberSaveable { mutableStateOf(null) }

    Box(
        Modifier.fillMaxSize()
    ) {
        WebViewWithRefresher(
            modifier = Modifier.fillMaxSize(),
            updateProgress = { currentProgress -> progress = currentProgress },
            onViewReceived = {
                fullScreenView = it
            },
        )
        ProgressIndicator(progress)
    }

    AnimatedVisibility(fullScreenView != null) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context -> fullScreenView!! })
    }

}

@Composable
private fun ProgressIndicator(progress: Int) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(), visible = progress in 1..99
    ) {
        LinearProgressIndicator(progress = { progress.toFloat() / 100 })
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewWithRefresher(
    modifier: Modifier = Modifier, updateProgress: (Int) -> Unit, onViewReceived: (View?) -> Unit
) {
    var webView: WebView? = null
    val webViewId = View.generateViewId()
    var isBackEnabled by rememberSaveable { mutableStateOf(false) }
    val primaryColorArgb = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColorArgb = MaterialTheme.colorScheme.secondary.toArgb()
    val tertiaryColorArgb = MaterialTheme.colorScheme.tertiary.toArgb()

    // Override back navigation to load WebView's previous webpage
    BackHandler(enabled = isBackEnabled) {
        webView?.goBack()
    }
    AndroidView(modifier = modifier.fillMaxSize(), factory = { context ->

        val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
            setColorSchemeColors(
                primaryColorArgb, secondaryColorArgb, tertiaryColorArgb
            )
            setOnRefreshListener {
                webView?.reload()
            }
        }

        webView = WebView(context).apply {
            id = webViewId
            webViewClient = object : WebViewClient() {

                // Open external links in web browser
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    if (request?.url.toString().startsWith(WEBSITE)) {
                        return false
                    }
                    Intent(Intent.ACTION_VIEW, request?.url).apply {
                        context.startActivity(this, null)
                    }
                    return true
                }

                // Enable BackHandler if WebView can go back
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isBackEnabled = view?.canGoBack() == true
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    swipeRefreshLayout.isRefreshing = false
                }

            }
            webChromeClient = object : WebChromeClient() {

                // Pass up current loading progress to be used by ProgressIndicator function
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    updateProgress(newProgress)
                    if (newProgress == 100) swipeRefreshLayout.isRefreshing = false
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    onViewReceived(view)
                    super.onShowCustomView(view, callback)
                }

                override fun onHideCustomView() {
                    onViewReceived(null)
                    super.onHideCustomView()
                }

            }
            // Configure WebView client
            with(settings) {
                domStorageEnabled = true
                javaScriptEnabled = true
                setSupportZoom(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isAlgorithmicDarkeningAllowed = true
                }
            }
            loadUrl(WEBSITE)
        }
        swipeRefreshLayout.addView(webView)
        swipeRefreshLayout
    }, update = { swipeRefreshLayout ->
        val view = swipeRefreshLayout.findViewById<WebView>(webViewId)
        webView = view
    })

}