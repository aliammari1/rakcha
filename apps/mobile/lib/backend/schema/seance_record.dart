import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';
import '/backend/schema/util/schema_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class SeanceRecord extends FirestoreRecord {
  SeanceRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "cinema" field.
  List<String>? _cinema;
  List<String> get cinema => _cinema ?? const [];
  bool hasCinema() => _cinema != null;

  // "salle" field.
  List<String>? _salle;
  List<String> get salle => _salle ?? const [];
  bool hasSalle() => _salle != null;

  // "HD" field.
  String? _hd;
  String get hd => _hd ?? '';
  bool hasHd() => _hd != null;

  // "HF" field.
  String? _hf;
  String get hf => _hf ?? '';
  bool hasHf() => _hf != null;

  // "date" field.
  String? _date;
  String get date => _date ?? '';
  bool hasDate() => _date != null;

  // "prix" field.
  double? _prix;
  double get prix => _prix ?? 0.0;
  bool hasPrix() => _prix != null;

  void _initializeFields() {
    _cinema = getDataList(snapshotData['cinema']);
    _salle = getDataList(snapshotData['salle']);
    _hd = snapshotData['HD'] as String?;
    _hf = snapshotData['HF'] as String?;
    _date = snapshotData['date'] as String?;
    _prix = castToType<double>(snapshotData['prix']);
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('Seance');

  static Stream<SeanceRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => SeanceRecord.fromSnapshot(s));

  static Future<SeanceRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => SeanceRecord.fromSnapshot(s));

  static SeanceRecord fromSnapshot(DocumentSnapshot snapshot) => SeanceRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static SeanceRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      SeanceRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'SeanceRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is SeanceRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createSeanceRecordData({
  String? hd,
  String? hf,
  String? date,
  double? prix,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'HD': hd,
      'HF': hf,
      'date': date,
      'prix': prix,
    }.withoutNulls,
  );

  return firestoreData;
}

class SeanceRecordDocumentEquality implements Equality<SeanceRecord> {
  const SeanceRecordDocumentEquality();

  @override
  bool equals(SeanceRecord? e1, SeanceRecord? e2) {
    const listEquality = ListEquality();
    return listEquality.equals(e1?.cinema, e2?.cinema) &&
        listEquality.equals(e1?.salle, e2?.salle) &&
        e1?.hd == e2?.hd &&
        e1?.hf == e2?.hf &&
        e1?.date == e2?.date &&
        e1?.prix == e2?.prix;
  }

  @override
  int hash(SeanceRecord? e) => const ListEquality()
      .hash([e?.cinema, e?.salle, e?.hd, e?.hf, e?.date, e?.prix]);

  @override
  bool isValidKey(Object? o) => o is SeanceRecord;
}
