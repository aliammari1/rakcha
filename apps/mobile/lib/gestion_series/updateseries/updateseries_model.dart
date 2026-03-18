import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'updateseries_widget.dart' show UpdateseriesWidget;
import 'package:flutter/material.dart';

class UpdateseriesModel extends FlutterFlowModel<UpdateseriesWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for name widget.
  FocusNode? nameFocusNode;
  TextEditingController? nameTextController;
  String? Function(BuildContext, String?)? nameTextControllerValidator;
  // State field(s) for summary widget.
  FocusNode? summaryFocusNode;
  TextEditingController? summaryTextController;
  String? Function(BuildContext, String?)? summaryTextControllerValidator;
  // State field(s) for director widget.
  FocusNode? directorFocusNode;
  TextEditingController? directorTextController;
  String? Function(BuildContext, String?)? directorTextControllerValidator;
  // State field(s) for country widget.
  FocusNode? countryFocusNode;
  TextEditingController? countryTextController;
  String? Function(BuildContext, String?)? countryTextControllerValidator;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {
    nameFocusNode?.dispose();
    nameTextController?.dispose();

    summaryFocusNode?.dispose();
    summaryTextController?.dispose();

    directorFocusNode?.dispose();
    directorTextController?.dispose();

    countryFocusNode?.dispose();
    countryTextController?.dispose();
  }
}
