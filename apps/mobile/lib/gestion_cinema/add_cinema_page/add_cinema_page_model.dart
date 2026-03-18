import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'add_cinema_page_widget.dart' show AddCinemaPageWidget;
import 'package:flutter/material.dart';

class AddCinemaPageModel extends FlutterFlowModel<AddCinemaPageWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadDataCinemaUpdate = false;
  FFUploadedFile uploadedLocalFile_uploadDataCinemaUpdate =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataCinemaUpdate = '';

  // State field(s) for Name widget.
  FocusNode? nameFocusNode;
  TextEditingController? nameTextController;
  String? Function(BuildContext, String?)? nameTextControllerValidator;
  // State field(s) for Address widget.
  FocusNode? addressFocusNode;
  TextEditingController? addressTextController;
  String? Function(BuildContext, String?)? addressTextControllerValidator;
  // State field(s) for Status widget.
  FocusNode? statusFocusNode;
  TextEditingController? statusTextController;
  String? Function(BuildContext, String?)? statusTextControllerValidator;
  // State field(s) for Responsable widget.
  FocusNode? responsableFocusNode;
  TextEditingController? responsableTextController;
  String? Function(BuildContext, String?)? responsableTextControllerValidator;
  // State field(s) for Description widget.
  FocusNode? descriptionFocusNode;
  TextEditingController? descriptionTextController;
  String? Function(BuildContext, String?)? descriptionTextControllerValidator;

  @override
  void initState(BuildContext context) {}

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
