import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class FavorisRecord extends FirestoreRecord {
  FavorisRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "event" field.
  DocumentReference? _event;
  DocumentReference? get event => _event;
  bool hasEvent() => _event != null;

  void _initializeFields() {
    _event = snapshotData['event'] as DocumentReference?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('Favoris');

  static Stream<FavorisRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => FavorisRecord.fromSnapshot(s));

  static Future<FavorisRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => FavorisRecord.fromSnapshot(s));

  static FavorisRecord fromSnapshot(DocumentSnapshot snapshot) =>
      FavorisRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static FavorisRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      FavorisRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'FavorisRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is FavorisRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createFavorisRecordData({
  DocumentReference? event,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'event': event,
    }.withoutNulls,
  );

  return firestoreData;
}

class FavorisRecordDocumentEquality implements Equality<FavorisRecord> {
  const FavorisRecordDocumentEquality();

  @override
  bool equals(FavorisRecord? e1, FavorisRecord? e2) {
    return e1?.event == e2?.event;
  }

  @override
  int hash(FavorisRecord? e) => const ListEquality().hash([e?.event]);

  @override
  bool isValidKey(Object? o) => o is FavorisRecord;
}
