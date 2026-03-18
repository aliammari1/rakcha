import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'updatecategorys_widget.dart' show UpdatecategorysWidget;
import 'package:flutter/material.dart';

class UpdatecategorysModel extends FlutterFlowModel<UpdatecategorysWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for sign_up_name widget.
  FocusNode? signUpNameFocusNode;
  TextEditingController? signUpNameTextController;
  String? Function(BuildContext, String?)? signUpNameTextControllerValidator;
  // State field(s) for sign_up_email widget.
  FocusNode? signUpEmailFocusNode;
  TextEditingController? signUpEmailTextController;
  String? Function(BuildContext, String?)? signUpEmailTextControllerValidator;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {
    signUpNameFocusNode?.dispose();
    signUpNameTextController?.dispose();

    signUpEmailFocusNode?.dispose();
    signUpEmailTextController?.dispose();
  }
}
