<h1 align="center">
     SuperWebView
</h1>
<p align="center">
     <a style="text-decoration:none" href="https://github.com/roozbehzarei/SuperWebView/commits/master">
          <img src="https://img.shields.io/github/last-commit/roozbehzarei/superwebview?color=informational&label=last%20update" alt="License" />
     </a>
     <a style="text-decoration:none" href="LICENSE">
          <img src="https://img.shields.io/github/license/roozbehzarei/superwebview" alt="License" />
     </a>
     <a style="text-decoration:none" href="CODE_OF_CONDUCT.md">
          <img src="https://img.shields.io/badge/Contributor%20Covenant-2.0-4baaaa.svg" alt="Code of Conduct" />
     </a>
</p>

If you want to create an Android app from any website without writing code, SuperWebView is the perfect solution for you. It is a ready-made WebView template that saves you time and hassle.

### Variants
|  | [Compose](https://github.com/roozbehzarei/SuperWebView/releases/tag/compose) | [Legacy (XML)](https://github.com/roozbehzarei/SuperWebView/releases/tag/legacy) |
| --- | --- | --- |
| Compatibility | Android 6.0 - 16 | Android 5.0 - 14 |
| Progress indicator | ✅ | ✅ |
| Pull-to-refresh | ✅ | ✅ |
| Custom error screen | ❌ | ✅ |
| Source code obfuscation | ✅ | ❌ |

> [!NOTE]
> For targeting both Android and iOS platforms, use the cross-platform solution [FlutterWebApp](https://github.com/ebadimobina/FlutterWebApp).

## Build
1. Click the **Code** button, which brings up a dialog.
2. In the dialog, click the **Download ZIP** button to save the project to your computer. Wait for the download to complete.
3. Locate the file on your computer (likely in the **Downloads** folder).
4. Double-click the ZIP file to unpack it. This creates a new folder that contains the project files.
5. Start Android Studio.
6. In the **Welcome to Android Studio** window, click **Open an existing Android Studio project**.
7. In the **Import Project** dialog, navigate to where the unzipped project folder is located.
8. Double-click on that project folder.
9. Wait for Android Studio to open the project.
10. Click the **Make Project** button to build the app.

## Customize
1. In *MainActivity.kt*, put your own website URL as the value of `WEBSITE` constant.
2. Replace default app icons and name with your own.
3. Change the unique identifier of your app, `applicationId`, in *app > build.gradle.kts*.
