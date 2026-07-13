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

1. Click the Code button, which brings up a dialog.
2. In the dialog, click the Download ZIP button to save the project to your computer. Wait for the download to complete.
3. Locate the file on your computer (likely in the Downloads folder).
4. Double-click the ZIP file to unpack it. This creates a new folder that contains the project files.
5. Start Android Studio.
6. In the Welcome to Android Studio window, click Open an existing Android Studio project.
7. In the Import Project dialog, navigate to where the unzipped project folder is located.
8. Double-click on that project folder.
9. Wait for Android Studio to open the project.
10. In *app/src/main/kotlin/com/roozbehzarei/superwebview/MainActivity.kt* file, put your desired website URL as the value of `WEBSITE` constant.
11. In *app/build.gradle.kts* file, replace the unique identifier `applicationId` with your own.
12. In *app/src/main/res/values/strings.xml*, update `app_name` string resource to your desired app name.
13. Use *Asset Studio* to override app icons with your own.
14. Click the Make Project button to build the app.

> [!IMPORTANT]
> Ensure your website is "mobile-friendly" before packaging it. Since the app displays your live website, a responsive design ensures the best user experience.
