import 'package:firebase_core/firebase_core.dart';

/// Local-only Firebase options used when `USE_FIREBASE_EMULATOR=true`.
///
/// The project ID is reserved by the Firebase CLI, so no production data can
/// be reached with these identifiers. Production builds use the native
/// `google-services.json` / `GoogleService-Info.plist` configuration instead.
class EmulatorFirebaseOptions {
  static const projectId = 'demo-rakcha';
  static const storageBucket = 'demo-rakcha.appspot.com';
  static const apiKey = 'AIzaSyDemoRakchaEmulatorKey000000000000000';
  static const appId = '1:123456789012:android:0a1b2c3d4e5f6071';
  static const messagingSenderId = '123456789012';

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: apiKey,
    appId: appId,
    messagingSenderId: messagingSenderId,
    projectId: projectId,
    storageBucket: storageBucket,
  );

  static const FirebaseOptions currentPlatform = android;
}
