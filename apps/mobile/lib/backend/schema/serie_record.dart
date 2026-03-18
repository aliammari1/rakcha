import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class SerieRecord extends FirestoreRecord {
  SerieRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "name" field.
  String? _name;
  String get name => _name ?? '';
  bool hasName() => _name != null;

  // "summary" field.
  String? _summary;
  String get summary => _summary ?? '';
  bool hasSummary() => _summary != null;

  // "director" field.
  String? _director;
  String get director => _director ?? '';
  bool hasDirector() => _director != null;

  // "country" field.
  String? _country;
  String get country => _country ?? '';
  bool hasCountry() => _country != null;

  // "pictureserie" field.
  String? _pictureserie;
  String get pictureserie => _pictureserie ?? '';
  bool hasPictureserie() => _pictureserie != null;

  void _initializeFields() {
    _name = snapshotData['name'] as String?;
    _summary = snapshotData['summary'] as String?;
    _director = snapshotData['director'] as String?;
    _country = snapshotData['country'] as String?;
    _pictureserie = snapshotData['pictureserie'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('serie');

  static Stream<SerieRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => SerieRecord.fromSnapshot(s));

  static Future<SerieRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => SerieRecord.fromSnapshot(s));

  static SerieRecord fromSnapshot(DocumentSnapshot snapshot) => SerieRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static SerieRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      SerieRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'SerieRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is SerieRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createSerieRecordData({
  String? name,
  String? summary,
  String? director,
  String? country,
  String? pictureserie,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'name': name,
      'summary': summary,
      'director': director,
      'country': country,
      'pictureserie': pictureserie,
    }.withoutNulls,
  );

  return firestoreData;
}

class SerieRecordDocumentEquality implements Equality<SerieRecord> {
  const SerieRecordDocumentEquality();

  @override
  bool equals(SerieRecord? e1, SerieRecord? e2) {
    return e1?.name == e2?.name &&
        e1?.summary == e2?.summary &&
        e1?.director == e2?.director &&
        e1?.country == e2?.country &&
        e1?.pictureserie == e2?.pictureserie;
  }

  @override
  int hash(SerieRecord? e) => const ListEquality()
      .hash([e?.name, e?.summary, e?.director, e?.country, e?.pictureserie]);

  @override
  bool isValidKey(Object? o) => o is SerieRecord;
}
