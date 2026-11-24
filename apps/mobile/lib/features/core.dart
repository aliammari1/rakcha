import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

// ---------------------------------------------------------------------------
// Theme
// ---------------------------------------------------------------------------

const bg = Color(0xFF0B0709);
const surface = Color(0xFF171015);
const surface2 = Color(0xFF24161C);
const line = Color(0xFF3A252C);
const red = Color(0xFFE11D36);
const redHot = Color(0xFFFF5A73);
const gold = Color(0xFFF5B94A);
const muted = Color(0xFFB9A6AC);

/// The single source of truth for Rakcha's visual language.
final ThemeData appTheme = ThemeData(
  useMaterial3: true,
  brightness: Brightness.dark,
  scaffoldBackgroundColor: bg,
  colorScheme: ColorScheme.fromSeed(
    seedColor: red,
    brightness: Brightness.dark,
    surface: surface,
  ).copyWith(primary: redHot, secondary: gold, onPrimary: bg),
  appBarTheme: const AppBarTheme(
    backgroundColor: bg,
    foregroundColor: Colors.white,
    elevation: 0,
    centerTitle: false,
  ),
  cardTheme: CardThemeData(
    color: surface2,
    elevation: 0,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(16),
      side: const BorderSide(color: line),
    ),
  ),
  navigationBarTheme: NavigationBarThemeData(
    backgroundColor: surface,
    indicatorColor: red,
    labelTextStyle: WidgetStateProperty.all(
      const TextStyle(fontSize: 11, fontWeight: FontWeight.w700),
    ),
  ),
  inputDecorationTheme: InputDecorationTheme(
    filled: true,
    fillColor: surface2,
    border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: line)),
    enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: line)),
    focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: red)),
    labelStyle: const TextStyle(color: muted),
    hintStyle: const TextStyle(color: Color(0xFF6E5A62)),
  ),
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: red,
      foregroundColor: Colors.white,
      minimumSize: const Size.fromHeight(48),
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      textStyle: const TextStyle(fontSize: 12, fontWeight: FontWeight.w800),
    ),
  ),
);

// ---------------------------------------------------------------------------
// Data models
// ---------------------------------------------------------------------------

class FilmData {
  final String id, name, description, year, image, trailerId, imdbId;
  final int price;
  final List<String> categories, actors;
  final double rating;

  FilmData(
    this.id,
    this.name,
    this.description,
    this.year,
    this.image,
    this.price,
    this.categories,
    this.actors,
    this.rating, {
    this.trailerId = '',
    this.imdbId = '',
  });

  static FilmData from(String id, Map<String, dynamic> d) => FilmData(
        id,
        d['name'] ?? '',
        d['description'] ?? '',
        switch (d['year']) {
          Timestamp year => '${year.toDate().year}',
          String year => year,
          _ => '',
        },
        d['image'] ?? '',
        (d['prix'] is num) ? (d['prix'] as num).toInt() : 0,
        List<String>.from(d['category'] ?? const []),
        List<String>.from(d['actors'] ?? const []),
        (d['rating'] is num) ? (d['rating'] as num).toDouble() : 0,
        trailerId: d['trailerId'] ?? '',
        imdbId: d['imdbId'] ?? '',
      );
}

class CinemaData {
  final String id, name, address, status, image, description, managerId;

  CinemaData(this.id, this.name, this.address, this.status, this.image,
      this.description, this.managerId);

  static CinemaData from(String id, Map<String, dynamic> d) => CinemaData(
        id,
        d['nom'] ?? '',
        d['address'] ?? '',
        d['statut'] ?? 'Pending',
        d['image_url'] ?? '',
        d['description'] ?? '',
        d['responsable'] ?? '',
      );
}

class ProductData {
  final String id, name, description, image, category;
  final int price;

  ProductData(this.id, this.name, this.description, this.image, this.price,
      this.category);

  static ProductData from(String id, Map<String, dynamic> d) => ProductData(
        id,
        d['nom'] ?? '',
        d['description'] ?? '',
        d['image'] ?? '',
        (d['prix'] is num) ? (d['prix'] as num).toInt() : 0,
        d['category'] ?? 'Collectible',
      );
}

class SeriesData {
  final String id, name, summary, director, country, image;

  SeriesData(this.id, this.name, this.summary, this.director, this.country,
      this.image);

  static SeriesData from(String id, Map<String, dynamic> d) => SeriesData(
        id,
        d['name'] ?? '',
        d['summary'] ?? '',
        d['director'] ?? '',
        d['country'] ?? '',
        d['pictureserie'] ?? '',
      );
}

class ShowtimeData {
  final String id, filmId, cinemaId, room, hd, hf, date;
  final double price;

