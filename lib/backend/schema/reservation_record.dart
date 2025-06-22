import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class ReservationRecord extends FirestoreRecord {
  ReservationRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "nbplaces" field.
  int? _nbplaces;
  int get nbplaces => _nbplaces ?? 0;
  bool hasNbplaces() => _nbplaces != null;

  // "note" field.
  String? _note;
  String get note => _note ?? '';
  bool hasNote() => _note != null;

  // "event" field.
  DocumentReference? _event;
  DocumentReference? get event => _event;
  bool hasEvent() => _event != null;

  void _initializeFields() {
    _nbplaces = castToType<int>(snapshotData['nbplaces']);
    _note = snapshotData['note'] as String?;
    _event = snapshotData['event'] as DocumentReference?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('Reservation');

  static Stream<ReservationRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => ReservationRecord.fromSnapshot(s));

  static Future<ReservationRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => ReservationRecord.fromSnapshot(s));

  static ReservationRecord fromSnapshot(DocumentSnapshot snapshot) =>
      ReservationRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static ReservationRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      ReservationRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'ReservationRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is ReservationRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createReservationRecordData({
  int? nbplaces,
  String? note,
  DocumentReference? event,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'nbplaces': nbplaces,
      'note': note,
      'event': event,
    }.withoutNulls,
  );

  return firestoreData;
}

class ReservationRecordDocumentEquality implements Equality<ReservationRecord> {
  const ReservationRecordDocumentEquality();

  @override
  bool equals(ReservationRecord? e1, ReservationRecord? e2) {
    return e1?.nbplaces == e2?.nbplaces &&
        e1?.note == e2?.note &&
        e1?.event == e2?.event;
  }

  @override
  int hash(ReservationRecord? e) =>
      const ListEquality().hash([e?.nbplaces, e?.note, e?.event]);

  @override
  bool isValidKey(Object? o) => o is ReservationRecord;
}
