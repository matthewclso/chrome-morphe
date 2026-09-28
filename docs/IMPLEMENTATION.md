# Implementation

Target: original `com.android.chrome` 153.0.8010.53, version code 801005304, ARM64.
The version gate runs inside each feature patch even if Morphe compatibility is forced. Stable strings locate host code;
exact descriptors, call counts and resource IDs validate the supported artifact. These are not cross-version hooks.

## Settings and state

A resource patch adds a native Preference entry to MainSettings, opening a private, non-exported MorpheSettingsActivity.
The four switches use one SharedPreferences file, `chrome_patch`. The Incognito-default key is preserved from the prototype.
Black mode is controlled only from Morphe settings. Hiding the toolbar button never hides the settings entry.

The application hook initializes only the browser's main process. Application context is retained, activities are weakly
tracked, and appearance changes recreate an existing activity on return so native backgrounds/layout can be restored.
Native authentication state and secure-window flags are not modified.

## Browsing modes

The mode button calls Chrome's native menu action for an empty collection and TabModelSelector for existing tabs.
Incognito availability uses Chrome's native profile policy check. Default-mode routing covers MAIN/LAUNCHER and external
HTTP(S) full-browser intents, preserving trusted internal regular-tab choices and CustomTabActivity routing.

Toolbar inflation is hooked immediately after the native super call: later R8 code reuses the receiver register and can
jump straight to a return. Branch-target external-link instructions are replaced rather than inserting a skippable prefix.
The button reads final model state before drawing; a disabled button uses GONE so the address field reclaims its width.

## Bottom positioning

The native toolbar controller retains its IME, tab-switcher and Find-in-page transitions. Runtime gates suppress its
new-tab/focus-to-top decisions only with True bottom enabled. That setting also overrides the native top preference;
choosing Top in Chrome's address-bar settings disables True bottom. Disabling the extension restores native decisions.

The suggestions container uses the available space above the actual toolbar. NTP morph hooks preserve a real editable
bottom field. HubToolbarView keeps its native controls/listeners; its wrapper anchors at the bottom and the tab grid
reserves the measured toolbar/search height. Layout changes recalculate that reservation for rotation and pane changes. The separate SearchActivity places its field above the visible keyboard and removes the native below-toolbar anchor from its result container, reserving the space above the field.

## Black theme

Black chooses Chrome's existing dark configuration plus an independent palette flag. Chrome's Theme screen retains
its original System default, Light and Dark choices. Selecting a native theme clears the Black flag so native theme
choices remain effective. The Morphe switch is the sole Black-mode control.

Dark neutral backgrounds map to #000000, including translucent fills composited over black; accent colors, text colors and rendered websites are not globally
recolored. Hooks cover native background/tint setters and ToolbarPhone's background palette, with a layout pass for
XML-created background drawables and the Discover card's rounded background paint. Super dispatch is preserved to avoid recursively re-entering overridden setters.
Stateful background palettes retain their state specifications and ordering using the validated [Android 16 ColorStateList parcel format](https://github.com/aosp-mirror/platform_frameworks_base/blob/android16-release/core/java/android/content/res/ColorStateList.java); an unrecognized format is left unchanged. Popup windows are normalized through their own content tree because they are outside the Activity decor tree. The flag gates all transforms; activity recreation restores original drawables when disabled.

## Packaging and provenance

The separate installation keeps original component/JNI class names but rewrites package identity, permissions,
provider authorities and selected identity strings. The local test key stays outside the repository. No browser data
is copied from stock Chrome. The installed .53 update has its own libchrome.so and is not the factory Trichrome package.

Prototype build setup was explored with Morphe's patch template. This publication checkout contains independently
written patch/runtime code and project files; template sample patches and helper implementations are not included.
Gradle wrapper files retain their Apache notices. See NOTICE for third-party tooling attribution.

Build tool versions: Morphe Patcher 1.14.1, Morphe Gradle plugin 1.3.4, Gradle 9.7.1, JDK 21, Android SDK 36.
