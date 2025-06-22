import '/backend/backend.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'list_panier_widget.dart' show ListPanierWidget;
import 'package:flutter/material.dart';

class ListPanierModel extends FlutterFlowModel<ListPanierWidget> {
  ///  Local state fields for this page.

  bool price = true;

  ///  State fields for stateful widgets in this page.

  // State field(s) for PageView widget.
  PageController? pageViewController;

  int get pageViewCurrentIndex => pageViewController != null &&
          pageViewController!.hasClients &&
          pageViewController!.page != null
      ? pageViewController!.page!.round()
      : 0;
  // Stores action output result for [Firestore Query - Query a collection] action in IconButton widget.
  List<CartRecord>? deleteQuery;
  // Stores action output result for [Firestore Query - Query a collection] action in IconButton widget.
  List<CartRecord>? updateQuery;
  // Stores action output result for [Firestore Query - Query a collection] action in Button widget.
  List<CartRecord>? pageLoad;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {}
}