  ShowtimeData(this.id, this.filmId, this.cinemaId, this.room, this.hd, this.hf,
      this.date, this.price);

  static ShowtimeData from(String id, Map<String, dynamic> d) {
    final cinemas = List<String>.from(d['cinema'] ?? const []);
    final rooms = List<String>.from(d['salle'] ?? const []);
    return ShowtimeData(
      id,
      d['filmId'] ?? '',
      cinemas.isNotEmpty ? cinemas.first.split('/').last : '',
      rooms.isNotEmpty ? rooms.first : 'Room 1',
      d['HD'] ?? '',
      d['HF'] ?? '',
      d['date'] ?? '',
      (d['prix'] is num) ? (d['prix'] as num).toDouble() : 0,
    );
  }
}

// ---------------------------------------------------------------------------
// Firestore streams and writes
// ---------------------------------------------------------------------------

final _db = FirebaseFirestore.instance;

Stream<List<FilmData>> watchFilms() => _db.collection('Film').snapshots().map(
      (snap) => snap.docs.map((d) => FilmData.from(d.id, d.data())).toList(),
    );

Stream<List<CinemaData>> watchCinemas() =>
    _db.collection('cinema').snapshots().map(
          (snap) =>
              snap.docs.map((d) => CinemaData.from(d.id, d.data())).toList(),
        );

Stream<List<ProductData>> watchProducts() =>
    _db.collection('produit').snapshots().map(
          (snap) =>
              snap.docs.map((d) => ProductData.from(d.id, d.data())).toList(),
        );

Stream<List<SeriesData>> watchSeries() =>
    _db.collection('serie').snapshots().map(
          (snap) =>
              snap.docs.map((d) => SeriesData.from(d.id, d.data())).toList(),
        );

Stream<List<ShowtimeData>> watchShowtimes() =>
    _db.collection('Seance').snapshots().map(
          (snap) =>
              snap.docs.map((d) => ShowtimeData.from(d.id, d.data())).toList(),
        );

Stream<List<Map<String, dynamic>>> watchReservations(String uid) => _db
    .collection('Reservation')
    .where('user_ref', isEqualTo: _db.collection('users').doc(uid))
    .snapshots()
    .map(
      (snap) => snap.docs.map((d) => {'id': d.id, ...d.data()}).toList(),
    );

DocumentReference<Map<String, dynamic>> _rating(String uid, String filmId) =>
    _db.collection('users').doc(uid).collection('filmRatings').doc(filmId);

Future<void> rateFilm(String uid, String filmId, int value) =>
    _rating(uid, filmId).set({'stars': value});

Stream<int> watchRating(String uid, String filmId) => _rating(uid, filmId)
    .snapshots()
    .map((doc) => doc.data()?['stars'] as int? ?? 0);

Future<void> toggleBookmark(String uid, String filmId, bool active) async {
  final existing = await _db
      .collection('Favoris')
      .where('user_ref', isEqualTo: _db.collection('users').doc(uid))
      .where('filmId', isEqualTo: filmId)
      .get();
  if (active) {
    if (existing.docs.isEmpty) {
      await _db.collection('Favoris').add({
        'user_ref': _db.collection('users').doc(uid),
        'filmId': filmId,
      });
    }
  } else {
    for (final doc in existing.docs) await doc.reference.delete();
  }
}

Stream<bool> isBookmarked(String uid, String filmId) => _db
    .collection('Favoris')
    .where('user_ref', isEqualTo: _db.collection('users').doc(uid))
    .where('filmId', isEqualTo: filmId)
    .snapshots()
    .map((s) => s.docs.isNotEmpty);

Future<void> createBooking({
  required String uid,
  required String filmId,
  required String filmName,
  required String showtimeId,
  required List<String> seats,
  required double total,
}) =>
    _db.collection('Reservation').add({
      'user_ref': _db.collection('users').doc(uid),
      'filmId': filmId,
      'filmName': filmName,
      'seanceId': showtimeId,
      'seats': seats,
      'total': total,
      'created': FieldValue.serverTimestamp(),
    });

Future<void> createShowtime({
  required String cinemaId,
  required String filmId,
  required String room,
  required String hd,
  required String hf,
  required String date,
  required double price,
}) =>
    _db.collection('Seance').add({
      'cinema': ['cinema/$cinemaId'],
      'cinemaId': cinemaId,
      'salle': [room],
      'filmId': filmId,
      'HD': hd,
      'HF': hf,
      'date': date,
      'prix': price,
    });

Future<void> setCinemaStatus(String cinemaId, String status) =>
    _db.collection('cinema').doc(cinemaId).update({'statut': status});

