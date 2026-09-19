package com.roozbehzarei.superwebview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.roozbehzarei.superwebview.data.download.DownloadHandler
import com.roozbehzarei.superwebview.data.model.Downloadable
import com.roozbehzarei.superwebview.shared.WebAppConfig
import com.roozbehzarei.superwebview.ui.theme.SuperWebViewTheme

/**
 * Main activity of the application.
 * Hosts the Jetpack Compose UI and manages the WebView.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     * Sets up the splash screen and the main content view.
     */
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

/**
 * Composable function that defines the main screen layout.
 * Includes a WebView, a progress indicator, and handles full-screen video playback.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainScreen() {
    var progress by rememberSaveable { mutableIntStateOf(0) }
    var fullScreenView: View? by remember { mutableStateOf(null) }

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

    // Shows the full-screen view (e.g., for videos) when fullScreenView is not null
    AnimatedVisibility(fullScreenView != null) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { context -> fullScreenView!! })
    }

}

/**
 * Displays a linear progress indicator.
 * The indicator is visible only when loading progress is between 1% and 99%.
 *
 * @param progress The current loading progress (0-100).
 */
@Composable
private fun ProgressIndicator(progress: Int) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(), visible = WebAppConfig.isProgressVisible(progress)
    ) {
        LinearProgressIndicator(progress = { progress.toFloat() / 100 })
    }
}

/**
 * Wraps a WebView with a SwipeRefreshLayout.
 * It handles WebView configuration, client settings, and back navigation.
 *
 * @param modifier
 * @param updateProgress Callback to update the loading progress.
 * @param onViewReceived Callback invoked when a custom view (e.g., for full-screen video) is shown or hidden.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewWithRefresher(
    modifier: Modifier = Modifier, updateProgress: (Int) -> Unit, onViewReceived: (View?) -> Unit
) {
    var webView: WebView? = null
    val webViewId = remember { View.generateViewId() }
    val context = LocalContext.current
    var pendingDownload by remember { mutableStateOf<Downloadable?>(null) }
    val storagePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val download = pendingDownload
        pendingDownload = null
        if (download != null) {
            if (granted) {
                try {
                    DownloadHandler.enqueue(context, download)
                    Toast.makeText(context, R.string.toast_download_started, Toast.LENGTH_LONG).show()
                } catch (_: Exception) {}
            } else {
                Toast.makeText(context, R.string.toast_storage_permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }
    var isBackEnabled by rememberSaveable { mutableStateOf(false) }
    val primaryColorArgb = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColorArgb = MaterialTheme.colorScheme.secondary.toArgb()
    val tertiaryColorArgb = MaterialTheme.colorScheme.tertiary.toArgb()

    // Override back navigation to allow WebView to go back in its history
    BackHandler(enabled = isBackEnabled) {
        webView?.goBack()
    }
    AndroidView(modifier = modifier.fillMaxSize(), factory = { context ->
        // Create SwipeRefreshLayout
        val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
            setColorSchemeColors(
                primaryColorArgb, secondaryColorArgb, tertiaryColorArgb
            )
            setOnRefreshListener {
                webView?.reload() // Reload WebView on swipe
            }
        }

        // Create and configure WebView
        webView = WebView(context).apply {
            id = webViewId
            setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                updateProgress(100)
                swipeRefreshLayout.isRefreshing = false
                val download = DownloadHandler.prepare(url, userAgent, contentDisposition, mimeType)
                    ?: return@setDownloadListener
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if (pendingDownload == null) {
                        pendingDownload = download
                        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                } else {
                    try {
                        DownloadHandler.enqueue(context, download)
                        Toast.makeText(context, R.string.toast_download_started, Toast.LENGTH_LONG).show()
                    } catch (_: Exception) {}
                }
            }
            webViewClient = object : WebViewClient() {

                /**
                 * Handles URL loading. Opens external links in a web browser,
                 * internal links (starting with WEBSITE) are loaded in the WebView.
                 */
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {

                    // Load in WebView
                    if (WebAppConfig.shouldLaunchInBrowser(request?.url.toString())
                            .not()
                    ) return false

                    // Open external links in web browser
                    try {
                        Intent(Intent.ACTION_VIEW, request?.url).apply {
                            context.startActivity(this, null)
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) e.printStackTrace()
                    }

                    // Indicate that the URL loading is handled
                    return true
                }

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

                /**
                 * Reports the loading progress of the current page.
                 */
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
            // Configure WebView settings
            with(settings) {
                domStorageEnabled = true
                javaScriptEnabled = true
                setSupportZoom(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isAlgorithmicDarkeningAllowed =
                        true // Enable algorithmic dark mode on Android Tiramisu+
                }
            }
            loadUrl(WebAppConfig.PROTOCOL + WebAppConfig.DOMAIN) // Load the initial website
        }
        swipeRefreshLayout.addView(webView) // Add WebView to SwipeRefreshLayout
        swipeRefreshLayout // Return SwipeRefreshLayout as the view for AndroidView
    }, update = { swipeRefreshLayout ->
        // Re-obtain the WebView instance during recomposition/update
        // This is necessary because the factory lambda might not be re-executed
        val view = swipeRefreshLayout.findViewById<WebView>(webViewId)
        webView = view
    })

}