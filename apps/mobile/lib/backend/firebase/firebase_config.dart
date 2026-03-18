import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';

Future initFirebase() async {
  if (kIsWeb) {
    await Firebase.initializeApp(
        options: FirebaseOptions(
            apiKey: "AIzaSyD5K3yznoR8ytbujRi2K4TnRpkL8rQQmcE",
            authDomain: "rakcha-hn94a9.firebaseapp.com",
            projectId: "rakcha-hn94a9",
            storageBucket: "rakcha-hn94a9.appspot.com",
            messagingSenderId: "916850713175",
            appId: "1:916850713175:web:38d13b4741622a3d743912"));
  } else {
    await Firebase.initializeApp();
  }
}
