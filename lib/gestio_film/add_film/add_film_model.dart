import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'add_film_widget.dart' show AddFilmWidget;
import 'package:flutter/material.dart';
import 'package:mask_text_input_formatter/mask_text_input_formatter.dart';

class AddFilmModel extends FlutterFlowModel<AddFilmWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadData123 = false;
  FFUploadedFile uploadedLocalFile_uploadData123 =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadData123 = '';

  // State field(s) for Name_Film widget.
  FocusNode? nameFilmFocusNode;
  TextEditingController? nameFilmTextController;
  String? Function(BuildContext, String?)? nameFilmTextControllerValidator;
  String? _nameFilmTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for Des_Film widget.
  FocusNode? desFilmFocusNode1;
  TextEditingController? desFilmTextController1;
  String? Function(BuildContext, String?)? desFilmTextController1Validator;
  String? _desFilmTextController1Validator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for Des_Film widget.
  FocusNode? desFilmFocusNode2;
  TextEditingController? desFilmTextController2;
  String? Function(BuildContext, String?)? desFilmTextController2Validator;
  String? _desFilmTextController2Validator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^\\d+\$').hasMatch(val)) {
      return 'entre  5 et 60';
    }
    return null;
  }

  // State field(s) for Des_Film widget.
  FocusNode? desFilmFocusNode3;
  TextEditingController? desFilmTextController3;
  String? Function(BuildContext, String?)? desFilmTextController3Validator;
  String? _desFilmTextController3Validator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp('^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.be)\\/.+')
        .hasMatch(val)) {
      return 'give me Youtube URL';
    }
    return null;
  }

  // State field(s) for DropDown widget.
  List<String>? dropDownValue;
  FormFieldController<List<String>>? dropDownValueController;
  // State field(s) for DropDown2 widget.
  List<String>? dropDown2Value;
  FormFieldController<List<String>>? dropDown2ValueController;
  // State field(s) for dateOfBirth_Create widget.
  FocusNode? dateOfBirthCreateFocusNode;
  TextEditingController? dateOfBirthCreateTextController;
  late MaskTextInputFormatter dateOfBirthCreateMask;
  String? Function(BuildContext, String?)?
      dateOfBirthCreateTextControllerValidator;
  String? _dateOfBirthCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  DateTime? datePicked;

  @override
  void initState(BuildContext context) {
    nameFilmTextControllerValidator = _nameFilmTextControllerValidator;
    desFilmTextController1Validator = _desFilmTextController1Validator;
    desFilmTextController2Validator = _desFilmTextController2Validator;
    desFilmTextController3Validator = _desFilmTextController3Validator;
    dateOfBirthCreateTextControllerValidator =
        _dateOfBirthCreateTextControllerValidator;
  }

  @override
  void dispose() {
    nameFilmFocusNode?.dispose();
    nameFilmTextController?.dispose();

    desFilmFocusNode1?.dispose();
    desFilmTextController1?.dispose();

    desFilmFocusNode2?.dispose();
    desFilmTextController2?.dispose();

    desFilmFocusNode3?.dispose();
    desFilmTextController3?.dispose();

    dateOfBirthCreateFocusNode?.dispose();
    dateOfBirthCreateTextController?.dispose();
  }
}
