import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import 'auth.dart';
import 'client.dart';
import 'core.dart';
import 'staff.dart';

/// Route-aware home. The legacy FlutterFlow shell showed all five tabs to
/// every account; this maps each stored role to its own workspace.
class RoleHome extends StatelessWidget {
  const RoleHome({super.key, required this.uid});

  final String uid;

  @override
  Widget build(BuildContext context) => _RoleHome(uid: uid);
}

Widget roleAwareHome() => const _RoleHome();

class _RoleHome extends StatelessWidget {
  const _RoleHome({this.uid});

  final String? uid;

  @override
  Widget build(BuildContext context) {
    final effectiveUid = uid ?? FirebaseAuth.instance.currentUser?.uid;
    if (effectiveUid == null) return const AuthScreen();

    return StreamBuilder<DocumentSnapshot>(
      stream: FirebaseFirestore.instance
          .collection('users')
          .doc(effectiveUid)
          .snapshots(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator(color: red)),
          );
        }
        if (snapshot.hasError) {
          return Scaffold(
            body: Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(mainAxisSize: MainAxisSize.min, children: [
                  const Icon(Icons.cloud_off, size: 48, color: muted),
                  const SizedBox(height: 14),
                  kText('Could not load your account.',
                      size: 16, weight: FontWeight.w800),
                  const SizedBox(height: 12),
                  kPrimaryButton(
                      'TRY AGAIN', () => FirebaseAuth.instance.signOut()),
                ]),
              ),
            ),
          );
        }
        final role = snapshot.data?.data() is Map
            ? (snapshot.data!.data()! as Map)['role']
            : null;
        switch (role) {
          case 'manager':
            return const ManagerHome();
          case 'admin':
            return const AdminHome();
          default:
            return const ClientHome();
        }
      },
    );
  }
}
