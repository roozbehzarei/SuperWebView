package com.roozbehzarei.superwebview.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import androidx.core.net.toUri
import androidx.webkit.URLUtilCompat
import com.roozbehzarei.superwebview.data.model.Downloadable
import java.net.URI

object DownloadHandler {

    fun prepare(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
    ): Downloadable? {
        val parsed = try {
            URI(url)
        } catch (_: Exception) {
            return null
        }
        if ((!parsed.scheme.equals("https", ignoreCase = true) &&
                    !parsed.scheme.equals("http", ignoreCase = true)) ||
            parsed.host.isNullOrBlank() || parsed.rawUserInfo != null || parsed.port !in -1..65535
        ) return null

        val fileName = URLUtilCompat.guessFileName(url, contentDisposition, mimeType)
            .replace(Regex("""[\\/:*?"<>|\p{Cntrl}]"""), "_")
            .trim().trim('.')
            .ifBlank { "download" }
        return Downloadable(parsed.toASCIIString().toUri().normalizeScheme(), fileName, userAgent, mimeType)
    }

    fun enqueue(context: Context, download: Downloadable) {
            val request = DownloadManager.Request(download.uri)
                .setTitle(download.fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.encode(download.fileName))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            download.mimeType?.takeIf { it.isNotBlank() }?.let { request.setMimeType(it) }
            download.userAgent?.takeIf { it.isNotBlank() }?.let { request.addRequestHeader("User-Agent", it) }
            CookieManager.getInstance().getCookie(download.uri.toString())
                ?.takeIf { it.isNotBlank() }?.let { request.addRequestHeader("Cookie", it) }
            val downloadManager = checkNotNull(context.getSystemService(DownloadManager::class.java))
            check(downloadManager.enqueue(request) != -1L)
        }
}
