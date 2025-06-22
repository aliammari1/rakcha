import 'dart:async';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '/backend/backend.dart';

import '/auth/base_auth_user_provider.dart';

import '/main.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';

import '/index.dart';

export 'package:go_router/go_router.dart';
export 'serialization_util.dart';

const kTransitionInfoKey = '__transition_info__';

GlobalKey<NavigatorState> appNavigatorKey = GlobalKey<NavigatorState>();

class AppStateNotifier extends ChangeNotifier {
  AppStateNotifier._();

  static AppStateNotifier? _instance;
  static AppStateNotifier get instance => _instance ??= AppStateNotifier._();

  BaseAuthUser? initialUser;
  BaseAuthUser? user;
  bool showSplashImage = true;
  String? _redirectLocation;

  /// Determines whether the app will refresh and build again when a sign
  /// in or sign out happens. This is useful when the app is launched or
  /// on an unexpected logout. However, this must be turned off when we
  /// intend to sign in/out and then navigate or perform any actions after.
  /// Otherwise, this will trigger a refresh and interrupt the action(s).
  bool notifyOnAuthChange = true;

  bool get loading => user == null || showSplashImage;
  bool get loggedIn => user?.loggedIn ?? false;
  bool get initiallyLoggedIn => initialUser?.loggedIn ?? false;
  bool get shouldRedirect => loggedIn && _redirectLocation != null;

  String getRedirectLocation() => _redirectLocation!;
  bool hasRedirect() => _redirectLocation != null;
  void setRedirectLocationIfUnset(String loc) => _redirectLocation ??= loc;
  void clearRedirectLocation() => _redirectLocation = null;

  /// Mark as not needing to notify on a sign in / out when we intend
  /// to perform subsequent actions (such as navigation) afterwards.
  void updateNotifyOnAuthChange(bool notify) => notifyOnAuthChange = notify;

  void update(BaseAuthUser newUser) {
    final shouldUpdate =
        user?.uid == null || newUser.uid == null || user?.uid != newUser.uid;
    initialUser ??= newUser;
    user = newUser;
    // Refresh the app on auth change unless explicitly marked otherwise.
    // No need to update unless the user has changed.
    if (notifyOnAuthChange && shouldUpdate) {
      notifyListeners();
    }
    // Once again mark the notifier as needing to update on auth change
    // (in order to catch sign in / out events).
    updateNotifyOnAuthChange(true);
  }

  void stopShowingSplashImage() {
    showSplashImage = false;
    notifyListeners();
  }
}

