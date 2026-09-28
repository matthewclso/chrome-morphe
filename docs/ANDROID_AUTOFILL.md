# Android autofill prototype

This is an unaccepted device experiment, not part of the public 0.2.0 release.
It keeps MicroG sign-in while allowing Google's stock Android autofill service
to handle website forms. Chrome 153 normally rejects that provider in its
platform-autofill mode; the optional **Android autofill** patch removes only
that exclusion in availability checking and provider preference persistence.
Native policy checks, service availability, explicit user selection, origin
information and provider authentication remain unchanged.

## Local validation

- The prototype builds and patches Chrome 153.0.8010.53 (801005304, ARM64).
- The resulting APK passes signature verification with the existing development
  key and 4 KB alignment checks.
- Disassembly confirms only the two Google-specific comparison results changed
  in `AutofillClientProviderUtils`; the remaining method instructions match the
  original APK.
- The loopback fixture accepts only its public dummy credential and never logs
  or persists submitted fields. Windows can reach the WSL fixture over localhost.
- Actual Google autofill, saving, website matching and Incognito behavior are
  **not yet verified**. Device testing is waiting for USB debugging.

## Device acceptance

1. Install the prototype as an update, with **Chrome customization**, **MicroG
   sign-in** and **Android autofill** selected. Retain the existing signing key.
2. Keep Google selected as Android's autofill provider. In Chrome's autofill
   settings, choose **Autofill using another service** and confirm the restart.
3. Start `python3 tests/autofill_server.py` and run
   `adb reverse tcp:8766 tcp:8766`. In an explicit regular tab, open
   `http://localhost:8766/login` and submit the fixture's dummy credential.
4. Check whether Google offers to save it. Reopen the form and test actual
   filling and successful submission. The expected result is
   **Expected dummy login matched: yes**.
5. Open `http://127.0.0.1:8766/login` and verify the localhost credential is not
   automatically offered as a matching login. Do not use a real credential or
   choose it through a manual password search.
6. Check native authentication when viewing/filling credentials, Incognito
   behavior (including no save prompt), cold-start retention, and unchanged
   MicroG sign-in/bookmarks. The user completes any authentication themselves.
7. Remove only the disposable fixture entry after testing. If the experiment
   fails, restore Chrome's original autofill setting and the preceding APK.

Native password viewing from Chrome's settings button is a separate integration
check. The existing 0.2.0 website fallback remains in this prototype.
