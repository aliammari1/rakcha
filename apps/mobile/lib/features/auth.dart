import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

import 'core.dart';

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final _email = TextEditingController();
  final _password = TextEditingController();
  final _name = TextEditingController();
  bool _registering = false;
  bool _busy = false;
  String? _error;

  Future<void> _submit() async {
    if (_busy) return;
    FocusScope.of(context).unfocus();
    if (_email.text.trim().isEmpty ||
        _password.text.isEmpty ||
        (_registering && _name.text.trim().isEmpty)) {
      setState(() => _error = 'Please fill in all fields.');
      return;
    }
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      if (_registering) {
        final cred = await FirebaseAuth.instance.createUserWithEmailAndPassword(
          email: _email.text.trim(),
          password: _password.text,
        );
        await cred.user!.updateDisplayName(_name.text.trim());
        await FirebaseFirestore.instance
            .collection('users')
            .doc(cred.user!.uid)
            .set({
          'email': _email.text.trim(),
          'display_name': _name.text.trim(),
          'uid': cred.user!.uid,
          'role': 'client',
        });
      } else {
        await FirebaseAuth.instance.signInWithEmailAndPassword(
          email: _email.text.trim(),
          password: _password.text,
        );
      }
    } on FirebaseException catch (e) {
      if (mounted) setState(() => _error = e.message ?? 'Could not sign in');
    } catch (_) {
      if (mounted) {
        setState(() => _error = 'Something went wrong. Please try again.');
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  void dispose() {
    _email.dispose();
    _password.dispose();
    _name.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(24, 32, 24, 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(children: [
                Container(
                  width: 44,
                  height: 44,
                  decoration: BoxDecoration(
                      color: red, borderRadius: BorderRadius.circular(12)),
                  child: const Icon(Icons.local_fire_department,
                      color: Colors.white),
                ),
                const SizedBox(width: 12),
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  kText('RAKCHA', size: 22, weight: FontWeight.w900),
                  kText('YOUR CINEMA UNIVERSE',
                      size: 9, color: muted, weight: FontWeight.w700),
                ]),
              ]),
              const SizedBox(height: 40),
              ClipRRect(
                borderRadius: BorderRadius.circular(18),
                child: Image.asset('assets/images/avengers.jpg',
                    height: 190,
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => const SizedBox(height: 190)),
              ),
              const SizedBox(height: 30),
              kEyebrow(_registering ? 'Join Rakcha' : 'Welcome back'),
              const SizedBox(height: 8),
              kText(
                  _registering
                      ? 'Create your account'
                      : 'Enter the world of cinema',
                  size: 26,
                  weight: FontWeight.w900),
              const SizedBox(height: 24),
              if (_registering)
                TextField(
                    controller: _name,
                    decoration: const InputDecoration(labelText: 'Full name')),
              if (_registering) const SizedBox(height: 14),
              TextField(
                controller: _email,
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                    labelText: 'Email address',
                    prefixIcon: Icon(Icons.email_outlined)),
              ),
              const SizedBox(height: 14),
              TextField(
                controller: _password,
                obscureText: true,
                enableSuggestions: false,
                autocorrect: false,
                textInputAction: TextInputAction.done,
                onSubmitted: (_) => _submit(),
                decoration: const InputDecoration(
                    labelText: 'Password',
                    prefixIcon: Icon(Icons.lock_outline)),
              ),
              if (_error != null) ...[
                const SizedBox(height: 12),
                kText(_error!, size: 12, color: redHot),
              ],
              const SizedBox(height: 22),
              kPrimaryButton(
                  _busy
                      ? 'PLEASE WAIT…'
                      : _registering
                          ? 'CREATE ACCOUNT'
                          : 'SIGN IN',
                  _busy ? null : _submit),
              const SizedBox(height: 14),
              TextButton(
                onPressed: () => setState(() {
                  _registering = !_registering;
                  _error = null;
                }),
                child: kText(
                    _registering
                        ? 'Already have an account? Sign in'
                        : "Don't have an account? Create account",
                    size: 12,
                    color: redHot),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
