# Device acceptance

Target: Chrome 153.0.8010.53 (801005304), unrooted Galaxy S26 SM-S942U1, Android 16/API 36,
ARM64, 4096-byte pages, Gboard. Test package: `app.matthew.chrome.test`.
Stock Chrome and the Samsung Internet default-browser role are not fixtures.

## MicroG acceptance, September 28, 2026

- The initial failure was reproduced in Chrome's own process: stock Google Play
  Services rejected the renamed package and replacement signing certificate.
- The account adapter builds and patches the exact target. Device installation
  uses the same signing key as the preceding installed Chrome Morphe, with `-r`
  and no uninstall or data clear.
- Morphe settings exposes the account permission flow only when the optional
  MicroG patch is selected. The user granted access and confirmed successful
  Chrome sign-in through MicroG 6.1.1.
- Sign-in persisted through subsequent Chrome Morphe updates. The remaining
  account error was traced to Trusted Vault's `KeyRetrieval.API`, which still
  used stock Google Play Services. MicroG 6.1.1 did not export this service.
- The Trusted Vault client now uses MicroG's package/action and direct service
  lookup. With 6.1.1, the error became `API_UNAVAILABLE`, rather than the Google
  certificate `DEVELOPER_ERROR`, confirming the changed transport boundary.
- With user approval, MicroG was updated in place to 7.1.1 after checking the
  APK signature matched the installed MicroG certificate. Android now resolves
  the required key-retrieval service. The user completed verification, confirmed
  the account error cleared, and confirmed existing bookmarks appeared.
- Sign-in and successful verification persisted after a cold restart; the account
  settings page no longer showed the verification error.
- The installed candidate's APK signature and 4 KB alignment pass. Applying only
  Chrome customization also succeeds without selecting the optional MicroG patch.
- Regression: the user reported Incognito and homepage articles disappearing;
  device inspection confirmed Chrome's native New Incognito tab was disabled.
  The same account permits Incognito in stock Chrome, as confirmed by the user.
  MicroG 6.1.1's actual APK returns `1` for every `hasCapabilities` request,
  falsely including parental controls. The initial integration failed to account
  for this stub. Version 7.1.1 returned `6` (not in cache) for that capability;
  Chrome retains previously known capability values when updates are unknown.
- A candidate using Google's Gaia capability endpoint was tested but not retained:
  both native token and Android authenticator routes failed to obtain its scoped
  token. A strictly filtered diagnostic found `RESTRICTED_CLIENT`; no raw MicroG
  logs, tokens, account identifiers or authentication responses were retained.
- A minimum MicroG 7.1.1 check now protects account listing, token and capability
  requests from the 6.1.1 stub. Recovery of the old account state is pending device
  acceptance. Public main/release remain at 0.1.2; integration work is on the
  `microg-integration` branch.
- Native Google Password Manager failed to launch. MicroG's corresponding UI only
  opens Google's website. The patch now offers that website with an explicit
  native-saving/autofill limitation. The main Settings entry opens the dialog on
  the S26; website navigation still needs acceptance.
- Custom passphrases, managed/supervised accounts and recovery on another device
  have not been accepted. Native Google password saving/autofill is unsupported.

Repeat permission denial/grant, account addition, cold-start sign-in retention,
transport failure, encrypted-data verification, actual bookmark synchronization,
and Incognito authentication when changing these hooks. Account display alone is
not Sync acceptance. Never record account credentials, tokens, encryption keys,
or MicroG authentication logs in test evidence.

## Settings and layout acceptance, September 27–28, 2026

| Check | Observed result |
| --- | --- |
| Main Settings → Morphe settings | Opens the private settings activity; remains available when the toolbar button is hidden. |
| Four switches | Persist across activity recreation and app updates. |
| Incognito address bar button off | Hides the button and returns its width to the address field. |
| True bottom off | Native new-tab toolbar and Hub controls return to their original top positions. |
| True bottom on | New-tab/address editing field stays below content; Hub action row, mode/group selector, menu and search move below the tab grid. |
| Active tab search | Field above Gboard, results above the field in portrait and landscape; accessible bounds do not overlap. Tapping a history result in landscape opens the fixture in a regular tab. |
| Native Theme screen | System default, Light and Dark choices remain; Black is controlled only from Morphe settings. |
| Black off / native Light | Restores Chrome’s native theme; selecting Light also disables Black. |
| Black palette | Pixel checks cover the toolbar, address field, NTP controls, suggested article cards and settings page. Text and prominent accents remain visible. |
| Settings cards and menus | Pixel checks confirm #000000 for settings cards, the three-dot menu and the homepage shortcut long-press menu. |
| Morphe back button | Transparent idle background matches the surrounding page, including #000000 in Black mode. |
| Native new-tab visibility | Unchanged, as requested. |

