import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'update_film_widget.dart' show UpdateFilmWidget;
import 'package:flutter/material.dart';

class UpdateFilmModel extends FlutterFlowModel<UpdateFilmWidget> {
  ///  Local state fields for this page.

  bool isImageUploaded = true;

  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadData0rx = false;
  FFUploadedFile uploadedLocalFile_uploadData0rx =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadData0rx = '';

  // State field(s) for Name_Film widget.
  FocusNode? nameFilmFocusNode;
  TextEditingController? nameFilmTextController;
  String? Function(BuildContext, String?)? nameFilmTextControllerValidator;
  // State field(s) for Des_Film widget.
  FocusNode? desFilmFocusNode1;
  TextEditingController? desFilmTextController1;
  String? Function(BuildContext, String?)? desFilmTextController1Validator;
  // State field(s) for Des_Film widget.
  FocusNode? desFilmFocusNode2;
  TextEditingController? desFilmTextController2;
  String? Function(BuildContext, String?)? desFilmTextController2Validator;
  // State field(s) for urlfilm widget.
  FocusNode? urlfilmFocusNode;
  TextEditingController? urlfilmTextController;
  String? Function(BuildContext, String?)? urlfilmTextControllerValidator;
  String? _urlfilmTextControllerValidator(BuildContext context, String? val) {
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
    urlfilmTextControllerValidator = _urlfilmTextControllerValidator;
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

    urlfilmFocusNode?.dispose();
    urlfilmTextController?.dispose();

    dateOfBirthCreateFocusNode?.dispose();
    dateOfBirthCreateTextController?.dispose();
  }
}