// Cart items live under the signed-in user so each account has its own basket.
DocumentReference<Map<String, dynamic>> _cart(String uid, String productId) =>
    _db.collection('users').doc(uid).collection('cart').doc(productId);

Stream<List<Map<String, dynamic>>> watchCart(String uid) =>
    _db.collection('users').doc(uid).collection('cart').snapshots().map(
          (snap) => snap.docs.map((d) => {'id': d.id, ...d.data()}).toList(),
        );

Future<void> addToCart(String uid, ProductData product, {int quantity = 1}) =>
    _cart(uid, product.id).set(
      {
        'productId': product.id,
        'name': product.name,
        'image': product.image,
        'price': product.price,
        'quantity': FieldValue.increment(quantity),
      },
      SetOptions(merge: true),
    );

Future<void> setCartQuantity(String uid, String productId, int quantity) =>
    _cart(uid, productId).update({'quantity': quantity});

Future<void> removeFromCart(String uid, String productId) =>
    _cart(uid, productId).delete();

// ---------------------------------------------------------------------------
// Shared widgets
// ---------------------------------------------------------------------------

Widget kText(String text,
        {double size = 14,
        Color color = Colors.white,
        FontWeight weight = FontWeight.normal,
        int? maxLines,
        TextAlign textAlign = TextAlign.start}) =>
    Text(text,
        maxLines: maxLines,
        textAlign: textAlign,
        overflow: maxLines == null ? null : TextOverflow.ellipsis,
        style: TextStyle(fontSize: size, color: color, fontWeight: weight));

Widget kEyebrow(String text) =>
    kText(text.toUpperCase(), size: 10, color: redHot, weight: FontWeight.w800);

Widget kButton(String label, VoidCallback? onPressed,
        {Color color = red, IconData? icon}) =>
    ElevatedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon ?? Icons.arrow_forward, size: 16),
      label: kText(label, size: 12, weight: FontWeight.w800),
      style: ElevatedButton.styleFrom(
        backgroundColor: color,
        foregroundColor: Colors.white,
        minimumSize: const Size(48, 48),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      ),
    );

/// Full-width primary action for forms, sheets and bottom bars.
Widget kPrimaryButton(String label, VoidCallback? onPressed,
        {IconData? icon}) =>
    SizedBox(
      width: double.infinity,
      child: kButton(label, onPressed, icon: icon),
    );

/// All bottom sheets use the same full-width, scrollable presentation.
Future<T?> showRakchaSheet<T>({
  required BuildContext context,
  required WidgetBuilder builder,
}) =>
    showModalBottomSheet<T>(
      context: context,
      backgroundColor: surface,
      isScrollControlled: true,
      useSafeArea: true,
      showDragHandle: true,
      constraints: const BoxConstraints(maxWidth: double.infinity),
      builder: builder,
    );

Widget kOutlineButton(String label, VoidCallback onPressed, {IconData? icon}) =>
    OutlinedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon ?? Icons.info_outline, size: 16),
      label: kText(label, weight: FontWeight.w700),
      style: OutlinedButton.styleFrom(
        foregroundColor: Colors.white,
        side: const BorderSide(color: line),
        minimumSize: const Size(48, 44),
      ),
    );

Widget kPoster(FilmData film,
        {double height = 190, double width = 128, bool showInfo = true}) =>
    SizedBox(
      width: width,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(12),
            child: Image.asset(film.image,
                height: height,
                width: width,
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) => Container(
                    height: height,
                    width: width,
                    color: surface2,
                    child: const Icon(Icons.movie, color: muted))),
          ),
          if (showInfo) ...[
            const SizedBox(height: 8),
            kText(film.name, size: 12, weight: FontWeight.w700, maxLines: 1),
            const SizedBox(height: 3),
            Row(children: [
              const Icon(Icons.star, size: 12, color: gold),
              const SizedBox(width: 3),
              kText(
                  film.rating > 0
                      ? film.rating.toStringAsFixed(1)
                      : 'Not rated yet',
                  size: 11,
                  color: muted),
            ]),
          ],
        ],
      ),
    );

Widget kSectionHead(String eyebrow, String title, {Widget? trailing}) => Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          kEyebrow(eyebrow),
          const SizedBox(height: 4),
          kText(title, size: 20, weight: FontWeight.w800),
        ]),
        if (trailing != null) trailing,
      ],
    );

Widget kKpi(String value, String label, IconData icon) => Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
          color: surface,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(color: line)),
      child: Column(children: [
        Icon(icon, color: redHot, size: 20),
        const SizedBox(height: 8),
        kText(value, size: 22, weight: FontWeight.w800),
        const SizedBox(height: 2),
        kText(label, size: 9, color: muted),
      ]),
    );
