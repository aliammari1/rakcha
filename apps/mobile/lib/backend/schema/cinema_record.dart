import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class CinemaRecord extends FirestoreRecord {
  CinemaRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "nom" field.
  String? _nom;
  String get nom => _nom ?? '';
  bool hasNom() => _nom != null;

  // "address" field.
  String? _address;
  String get address => _address ?? '';
  bool hasAddress() => _address != null;

  // "image_url" field.
  String? _imageUrl;
  String get imageUrl => _imageUrl ?? '';
  bool hasImageUrl() => _imageUrl != null;

  // "responsable" field.
  String? _responsable;
  String get responsable => _responsable ?? '';
  bool hasResponsable() => _responsable != null;

  // "statut" field.
  String? _statut;
  String get statut => _statut ?? '';
  bool hasStatut() => _statut != null;

  // "description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  void _initializeFields() {
    _nom = snapshotData['nom'] as String?;
    _address = snapshotData['address'] as String?;
    _imageUrl = snapshotData['image_url'] as String?;
    _responsable = snapshotData['responsable'] as String?;
    _statut = snapshotData['statut'] as String?;
    _description = snapshotData['description'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('cinema');

  static Stream<CinemaRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => CinemaRecord.fromSnapshot(s));

  static Future<CinemaRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => CinemaRecord.fromSnapshot(s));

  static CinemaRecord fromSnapshot(DocumentSnapshot snapshot) => CinemaRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static CinemaRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      CinemaRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'CinemaRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is CinemaRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createCinemaRecordData({
  String? nom,
  String? address,
  String? imageUrl,
  String? responsable,
  String? statut,
  String? description,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'nom': nom,
      'address': address,
      'image_url': imageUrl,
      'responsable': responsable,
      'statut': statut,
      'description': description,
    }.withoutNulls,
  );

  return firestoreData;
}

class CinemaRecordDocumentEquality implements Equality<CinemaRecord> {
  const CinemaRecordDocumentEquality();

  @override
  bool equals(CinemaRecord? e1, CinemaRecord? e2) {
    return e1?.nom == e2?.nom &&
        e1?.address == e2?.address &&
        e1?.imageUrl == e2?.imageUrl &&
        e1?.responsable == e2?.responsable &&
        e1?.statut == e2?.statut &&
        e1?.description == e2?.description;
  }

  @override
  int hash(CinemaRecord? e) => const ListEquality().hash([
        e?.nom,
        e?.address,
        e?.imageUrl,
        e?.responsable,
        e?.statut,
        e?.description
      ]);

  @override
  bool isValidKey(Object? o) => o is CinemaRecord;
}
