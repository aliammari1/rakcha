import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import 'auth.dart';
import 'core.dart';
import 'shell.dart';

/// Rebuilt whenever auth changes, so a signed-out screen never lingers after
/// sign-in and staff workspaces never linger after sign-out.
class AuthGate extends StatelessWidget {
  const AuthGate({super.key});

  @override
  Widget build(BuildContext context) => StreamBuilder<User?>(
        stream: FirebaseAuth.instance.authStateChanges(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }
          final user = snapshot.data;
          if (user == null) return const AuthScreen();
          return RoleHome(uid: user.uid);
        },
      );
}

class RakchaApp extends StatefulWidget {
  const RakchaApp({super.key, this.themeMode = ThemeMode.dark});

  final ThemeMode themeMode;

  @override
  State<RakchaApp> createState() => _RakchaAppState();
}

class _RakchaAppState extends State<RakchaApp> {
  String? _uid;

  @override
  void initState() {
    super.initState();
    FirebaseAuth.instance.authStateChanges().listen((user) {
      if (!mounted) return;
      setState(() => _uid = user?.uid);
    });
  }

  @override
  Widget build(BuildContext context) => MaterialApp(
        // Recreating the Navigator on auth change clears pushed routes such
        // as Profile, so sign-out always lands on the sign-in screen.
        key: ValueKey(_uid ?? 'signed-out'),
        title: 'Rakcha',
        debugShowCheckedModeBanner: false,
        theme: appTheme,
        themeMode: widget.themeMode,
        home: const AuthGate(),
      );
}
