import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class CategoryProductRecord extends FirestoreRecord {
  CategoryProductRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "nomCategory" field.
  String? _nomCategory;
  String get nomCategory => _nomCategory ?? '';
  bool hasNomCategory() => _nomCategory != null;

  // "description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  void _initializeFields() {
    _nomCategory = snapshotData['nomCategory'] as String?;
    _description = snapshotData['description'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('CategoryProduct');

  static Stream<CategoryProductRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => CategoryProductRecord.fromSnapshot(s));

  static Future<CategoryProductRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => CategoryProductRecord.fromSnapshot(s));

  static CategoryProductRecord fromSnapshot(DocumentSnapshot snapshot) =>
      CategoryProductRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static CategoryProductRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      CategoryProductRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'CategoryProductRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is CategoryProductRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createCategoryProductRecordData({
  String? nomCategory,
  String? description,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'nomCategory': nomCategory,
      'description': description,
    }.withoutNulls,
  );

  return firestoreData;
}

class CategoryProductRecordDocumentEquality
    implements Equality<CategoryProductRecord> {
  const CategoryProductRecordDocumentEquality();

  @override
  bool equals(CategoryProductRecord? e1, CategoryProductRecord? e2) {
    return e1?.nomCategory == e2?.nomCategory &&
        e1?.description == e2?.description;
  }

  @override
  int hash(CategoryProductRecord? e) =>
      const ListEquality().hash([e?.nomCategory, e?.description]);

  @override
  bool isValidKey(Object? o) => o is CategoryProductRecord;
}
