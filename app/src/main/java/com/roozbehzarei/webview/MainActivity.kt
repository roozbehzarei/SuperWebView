package com.roozbehzarei.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.roozbehzarei.webview.ui.theme.SuperWebViewTheme

// The URL of the website to be loaded in the app
private const val WEBSITE = "https://roozbehzarei.me/"

class MainActivity : ComponentActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
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
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        isRefreshing = true
    })

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        WebViewer(isRefreshing = isRefreshing,
            setRefreshed = { isRefreshing = false },
            updateProgress = { currentProgress -> progress = currentProgress })
        ProgressIndicator(progress)
        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.background
        )
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
private fun WebViewer(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    setRefreshed: () -> Unit,
    updateProgress: (Int) -> Unit
) {
    var webView: WebView? = null
    var isBackEnabled by rememberSaveable { mutableStateOf(false) }

    // Override back navigation to load WebView's previous webpage
    BackHandler(enabled = isBackEnabled) {
        webView?.goBack()
    }

    AndroidView(modifier = modifier.fillMaxSize(), factory = { context ->
        WebView(context).apply {
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

            }
            webChromeClient = object : WebChromeClient() {

                // Pass up current loading progress to be used by ProgressIndicator function
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    updateProgress(newProgress)
                }

            }
            // Configure WebView client
            with(this.settings) {
                domStorageEnabled = true
                javaScriptEnabled = true
                setSupportZoom(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isAlgorithmicDarkeningAllowed = true
                }
            }
            this.loadUrl(WEBSITE)
            webView = this
        }
    }, update = {
        if (isRefreshing) {
            it.reload()
            setRefreshed()
        }
        webView = it
    })
}