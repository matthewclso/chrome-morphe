# Chrome Tweaks

[![Build](https://github.com/matthewclso/chrome-morphe/actions/workflows/build.yml/badge.svg)](https://github.com/matthewclso/chrome-morphe/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Independent Android Chrome patches for use with [Morphe Manager](https://github.com/MorpheApp/morphe-manager).

## Features

Open **Chrome Settings → Morphe settings** to enable or disable:

| Setting | Behavior |
| --- | --- |
| Incognito address bar button | Switch between existing regular and private tabs; create a tab if the destination is empty. |
| Black mode | Use `#000000` for dark neutral Chrome backgrounds. Also selectable as **Black** in **Appearance → Theme**, alongside System default, Light and Dark. Web page content and accent colors retain their own colors. |
| True bottom address bar | Keep the address field at the bottom on new-tab pages and above the keyboard. Move the tab-view action row, mode selector, tab groups, menu and search field to the bottom. |
| Open in Incognito by default | Use Incognito for the launcher and external HTTP(S) links that open the full browser. Embedded Custom Tabs keep their normal behavior. |

The toolbar button, true bottom and Incognito default are initially enabled; Black mode is initially disabled.
Holding the toolbar mode button also opens Morphe settings. The settings entry remains available when the button is disabled.
Chrome’s native Incognito authentication and new-tab button visibility rules are retained.

## Supported app

| App | Version | Version code | Architecture |
| --- | --- | --- | --- |
| Google Chrome (`com.android.chrome`) | **153.0.8010.53** | **801005304** | **arm64-v8a** |

Experimental, exact-build support. Other builds are rejected even if compatibility checks are forced.
Device acceptance uses an unrooted Galaxy S26 running Android 16 with 4 KB memory pages.
Other devices and 16 KB page configurations have not been accepted. See [testing](docs/TESTING.md).

## Install with Morphe Manager

1. Install a current [Morphe Manager release](https://github.com/MorpheApp/morphe-manager/releases/latest) with Patcher **1.14.1 or newer**.
2. Open **Patch sources → Add patch source → Remote** and enter:

   ```text
   https://github.com/matthewclso/chrome-morphe
   ```

   The explicit metadata URL is also supported:

   ```text
   https://raw.githubusercontent.com/matthewclso/chrome-morphe/main/patches-bundle.json
   ```

   Alternatively, download the `.mpp` file from [Releases](https://github.com/matthewclso/chrome-morphe/releases) and add it as a **Local** patch source.
3. Enable **Experimental app versions** for this source if the supported Chrome build is hidden. Select the original Chrome version listed above. Use the complete installed split package or a complete original APK/APKS, including its native libraries; a lone configuration split is insufficient.
4. Select **Chrome customization**. Its dependencies include **Separate Chrome test installation** and the feature hooks.
5. Patch and install the result. It appears as **Chrome Patch Test** (`app.matthew.chrome.test`) alongside stock Chrome. Complete welcome screens using **Use without an account** or **Skip**.
6. Open **Settings → Morphe settings**. True bottom enables the bottom position. Selecting **Top** in Chrome’s own address-bar settings turns True bottom off.

The renamed app has separate tabs, settings and storage. Google account integration rejects its replacement signing certificate, so use it without an account.
Keep the same Manager signing key for updates. A signature mismatch means an existing test installation used a different key; reuse that key or back up what you need before removing **only the test app**. Removing it deletes its data.

This repository distributes patch bundles, not Chrome APKs. Obtain the exact unmodified app yourself.
This is a development project and does not provide Chrome security updates automatically.

## Build

Requirements: JDK 21, Android SDK platform/build tools 36, and GitHub Packages credentials with `read:packages` access to the Morphe registry.

```sh
export JAVA_HOME=/path/to/jdk-21
export ANDROID_HOME=/path/to/android-sdk
export GITHUB_ACTOR=your-github-login
# Set GITHUB_TOKEN securely, or use gpr.user/gpr.key in your private Gradle user properties.
bash gradlew buildAndroid --no-daemon
```

Bundle output: `patches/build/libs/patches-0.1.0.mpp`.
The local development helpers in `scripts/` also support the prepared JDK/SDK layout described in [development](docs/DEVELOPMENT.md).

## Project layout

- `patches/`: exact-version Kotlin bytecode and resource hooks.
- `extensions/extension/`: Java runtime controls and settings UI.
- `tests/`: separate sender-app and local storage fixtures.
- `docs/`: implementation, development and device acceptance notes.
- `.github/workflows/`: build checks and tagged draft releases.

See [CONTRIBUTING.md](CONTRIBUTING.md) for compatibility and validation expectations.

## License and credits

Original code is [MIT licensed](LICENSE). Third-party notices are in [NOTICE](NOTICE).
Built with [Morphe Patcher](https://github.com/MorpheApp/morphe-patcher) and its Gradle plugin.
Native behavior was investigated against [Chromium 153.0.8010.53](https://github.com/chromium/chromium/tree/153.0.8010.53).
This project is not affiliated with Google, Chromium or Morphe.
