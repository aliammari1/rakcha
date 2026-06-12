import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';

// Firebase web/mobile API keys are publishable identifiers, but we still
// externalize them so the value lives in build config rather than source.
// Provide it at build/run time with:
//   flutter run --dart-define=FIREBASE_API_KEY=...
// The documented fallback value lives in apps/mobile/.env.example (not here).
const String _kFirebaseApiKey = String.fromEnvironment('FIREBASE_API_KEY');

Future initFirebase() async {
  if (kIsWeb) {
    await Firebase.initializeApp(
        options: FirebaseOptions(
            apiKey: _kFirebaseApiKey,
            authDomain: "rakcha-hn94a9.firebaseapp.com",
            projectId: "rakcha-hn94a9",
            storageBucket: "rakcha-hn94a9.appspot.com",
            messagingSenderId: "916850713175",
            appId: "1:916850713175:web:38d13b4741622a3d743912"));
  } else {
    await Firebase.initializeApp();
  }
}
