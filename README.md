<h1 align="center">
     SuperWebView
</h1>
<p align="center">
     <a style="text-decoration:none" href="https://github.com/roozbehzarei/SuperWebView/commits/master">
          <img src="https://img.shields.io/github/last-commit/roozbehzarei/superwebview?color=informational&label=last%20update&style=for-the-badge" alt="License" />
     </a>
     <a style="text-decoration:none" href="LICENSE">
          <img src="https://img.shields.io/github/license/roozbehzarei/superwebview?style=for-the-badge" alt="License" />
     </a>
     <a style="text-decoration:none" href="CODE_OF_CONDUCT.md">
          <img src="https://img.shields.io/badge/Contributor%20Covenant-2.0-4baaaa.svg?style=for-the-badge" alt="Code of Conduct" />
     </a>
</p>

If you already have a website for your business, you don't need to invest in building an Android app from scratch.

**SuperWebView** is a ready-to-use template that acts as a secure, premium wrapper for your website. It takes your existing website and displays it inside a native Android application. When your website updates, your app updates automatically!

###  Key features

* **Progress indicator:** Reports loading progress of the current webpage in real-time
* **Pull-to-refresh:** Reloads the current webpage with a simple pull-down gesture 
* **Smart links:** Opens external links (like social media or maps) in the phone's web browser, keeping your main site open
* **Video & media friendly:** Allows videos and media to play in full screen
* **Auto dark mode** Adapts automatically to the phone's dark mode settings (targeted website must support this feature)

> [!TIP]
> For targeting both Android and iOS platforms, use the cross-platform solution [FlutterWebApp](https://github.com/ebadimobina/FlutterWebApp).

### Build

1. Start [Android Studio](https://developer.android.com/studio) (Quail 2 or newer). Make sure it has [Kotlin Multiplatform](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform) plugin installed.
2. In the Welcome to Android Studio window, click Clone Repository.
3. In the Clone Repository dialog, paste `https://github.com/roozbehzarei/SuperWebView.git` into the URL text field and click on Clone.
4. Wait for Android Studio to open the project, click on Trust Project if prompted.
5. Using project tool window, open *shared > src > commonMain > ... > WebAppConfig.kt* file and put your desired website domain address as the value of `WEBSITE` constant.
6. In *androidApp > build.gradle.kts* file, replace the existing `applicationId` value with your own unique application ID.
7. In *androidApp > src > main > res > values > strings.xml*, update `app_name` string resource to your desired app name.
8. Use *Asset Studio* to override app icons with your own.
9. Using the hamburger menu, click on *Build > Generate Signed App Bundle or APK...*.
10. Follow the *Generate Signed App Bundle or APK* wizard instructions to sign and generate your app APK or bundle.   

> [!IMPORTANT]
> Ensure your website is "mobile-friendly" before packaging it. Since the app displays your live website, a responsive design ensures the best user experience.
