import '/flutter_flow/flutter_flow_drop_down.dart';
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'h_o_m_e_film_model.dart';
export 'h_o_m_e_film_model.dart';

class HOMEFilmWidget extends StatefulWidget {
  const HOMEFilmWidget({super.key});

  static String routeName = 'HOMEFilm';
  static String routePath = '/hOMEFilm';

  @override
  State<HOMEFilmWidget> createState() => _HOMEFilmWidgetState();
}

class _HOMEFilmWidgetState extends State<HOMEFilmWidget> {
  late HOMEFilmModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => HOMEFilmModel());

    logFirebaseEvent('screen_view', parameters: {'screen_name': 'HOMEFilm'});
  }

  @override
  void dispose() {
    _model.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).unfocus();
        FocusManager.instance.primaryFocus?.unfocus();
      },
      child: Scaffold(
        key: scaffoldKey,
        backgroundColor: FlutterFlowTheme.of(context).primaryBackground,
        appBar: AppBar(
          backgroundColor: Color(0xFFC40708),
          automaticallyImplyLeading: false,
          leading: FlutterFlowIconButton(
            borderColor: Colors.transparent,
            borderRadius: 30.0,
            borderWidth: 1.0,
            buttonSize: 60.0,
            icon: Icon(
              Icons.arrow_back_rounded,
              color: Colors.white,
              size: 30.0,
            ),
            onPressed: () async {
              logFirebaseEvent('H_O_M_E_FILM_arrow_back_rounded_ICN_ON_T');
              logFirebaseEvent('IconButton_navigate_to');

              context.pushNamed(Home1Widget.routeName);
            },
          ),
          title: Align(
            alignment: AlignmentDirectional(0.0, 0.0),
            child: Text(
              'Gestion Film',
              textAlign: TextAlign.center,
              style: FlutterFlowTheme.of(context).headlineMedium.override(
                    font: GoogleFonts.outfit(
                      fontWeight: FlutterFlowTheme.of(context)
                          .headlineMedium
                          .fontWeight,
                      fontStyle:
                          FlutterFlowTheme.of(context).headlineMedium.fontStyle,
                    ),
                    color: Colors.white,
                    fontSize: 22.0,
                    letterSpacing: 0.0,
                    fontWeight:
                        FlutterFlowTheme.of(context).headlineMedium.fontWeight,
                    fontStyle:
                        FlutterFlowTheme.of(context).headlineMedium.fontStyle,
                  ),
            ),
          ),
          actions: [],
          centerTitle: false,
          elevation: 2.0,
        ),
        body: SafeArea(
          top: true,
          child: Stack(
            children: [
              Align(
                alignment: AlignmentDirectional(-0.06, 0.0),
                child: Padding(
                  padding: EdgeInsetsDirectional.fromSTEB(0.0, 100.0, 0.0, 0.0),
                  child: Column(
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      Padding(
                        padding:
                            EdgeInsetsDirectional.fromSTEB(0.0, 25.0, 0.0, 0.0),
                        child: FlutterFlowDropDown<String>(
                          controller: _model.dropDownValueController1 ??=
                              FormFieldController<String>(null),
                          options: ['Add film', 'List of films'],
                          onChanged: (val) async {
                            safeSetState(() => _model.dropDownValue1 = val);
                            logFirebaseEvent(
                                'H_O_M_E_FILM_DropDown_m3nobypi_ON_FORM_W');
                            logFirebaseEvent('DropDown_navigate_to');

                            context.pushNamed(AddFilmWidget.routeName);
                          },
                          width: 300.0,
                          height: 56.0,
                          textStyle:
                              FlutterFlowTheme.of(context).bodyMedium.override(
                                    font: GoogleFonts.readexPro(
                                      fontWeight: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontWeight,
                                      fontStyle: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontStyle,
                                    ),
                                    color: Color(0xFFD5DDE3),
                                    letterSpacing: 0.0,
                                    fontWeight: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontWeight,
                                    fontStyle: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontStyle,
                                  ),
                          hintText: 'Films',
                          icon: Icon(
                            Icons.keyboard_arrow_down_rounded,
                            color: Colors.white,
                            size: 24.0,
                          ),
                          fillColor: Color(0xFFC40708),
                          elevation: 2.0,
                          borderColor: FlutterFlowTheme.of(context).alternate,
                          borderWidth: 2.0,
                          borderRadius: 8.0,
                          margin: EdgeInsetsDirectional.fromSTEB(
                              16.0, 4.0, 16.0, 4.0),
                          hidesUnderline: true,
                          isOverButton: true,
                          isSearchable: false,
                          isMultiSelect: false,
                        ),
                      ),
                      Padding(
                        padding:
                            EdgeInsetsDirectional.fromSTEB(0.0, 25.0, 0.0, 0.0),
                        child: FlutterFlowDropDown<String>(
                          controller: _model.dropDownValueController2 ??=
                              FormFieldController<String>(
                            _model.dropDownValue2 ??= '',
                          ),
                          options: List<String>.from(
                              ['Add category', 'List of categorys']),
                          optionLabels: ['add', 'list'],
                          onChanged: (val) async {
                            safeSetState(() => _model.dropDownValue2 = val);
                            logFirebaseEvent(
                                'H_O_M_E_FILM_DropDown_wgozlcg8_ON_FORM_W');
                            logFirebaseEvent('DropDown_navigate_to');

                            context.pushNamed(AddcategoryWidget.routeName);
                          },
                          width: 300.0,
                          height: 56.0,
                          textStyle:
                              FlutterFlowTheme.of(context).bodyMedium.override(
                                    font: GoogleFonts.readexPro(
                                      fontWeight: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontWeight,
                                      fontStyle: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontStyle,
                                    ),
                                    color: Color(0xFFD5DDE3),
                                    letterSpacing: 0.0,
                                    fontWeight: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontWeight,
                                    fontStyle: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontStyle,
                                  ),
                          hintText: 'Categorys',
                          icon: Icon(
                            Icons.keyboard_arrow_down_rounded,
                            color: Colors.white,
                            size: 24.0,
                          ),
                          fillColor: Color(0xFFC40708),
                          elevation: 2.0,
                          borderColor: FlutterFlowTheme.of(context).alternate,
                          borderWidth: 2.0,
                          borderRadius: 8.0,
                          margin: EdgeInsetsDirectional.fromSTEB(
                              16.0, 4.0, 16.0, 4.0),
                          hidesUnderline: true,
                          isOverButton: true,
                          isSearchable: false,
                          isMultiSelect: false,
                        ),
                      ),
                      Padding(
                        padding:
                            EdgeInsetsDirectional.fromSTEB(0.0, 25.0, 0.0, 0.0),
                        child: FlutterFlowDropDown<String>(
                          controller: _model.dropDownValueController3 ??=
                              FormFieldController<String>(null),
                          options: ['Add actor', 'List of actors'],
                          onChanged: (val) async {
                            safeSetState(() => _model.dropDownValue3 = val);
                            logFirebaseEvent(
                                'H_O_M_E_FILM_DropDown_dnn367ak_ON_FORM_W');
                            logFirebaseEvent('DropDown_navigate_to');

                            context.pushNamed(ADdActorWidget.routeName);
                          },
                          width: 300.0,
                          height: 56.0,
                          textStyle:
                              FlutterFlowTheme.of(context).bodyMedium.override(
                                    font: GoogleFonts.readexPro(
                                      fontWeight: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontWeight,
                                      fontStyle: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .fontStyle,
                                    ),
                                    color: Color(0xFFD5DDE3),
                                    letterSpacing: 0.0,
                                    fontWeight: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontWeight,
                                    fontStyle: FlutterFlowTheme.of(context)
                                        .bodyMedium
                                        .fontStyle,
                                  ),
                          hintText: 'Actors',
                          icon: Icon(
                            Icons.keyboard_arrow_down_rounded,
                            color: Colors.white,
                            size: 24.0,
                          ),
                          fillColor: Color(0xFFC40708),
                          elevation: 2.0,
                          borderColor: FlutterFlowTheme.of(context).alternate,
                          borderWidth: 2.0,
                          borderRadius: 8.0,
                          margin: EdgeInsetsDirectional.fromSTEB(
                              16.0, 4.0, 16.0, 4.0),
                          hidesUnderline: true,
                          isOverButton: true,
                          isSearchable: false,
                          isMultiSelect: false,
                        ),
                      ),
                      Padding(
                        padding:
                            EdgeInsetsDirectional.fromSTEB(0.0, 30.0, 0.0, 0.0),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(8.0),
                          child: Image.asset(
                            'assets/images/Logo.png',
                            width: 300.0,
                            height: 223.0,
                            fit: BoxFit.cover,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              Align(
                alignment: AlignmentDirectional(0.0, -0.9),
                child: Padding(
                  padding: EdgeInsetsDirectional.fromSTEB(0.0, 0.0, 0.0, 8.0),
                  child: Text(
                    'ALL you need in films',
                    style: FlutterFlowTheme.of(context).headlineMedium.override(
                          font: GoogleFonts.outfit(
                            fontWeight: FlutterFlowTheme.of(context)
                                .headlineMedium
                                .fontWeight,
                            fontStyle: FlutterFlowTheme.of(context)
                                .headlineMedium
                                .fontStyle,
                          ),
                          letterSpacing: 0.0,
                          fontWeight: FlutterFlowTheme.of(context)
                              .headlineMedium
                              .fontWeight,
                          fontStyle: FlutterFlowTheme.of(context)
                              .headlineMedium
                              .fontStyle,
                        ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
