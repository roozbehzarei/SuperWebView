package com.roozbehzarei.superwebview.shared

object WebAppConfig {

    const val PROTOCOL = "https://"
    const val DOMAIN = "roozbehzarei.com"

    fun shouldLaunchInBrowser(url: String): Boolean = !url.startsWith(
        prefix = PROTOCOL + DOMAIN, ignoreCase = true
    ) and !url.startsWith(prefix = PROTOCOL + "www." + DOMAIN, ignoreCase = true)

    fun isProgressVisible(progress: Int): Boolean = progress in 1..99

}