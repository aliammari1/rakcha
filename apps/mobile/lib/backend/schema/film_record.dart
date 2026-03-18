import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';
import '/backend/schema/util/schema_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class FilmRecord extends FirestoreRecord {
  FilmRecord._(
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

  // "year" field.
  DateTime? _year;
  DateTime? get year => _year;
  bool hasYear() => _year != null;

  // "prix" field.
  int? _prix;
  int get prix => _prix ?? 0;
  bool hasPrix() => _prix != null;

  // "image" field.
  String? _image;
  String get image => _image ?? '';
  bool hasImage() => _image != null;

  // "category" field.
  List<String>? _category;
  List<String> get category => _category ?? const [];
  bool hasCategory() => _category != null;

  // "actors" field.
  List<String>? _actors;
  List<String> get actors => _actors ?? const [];
  bool hasActors() => _actors != null;

  // "path" field.
  String? _path;
  String get path => _path ?? '';
  bool hasPath() => _path != null;

  void _initializeFields() {
    _name = snapshotData['name'] as String?;
    _description = snapshotData['description'] as String?;
    _year = snapshotData['year'] as DateTime?;
    _prix = castToType<int>(snapshotData['prix']);
    _image = snapshotData['image'] as String?;
    _category = getDataList(snapshotData['category']);
    _actors = getDataList(snapshotData['actors']);
    _path = snapshotData['path'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('Film');

  static Stream<FilmRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => FilmRecord.fromSnapshot(s));

  static Future<FilmRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => FilmRecord.fromSnapshot(s));

  static FilmRecord fromSnapshot(DocumentSnapshot snapshot) => FilmRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static FilmRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      FilmRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'FilmRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is FilmRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createFilmRecordData({
  String? name,
  String? description,
  DateTime? year,
  int? prix,
  String? image,
  String? path,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'name': name,
      'description': description,
      'year': year,
      'prix': prix,
      'image': image,
      'path': path,
    }.withoutNulls,
  );

  return firestoreData;
}

class FilmRecordDocumentEquality implements Equality<FilmRecord> {
  const FilmRecordDocumentEquality();

  @override
  bool equals(FilmRecord? e1, FilmRecord? e2) {
    const listEquality = ListEquality();
    return e1?.name == e2?.name &&
        e1?.description == e2?.description &&
        e1?.year == e2?.year &&
        e1?.prix == e2?.prix &&
        e1?.image == e2?.image &&
        listEquality.equals(e1?.category, e2?.category) &&
        listEquality.equals(e1?.actors, e2?.actors) &&
        e1?.path == e2?.path;
  }

  @override
  int hash(FilmRecord? e) => const ListEquality().hash([
        e?.name,
        e?.description,
        e?.year,
        e?.prix,
        e?.image,
        e?.category,
        e?.actors,
        e?.path
      ]);

  @override
  bool isValidKey(Object? o) => o is FilmRecord;
}
