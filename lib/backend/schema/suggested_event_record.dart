import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class SuggestedEventRecord extends FirestoreRecord {
  SuggestedEventRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "Name" field.
  String? _name;
  String get name => _name ?? '';
  bool hasName() => _name != null;

  // "Category" field.
  String? _category;
  String get category => _category ?? '';
  bool hasCategory() => _category != null;

  // "Description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  void _initializeFields() {
    _name = snapshotData['Name'] as String?;
    _category = snapshotData['Category'] as String?;
    _description = snapshotData['Description'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('SuggestedEvent');

  static Stream<SuggestedEventRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => SuggestedEventRecord.fromSnapshot(s));

  static Future<SuggestedEventRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => SuggestedEventRecord.fromSnapshot(s));

  static SuggestedEventRecord fromSnapshot(DocumentSnapshot snapshot) =>
      SuggestedEventRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static SuggestedEventRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      SuggestedEventRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'SuggestedEventRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is SuggestedEventRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createSuggestedEventRecordData({
  String? name,
  String? category,
  String? description,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'Name': name,
      'Category': category,
      'Description': description,
    }.withoutNulls,
  );

  return firestoreData;
}

class SuggestedEventRecordDocumentEquality
    implements Equality<SuggestedEventRecord> {
  const SuggestedEventRecordDocumentEquality();

  @override
  bool equals(SuggestedEventRecord? e1, SuggestedEventRecord? e2) {
    return e1?.name == e2?.name &&
        e1?.category == e2?.category &&
        e1?.description == e2?.description;
  }

  @override
  int hash(SuggestedEventRecord? e) =>
      const ListEquality().hash([e?.name, e?.category, e?.description]);

  @override
  bool isValidKey(Object? o) => o is SuggestedEventRecord;
}
