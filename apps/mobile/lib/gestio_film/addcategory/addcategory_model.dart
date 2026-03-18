import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'addcategory_widget.dart' show AddcategoryWidget;
import 'package:flutter/material.dart';

class AddcategoryModel extends FlutterFlowModel<AddcategoryWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for sign_up_name widget.
  FocusNode? signUpNameFocusNode;
  TextEditingController? signUpNameTextController;
  String? Function(BuildContext, String?)? signUpNameTextControllerValidator;
  String? _signUpNameTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^([A-Z]\\w*|\\d[A-Z\\d\\s-]*)\$').hasMatch(val)) {
      return 'Start with upper case';
    }
    return null;
  }

  // State field(s) for sign_up_email widget.
  FocusNode? signUpEmailFocusNode;
  TextEditingController? signUpEmailTextController;
  String? Function(BuildContext, String?)? signUpEmailTextControllerValidator;
  String? _signUpEmailTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^([A-Z]\\w*|\\d[A-Z\\d\\s-]*)\$').hasMatch(val)) {
      return 'Start with upper case';
    }
    return null;
  }

  @override
  void initState(BuildContext context) {
    signUpNameTextControllerValidator = _signUpNameTextControllerValidator;
    signUpEmailTextControllerValidator = _signUpEmailTextControllerValidator;
  }

  @override
  void dispose() {
    signUpNameFocusNode?.dispose();
    signUpNameTextController?.dispose();

    signUpEmailFocusNode?.dispose();
    signUpEmailTextController?.dispose();
  }
}
