import 'dart:async';

import 'package:collection/collection.dart';

import '/backend/schema/util/firestore_util.dart';

import 'index.dart';
import '/flutter_flow/flutter_flow_util.dart';

class ProduitRecord extends FirestoreRecord {
  ProduitRecord._(
    DocumentReference reference,
    Map<String, dynamic> data,
  ) : super(reference, data) {
    _initializeFields();
  }

  // "id" field.
  String? _id;
  String get id => _id ?? '';
  bool hasId() => _id != null;

  // "nom" field.
  String? _nom;
  String get nom => _nom ?? '';
  bool hasNom() => _nom != null;

  // "description" field.
  String? _description;
  String get description => _description ?? '';
  bool hasDescription() => _description != null;

  // "prix" field.
  int? _prix;
  int get prix => _prix ?? 0;
  bool hasPrix() => _prix != null;

  // "quantiteP" field.
  int? _quantiteP;
  int get quantiteP => _quantiteP ?? 0;
  bool hasQuantiteP() => _quantiteP != null;

  // "image" field.
  String? _image;
  String get image => _image ?? '';
  bool hasImage() => _image != null;

  // "category" field.
  String? _category;
  String get category => _category ?? '';
  bool hasCategory() => _category != null;

  void _initializeFields() {
    _id = snapshotData['id'] as String?;
    _nom = snapshotData['nom'] as String?;
    _description = snapshotData['description'] as String?;
    _prix = castToType<int>(snapshotData['prix']);
    _quantiteP = castToType<int>(snapshotData['quantiteP']);
    _image = snapshotData['image'] as String?;
    _category = snapshotData['category'] as String?;
  }

  static CollectionReference get collection =>
      FirebaseFirestore.instance.collection('produit');

  static Stream<ProduitRecord> getDocument(DocumentReference ref) =>
      ref.snapshots().map((s) => ProduitRecord.fromSnapshot(s));

  static Future<ProduitRecord> getDocumentOnce(DocumentReference ref) =>
      ref.get().then((s) => ProduitRecord.fromSnapshot(s));

  static ProduitRecord fromSnapshot(DocumentSnapshot snapshot) =>
      ProduitRecord._(
        snapshot.reference,
        mapFromFirestore(snapshot.data() as Map<String, dynamic>),
      );

  static ProduitRecord getDocumentFromData(
    Map<String, dynamic> data,
    DocumentReference reference,
  ) =>
      ProduitRecord._(reference, mapFromFirestore(data));

  @override
  String toString() =>
      'ProduitRecord(reference: ${reference.path}, data: $snapshotData)';

  @override
  int get hashCode => reference.path.hashCode;

  @override
  bool operator ==(other) =>
      other is ProduitRecord &&
      reference.path.hashCode == other.reference.path.hashCode;
}

Map<String, dynamic> createProduitRecordData({
  String? id,
  String? nom,
  String? description,
  int? prix,
  int? quantiteP,
  String? image,
  String? category,
}) {
  final firestoreData = mapToFirestore(
    <String, dynamic>{
      'id': id,
      'nom': nom,
      'description': description,
      'prix': prix,
      'quantiteP': quantiteP,
      'image': image,
      'category': category,
    }.withoutNulls,
  );

  return firestoreData;
}

class ProduitRecordDocumentEquality implements Equality<ProduitRecord> {
  const ProduitRecordDocumentEquality();

  @override
  bool equals(ProduitRecord? e1, ProduitRecord? e2) {
    return e1?.id == e2?.id &&
        e1?.nom == e2?.nom &&
        e1?.description == e2?.description &&
        e1?.prix == e2?.prix &&
        e1?.quantiteP == e2?.quantiteP &&
        e1?.image == e2?.image &&
        e1?.category == e2?.category;
  }

  @override
  int hash(ProduitRecord? e) => const ListEquality().hash([
        e?.id,
        e?.nom,
        e?.description,
        e?.prix,
        e?.quantiteP,
        e?.image,
        e?.category
      ]);

  @override
  bool isValidKey(Object? o) => o is ProduitRecord;
}
