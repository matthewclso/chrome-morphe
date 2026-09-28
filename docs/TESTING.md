# Device acceptance

Target: Chrome 153.0.8010.53 (801005304), unrooted Galaxy S26 SM-S942U1, Android 16/API 36,
ARM64, 4096-byte pages, Gboard. Test package: `app.matthew.chrome.test`.
Stock Chrome and the Samsung Internet default-browser role are not fixtures.

## Settings and layout acceptance, September 27–28, 2026

| Check | Observed result |
| --- | --- |
| Main Settings → Morphe settings | Opens the private settings activity; remains available when the toolbar button is hidden. |
| Four switches | Persist across activity recreation and app updates. |
| Incognito address bar button off | Hides the button and returns its width to the address field. |
| True bottom off | Native new-tab toolbar and Hub controls return to their original top positions. |
| True bottom on | New-tab/address editing field stays below content; Hub action row, mode/group selector, menu and search move below the tab grid. |
| Active tab search | Field above Gboard, results above the field; accessible bounds do not overlap. |
| Native Theme screen | System default, Light, Dark and Black choices; Black also updates the Appearance summary. |
| Black → Dark | Restores gray backgrounds; selecting Light also disables Black. |
| Black palette | Pixel checks cover the toolbar, address field, NTP controls and settings page. Text and prominent accents remain visible. |
| Morphe back button | Transparent idle background matches the surrounding page, including #000000 in Black mode. |
| Native new-tab visibility | Unchanged, as requested. |

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
