import '/auth/firebase_auth/auth_util.dart';
import '/backend/backend.dart';
import '/flutter_flow/flutter_flow_choice_chips.dart';
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import '/flutter_flow/form_field_controller.dart';
import 'dart:ui';
import '/flutter_flow/custom_functions.dart' as functions;
import '/index.dart';
import 'package:badges/badges.dart' as badges;
import 'package:smooth_page_indicator/smooth_page_indicator.dart'
    as smooth_page_indicator;
import 'package:collection/collection.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'list_product_client_model.dart';
export 'list_product_client_model.dart';

class ListProductClientWidget extends StatefulWidget {
  const ListProductClientWidget({
    super.key,
    this.produit,
    this.listpanier,
  });

  final DocumentReference? produit;
  final ProduitRecord? listpanier;

  static String routeName = 'ListProductClient';
  static String routePath = '/listProductClient';

  @override
  State<ListProductClientWidget> createState() =>
      _ListProductClientWidgetState();
}

class _ListProductClientWidgetState extends State<ListProductClientWidget> {
  late ListProductClientModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => ListProductClientModel());

    logFirebaseEvent('screen_view',
        parameters: {'screen_name': 'ListProductClient'});
    _model.textController ??= TextEditingController();
    _model.textFieldFocusNode ??= FocusNode();
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
        body: SafeArea(
          top: true,
          child: Padding(
            padding: EdgeInsetsDirectional.fromSTEB(0.0, 0.0, 0.0, 10.0),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: [
                Column(
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    SingleChildScrollView(
                      child: Column(
                        mainAxisSize: MainAxisSize.max,
                        children: [
                          Container(
                            width: 396.0,
                            height: 307.0,
                            decoration: BoxDecoration(
                              color: FlutterFlowTheme.of(context)
                                  .secondaryBackground,
                            ),
                            child: Stack(
                              children: [
                                Align(
                                  alignment: AlignmentDirectional(0.0, 0.0),
                                  child: Container(
                                    width: double.infinity,
                                    height: 505.0,
                                    child: Stack(
                                      children: [
                                        Padding(
                                          padding:
                                              EdgeInsetsDirectional.fromSTEB(
                                                  0.0, 0.0, 0.0, 40.0),
                                          child: PageView(
                                            controller: _model
                                                    .pageViewController ??=
                                                PageController(initialPage: 0),
                                            scrollDirection: Axis.horizontal,
                                            children: [
                                              Stack(
                                                children: [
                                                  Align(
                                                    alignment:
                                                        AlignmentDirectional(
                                                            0.0, 0.0),
                                                    child: ClipRRect(
                                                      borderRadius:
                                                          BorderRadius.only(
                                                        bottomLeft:
                                                            Radius.circular(
                                                                40.0),
                                                        bottomRight:
                                                            Radius.circular(
                                                                40.0),
                                                        topLeft:
                                                            Radius.circular(
                                                                0.0),
                                                        topRight:
                                                            Radius.circular(
                                                                0.0),
                                                      ),
                                                      child: Image.network(
                                                        'https://images.unsplash.com/photo-1508557446787-73acf2380f97?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0NTYyMDF8MHwxfHNlYXJjaHw4fHxqb2tlcnxlbnwwfHx8fDE3MTUwOTIwODd8MA&ixlib=rb-4.0.3&q=80&w=1080',
                                                        width: double.infinity,
                                                        height: double.infinity,
                                                        fit: BoxFit.cover,
                                                      ),
                                                    ),
                                                  ),
                                                  Align(
                                                    alignment:
                                                        AlignmentDirectional(
                                                            0.0, 0.0),
                                                    child: Container(
                                                      width: double.infinity,
                                                      height: double.infinity,
                                                      decoration: BoxDecoration(
                                                        gradient:
                                                            LinearGradient(
                                                          colors: [
                                                            Color(0x7F14181B),
                                                            Color(0x2614181B)
                                                          ],
                                                          stops: [0.0, 1.0],
                                                          begin:
                                                              AlignmentDirectional(
                                                                  0.0, -1.0),
                                                          end:
                                                              AlignmentDirectional(
                                                                  0, 1.0),
                                                        ),
                                                      ),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              ClipRRect(
                                                borderRadius:
                                                    BorderRadius.circular(8.0),
                                                child: Image.network(
                                                  'https://images.unsplash.com/photo-1553528989-978b279600f0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0NTYyMDF8MHwxfHNlYXJjaHwxNnx8c2N1bHB0dXJlfGVufDB8fHx8MTcxNTA5MjEwOXww&ixlib=rb-4.0.3&q=80&w=1080',
                                                  width: 300.0,
                                                  height: 200.0,
                                                  fit: BoxFit.cover,
                                                ),
                                              ),
                                              ClipRRect(
                                                borderRadius:
                                                    BorderRadius.circular(8.0),
                                                child: Image.network(
                                                  'https://images.unsplash.com/photo-1536440136628-849c177e76a1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0NTYyMDF8MHwxfHNlYXJjaHwzfHxjaW5lbWF8ZW58MHx8fHwxNzE0NzczOTgxfDA&ixlib=rb-4.0.3&q=80&w=1080',
                                                  width: 300.0,
                                                  height: 200.0,
                                                  fit: BoxFit.fitWidth,
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                        Align(
                                          alignment:
                                              AlignmentDirectional(0.0, 1.0),
                                          child: Padding(
                                            padding:
                                                EdgeInsetsDirectional.fromSTEB(
                                                    16.0, 0.0, 0.0, 10.0),
                                            child: smooth_page_indicator
                                                .SmoothPageIndicator(
                                              controller:
                                                  _model.pageViewController ??=
                                                      PageController(
                                                          initialPage: 0),
                                              count: 3,
                                              axisDirection: Axis.horizontal,
                                              onDotClicked: (i) async {
                                                await _model.pageViewController!
                                                    .animateToPage(
                                                  i,
                                                  duration: Duration(
                                                      milliseconds: 500),
                                                  curve: Curves.ease,
                                                );
                                                safeSetState(() {});
                                              },
                                              effect: smooth_page_indicator
                                                  .ExpandingDotsEffect(
                                                expansionFactor: 3.0,
                                                spacing: 4.0,
                                                radius: 8.0,
                                                dotWidth: 16.0,
                                                dotHeight: 5.0,
                                                dotColor:
                                                    FlutterFlowTheme.of(context)
                                                        .accent1,
                                                activeDotColor:
                                                    Color(0xFFE1D044),
                                                paintStyle: PaintingStyle.fill,
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                Container(
                                  width: 393.0,
                                  height: 66.0,
                                  decoration: BoxDecoration(),
                                  child: Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        24.0, 0.0, 24.0, 0.0),
                                    child: Row(
                                      mainAxisSize: MainAxisSize.max,
                                      children: [
                                        FlutterFlowIconButton(
                                          borderColor: Color(0xFF63161A),
                                          borderRadius: 20.0,
                                          borderWidth: 1.0,
                                          buttonSize: 40.0,
                                          fillColor:
                                              FlutterFlowTheme.of(context)
                                                  .alternate,
                                          icon: Icon(
                                            Icons.list_sharp,
                                            color: FlutterFlowTheme.of(context)
                                                .primaryText,
                                            size: 24.0,
                                          ),
                                          onPressed: () async {
                                            logFirebaseEvent(
                                                'LIST_PRODUCT_CLIENT_list_sharp_ICN_ON_TA');
                                            logFirebaseEvent(
                                                'IconButton_navigate_to');

                                            context.pushNamed(
                                                Home1Widget.routeName);
                                          },
                                        ),
                                        badges.Badge(
                                          badgeContent: Text(
                                            '3',
                                            textAlign: TextAlign.center,
                                            style: FlutterFlowTheme.of(context)
                                                .titleSmall
                                                .override(
                                                  font: GoogleFonts.readexPro(
                                                    fontWeight:
                                                        FlutterFlowTheme.of(
                                                                context)
                                                            .titleSmall
                                                            .fontWeight,
                                                    fontStyle:
                                                        FlutterFlowTheme.of(
                                                                context)
                                                            .titleSmall
                                                            .fontStyle,
                                                  ),
                                                  color: Colors.white,
                                                  fontSize: 12.0,
                                                  letterSpacing: 0.0,
                                                  fontWeight:
                                                      FlutterFlowTheme.of(
                                                              context)
                                                          .titleSmall
                                                          .fontWeight,
                                                  fontStyle:
                                                      FlutterFlowTheme.of(
                                                              context)
                                                          .titleSmall
                                                          .fontStyle,
                                                ),
                                          ),
                                          showBadge: true,
                                          shape: badges.BadgeShape.circle,
                                          badgeColor: Color(0xFFEA251F),
                                          elevation: 3.0,
                                          padding:
                                              EdgeInsetsDirectional.fromSTEB(
                                                  8.0, 8.0, 8.0, 8.0),
                                          position:
                                              badges.BadgePosition.topEnd(),
                                          animationType:
                                              badges.BadgeAnimationType.scale,
                                          toAnimate: true,
                                          child: FlutterFlowIconButton(
                                            borderColor: Color(0xFF8D201B),
                                            borderRadius: 20.0,
                                            borderWidth: 1.0,
                                            buttonSize: 40.0,
                                            fillColor:
                                                FlutterFlowTheme.of(context)
                                                    .alternate,
                                            icon: Icon(
                                              Icons.shopping_basket,
                                              color:
                                                  FlutterFlowTheme.of(context)
                                                      .primaryText,
                                              size: 24.0,
                                            ),
                                            onPressed: () async {
                                              logFirebaseEvent(
                                                  'LIST_PRODUCT_CLIENT_shopping_basket_ICN_');
                                              logFirebaseEvent(
                                                  'IconButton_firestore_query');
                                              _model.queryCart =
                                                  await queryCartRecordOnce(
                                                queryBuilder: (cartRecord) =>
                                                    cartRecord.where(
                                                  'user_ref',
                                                  isEqualTo:
                                                      currentUserReference,
                                                ),
                                              );
                                              logFirebaseEvent(
                                                  'IconButton_navigate_to');

                                              context.pushNamed(
                                                ListPanierWidget.routeName,
                                                queryParameters: {
                                                  'finalePrice': serializeParam(
                                                    functions.sumlist(_model
                                                        .queryCart!
                                                        .map(
                                                            (e) => e.finalPrice)
                                                        .toList()
                                                        .sortedList(
                                                            keyOf: (e) =>
                                                                e.toString(),
                                                            desc: false)
                                                        .toList()),
                                                    ParamType.double,
                                                  ),
                                                }.withoutNulls,
                                              );

                                              safeSetState(() {});
                                            },
                                          ),
                                        ),
                                        Padding(
                                          padding:
                                              EdgeInsetsDirectional.fromSTEB(
                                                  200.0, 0.0, 0.0, 0.0),
                                          child: FlutterFlowIconButton(
                                            borderColor: Color(0xFF922F27),
                                            borderRadius: 20.0,
                                            borderWidth: 1.0,
                                            buttonSize: 40.0,
                                            fillColor:
                                                FlutterFlowTheme.of(context)
                                                    .alternate,
                                            icon: Icon(
                                              Icons.add,
                                              color:
                                                  FlutterFlowTheme.of(context)
                                                      .primaryText,
                                              size: 24.0,
                                            ),
                                            onPressed: () async {
                                              logFirebaseEvent(
                                                  'LIST_PRODUCT_CLIENT_PAGE_add_ICN_ON_TAP');
                                              logFirebaseEvent(
                                                  'IconButton_navigate_to');

                                              context.pushNamed(
                                                  AddProductWidget.routeName);
                                            },
                                          ),
                                        ),
                                      ].divide(SizedBox(width: 8.0)),
                                    ),
                                  ),
                                ),
                                Align(
                                  alignment: AlignmentDirectional(0.0, 0.72),
                                  child: Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        24.0, 0.0, 24.0, 24.0),
                                    child: ClipRRect(
                                      borderRadius: BorderRadius.circular(16.0),
                                      child: Container(
                                        width: double.infinity,
                                        height: 44.0,
                                        decoration: BoxDecoration(
                                          color: FlutterFlowTheme.of(context)
                                              .primaryBackground,
                                          boxShadow: [
                                            BoxShadow(
                                              blurRadius: 15.0,
                                              color: Color(0x66000000),
                                              offset: Offset(
                                                0.0,
                                                4.0,
                                              ),
                                            )
                                          ],
                                          borderRadius:
                                              BorderRadius.circular(16.0),
                                        ),
                                        child: Row(
                                          mainAxisSize: MainAxisSize.max,
                                          children: [
                                            Expanded(
                                              child: Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(
                                                        0.0, 0.0, 8.0, 0.0),
                                                child: Container(
                                                  width: 300.0,
                                                  child: TextFormField(
                                                    controller:
                                                        _model.textController,
                                                    focusNode: _model
                                                        .textFieldFocusNode,
                                                    autofocus: true,
                                                    obscureText: false,
                                                    decoration: InputDecoration(
                                                      labelText:
                                                          'Label here...',
                                                      labelStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .labelMedium
                                                              .override(
                                                                font: GoogleFonts
                                                                    .readexPro(
                                                                  fontWeight: FlutterFlowTheme.of(
                                                                          context)
                                                                      .labelMedium
                                                                      .fontWeight,
                                                                  fontStyle: FlutterFlowTheme.of(
                                                                          context)
                                                                      .labelMedium
                                                                      .fontStyle,
                                                                ),
                                                                letterSpacing:
                                                                    0.0,
                                                                fontWeight: FlutterFlowTheme.of(
                                                                        context)
                                                                    .labelMedium
                                                                    .fontWeight,
                                                                fontStyle: FlutterFlowTheme.of(
                                                                        context)
                                                                    .labelMedium
                                                                    .fontStyle,
                                                              ),
                                                      hintStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .labelMedium
                                                              .override(
                                                                font: GoogleFonts
                                                                    .readexPro(
                                                                  fontWeight: FlutterFlowTheme.of(
                                                                          context)
                                                                      .labelMedium
                                                                      .fontWeight,
                                                                  fontStyle: FlutterFlowTheme.of(
                                                                          context)
                                                                      .labelMedium
                                                                      .fontStyle,
                                                                ),
                                                                letterSpacing:
                                                                    0.0,
                                                                fontWeight: FlutterFlowTheme.of(
                                                                        context)
                                                                    .labelMedium
                                                                    .fontWeight,
                                                                fontStyle: FlutterFlowTheme.of(
                                                                        context)
                                                                    .labelMedium
                                                                    .fontStyle,
                                                              ),
                                                      enabledBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color: FlutterFlowTheme
                                                                  .of(context)
                                                              .alternate,
                                                          width: 2.0,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(16.0),
                                                      ),
                                                      focusedBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color: FlutterFlowTheme
                                                                  .of(context)
                                                              .primaryText,
                                                          width: 2.0,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(16.0),
                                                      ),
                                                      errorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color: FlutterFlowTheme
                                                                  .of(context)
                                                              .primaryText,
                                                          width: 2.0,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(16.0),
                                                      ),
                                                      focusedErrorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color: FlutterFlowTheme
                                                                  .of(context)
                                                              .primaryText,
                                                          width: 2.0,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(16.0),
                                                      ),
                                                      filled: true,
                                                      fillColor:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .primaryBackground,
                                                      prefixIcon: Icon(
                                                        Icons.search_sharp,
                                                        color:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .primaryText,
                                                        size: 24.0,
                                                      ),
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyMedium
                                                        .override(
                                                          font: GoogleFonts
                                                              .readexPro(
                                                            fontWeight:
                                                                FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyMedium
                                                                    .fontWeight,
                                                            fontStyle:
                                                                FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyMedium
                                                                    .fontStyle,
                                                          ),
                                                          letterSpacing: 0.0,
                                                          fontWeight:
                                                              FlutterFlowTheme.of(
                                                                      context)
                                                                  .bodyMedium
                                                                  .fontWeight,
                                                          fontStyle:
                                                              FlutterFlowTheme.of(
                                                                      context)
                                                                  .bodyMedium
                                                                  .fontStyle,
                                                        ),
                                                    validator: _model
                                                        .textControllerValidator
                                                        .asValidator(context),
                                                  ),
                                                ),
                                              ),
                                            ),
                                            FlutterFlowIconButton(
                                              borderColor: Colors.transparent,
                                              borderRadius: 10.0,
                                              borderWidth: 0.0,
                                              buttonSize: 40.0,
                                              fillColor:
                                                  FlutterFlowTheme.of(context)
                                                      .primaryBackground,
                                              icon: Icon(
                                                Icons.filter_alt,
                                                color: Color(0xFFCC1818),
                                                size: 24.0,
                                              ),
                                              onPressed: () {
                                                print('IconButton pressed ...');
                                              },
                                            ),
                                          ],
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                                Align(
                                  alignment: AlignmentDirectional(-0.09, -0.36),
                                  child: Container(
                                    width: 315.0,
                                    height: 41.0,
                                    decoration: BoxDecoration(),
                                    child: Row(
                                      mainAxisSize: MainAxisSize.max,
                                      children: [
                                        Padding(
                                          padding:
                                              EdgeInsetsDirectional.fromSTEB(
                                                  20.0, 0.0, 24.0, 0.0),
                                          child: Row(
                                            mainAxisSize: MainAxisSize.max,
                                            children: [
                                              RichText(
                                                textScaler:
                                                    MediaQuery.of(context)
                                                        .textScaler,
                                                text: TextSpan(
                                                  children: [
                                                    TextSpan(
                                                      text:
                                                          'Rare cinematic treasure',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .titleMedium
                                                              .override(
                                                                font: GoogleFonts
                                                                    .readexPro(
                                                                  fontWeight: FlutterFlowTheme.of(
                                                                          context)
                                                                      .titleMedium
                                                                      .fontWeight,
                                                                  fontStyle:
                                                                      FontStyle
                                                                          .italic,
                                                                ),
                                                                color: FlutterFlowTheme.of(
                                                                        context)
                                                                    .alternate,
                                                                letterSpacing:
                                                                    0.0,
                                                                fontWeight: FlutterFlowTheme.of(
                                                                        context)
                                                                    .titleMedium
                                                                    .fontWeight,
                                                                fontStyle:
                                                                    FontStyle
                                                                        .italic,
                                                              ),
                                                    ),
                                                    TextSpan(
                                                      text: ' to buy!',
                                                      style: TextStyle(
                                                        color:
                                                            Color(0xFFFFFF53),
                                                        fontSize: 18.0,
                                                      ),
                                                    )
                                                  ],
                                                  style: FlutterFlowTheme.of(
                                                          context)
                                                      .bodyMedium
                                                      .override(
                                                        font: GoogleFonts
                                                            .readexPro(
                                                          fontWeight:
                                                              FlutterFlowTheme.of(
                                                                      context)
                                                                  .bodyMedium
                                                                  .fontWeight,
                                                          fontStyle:
                                                              FlutterFlowTheme.of(
                                                                      context)
                                                                  .bodyMedium
                                                                  .fontStyle,
                                                        ),
                                                        letterSpacing: 0.0,
                                                        fontWeight:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .bodyMedium
                                                                .fontWeight,
                                                        fontStyle:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .bodyMedium
                                                                .fontStyle,
                                                      ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                    Column(
                      mainAxisSize: MainAxisSize.max,
                      children: [
                        Padding(
                          padding: EdgeInsetsDirectional.fromSTEB(
                              0.0, 5.0, 0.0, 0.0),
                          child: Row(
                            mainAxisSize: MainAxisSize.max,
                            children: [
                              Expanded(
                                child: Padding(
                                  padding: EdgeInsetsDirectional.fromSTEB(
                                      0.0, 10.0, 0.0, 0.0),
                                  child: FlutterFlowChoiceChips(
                                    options: [
                                      ChipData('All'),
                                      ChipData('Sculptures'),
                                      ChipData('Posters'),
                                      ChipData('Books'),
                                      ChipData('Painting')
                                    ],
                                    onChanged: (val) => safeSetState(() =>
                                        _model.choiceChipsValue =
                                            val?.firstOrNull),
                                    selectedChipStyle: ChipStyle(
                                      backgroundColor: Color(0xFFC91B11),
                                      textStyle: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .override(
                                            font: GoogleFonts.readexPro(
                                              fontWeight:
                                                  FlutterFlowTheme.of(context)
                                                      .bodyMedium
                                                      .fontWeight,
                                              fontStyle:
                                                  FlutterFlowTheme.of(context)
                                                      .bodyMedium
                                                      .fontStyle,
                                            ),
                                            color: FlutterFlowTheme.of(context)
                                                .primaryText,
                                            letterSpacing: 0.0,
                                            fontWeight:
                                                FlutterFlowTheme.of(context)
                                                    .bodyMedium
                                                    .fontWeight,
                                            fontStyle:
                                                FlutterFlowTheme.of(context)
                                                    .bodyMedium
                                                    .fontStyle,
                                          ),
                                      iconColor: FlutterFlowTheme.of(context)
                                          .primaryText,
                                      iconSize: 18.0,
                                      elevation: 4.0,
                                      borderRadius: BorderRadius.circular(16.0),
                                    ),
                                    unselectedChipStyle: ChipStyle(
                                      backgroundColor:
                                          FlutterFlowTheme.of(context)
                                              .alternate,
                                      textStyle: FlutterFlowTheme.of(context)
                                          .bodyMedium
                                          .override(
                                            font: GoogleFonts.readexPro(
                                              fontWeight:
                                                  FlutterFlowTheme.of(context)
                                                      .bodyMedium
                                                      .fontWeight,
                                              fontStyle:
                                                  FlutterFlowTheme.of(context)
                                                      .bodyMedium
                                                      .fontStyle,
                                            ),
                                            color: FlutterFlowTheme.of(context)
                                                .secondaryText,
                                            letterSpacing: 0.0,
                                            fontWeight:
                                                FlutterFlowTheme.of(context)
                                                    .bodyMedium
                                                    .fontWeight,
                                            fontStyle:
                                                FlutterFlowTheme.of(context)
                                                    .bodyMedium
                                                    .fontStyle,
                                          ),
                                      iconColor: FlutterFlowTheme.of(context)
                                          .secondaryText,
                                      iconSize: 18.0,
                                      elevation: 0.0,
                                      borderRadius: BorderRadius.circular(16.0),
                                    ),
                                    chipSpacing: 5.0,
                                    rowSpacing: 12.0,
                                    multiselect: false,
                                    alignment: WrapAlignment.start,
                                    controller:
                                        _model.choiceChipsValueController ??=
                                            FormFieldController<List<String>>(
                                      [],
                                    ),
                                    wrapped: true,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                Column(
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Row(
                        mainAxisSize: MainAxisSize.max,
                        children: [
                          Padding(
                            padding: EdgeInsetsDirectional.fromSTEB(
                                0.0, 10.0, 0.0, 0.0),
                            child: Container(
                              width: 360.0,
                              height: 439.0,
                              decoration: BoxDecoration(),
                              child: StreamBuilder<List<ProduitRecord>>(
                                stream: queryProduitRecord(),
                                builder: (context, snapshot) {
                                  // Customize what your widget looks like when it's loading.
                                  if (!snapshot.hasData) {
                                    return Center(
                                      child: SizedBox(
                                        width: 50.0,
                                        height: 50.0,
                                        child: CircularProgressIndicator(
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                            FlutterFlowTheme.of(context)
                                                .primary,
                                          ),
                                        ),
                                      ),
                                    );
                                  }
                                  List<ProduitRecord>
                                      listViewProduitRecordList =
                                      snapshot.data!;

                                  return ListView.separated(
                                    padding: EdgeInsets.zero,
                                    scrollDirection: Axis.vertical,
                                    itemCount: listViewProduitRecordList.length,
                                    separatorBuilder: (_, __) =>
                                        SizedBox(height: 10.0),
                                    itemBuilder: (context, listViewIndex) {
                                      final listViewProduitRecord =
                                          listViewProduitRecordList[
                                              listViewIndex];
                                      return InkWell(
                                        splashColor: Colors.transparent,
                                        focusColor: Colors.transparent,
                                        hoverColor: Colors.transparent,
                                        highlightColor: Colors.transparent,
                                        onTap: () async {
                                          logFirebaseEvent(
                                              'LIST_PRODUCT_CLIENT_Container_ftwhcwx5_O');
                                          logFirebaseEvent(
                                              'Container_navigate_to');

                                          context.pushNamed(
                                            DetailProduitWidget.routeName,
                                            queryParameters: {
                                              'produit': serializeParam(
                                                listViewProduitRecord,
                                                ParamType.Document,
                                              ),
                                            }.withoutNulls,
                                            extra: <String, dynamic>{
                                              'produit': listViewProduitRecord,
                                            },
                                          );
                                        },
                                        child: Container(
                                          width: 100.0,
                                          height: 268.0,
                                          decoration: BoxDecoration(
                                            color: FlutterFlowTheme.of(context)
                                                .secondaryBackground,
                                          ),
                                          child: Column(
                                            mainAxisSize: MainAxisSize.max,
                                            children: [
                                              Container(
                                                width: 357.0,
                                                height: 188.0,
                                                decoration: BoxDecoration(
                                                  color: FlutterFlowTheme.of(
                                                          context)
                                                      .secondaryBackground,
                                                ),
                                                child: Stack(
                                                  children: [
                                                    Align(
                                                      alignment:
                                                          AlignmentDirectional(
                                                              0.1, 0.0),
                                                      child: ClipRRect(
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(8.0),
                                                        child: Image.network(
                                                          listViewProduitRecord
                                                              .image,
                                                          width: 320.0,
                                                          height: 280.0,
                                                          fit: BoxFit.contain,
                                                        ),
                                                      ),
                                                    ),
                                                    ClipRRect(
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              2.0),
                                                      child: Container(
                                                        width: 55.0,
                                                        height: 31.0,
                                                        decoration:
                                                            BoxDecoration(
                                                          color:
                                                              Color(0x7F191D31),
                                                          borderRadius:
                                                              BorderRadius
                                                                  .circular(
                                                                      2.0),
                                                        ),
                                                        child: ClipRRect(
                                                          borderRadius:
                                                              BorderRadius
                                                                  .circular(
                                                                      8.0),
                                                          child: BackdropFilter(
                                                            filter: ImageFilter
                                                                .blur(
                                                              sigmaX: 2.0,
                                                              sigmaY: 2.0,
                                                            ),
                                                            child: Row(
                                                              mainAxisSize:
                                                                  MainAxisSize
                                                                      .max,
                                                              children: [
                                                                Icon(
                                                                  Icons.star,
                                                                  color: Color(
                                                                      0xFFB0AD3F),
                                                                  size: 24.0,
                                                                ),
                                                                Text(
                                                                  '4.5',
                                                                  style: FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyMedium
                                                                      .override(
                                                                        font: GoogleFonts
                                                                            .readexPro(
                                                                          fontWeight:
                                                                              FontWeight.w600,
                                                                          fontStyle: FlutterFlowTheme.of(context)
                                                                              .bodyMedium
                                                                              .fontStyle,
                                                                        ),
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .alternate,
                                                                        letterSpacing:
                                                                            0.0,
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                        fontStyle: FlutterFlowTheme.of(context)
                                                                            .bodyMedium
                                                                            .fontStyle,
                                                                      ),
                                                                ),
                                                              ].divide(SizedBox(
                                                                  width: 5.0)),
                                                            ),
                                                          ),
                                                        ),
                                                      ),
                                                    ),
                                                    Align(
                                                      alignment:
                                                          AlignmentDirectional(
                                                              73.22, 0.58),
                                                      child: Padding(
                                                        padding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    3.0,
                                                                    55.0,
                                                                    0.0,
                                                                    0.0),
                                                        child: Row(
                                                          mainAxisSize:
                                                              MainAxisSize.max,
                                                          children: [
                                                            InkWell(
                                                              splashColor: Colors
                                                                  .transparent,
                                                              focusColor: Colors
                                                                  .transparent,
                                                              hoverColor: Colors
                                                                  .transparent,
                                                              highlightColor:
                                                                  Colors
                                                                      .transparent,
                                                              onTap: () async {
                                                                logFirebaseEvent(
                                                                    'LIST_PRODUCT_CLIENT_Text_88sy4enj_ON_TAP');
                                                                logFirebaseEvent(
                                                                    'Text_navigate_to');

                                                                context
                                                                    .pushNamed(
                                                                  DetailProduitWidget
                                                                      .routeName,
                                                                  queryParameters:
                                                                      {
                                                                    'produit':
                                                                        serializeParam(
                                                                      listViewProduitRecord,
                                                                      ParamType
                                                                          .Document,
                                                                    ),
                                                                  }.withoutNulls,
                                                                  extra: <String,
                                                                      dynamic>{
                                                                    'produit':
                                                                        listViewProduitRecord,
                                                                  },
                                                                );
                                                              },
                                                              child: Text(
                                                                'Details',
                                                                style: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyMedium
                                                                    .override(
                                                                      font: GoogleFonts
                                                                          .readexPro(
                                                                        fontWeight: FlutterFlowTheme.of(context)
                                                                            .bodyMedium
                                                                            .fontWeight,
                                                                        fontStyle: FlutterFlowTheme.of(context)
                                                                            .bodyMedium
                                                                            .fontStyle,
                                                                      ),
                                                                      color: Color(
                                                                          0xFF535C62),
                                                                      letterSpacing:
                                                                          0.0,
                                                                      fontWeight: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyMedium
                                                                          .fontWeight,
                                                                      fontStyle: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyMedium
                                                                          .fontStyle,
                                                                      decoration:
                                                                          TextDecoration
                                                                              .underline,
                                                                    ),
                                                              ),
                                                            ),
                                                          ],
                                                        ),
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                              ),
                                              Container(
                                                width: 363.0,
                                                height: 77.0,
                                                decoration: BoxDecoration(
                                                  color: FlutterFlowTheme.of(
                                                          context)
                                                      .secondaryBackground,
                                                ),
                                                child: Stack(
                                                  children: [
                                                    Align(
                                                      alignment:
                                                          AlignmentDirectional(
                                                              0.0, -1.0),
                                                      child: Padding(
                                                        padding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    3.0,
                                                                    3.0,
                                                                    0.0,
                                                                    0.0),
                                                        child: Row(
                                                          mainAxisSize:
                                                              MainAxisSize.max,
                                                          children: [
                                                            Text(
                                                              listViewProduitRecord
                                                                  .nom,
                                                              style: FlutterFlowTheme
                                                                      .of(context)
                                                                  .bodyLarge
                                                                  .override(
                                                                    font: GoogleFonts
                                                                        .readexPro(
                                                                      fontWeight: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyLarge
                                                                          .fontWeight,
                                                                      fontStyle: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyLarge
                                                                          .fontStyle,
                                                                    ),
                                                                    letterSpacing:
                                                                        0.0,
                                                                    fontWeight: FlutterFlowTheme.of(
                                                                            context)
                                                                        .bodyLarge
                                                                        .fontWeight,
                                                                    fontStyle: FlutterFlowTheme.of(
                                                                            context)
                                                                        .bodyLarge
                                                                        .fontStyle,
                                                                  ),
                                                            ),
                                                          ],
                                                        ),
                                                      ),
                                                    ),
                                                    Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  3.0,
                                                                  32.0,
                                                                  0.0,
                                                                  0.0),
                                                      child: Row(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        children: [
                                                          RichText(
                                                            textScaler:
                                                                MediaQuery.of(
                                                                        context)
                                                                    .textScaler,
                                                            text: TextSpan(
                                                              children: [
                                                                TextSpan(
                                                                  text: listViewProduitRecord
                                                                      .prix
                                                                      .toString(),
                                                                  style: FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyMedium
                                                                      .override(
                                                                        font: GoogleFonts
                                                                            .readexPro(
                                                                          fontWeight:
                                                                              FontWeight.bold,
                                                                          fontStyle: FlutterFlowTheme.of(context)
                                                                              .bodyMedium
                                                                              .fontStyle,
                                                                        ),
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        letterSpacing:
                                                                            0.0,
                                                                        fontWeight:
                                                                            FontWeight.bold,
                                                                        fontStyle: FlutterFlowTheme.of(context)
                                                                            .bodyMedium
                                                                            .fontStyle,
                                                                      ),
                                                                ),
                                                                TextSpan(
                                                                  text: 'DT',
                                                                  style:
                                                                      TextStyle(),
                                                                )
                                                              ],
                                                              style: FlutterFlowTheme
                                                                      .of(context)
                                                                  .bodyMedium
                                                                  .override(
                                                                    font: GoogleFonts
                                                                        .readexPro(
                                                                      fontWeight: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyMedium
                                                                          .fontWeight,
                                                                      fontStyle: FlutterFlowTheme.of(
                                                                              context)
                                                                          .bodyMedium
                                                                          .fontStyle,
                                                                    ),
                                                                    letterSpacing:
                                                                        0.0,
                                                                    fontWeight: FlutterFlowTheme.of(
                                                                            context)
                                                                        .bodyMedium
                                                                        .fontWeight,
                                                                    fontStyle: FlutterFlowTheme.of(
                                                                            context)
                                                                        .bodyMedium
                                                                        .fontStyle,
                                                                  ),
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                    Align(
                                                      alignment:
                                                          AlignmentDirectional(
                                                              0.86, 0.0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        children: [],
                                                      ),
                                                    ),
                                                    Align(
                                                      alignment:
                                                          AlignmentDirectional(
                                                              0.98, 6.82),
                                                      child: Container(
                                                        width: 122.0,
                                                        height: 100.0,
                                                        decoration:
                                                            BoxDecoration(
                                                          color: FlutterFlowTheme
                                                                  .of(context)
                                                              .secondaryBackground,
                                                        ),
                                                        child: Stack(
                                                          children: [
                                                            Align(
                                                              alignment:
                                                                  AlignmentDirectional(
                                                                      0.0,
                                                                      -1.0),
                                                              child: Row(
                                                                mainAxisSize:
                                                                    MainAxisSize
                                                                        .max,
                                                                children: [
                                                                  Padding(
                                                                    padding: EdgeInsetsDirectional
                                                                        .fromSTEB(
                                                                            70.0,
                                                                            0.0,
                                                                            0.0,
                                                                            0.0),
                                                                    child: Icon(
                                                                      Icons
                                                                          .favorite_border,
                                                                      color: Color(
                                                                          0xFFC32417),
                                                                      size:
                                                                          24.0,
                                                                    ),
                                                                  ),
                                                                ],
                                                              ),
                                                            ),
                                                            Padding(
                                                              padding:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          0.0,
                                                                          50.0,
                                                                          0.0,
                                                                          0.0),
                                                              child: Row(
                                                                mainAxisSize:
                                                                    MainAxisSize
                                                                        .max,
                                                                children: [
                                                                  FFButtonWidget(
                                                                    onPressed:
                                                                        () async {
                                                                      logFirebaseEvent(
                                                                          'LIST_PRODUCT_CLIENT_BY_NOW_BTN_ON_TAP');
                                                                      logFirebaseEvent(
                                                                          'Button_backend_call');

                                                                      var cartRecordReference = CartRecord
                                                                          .collection
                                                                          .doc();
                                                                      await cartRecordReference
                                                                          .set(
                                                                              createCartRecordData(
                                                                        idPanier:
                                                                            listViewProduitRecord.id,
                                                                        productRef:
                                                                            listViewProduitRecord.reference,
                                                                        price: listViewProduitRecord
                                                                            .prix
                                                                            .toDouble(),
                                                                        finalPrice: functions.quantityPrice(
                                                                            listViewProduitRecord.prix.toDouble(),
                                                                            1.0),
                                                                        quantity:
                                                                            1,
                                                                        userRef:
                                                                            currentUserReference,
                                                                      ));
                                                                      _model.cardcreate = CartRecord.getDocumentFromData(
                                                                          createCartRecordData(
                                                                            idPanier:
                                                                                listViewProduitRecord.id,
                                                                            productRef:
                                                                                listViewProduitRecord.reference,
                                                                            price:
                                                                                listViewProduitRecord.prix.toDouble(),
                                                                            finalPrice:
                                                                                functions.quantityPrice(listViewProduitRecord.prix.toDouble(), 1.0),
                                                                            quantity:
                                                                                1,
                                                                            userRef:
                                                                                currentUserReference,
                                                                          ),
                                                                          cartRecordReference);
                                                                      logFirebaseEvent(
                                                                          'Button_show_snack_bar');
                                                                      ScaffoldMessenger.of(
                                                                              context)
                                                                          .showSnackBar(
                                                                        SnackBar(
                                                                          content:
                                                                              Text(
                                                                            'Product Added',
                                                                            style: FlutterFlowTheme.of(context).titleSmall.override(
                                                                                  font: GoogleFonts.readexPro(
                                                                                    fontWeight: FlutterFlowTheme.of(context).titleSmall.fontWeight,
                                                                                    fontStyle: FlutterFlowTheme.of(context).titleSmall.fontStyle,
                                                                                  ),
                                                                                  color: FlutterFlowTheme.of(context).primaryText,
                                                                                  letterSpacing: 0.0,
                                                                                  fontWeight: FlutterFlowTheme.of(context).titleSmall.fontWeight,
                                                                                  fontStyle: FlutterFlowTheme.of(context).titleSmall.fontStyle,
                                                                                ),
                                                                          ),
                                                                          duration:
                                                                              Duration(milliseconds: 4000),
                                                                          backgroundColor:
                                                                              Color(0xFF960B0B),
                                                                        ),
                                                                      );

                                                                      safeSetState(
                                                                          () {});
                                                                    },
                                                                    text:
                                                                        'By Now',
                                                                    options:
                                                                        FFButtonOptions(
                                                                      height:
                                                                          40.0,
                                                                      padding: EdgeInsetsDirectional.fromSTEB(
                                                                          24.0,
                                                                          0.0,
                                                                          24.0,
                                                                          0.0),
                                                                      iconPadding: EdgeInsetsDirectional.fromSTEB(
                                                                          0.0,
                                                                          0.0,
                                                                          0.0,
                                                                          0.0),
                                                                      color: Color(
                                                                          0xFF9E0909),
                                                                      textStyle: FlutterFlowTheme.of(
                                                                              context)
                                                                          .titleSmall
                                                                          .override(
                                                                            font:
                                                                                GoogleFonts.readexPro(
                                                                              fontWeight: FlutterFlowTheme.of(context).titleSmall.fontWeight,
                                                                              fontStyle: FlutterFlowTheme.of(context).titleSmall.fontStyle,
                                                                            ),
                                                                            letterSpacing:
                                                                                0.0,
                                                                            fontWeight:
                                                                                FlutterFlowTheme.of(context).titleSmall.fontWeight,
                                                                            fontStyle:
                                                                                FlutterFlowTheme.of(context).titleSmall.fontStyle,
                                                                          ),
                                                                      elevation:
                                                                          3.0,
                                                                      borderSide:
                                                                          BorderSide(
                                                                        color: Colors
                                                                            .transparent,
                                                                        width:
                                                                            1.0,
                                                                      ),
                                                                      borderRadius:
                                                                          BorderRadius.circular(
                                                                              8.0),
                                                                    ),
                                                                  ),
                                                                ],
                                                              ),
                                                            ),
                                                          ],
                                                        ),
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      );
                                    },
                                  );
                                },
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
