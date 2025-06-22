import '/flutter_flow/flutter_flow_util.dart';
import '/index.dart';
import 'auth3_widget.dart' show Auth3Widget;
import 'package:flutter/material.dart';

class Auth3Model extends FlutterFlowModel<Auth3Widget> {
  ///  State fields for stateful widgets in this page.

  final formKey1 = GlobalKey<FormState>();
  final formKey2 = GlobalKey<FormState>();
  // State field(s) for TabBar widget.
  TabController? tabBarController;
  int get tabBarCurrentIndex =>
      tabBarController != null ? tabBarController!.index : 0;
  int get tabBarPreviousIndex =>
      tabBarController != null ? tabBarController!.previousIndex : 0;

  bool isDataUploading_uploadDataCreate = false;
  FFUploadedFile uploadedLocalFile_uploadDataCreate =
      FFUploadedFile(bytes: Uint8List.fromList([]));
  String uploadedFileUrl_uploadDataCreate = '';

  // State field(s) for firstName_Create widget.
  FocusNode? firstNameCreateFocusNode;
  TextEditingController? firstNameCreateTextController;
  String? Function(BuildContext, String?)?
      firstNameCreateTextControllerValidator;
  String? _firstNameCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for lastName_Create widget.
  FocusNode? lastNameCreateFocusNode;
  TextEditingController? lastNameCreateTextController;
  String? Function(BuildContext, String?)?
      lastNameCreateTextControllerValidator;
  String? _lastNameCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for emailAddress_Create widget.
  FocusNode? emailAddressCreateFocusNode;
  TextEditingController? emailAddressCreateTextController;
  String? Function(BuildContext, String?)?
      emailAddressCreateTextControllerValidator;
  String? _emailAddressCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp(kTextValidatorEmailRegex).hasMatch(val)) {
      return 'give me the right mail';
    }
    return null;
  }

  // State field(s) for phoneNumber_Create widget.
  FocusNode? phoneNumberCreateFocusNode;
  TextEditingController? phoneNumberCreateTextController;
  String? Function(BuildContext, String?)?
      phoneNumberCreateTextControllerValidator;
  String? _phoneNumberCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for address_Create widget.
  FocusNode? addressCreateFocusNode;
  TextEditingController? addressCreateTextController;
  String? Function(BuildContext, String?)? addressCreateTextControllerValidator;
  String? _addressCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

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
  // State field(s) for password_Create widget.
  FocusNode? passwordCreateFocusNode;
  TextEditingController? passwordCreateTextController;
  late bool passwordCreateVisibility;
  String? Function(BuildContext, String?)?
      passwordCreateTextControllerValidator;
  String? _passwordCreateTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for password_CreateConfirm widget.
  FocusNode? passwordCreateConfirmFocusNode;
  TextEditingController? passwordCreateConfirmTextController;
  late bool passwordCreateConfirmVisibility;
  String? Function(BuildContext, String?)?
      passwordCreateConfirmTextControllerValidator;
  String? _passwordCreateConfirmTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  // State field(s) for emailAddress widget.
  FocusNode? emailAddressFocusNode;
  TextEditingController? emailAddressTextController;
  String? Function(BuildContext, String?)? emailAddressTextControllerValidator;
  String? _emailAddressTextControllerValidator(
      BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    if (!RegExp(kTextValidatorEmailRegex).hasMatch(val)) {
      return 'give me a valid mail';
    }
    return null;
  }

  // State field(s) for password widget.
  FocusNode? passwordFocusNode;
  TextEditingController? passwordTextController;
  late bool passwordVisibility;
  String? Function(BuildContext, String?)? passwordTextControllerValidator;
  String? _passwordTextControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    return null;
  }

  @override
  void initState(BuildContext context) {
    firstNameCreateTextControllerValidator =
        _firstNameCreateTextControllerValidator;
    lastNameCreateTextControllerValidator =
        _lastNameCreateTextControllerValidator;
    emailAddressCreateTextControllerValidator =
        _emailAddressCreateTextControllerValidator;
    phoneNumberCreateTextControllerValidator =
        _phoneNumberCreateTextControllerValidator;
    addressCreateTextControllerValidator =
        _addressCreateTextControllerValidator;
    dateOfBirthCreateTextControllerValidator =
        _dateOfBirthCreateTextControllerValidator;
    passwordCreateVisibility = false;
    passwordCreateTextControllerValidator =
        _passwordCreateTextControllerValidator;
    passwordCreateConfirmVisibility = false;
    passwordCreateConfirmTextControllerValidator =
        _passwordCreateConfirmTextControllerValidator;
    emailAddressTextControllerValidator = _emailAddressTextControllerValidator;
    passwordVisibility = false;
    passwordTextControllerValidator = _passwordTextControllerValidator;
  }

  @override
  void dispose() {
    tabBarController?.dispose();
    firstNameCreateFocusNode?.dispose();
    firstNameCreateTextController?.dispose();

    lastNameCreateFocusNode?.dispose();
    lastNameCreateTextController?.dispose();

    emailAddressCreateFocusNode?.dispose();
    emailAddressCreateTextController?.dispose();

    phoneNumberCreateFocusNode?.dispose();
    phoneNumberCreateTextController?.dispose();

    addressCreateFocusNode?.dispose();
    addressCreateTextController?.dispose();

    dateOfBirthCreateFocusNode?.dispose();
    dateOfBirthCreateTextController?.dispose();

    passwordCreateFocusNode?.dispose();
    passwordCreateTextController?.dispose();

    passwordCreateConfirmFocusNode?.dispose();
    passwordCreateConfirmTextController?.dispose();

    emailAddressFocusNode?.dispose();
    emailAddressTextController?.dispose();

    passwordFocusNode?.dispose();
    passwordTextController?.dispose();
  }
}
