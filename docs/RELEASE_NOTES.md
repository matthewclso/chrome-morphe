Chrome Morphe 0.2.0 for Chrome 153.0.8010.53 (801005304), ARM64.

- Add an optional **MicroG sign-in** patch. Select it alongside **Chrome customization** in Morphe Manager.
- Add account-access setup in Morphe settings. Android groups the account-list permission under Contacts.
- Route account listing, sign-in, token requests, Add account and credential repair through Morphe MicroG, preserving Chrome's real account IDs and consent handling.
- Route Trusted Vault verification through MicroG so the **Verify it's you** button can complete encrypted-data recovery. This requires **Morphe MicroG 7.1.1 or newer**; 6.1.1 lacks the required service.
- Send capability queries using the MicroG account type, preserving the provider's real eligibility results.
- Replace the nonworking Google Password Manager launch with an explanation and an option to open Google's password website in Chrome Morphe. Native Google password saving/autofill is unsupported.

On the Galaxy S26, the user confirmed successful sign-in, verification and existing bookmarks appearing. Custom Sync passphrases, managed/supervised accounts and recovery on another device remain unverified.
The Google Wallet account-data switch remains disabled on the test device; Wallet support is not claimed.

MicroG uses its own account provider and may require a separate login. Keep the same Manager signing key when updating Chrome Morphe. Existing UI/Incognito patches and exact version/build checks are retained. See the README and docs/MICROG.md for setup.

Device acceptance is limited to Galaxy S26 / Android 16 / 4 KB pages. Releases contain patches, not Chrome or MicroG APKs.
