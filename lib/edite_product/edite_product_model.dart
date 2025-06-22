import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'edite_product_widget.dart' show EditeProductWidget;
import 'package:flutter/material.dart';

class EditeProductModel extends FlutterFlowModel<EditeProductWidget> {
  ///  Local state fields for this page.

  bool isImageUploaded = true;

  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  bool isDataUploading_uploadDataProduitCreate = false;
  FFUploadedFile uploadedLocalFile_uploadDataProduitCreate =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataProduitCreate = '';

  // State field(s) for name widget.
  FocusNode? nameFocusNode;
  TextEditingController? nameTextController;
  String? Function(BuildContext, String?)? nameTextControllerValidator;
  String? _nameTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'name is emty';
    }

    return null;
  }

  // State field(s) for price widget.
  FocusNode? priceFocusNode;
  TextEditingController? priceTextController;
  String? Function(BuildContext, String?)? priceTextControllerValidator;
  String? _priceTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'price is empty';
    }

    if (!RegExp('^\\d+\$').hasMatch(val)) {
      return 'price must be a number';
    }
    return null;
  }

  // State field(s) for description widget.
  FocusNode? descriptionFocusNode;
  TextEditingController? descriptionTextController;
  String? Function(BuildContext, String?)? descriptionTextControllerValidator;
  String? _descriptionTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'description is empty';
    }

    if (val.length < 10) {
      return 'Description too short (min. 10 chars)';
    }

    return null;
  }

  // State field(s) for quantity widget.
  FocusNode? quantityFocusNode;
  TextEditingController? quantityTextController;
  String? Function(BuildContext, String?)? quantityTextControllerValidator;
  String? _quantityTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'quantity is emty';
    }

    if (!RegExp('^\\d+\$').hasMatch(val)) {
      return 'quantity must be a number';
    }
    return null;
  }

  // State field(s) for category widget.
  String? categoryValue;
  FormFieldController<String>? categoryValueController;

  @override
  void initState(BuildContext context) {
    nameTextControllerValidator = _nameTextControllerValidator;
    priceTextControllerValidator = _priceTextControllerValidator;
    descriptionTextControllerValidator = _descriptionTextControllerValidator;
    quantityTextControllerValidator = _quantityTextControllerValidator;
  }

  @override
  void dispose() {
    nameFocusNode?.dispose();
    nameTextController?.dispose();

    priceFocusNode?.dispose();
    priceTextController?.dispose();

    descriptionFocusNode?.dispose();
    descriptionTextController?.dispose();

    quantityFocusNode?.dispose();
    quantityTextController?.dispose();
  }
}
