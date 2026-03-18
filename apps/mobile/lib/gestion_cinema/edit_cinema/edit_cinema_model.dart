import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'edit_cinema_widget.dart' show EditCinemaWidget;
import 'package:flutter/material.dart';

class EditCinemaModel extends FlutterFlowModel<EditCinemaWidget> {
  ///  Local state fields for this page.

  bool isImageUploaded = true;

  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadDataCinemaCreate = false;
  FFUploadedFile uploadedLocalFile_uploadDataCinemaCreate =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataCinemaCreate = '';

  // State field(s) for Name widget.
  FocusNode? nameFocusNode;
  TextEditingController? nameTextController;
  String? Function(BuildContext, String?)? nameTextControllerValidator;
  String? _nameTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (val.length < 2) {
      return '\"Please enter a valid name (between 2 and 50 characters).\"';
    }
    if (val.length > 50) {
      return '\"Please enter a valid name (between 2 and 50 characters).\"';
    }

    return null;
  }

  // State field(s) for Address widget.
  FocusNode? addressFocusNode;
  TextEditingController? addressTextController;
  String? Function(BuildContext, String?)? addressTextControllerValidator;
  String? _addressTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (val.length < 3) {
      return '\"Please enter a valid address (between 3 and 100 characters).\"';
    }
    if (val.length > 100) {
      return '\"Please enter a valid address (between 3 and 100 characters).\"';
    }

    return null;
  }

  // State field(s) for Status widget.
  FocusNode? statusFocusNode;
  TextEditingController? statusTextController;
  String? Function(BuildContext, String?)? statusTextControllerValidator;
  // State field(s) for Responsable widget.
  FocusNode? responsableFocusNode;
  TextEditingController? responsableTextController;
  String? Function(BuildContext, String?)? responsableTextControllerValidator;
  String? _responsableTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (val.length < 3) {
      return '\"Please enter a valid responsible name (between 3 and 50 characters).\"';
    }
    if (val.length > 50) {
      return '\"Please enter a valid responsible name (between 3 and 50 characters).\"';
    }

    return null;
  }

  // State field(s) for Description widget.
  FocusNode? descriptionFocusNode;
  TextEditingController? descriptionTextController;
  String? Function(BuildContext, String?)? descriptionTextControllerValidator;

  @override
  void initState(BuildContext context) {
    nameTextControllerValidator = _nameTextControllerValidator;
    addressTextControllerValidator = _addressTextControllerValidator;
    responsableTextControllerValidator = _responsableTextControllerValidator;
  }

  @override
  void dispose() {
    nameFocusNode?.dispose();
    nameTextController?.dispose();

    addressFocusNode?.dispose();
    addressTextController?.dispose();

    statusFocusNode?.dispose();
    statusTextController?.dispose();

    responsableFocusNode?.dispose();
    responsableTextController?.dispose();

    descriptionFocusNode?.dispose();
    descriptionTextController?.dispose();
  }
}
