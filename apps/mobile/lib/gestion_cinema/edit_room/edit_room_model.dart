import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'edit_room_widget.dart' show EditRoomWidget;
import 'package:flutter/material.dart';

class EditRoomModel extends FlutterFlowModel<EditRoomWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for Name widget.
  FocusNode? nameFocusNode;
  TextEditingController? nameTextController;
  String? Function(BuildContext, String?)? nameTextControllerValidator;
  String? _nameTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (val.length < 5) {
      return '\"Please enter a valid name (between 3 and 50 characters).\"';
    }
    if (val.length > 50) {
      return '\"Please enter a valid name (between 3 and 50 characters).\"';
    }

    return null;
  }

  // State field(s) for Numberofseats widget.
  FocusNode? numberofseatsFocusNode;
  TextEditingController? numberofseatsTextController;
  String? Function(BuildContext, String?)? numberofseatsTextControllerValidator;
  String? _numberofseatsTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('const numberRegex = /^\\d+\$/;').hasMatch(val)) {
      return 'Please enter a valid number of seats';
    }
    return null;
  }

  // State field(s) for Cinema widget.
  List<String>? cinemaValue;
  FormFieldController<List<String>>? cinemaValueController;

  @override
  void initState(BuildContext context) {
    nameTextControllerValidator = _nameTextControllerValidator;
    numberofseatsTextControllerValidator =
        _numberofseatsTextControllerValidator;
  }

  @override
  void dispose() {
    nameFocusNode?.dispose();
    nameTextController?.dispose();

    numberofseatsFocusNode?.dispose();
    numberofseatsTextController?.dispose();
  }
}
