# Android autofill prototype

This is a device experiment, not part of the public 0.2.0 release.
It keeps MicroG sign-in while allowing Google's stock Android autofill service
to handle website forms. Chrome 153 normally rejects that provider in its
platform-autofill mode; the optional **Android autofill** patch removes
that exclusion in availability checking and provider preference persistence.
It also disables Android autofill entirely for off-the-record profiles,
including Incognito and private Custom Tabs.
Native policy checks, service availability, explicit user selection, origin
information and provider authentication remain unchanged.

## Local validation

- The prototype builds and patches Chrome 153.0.8010.53 (801005304, ARM64).
- The resulting APK passes signature verification with the existing development
  key and 4 KB alignment checks.
- Disassembly confirms two Google-specific comparison results changed in
  `AutofillClientProviderUtils`. The tab's provider preparation now routes
  off-the-record profiles through Chrome's existing disabled path, which
  destroys any attached provider and sets the content view to
  `IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS`.
- The loopback fixture accepts only its public dummy credential and never logs
  or persists submitted fields. Windows can reach the WSL fixture over localhost.

## Device findings, 2026-09-28

On the Galaxy S26, while retaining MicroG sign-in:

- Prototype v43 saved a disposable login to Google's system password service.
  Revisiting the form offered that login; after Google's confirmation, both
  fields were filled correctly and the fixture accepted the submission.
- A second hostname did not receive that saved login or populate either field.
- The HTTPS fixture was served through local DevTools request interception at
  `https://example.com/morphe-autofill-fixture/`; no submitted fields were sent to
  that domain. `example.org` was the separate-host negative check. The plain
  loopback fixture did not receive a save offer on this device.
- **v43 failed Incognito privacy testing:** the user observed a Google save
  prompt after submitting a private dummy login. Android autofill was disabled
  immediately afterward. Do not distribute that prototype.
- v45 adds the off-the-record guard described above. Its device privacy and
  regular-mode regression checks are pending. Private autofill is deliberately
  unavailable; merely suppressing the submission callback would not prevent
  disclosure of private form values or later save prompts.

## Device acceptance

1. Install the prototype as an update, with **Chrome customization**, **MicroG
   sign-in** and **Android autofill** selected. Retain the existing signing key.
2. Keep Google selected as Android's autofill provider. In Chrome's autofill
   settings, choose **Autofill using another service** and confirm the restart.
3. Start `python3 tests/autofill_server.py` and run
   `adb reverse tcp:8766 tcp:8766`. In an explicit regular tab, open
   `http://localhost:8766/login`. For a provider that excludes localhost, use a
   controlled HTTPS fixture: forward `tcp:9223` to
   `localabstract:chrome_devtools_remote` and run
   `node tests/https_autofill_fixture.mjs <new-fixture-tab-id>` (Node 24).
   It intercepts every fixture request locally, including POSTs. Close the test
   tab before stopping the harness. Never submit fixture data to an unrelated
   public website.
4. Check whether Google offers to save it. Reopen the form and test actual
   filling and successful submission. The expected result is
   **Expected dummy login matched: yes**.
5. Open `http://127.0.0.1:8766/login` (or change `example.com` to `example.org`
   with the HTTPS harness active) and verify the first host's credential is not
   automatically offered as a matching login. Do not use a real credential or
   choose it through a manual password search.
6. Check native authentication when viewing/filling credentials, Incognito
   behavior (no provider requests, filling or save prompt), cold-start retention, and unchanged
   MicroG sign-in/bookmarks. The user completes any authentication themselves.
7. Remove only the disposable fixture entry after testing. If the experiment
   fails, restore Chrome's original autofill setting and the preceding APK.

Native password viewing from Chrome's settings button is a separate integration
check. v45 offers Android password settings through the public
`android.settings.CREDENTIAL_PROVIDER` intent with Google as its package URI,
plus the existing website fallback. Android Settings must open the provider's
protected manager under its own authority. This does not impersonate stock
Chrome or bypass Google's authentication.

Source references, pinned to the target version:

- [AutofillClientProviderUtils](https://github.com/chromium/chromium/blob/153.0.8010.53/chrome/browser/autofill/android/java/src/org/chromium/chrome/browser/autofill/AutofillClientProviderUtils.java)
- [TabImpl provider and content-view lifecycle](https://github.com/chromium/chromium/blob/153.0.8010.53/chrome/android/java/src/org/chromium/chrome/browser/tab/TabImpl.java)
- [Profile.isOffTheRecord](https://github.com/chromium/chromium/blob/153.0.8010.53/chrome/browser/profiles/android/java/src/org/chromium/chrome/browser/profiles/Profile.java)
