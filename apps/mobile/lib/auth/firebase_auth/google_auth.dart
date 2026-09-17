import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/foundation.dart';
import 'package:google_sign_in/google_sign_in.dart';

final GoogleSignIn _googleSignIn = GoogleSignIn.instance;
bool _googleSignInInitialized = false;

Future<void> _ensureGoogleSignInInitialized() async {
  if (_googleSignInInitialized) return;
  await _googleSignIn.initialize();
  _googleSignInInitialized = true;
}

Future<UserCredential?> googleSignInFunc() async {
  if (kIsWeb) {
    // Once signed in, return the UserCredential
    return await FirebaseAuth.instance.signInWithPopup(GoogleAuthProvider());
  }

  await _ensureGoogleSignInInitialized();

  GoogleSignInAccount googleUser;
  try {
    // authenticate() replaces signIn() — but it THROWS on cancel/failure
    // instead of returning null, so cancellation has to be caught explicitly.
    googleUser = await _googleSignIn.authenticate();
  } on GoogleSignInException catch (e) {
    if (e.code == GoogleSignInExceptionCode.canceled) {
      return null;
    }
    rethrow;
  }

  // .authentication is synchronous now and only carries the idToken.
  // An access token is a separate, explicit authorization step in v7.
  final idToken = googleUser.authentication.idToken;
  final authorization = await googleUser.authorizationClient
      .authorizeScopes(['email', 'profile']);

  final credential = GoogleAuthProvider.credential(
    idToken: idToken,
    accessToken: authorization.accessToken,
  );
  return FirebaseAuth.instance.signInWithCredential(credential);
}

Future<void> signOutWithGoogle() => _googleSignIn.signOut();
