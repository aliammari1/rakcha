import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class CategoryFilmRecord extends FirestoreRecord {
  CategoryFilmRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "name" field.
  String? _name;
  String get name => _name ?? '';
  bool hasName() => _name != null;

  // "description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  void _initializeFields() {
    _name = snapshotData['name'] as String?;
    _description = snapshotData['description'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('categoryFilm');

  static Stream<CategoryFilmRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => CategoryFilmRecord.fromSnapshot(s));

  static Future<CategoryFilmRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => CategoryFilmRecord.fromSnapshot(s));

  static CategoryFilmRecord fromSnapshot(DocumentSnapshot snapshot) =>
      CategoryFilmRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static CategoryFilmRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      CategoryFilmRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'CategoryFilmRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is CategoryFilmRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createCategoryFilmRecordData({
  String? name,
  String? description,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'name': name,
      'description': description,
    }.withoutNulls,
  );

  return firestoreData;
}

class CategoryFilmRecordDocumentEquality
    implements Equality<CategoryFilmRecord> {
  const CategoryFilmRecordDocumentEquality();

  @override
  bool equals(CategoryFilmRecord? e1, CategoryFilmRecord? e2) {
    return e1?.name == e2?.name && e1?.description == e2?.description;
  }

  @override
  int hash(CategoryFilmRecord? e) =>
      const ListEquality().hash([e?.name, e?.description]);

  @override
  bool isValidKey(Object? o) => o is CategoryFilmRecord;
}
