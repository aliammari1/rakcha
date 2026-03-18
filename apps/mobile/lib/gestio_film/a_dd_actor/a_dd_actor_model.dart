import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'a_dd_actor_widget.dart' show ADdActorWidget;
import 'package:flutter/material.dart';

class ADdActorModel extends FlutterFlowModel<ADdActorWidget> {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadDataZ68 = false;
  FFUploadedFile uploadedLocalFile_uploadDataZ68 =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataZ68 = '';

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
