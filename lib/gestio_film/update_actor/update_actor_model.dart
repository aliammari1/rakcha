import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'update_actor_widget.dart' show UpdateActorWidget;
import 'package:flutter/material.dart';

class UpdateActorModel extends FlutterFlowModel<UpdateActorWidget> {
  ///  Local state fields for this page.

  bool isImageUploaded = true;

  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadDataM8o = false;
  FFUploadedFile uploadedLocalFile_uploadDataM8o =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataM8o = '';

  // State field(s) for Name_Actor widget.
  FocusNode? nameActorFocusNode;
  TextEditingController? nameActorTextController;
  String? Function(BuildContext, String?)? nameActorTextControllerValidator;
  String? _nameActorTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for Des_Actor widget.
  FocusNode? desActorFocusNode;
  TextEditingController? desActorTextController;
  String? Function(BuildContext, String?)? desActorTextControllerValidator;
  String? _desActorTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  @override
  void initState(BuildContext context) {
    nameActorTextControllerValidator = _nameActorTextControllerValidator;
    desActorTextControllerValidator = _desActorTextControllerValidator;
  }

  @override
  void dispose() {
    nameActorFocusNode?.dispose();
    nameActorTextController?.dispose();

    desActorFocusNode?.dispose();
    desActorTextController?.dispose();
  }
}
