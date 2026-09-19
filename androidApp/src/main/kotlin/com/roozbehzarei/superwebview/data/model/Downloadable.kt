package com.roozbehzarei.superwebview.data.model

import android.net.Uri

data class Downloadable(
    val uri: Uri,
    val fileName: String,
    val userAgent: String?,
    val mimeType: String?,
)