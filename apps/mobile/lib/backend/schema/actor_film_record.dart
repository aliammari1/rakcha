import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class ActorFilmRecord extends FirestoreRecord {
  ActorFilmRecord._(
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

  // "image" field.
  String? _image;
  String get image => _image ?? '';
  bool hasImage() => _image != null;

  void _initializeFields() {
    _name = snapshotData['name'] as String?;
    _description = snapshotData['description'] as String?;
    _image = snapshotData['image'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('ActorFilm');

  static Stream<ActorFilmRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => ActorFilmRecord.fromSnapshot(s));

  static Future<ActorFilmRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => ActorFilmRecord.fromSnapshot(s));

  static ActorFilmRecord fromSnapshot(DocumentSnapshot snapshot) =>
      ActorFilmRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static ActorFilmRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      ActorFilmRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'ActorFilmRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is ActorFilmRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createActorFilmRecordData({
  String? name,
  String? description,
  String? image,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'name': name,
      'description': description,
      'image': image,
    }.withoutNulls,
  );

  return firestoreData;
}

class ActorFilmRecordDocumentEquality implements Equality<ActorFilmRecord> {
  const ActorFilmRecordDocumentEquality();

  @override
  bool equals(ActorFilmRecord? e1, ActorFilmRecord? e2) {
    return e1?.name == e2?.name &&
        e1?.description == e2?.description &&
        e1?.image == e2?.image;
  }

  @override
  int hash(ActorFilmRecord? e) =>
      const ListEquality().hash([e?.name, e?.description, e?.image]);

  @override
  bool isValidKey(Object? o) => o is ActorFilmRecord;
}
