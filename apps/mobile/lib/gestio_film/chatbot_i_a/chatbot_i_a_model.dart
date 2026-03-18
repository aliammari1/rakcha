import '/flutter_flow/flutter_flow_util.dart';
import 'chatbot_i_a_widget.dart' show ChatbotIAWidget;
import 'package:flutter/material.dart';

class ChatbotIAModel extends FlutterFlowModel<ChatbotIAWidget> {
  ///  State fields for stateful widgets in this page.

  // State field(s) for TextField widget.
  FocusNode? textFieldFocusNode;
  TextEditingController? textController;
  String? Function(BuildContext, String?)? textControllerValidator;
  // Stores action output result for [Gemini - Generate Text] action in Icon widget.
  String? outPut;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {
    textFieldFocusNode?.dispose();
    textController?.dispose();
  }
}
