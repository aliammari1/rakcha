import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class ReviewRecord extends FirestoreRecord {
  ReviewRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  // "rating" field.
  int? _rating;
  int get rating => _rating ?? 0;
  bool hasRating() => _rating != null;

  // "nom" field.
  String? _nom;
  String get nom => _nom ?? '';
  bool hasNom() => _nom != null;

  // "prenom" field.
  String? _prenom;
  String get prenom => _prenom ?? '';
  bool hasPrenom() => _prenom != null;

  void _initializeFields() {
    _description = snapshotData['description'] as String?;
    _rating = castToType<int>(snapshotData['rating']);
    _nom = snapshotData['nom'] as String?;
    _prenom = snapshotData['prenom'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('review');

  static Stream<ReviewRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => ReviewRecord.fromSnapshot(s));

  static Future<ReviewRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => ReviewRecord.fromSnapshot(s));

  static ReviewRecord fromSnapshot(DocumentSnapshot snapshot) => ReviewRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static ReviewRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      ReviewRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'ReviewRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is ReviewRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createReviewRecordData({
  String? description,
  int? rating,
  String? nom,
  String? prenom,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'description': description,
      'rating': rating,
      'nom': nom,
      'prenom': prenom,
    }.withoutNulls,
  );

  return firestoreData;
}

class ReviewRecordDocumentEquality implements Equality<ReviewRecord> {
  const ReviewRecordDocumentEquality();

  @override
  bool equals(ReviewRecord? e1, ReviewRecord? e2) {
    return e1?.description == e2?.description &&
        e1?.rating == e2?.rating &&
        e1?.nom == e2?.nom &&
        e1?.prenom == e2?.prenom;
  }

  @override
  int hash(ReviewRecord? e) =>
      const ListEquality().hash([e?.description, e?.rating, e?.nom, e?.prenom]);

  @override
  bool isValidKey(Object? o) => o is ReviewRecord;
}
