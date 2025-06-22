import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'edit_seance_widget.dart' show EditSeanceWidget;
import 'package:flutter/material.dart';

class EditSeanceModel extends FlutterFlowModel<EditSeanceWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for cinema widget.
  List<String>? cinemaValue;
  FormFieldController<List<String>>? cinemaValueController;
  // State field(s) for Room widget.
  List<String>? roomValue;
  FormFieldController<List<String>>? roomValueController;
  // State field(s) for Date widget.
  FocusNode? dateFocusNode;
  TextEditingController? dateTextController;
  String? Function(BuildContext, String?)? dateTextControllerValidator;
  String? _dateTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^\\d{4}-\\d{2}-\\d{2}\$').hasMatch(val)) {
      return '\"YYYY-MM-DD\" form recomanded';
    }
    return null;
  }

  // State field(s) for HD widget.
  FocusNode? hdFocusNode;
  TextEditingController? hdTextController;
  String? Function(BuildContext, String?)? hdTextControllerValidator;
  String? _hdTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\$').hasMatch(val)) {
      return '\"HH:MM:SS\" form recomanded';
    }
    return null;
  }

  // State field(s) for HF widget.
  FocusNode? hfFocusNode;
  TextEditingController? hfTextController;
  String? Function(BuildContext, String?)? hfTextControllerValidator;
  String? _hfTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\$').hasMatch(val)) {
      return '\"HH:MM:SS\" form recomanded';
    }
    return null;
  }

  // State field(s) for Prix widget.
  FocusNode? prixFocusNode;
  TextEditingController? prixTextController;
  String? Function(BuildContext, String?)? prixTextControllerValidator;
  String? _prixTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^\\d+(\\.\\d{1,2})?\$').hasMatch(val)) {
      return 'not valid price';
    }
    return null;
  }

  @override
  void initState(BuildContext context) {
    dateTextControllerValidator = _dateTextControllerValidator;
    hdTextControllerValidator = _hdTextControllerValidator;
    hfTextControllerValidator = _hfTextControllerValidator;
    prixTextControllerValidator = _prixTextControllerValidator;
  }

  @override
  void dispose() {
    dateFocusNode?.dispose();
    dateTextController?.dispose();

    hdFocusNode?.dispose();
    hdTextController?.dispose();

    hfFocusNode?.dispose();
    hfTextController?.dispose();

    prixFocusNode?.dispose();
    prixTextController?.dispose();
  }
}
