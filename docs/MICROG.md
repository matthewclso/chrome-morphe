# MicroG sign-in

The optional **MicroG sign-in** patch connects Chrome's account interface to
[Morphe MicroG](https://github.com/MorpheApp/MicroG-RE), package
`app.revanced.android.gms`, version **7.1.1 or newer**. It is separate from **Chrome customization** because
it changes the account provider. The phone can retain its ordinary Google Play
Services and stock Chrome installation.

After patching with this option, open **Settings → Morphe settings → Allow account
access**. Android groups account enumeration under the Contacts permission. Then
return to Chrome settings and choose **Sign in**. Accounts must be available in
MicroG; an account in ordinary Google Play Services is not automatically shared.
Chrome's **Add account** opens the MicroG authenticator. Complete login and consent
on the device. Do not remove an existing Google account to work around the
"already exists" message from an older build.

## Implementation boundary

- Account enumeration calls MicroG's `auth.accounts` provider with `get_accounts`
  and account type `app.revanced`. Chromium still receives its expected
  `com.google` account objects. Android authenticator operations are converted
  back to the MicroG type.
- Capability requests also use the MicroG account type because MicroG reads its
  account cache through Android AccountManager. Responses remain the provider's
  allowed/denied/unknown results; eligibility and policy are not overridden.
  Version 7.1.1 is enforced before account enumeration, token requests and
  capability queries. Version 6.1.1 returned true for every capability, including
  parental controls, and must never be used for Chrome sign-in.
  On the test device, 7.1.1 returned an unknown parental-control result; recovery
  from the earlier incorrect cached state is not yet accepted.
- Token requests explicitly bind MicroG's `com.google.android.gms.auth.GetToken`
  component. Chrome's original binder callbacks retain token parsing, expiration,
  invalidation, scopes, real Gaia account IDs, and recoverable consent errors.
- Service binding runs on Chrome's account worker threads, has a 15-second
  connection deadline, and unbinds on success, failure or interruption.
- `MigrateAccountManagerDelegate` stays disabled: MicroG implements the legacy
  account protocol, while its newer AANG account methods are unimplemented.
  Other Chrome feature flags retain their original behavior.
- A provider failure becomes the `RemoteException` Chrome already retries; it
  does not become an empty account list that could be mistaken for account removal.
- Chrome's Trusted Vault client binds MicroG's key-retrieval service directly.
  The original `chromesync` security domain, shared-key validation, recovery UI
  and disabled reset offer are preserved. MicroG 6.1.1 lacks this service and
  cannot complete the encrypted-data verification step.
- MicroG receives original Chrome package/certificate metadata through its
  documented patch integration mechanism. This does not change Android's APK
  signature or grant trust in stock Google Play Services.
- No global Google service rewrite, generated account IDs, token persistence,
  token logging, or changes to Incognito authentication are introduced.

This adapter was independently written for this repository. Protocol references:
[MicroG AccountContentProvider](https://github.com/MorpheApp/MicroG-RE/blob/main/play-services-core/src/main/java/org/microg/gms/auth/AccountContentProvider.java),
[MicroG AuthManagerServiceImpl](https://github.com/MorpheApp/MicroG-RE/blob/main/play-services-core/src/main/java/org/microg/gms/auth/AuthManagerServiceImpl.java),
[MicroG KeyRetrievalService](https://github.com/MorpheApp/MicroG-RE/blob/main/play-services-core/src/main/kotlin/org/microg/gms/auth/folsom/KeyRetrievalService.kt),
and the actual supported Chrome APK's account delegate and binder callbacks.
Upstream code is not bundled here.

## Acceptance

Account visibility, successful Chrome sign-in and actual Chrome Sync are separate
checks. The S26 user confirmed successful sign-in, encrypted-data verification
and existing bookmarks appearing with MicroG 7.1.1. Custom Sync passphrases,
supervised accounts, managed accounts and recovery on
another device still require their own acceptance; no policy/capability result
is fabricated to enable them. See [device testing](TESTING.md).

**Google Password Manager:** MicroG 7.1.1's own
[PasswordManagerActivity](https://github.com/MorpheApp/MicroG-RE/blob/7.1.1/play-services-core/src/main/kotlin/com/google/android/gms/credential/manager/PasswordManagerActivity.kt)
opens `passwords.google.com`; it does not supply the native Google Password Manager
interface. Chrome Morphe's button explains the limitation and offers that website
inside Chrome Morphe, honoring the Incognito-default setting. Native Google
password saving/autofill is unsupported. A separate website login may be required.

**Account-state regression:** after initial sign-in with MicroG 6.1.1, the test
S26 lost Incognito and homepage articles, and its Wallet account-data switch was
disabled. The 6.1.1 capability stub is confirmed in both source and APK bytecode.
Upgrading MicroG does not by itself clear Chrome's previously cached account
state. Recovery is still being tested; the sign-in patch is not released.
Wallet service support is not claimed. Native policies are not overridden.

When debugging, inspect only Chrome's process and redact account identifiers.
Do not collect MicroG logs: upstream authentication logging can include tokens.
