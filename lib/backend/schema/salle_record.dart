import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';
import '/backend/schema/util/schema_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class SalleRecord extends FirestoreRecord {
  SalleRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "nom" field.
  String? _nom;
  String get nom => _nom ?? '';
  bool hasNom() => _nom != null;

  // "nbr_places" field.
  int? _nbrPlaces;
  int get nbrPlaces => _nbrPlaces ?? 0;
  bool hasNbrPlaces() => _nbrPlaces != null;

  // "cinema" field.
  List<String>? _cinema;
  List<String> get cinema => _cinema ?? const [];
  bool hasCinema() => _cinema != null;

  void _initializeFields() {
    _nom = snapshotData['nom'] as String?;
    _nbrPlaces = castToType<int>(snapshotData['nbr_places']);
    _cinema = getDataList(snapshotData['cinema']);
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('salle');

  static Stream<SalleRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => SalleRecord.fromSnapshot(s));

  static Future<SalleRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => SalleRecord.fromSnapshot(s));

  static SalleRecord fromSnapshot(DocumentSnapshot snapshot) => SalleRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static SalleRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      SalleRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'SalleRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is SalleRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createSalleRecordData({
  String? nom,
  int? nbrPlaces,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'nom': nom,
      'nbr_places': nbrPlaces,
    }.withoutNulls,
  );

  return firestoreData;
}

class SalleRecordDocumentEquality implements Equality<SalleRecord> {
  const SalleRecordDocumentEquality();

  @override
  bool equals(SalleRecord? e1, SalleRecord? e2) {
    const listEquality = ListEquality();
    return e1?.nom == e2?.nom &&
        e1?.nbrPlaces == e2?.nbrPlaces &&
        listEquality.equals(e1?.cinema, e2?.cinema);
  }

  @override
  int hash(SalleRecord? e) =>
      const ListEquality().hash([e?.nom, e?.nbrPlaces, e?.cinema]);

  @override
  bool isValidKey(Object? o) => o is SalleRecord;
}
