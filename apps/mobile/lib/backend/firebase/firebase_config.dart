import 'dart:io';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';

import '../../config/firebase_options.dart';

// Firebase web/mobile API keys are publishable identifiers, but we still
// externalize them so the value lives in build config rather than source.
// Provide it at build/run time with:
//   flutter run --dart-define=FIREBASE_API_KEY=...
// The documented fallback value lives in apps/mobile/.env.example (not here).
const String _kFirebaseApiKey = String.fromEnvironment('FIREBASE_API_KEY');

/// Set `--dart-define=USE_FIREBASE_EMULATOR=true` for a local walkthrough.
/// Without it the app connects to the configured Firebase project, so a normal
/// release build never silently points at a workstation emulator.
const bool useEmulator =
    bool.fromEnvironment('USE_FIREBASE_EMULATOR', defaultValue: false);

Future initFirebase() async {
  if (kIsWeb) {
    await Firebase.initializeApp(
      options: FirebaseOptions(
        apiKey: _kFirebaseApiKey,
        authDomain: 'rakcha-hn94a9.firebaseapp.com',
        projectId: 'rakcha-hn94a9',
        storageBucket: 'rakcha-hn94a9.appspot.com',
        messagingSenderId: '916850713175',
        appId: '1:916850713175:web:38d13b4741622a3d743912',
      ),
    );
  } else if (useEmulator) {
    await Firebase.initializeApp(
        options: EmulatorFirebaseOptions.currentPlatform);
    // adb reverse maps the device's 127.0.0.1 to the workstation.
    // automaticHostMapping would rewrite it to 10.0.2.2 on an emulator.
    FirebaseFirestore.instance.useFirestoreEmulator(
      '127.0.0.1',
      8080,
      automaticHostMapping: false,
    );
    await FirebaseAuth.instance.useAuthEmulator(
      '127.0.0.1',
      9099,
      automaticHostMapping: false,
    );
  } else if (Platform.isAndroid) {
    await Firebase.initializeApp();
  } else {
    await Firebase.initializeApp(
      options: EmulatorFirebaseOptions.currentPlatform,
    );
  }
}