GoRouter createRouter(AppStateNotifier appStateNotifier) => GoRouter(
      initialLocation: '/',
      debugLogDiagnostics: true,
      refreshListenable: appStateNotifier,
      navigatorKey: appNavigatorKey,
      errorBuilder: (context, state) =>
          appStateNotifier.loggedIn ? NavBarPage() : Auth3Widget(),
      routes: [
        FFRoute(
          name: '_initialize',
          path: '/',
          builder: (context, _) =>
              appStateNotifier.loggedIn ? NavBarPage() : Auth3Widget(),
        ),
        FFRoute(
          name: Auth3Widget.routeName,
          path: Auth3Widget.routePath,
          builder: (context, params) => Auth3Widget(),
        ),
        FFRoute(
          name: ForgotPassword01Widget.routeName,
          path: ForgotPassword01Widget.routePath,
          builder: (context, params) => ForgotPassword01Widget(),
        ),
        FFRoute(
          name: AddcategoryWidget.routeName,
          path: AddcategoryWidget.routePath,
          builder: (context, params) => AddcategoryWidget(),
        ),
        FFRoute(
          name: ReadcatgegorfilmWidget.routeName,
          path: ReadcatgegorfilmWidget.routePath,
          builder: (context, params) => ReadcatgegorfilmWidget(),
        ),
        FFRoute(
          name: UpdatecategorysWidget.routeName,
          path: UpdatecategorysWidget.routePath,
          builder: (context, params) => UpdatecategorysWidget(
            categfilmref: params.getParam(
              'categfilmref',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['categoryFilm'],
            ),
          ),
        ),
        FFRoute(
          name: ADdActorWidget.routeName,
          path: ADdActorWidget.routePath,
          builder: (context, params) => ADdActorWidget(),
        ),
        FFRoute(
          name: AddFilmWidget.routeName,
          path: AddFilmWidget.routePath,
          builder: (context, params) => AddFilmWidget(),
        ),
        FFRoute(
          name: ListofFilmsWidget.routeName,
          path: ListofFilmsWidget.routePath,
          builder: (context, params) => ListofFilmsWidget(),
        ),
        FFRoute(
          name: ProfileWidget.routeName,
          path: ProfileWidget.routePath,
          builder: (context, params) => params.isEmpty
              ? NavBarPage(initialPage: 'profile')
              : ProfileWidget(),
        ),
        FFRoute(
          name: EditWidget.routeName,
          path: EditWidget.routePath,
          builder: (context, params) => EditWidget(),
        ),
        FFRoute(
          name: ReadActorsWidget.routeName,
          path: ReadActorsWidget.routePath,
          builder: (context, params) => ReadActorsWidget(),
        ),
        FFRoute(
          name: FILMdetailsWidget.routeName,
          path: FILMdetailsWidget.routePath,
          asyncParams: {
            'filmd': getDoc(['Film'], FilmRecord.fromSnapshot),
          },
          builder: (context, params) => FILMdetailsWidget(
            filmd: params.getParam(
              'filmd',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: UpdateActorWidget.routeName,
          path: UpdateActorWidget.routePath,
          builder: (context, params) => UpdateActorWidget(
            updateActorref: params.getParam(
              'updateActorref',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['ActorFilm'],
            ),
          ),
        ),
        FFRoute(
          name: UpdateFilmWidget.routeName,
          path: UpdateFilmWidget.routePath,
          builder: (context, params) => UpdateFilmWidget(
            filmIdupd: params.getParam(
              'filmIdupd',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['Film'],
            ),
          ),
        ),
        FFRoute(
          name: ChatbotIAWidget.routeName,
          path: ChatbotIAWidget.routePath,
          builder: (context, params) => params.isEmpty
              ? NavBarPage(initialPage: 'chatbotIA')
              : ChatbotIAWidget(),
        ),
        FFRoute(
          name: AddCinemaPageWidget.routeName,
          path: AddCinemaPageWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
          },
          builder: (context, params) => AddCinemaPageWidget(
            cinema: params.getParam(
              'cinema',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: AddRoomPageWidget.routeName,
          path: AddRoomPageWidget.routePath,
          builder: (context, params) => AddRoomPageWidget(),
        ),
        FFRoute(
          name: PlanningInterfaceWidget.routeName,
          path: PlanningInterfaceWidget.routePath,
          builder: (context, params) => PlanningInterfaceWidget(),
        ),
        FFRoute(
          name: EditCinemaWidget.routeName,
          path: EditCinemaWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
          },
          builder: (context, params) => EditCinemaWidget(
            cinema: params.getParam(
              'cinema',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: CinemaDetailsWidget.routeName,
          path: CinemaDetailsWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
          },
          builder: (context, params) => CinemaDetailsWidget(
            cinema: params.getParam(
              'cinema',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: AddCategoryProductWidget.routeName,
          path: AddCategoryProductWidget.routePath,
          builder: (context, params) => AddCategoryProductWidget(
            produit: params.getParam(
              'produit',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['produit'],
            ),
          ),
        ),
        FFRoute(
          name: AddProductWidget.routeName,
          path: AddProductWidget.routePath,
          builder: (context, params) => AddProductWidget(
            produit: params.getParam(
              'produit',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['produit'],
            ),
          ),
        ),
        FFRoute(
          name: AdminCategoryListWidget.routeName,
          path: AdminCategoryListWidget.routePath,
          builder: (context, params) => AdminCategoryListWidget(),
        ),
        FFRoute(
          name: AdminProductListWidget.routeName,
          path: AdminProductListWidget.routePath,
          builder: (context, params) => AdminProductListWidget(),
        ),
        FFRoute(
          name: ListProductClientWidget.routeName,
          path: ListProductClientWidget.routePath,
          asyncParams: {
            'listpanier': getDoc(['produit'], ProduitRecord.fromSnapshot),
          },
          builder: (context, params) => params.isEmpty
              ? NavBarPage(initialPage: 'ListProductClient')
              : ListProductClientWidget(
                  produit: params.getParam(
                    'produit',
                    ParamType.DocumentReference,
                    isList: false,
                    collectionNamePath: ['produit'],
                  ),
                  listpanier: params.getParam(
                    'listpanier',
                    ParamType.Document,
                  ),
                ),
        ),
        FFRoute(
          name: EditeCategoryProductWidget.routeName,
          path: EditeCategoryProductWidget.routePath,
          asyncParams: {
            'categoryProduct':
                getDoc(['CategoryProduct'], CategoryProductRecord.fromSnapshot),
          },
          builder: (context, params) => EditeCategoryProductWidget(
            categoryProduct: params.getParam(
              'categoryProduct',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: EditeProductWidget.routeName,
          path: EditeProductWidget.routePath,
          asyncParams: {
            'produit': getDoc(['produit'], ProduitRecord.fromSnapshot),
          },
          builder: (context, params) => EditeProductWidget(
            produit: params.getParam(
              'produit',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: ListPanierWidget.routeName,
          path: ListPanierWidget.routePath,
          asyncParams: {
            'produit': getDoc(['produit'], ProduitRecord.fromSnapshot),
          },
          builder: (context, params) => ListPanierWidget(
            produit: params.getParam(
              'produit',
              ParamType.Document,
            ),
            finalePrice: params.getParam(
              'finalePrice',
              ParamType.double,
            ),
          ),
        ),
        FFRoute(
          name: DetailProduitWidget.routeName,
          path: DetailProduitWidget.routePath,
          asyncParams: {
            'produit': getDoc(['produit'], ProduitRecord.fromSnapshot),
          },
          builder: (context, params) => DetailProduitWidget(
            produit: params.getParam(
              'produit',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: CinemaListUserWidget.routeName,
          path: CinemaListUserWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
          },
          builder: (context, params) => params.isEmpty
              ? NavBarPage(initialPage: 'CinemaListUser')
              : CinemaListUserWidget(
                  cinema: params.getParam(
                    'cinema',
                    ParamType.Document,
                  ),
                ),
        ),
        FFRoute(
          name: EditRoomWidget.routeName,
          path: EditRoomWidget.routePath,
          asyncParams: {
            'salle': getDoc(['salle'], SalleRecord.fromSnapshot),
          },
          builder: (context, params) => EditRoomWidget(
            salle: params.getParam(
              'salle',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: ListCinemaResponsableWidget.routeName,
          path: ListCinemaResponsableWidget.routePath,
          builder: (context, params) => params.isEmpty
              ? NavBarPage(initialPage: 'LIST_CINEMA_RESPONSABLE')
              : ListCinemaResponsableWidget(),
        ),
        FFRoute(
          name: AddSeancePageWidget.routeName,
          path: AddSeancePageWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
          },
          builder: (context, params) => AddSeancePageWidget(
            cinema: params.getParam(
              'cinema',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: HOMEFilmWidget.routeName,
          path: HOMEFilmWidget.routePath,
          builder: (context, params) => HOMEFilmWidget(),
        ),
        FFRoute(
          name: FeedbackajoutWidget.routeName,
          path: FeedbackajoutWidget.routePath,
          builder: (context, params) => FeedbackajoutWidget(),
        ),
        FFRoute(
          name: ReviewaffichageWidget.routeName,
          path: ReviewaffichageWidget.routePath,
          builder: (context, params) => ReviewaffichageWidget(),
        ),
        FFRoute(
          name: FeedbackupdateWidget.routeName,
          path: FeedbackupdateWidget.routePath,
          asyncParams: {
            'review': getDoc(['review'], ReviewRecord.fromSnapshot),
          },
          builder: (context, params) => FeedbackupdateWidget(
            review: params.getParam(
              'review',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: SerieaffichageWidget.routeName,
          path: SerieaffichageWidget.routePath,
          builder: (context, params) => SerieaffichageWidget(),
        ),
        FFRoute(
          name: UpdateseriesWidget.routeName,
          path: UpdateseriesWidget.routePath,
          builder: (context, params) => UpdateseriesWidget(
            update: params.getParam(
              'update',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['serie'],
            ),
          ),
        ),
        FFRoute(
          name: AjoutSeriesWidget.routeName,
          path: AjoutSeriesWidget.routePath,
          builder: (context, params) => AjoutSeriesWidget(),
        ),
        FFRoute(
          name: HomealllWidget.routeName,
          path: HomealllWidget.routePath,
          builder: (context, params) => HomealllWidget(),
        ),
        FFRoute(
          name: EditSeanceWidget.routeName,
          path: EditSeanceWidget.routePath,
          asyncParams: {
            'cinema': getDoc(['cinema'], CinemaRecord.fromSnapshot),
            'seance': getDoc(['Seance'], SeanceRecord.fromSnapshot),
          },
          builder: (context, params) => EditSeanceWidget(
            cinema: params.getParam(
              'cinema',
              ParamType.Document,
            ),
            seance: params.getParam(
              'seance',
              ParamType.Document,
            ),
          ),
        ),
        FFRoute(
          name: Home1Widget.routeName,
          path: Home1Widget.routePath,
          asyncParams: {
            'listpanier': getDoc(['cart'], CartRecord.fromSnapshot),
          },
          builder: (context, params) => Home1Widget(
            produit: params.getParam(
              'produit',
              ParamType.DocumentReference,
              isList: false,
              collectionNamePath: ['produit'],
            ),
            listpanier: params.getParam(
              'listpanier',
              ParamType.Document,
            ),
          ),
        )
      ].map((r) => r.toRoute(appStateNotifier)).toList(),
    );

extension NavParamExtensions on Map<String, String?> {
  Map<String, String> get withoutNulls => Map.fromEntries(
        entries
            .where((e) => e.value != null)
            .map((e) => MapEntry(e.key, e.value!)),
      );
}

extension NavigationExtensions on BuildContext {
  void goNamedAuth(
    String name,
    bool mounted, {
    Map<String, String> pathParameters = const <String, String>{},
    Map<String, String> queryParameters = const <String, String>{},
    Object? extra,
    bool ignoreRedirect = false,
  }) =>
      !mounted || GoRouter.of(this).shouldRedirect(ignoreRedirect)
          ? null
          : goNamed(
              name,
              pathParameters: pathParameters,
              queryParameters: queryParameters,
              extra: extra,
            );

  void pushNamedAuth(
    String name,
    bool mounted, {
    Map<String, String> pathParameters = const <String, String>{},
    Map<String, String> queryParameters = const <String, String>{},
    Object? extra,
    bool ignoreRedirect = false,
  }) =>
      !mounted || GoRouter.of(this).shouldRedirect(ignoreRedirect)
          ? null
          : pushNamed(
              name,
              pathParameters: pathParameters,
              queryParameters: queryParameters,
              extra: extra,
            );

  void safePop() {
    // If there is only one route on the stack, navigate to the initial
    // page instead of popping.
    if (canPop()) {
      pop();
    } else {
      go('/');
    }
  }
}

extension GoRouterExtensions on GoRouter {
  AppStateNotifier get appState => AppStateNotifier.instance;
  void prepareAuthEvent([bool ignoreRedirect = false]) =>
      appState.hasRedirect() && !ignoreRedirect
          ? null
          : appState.updateNotifyOnAuthChange(false);
  bool shouldRedirect(bool ignoreRedirect) =>
      !ignoreRedirect && appState.hasRedirect();
  void clearRedirectLocation() => appState.clearRedirectLocation();
  void setRedirectLocationIfUnset(String location) =>
      appState.updateNotifyOnAuthChange(false);
}

extension _GoRouterStateExtensions on GoRouterState {
  Map<String, dynamic> get extraMap =>
      extra != null ? extra as Map<String, dynamic> : {};
  Map<String, dynamic> get allParams => <String, dynamic>{}
    ..addAll(pathParameters)
    ..addAll(uri.queryParameters)
    ..addAll(extraMap);
  TransitionInfo get transitionInfo => extraMap.containsKey(kTransitionInfoKey)
      ? extraMap[kTransitionInfoKey] as TransitionInfo
      : TransitionInfo.appDefault();
}

class FFParameters {
  FFParameters(this.state, [this.asyncParams = const {}]);

  final GoRouterState state;
  final Map<String, Future<dynamic> Function(String)> asyncParams;

  Map<String, dynamic> futureParamValues = {};

  // Parameters are empty if the params map is empty or if the only parameter
  // present is the special extra parameter reserved for the transition info.
  bool get isEmpty =>
      state.allParams.isEmpty ||
      (state.allParams.length == 1 &&
          state.extraMap.containsKey(kTransitionInfoKey));
  bool isAsyncParam(MapEntry<String, dynamic> param) =>
      asyncParams.containsKey(param.key) && param.value is String;
  bool get hasFutures => state.allParams.entries.any(isAsyncParam);
  Future<bool> completeFutures() => Future.wait(
        state.allParams.entries.where(isAsyncParam).map(
          (param) async {
            final doc = await asyncParams[param.key]!(param.value)
                .onError((_, __) => null);
            if (doc != null) {
              futureParamValues[param.key] = doc;
              return true;
            }
            return false;
          },
        ),
      ).onError((_, __) => [false]).then((v) => v.every((e) => e));

  dynamic getParam<T>(
    String paramName,
    ParamType type, {
    bool isList = false,
    List<String>? collectionNamePath,
  }) {
    if (futureParamValues.containsKey(paramName)) {
      return futureParamValues[paramName];
    }
    if (!state.allParams.containsKey(paramName)) {
      return null;
    }
    final param = state.allParams[paramName];
    // Got parameter from `extras`, so just directly return it.
    if (param is! String) {
      return param;
    }
    // Return serialized value.
    return deserializeParam<T>(
      param,
      type,
      isList,
      collectionNamePath: collectionNamePath,
    );
  }
}

class FFRoute {
  const FFRoute({
    required this.name,
    required this.path,
    required this.builder,
    this.requireAuth = false,
    this.asyncParams = const {},
    this.routes = const [],
  });

  final String name;
  final String path;
  final bool requireAuth;
  final Map<String, Future<dynamic> Function(String)> asyncParams;
  final Widget Function(BuildContext, FFParameters) builder;
  final List<GoRoute> routes;

  GoRoute toRoute(AppStateNotifier appStateNotifier) => GoRoute(
        name: name,
        path: path,
        redirect: (context, state) {
          if (appStateNotifier.shouldRedirect) {
            final redirectLocation = appStateNotifier.getRedirectLocation();
            appStateNotifier.clearRedirectLocation();
            return redirectLocation;
          }

          if (requireAuth && !appStateNotifier.loggedIn) {
            appStateNotifier.setRedirectLocationIfUnset(state.uri.toString());
            return '/auth3';
          }
          return null;
        },
        pageBuilder: (context, state) {
          fixStatusBarOniOS16AndBelow(context);
          final ffParams = FFParameters(state, asyncParams);
          final page = ffParams.hasFutures
              ? FutureBuilder(
                  future: ffParams.completeFutures(),
                  builder: (context, _) => builder(context, ffParams),
                )
              : builder(context, ffParams);
          final child = appStateNotifier.loading
              ? Center(
                  child: SizedBox(
                    width: 50.0,
                    height: 50.0,
                    child: CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(
                        FlutterFlowTheme.of(context).primary,
                      ),
                    ),
                  ),
                )
              : page;

          final transitionInfo = state.transitionInfo;
          return transitionInfo.hasTransition
              ? CustomTransitionPage(
                  key: state.pageKey,
                  child: child,
                  transitionDuration: transitionInfo.duration,
                  transitionsBuilder:
                      (context, animation, secondaryAnimation, child) =>
                          PageTransition(
                    type: transitionInfo.transitionType,
                    duration: transitionInfo.duration,
                    reverseDuration: transitionInfo.duration,
                    alignment: transitionInfo.alignment,
                    child: child,
                  ).buildTransitions(
                    context,
                    animation,
                    secondaryAnimation,
                    child,
                  ),
                )
              : MaterialPage(key: state.pageKey, child: child);
        },
        routes: routes,
      );
}

class TransitionInfo {
  const TransitionInfo({
    required this.hasTransition,
    this.transitionType = PageTransitionType.fade,
    this.duration = const Duration(milliseconds: 300),
    this.alignment,
  });

  final bool hasTransition;
  final PageTransitionType transitionType;
  final Duration duration;
  final Alignment? alignment;

  static TransitionInfo appDefault() => TransitionInfo(hasTransition: false);
}

class RootPageContext {
  const RootPageContext(this.isRootPage, [this.errorRoute]);
  final bool isRootPage;
  final String? errorRoute;

  static bool isInactiveRootPage(BuildContext context) {
    final rootPageContext = context.read<RootPageContext?>();
    final isRootPage = rootPageContext?.isRootPage ?? false;
    final location = GoRouterState.of(context).uri.toString();
    return isRootPage &&
        location != '/' &&
        location != rootPageContext?.errorRoute;
  }

  static Widget wrap(Widget child, {String? errorRoute}) => Provider.value(
        value: RootPageContext(true, errorRoute),
        child: child,
      );
}

extension GoRouterLocationExtension on GoRouter {
  String getCurrentLocation() {
    final RouteMatch lastMatch = routerDelegate.currentConfiguration.last;
    final RouteMatchList matchList = lastMatch is ImperativeRouteMatch
        ? lastMatch.matches
        : routerDelegate.currentConfiguration;
    return matchList.uri.toString();
  }
}
