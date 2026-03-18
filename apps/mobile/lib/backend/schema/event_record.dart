import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class EventRecord extends FirestoreRecord {
  EventRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "Name" field.
  String? _name;
  String get name => _name ?? '';
  bool hasName() => _name != null;

  // "StartDate" field.
  DateTime? _startDate;
  DateTime? get startDate => _startDate;
  bool hasStartDate() => _startDate != null;

  // "EndDate" field.
  DateTime? _endDate;
  DateTime? get endDate => _endDate;
  bool hasEndDate() => _endDate != null;

  // "Category" field.
  String? _category;
  String get category => _category ?? '';
  bool hasCategory() => _category != null;

  // "Location" field.
  String? _location;
  String get location => _location ?? '';
  bool hasLocation() => _location != null;

  // "Status" field.
  String? _status;
  String get status => _status ?? '';
  bool hasStatus() => _status != null;

  // "Description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  // "Poster" field.
  String? _poster;
  String get poster => _poster ?? '';
  bool hasPoster() => _poster != null;

  void _initializeFields() {
    _name = snapshotData['Name'] as String?;
    _startDate = snapshotData['StartDate'] as DateTime?;
    _endDate = snapshotData['EndDate'] as DateTime?;
    _category = snapshotData['Category'] as String?;
    _location = snapshotData['Location'] as String?;
    _status = snapshotData['Status'] as String?;
    _description = snapshotData['Description'] as String?;
    _poster = snapshotData['Poster'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('Event');

  static Stream<EventRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => EventRecord.fromSnapshot(s));

  static Future<EventRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => EventRecord.fromSnapshot(s));

  static EventRecord fromSnapshot(DocumentSnapshot snapshot) => EventRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static EventRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      EventRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'EventRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is EventRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createEventRecordData({
  String? name,
  DateTime? startDate,
  DateTime? endDate,
  String? category,
  String? location,
  String? status,
  String? description,
  String? poster,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'Name': name,
      'StartDate': startDate,
      'EndDate': endDate,
      'Category': category,
      'Location': location,
      'Status': status,
      'Description': description,
      'Poster': poster,
    }.withoutNulls,
  );

  return firestoreData;
}

class EventRecordDocumentEquality implements Equality<EventRecord> {
  const EventRecordDocumentEquality();

  @override
  bool equals(EventRecord? e1, EventRecord? e2) {
    return e1?.name == e2?.name &&
        e1?.startDate == e2?.startDate &&
        e1?.endDate == e2?.endDate &&
        e1?.category == e2?.category &&
        e1?.location == e2?.location &&
        e1?.status == e2?.status &&
        e1?.description == e2?.description &&
        e1?.poster == e2?.poster;
  }

  @override
  int hash(EventRecord? e) => const ListEquality().hash([
        e?.name,
        e?.startDate,
        e?.endDate,
        e?.category,
        e?.location,
        e?.status,
        e?.description,
        e?.poster
      ]);

  @override
  bool isValidKey(Object? o) => o is EventRecord;
}