A further device regression on September 28 confirmed full-browser external links use regular tabs with the default option off, private tabs with it on, unchanged Custom Tab controls and regular storage, and return to the native authentication screen after changing appearance from Incognito.

Black mode off/on restored native gray article cards and then #000000 cards. A final rotation check found that a portrait search-field margin could collapse its height in landscape; v0.1.1 fixes this by translating the field while retaining its native measurement.

Morphe Manager on the S26 successfully imported the repository URL and displayed Chrome 153.0.8010.53, build 801005304. With source 0.1.2, the source name updates to Chrome Morphe. Selecting the matching original APK through Manager's file picker starts patching without an unsupported/experimental-version warning. Manager also completed a patch using its saved original APK; the exported result has the Chrome Morphe label, the expected package/version/build, a valid signature and 4 KB alignment. Device installation used the desktop-patched APK and development signing key; installation of the Manager-signed result remains untested.

Local UI XML, screenshots, PID-filtered logs and APK reports are retained outside this repository.
Protected Incognito screenshots remain protected; inspect accessible mode controls and the physical display.
Passing compilation, signing or fingerprint matching is not evidence of UI correctness.

## Earlier privacy acceptance, September 27, 2026

The preceding prototype established the following behaviors on the same exact Chrome build:

- Launcher opens Incognito on cold and warm starts.
- External HTTP(S) full-browser links open Incognito, including links sent by a separate fixture app.
- Disabling the default option restores regular external navigation; re-enabling it restores private navigation.
- Custom Tabs keep their own activity and regular cookie/localStorage context, without the toolbar mode button.
- Regular/private cookies and localStorage remain separate in both directions; a private marker is absent from regular history.
- Closing all private tabs discards private storage.
- Mode switching preserves existing regular tabs and creates a native tab if a destination collection is empty.
- The user confirmed rotation, address entry, suggestion tapping and mode switching in both orientations.
- The user confirmed authentication on returning through the launcher. An additional check of “See other tabs” → regular tab → mode button returned to the Incognito lock screen.

These earlier checks are distinct from the settings/layout acceptance above. Recheck the flows below when changing the relevant hooks.
No real browsing data is needed for any privacy assertion.

## Repeatable fixtures

1. Run `python3 tests/storage_server.py` on the development machine, then forward the device port with `adb reverse tcp:8765 tcp:8765`.
2. In an explicit regular tab, enter `http://127.0.0.1:8765/set?value=regular`. Confirm both storage values are regular.
3. Build `python3 scripts/build_link_harness.py` and install the resulting local fixture APK.
4. Send a full-browser link from that separate app:

   ```sh
   adb shell am start -S --user 0 -n app.matthew.chrome.linktest/.MainActivity \
     --es kind full --es url http://127.0.0.1:8765/read
   ```

   Confirm Incognito and empty storage. `-S` stops only the fixture app so a previous Custom Tab task cannot merely be brought to the foreground.
5. Send `http://127.0.0.1:8765/private-only?value=private`, switch to regular, and confirm regular values remain. Search regular history for `private-only`; expect no results.
6. Send `--es kind custom --es url http://127.0.0.1:8765/custom-tab`. Expect `CustomTabActivity`, Close/Minimize/Share controls, regular storage, and no added mode button.
7. Close all Incognito tabs in the test app, reopen the `/read` fixture privately, and expect empty storage.
8. Disable the default setting by holding the mode button. Verify launcher and external link behavior, then restore it.

Use `scripts/device_ui.py LABEL` for accessible UI evidence. `--tap` taps one exact matching test-app label and refuses ambiguous matches. `--serial` or `ANDROID_SERIAL` selects the phone when more than one device is connected.

## Checks to repeat for future builds

- Rotate during regular/private address entry; confirm the address bar stays above the keyboard and suggestions can be tapped and scrolled.
- Enable Chrome's **Lock Incognito tabs when you leave Chrome**. Leave the app and return through both the launcher and the mode button; authentication must still be required.
- Close every regular tab, then switch from Incognito to regular; one regular new tab should be created.
- Select Chrome's top address-bar position and verify the native top behavior still works, then restore bottom.
- Check portrait, landscape, keyboard dismissal, back navigation, new tab, tab switcher, scrolling, and a cold start.

All private-session assertions use test data. Do not import real browsing data to make these tests pass.
