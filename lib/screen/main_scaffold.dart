import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../common/gradient_box.dart';
import '../constant/app_constant.dart';

class MainScaffold extends StatelessWidget {
  final Widget child;

  const MainScaffold({super.key, required this.child});

  int _locationToIndex(String location) {
    if (location.startsWith(Routes.profile)) return 2;
    if (location.startsWith(Routes.friends)) return 1;
    return 0; // home
  }

  String _title(String location) {
    switch (location) {
      case Routes.messages:
        return HomeConstant.messageTitle;
      case Routes.friends:
        return HomeConstant.friendTitle;
      default:
        return HomeConstant.messageTitle;
    }
  }

  String _appBarIcon(String location) {
    switch (location) {
      case Routes.messages:
        return HomeAssets.newChat;
      case Routes.friends:
        return HomeAssets.newFriend;
      default:
        return HomeAssets.newChat;
    }
  }

  @override
  Widget build(BuildContext context) {
    final currentLocation = GoRouterState.of(context).uri.path.toString();
    var currentIndex = _locationToIndex(currentLocation);
    final appBarIcon = _appBarIcon(currentLocation);
    final bool hideAppBar = currentLocation == Routes.profile;

    return Stack(
      children: [
        GradientBox(),
        Scaffold(
          backgroundColor: Colors.transparent,
          appBar: hideAppBar
              ? null
              : AppBar(
                  backgroundColor: Colors.transparent,
                  title: Text(
                    _title(currentLocation),
                    style: TextStyle(
                      fontSize: 30,
                      fontWeight: FontWeight.w700,
                      color: Colors.white,
                    ),
                  ),
                  actions: [
                    Container(
                      margin: EdgeInsets.only(right: 16),
                      child: IconButton(
                        onPressed: () {
                          switch (currentIndex) {
                            case 0:
                              if(context.mounted) context.push('/create_new_message');
                              break;
                            case 1:
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text('Bạn bè'),
                                  duration: Duration(seconds: 2),
                                ),
                              );
                              break;
                            case 2:
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text('Profile'),
                                  duration: Duration(seconds: 2),
                                ),
                              );
                              break;
                          }
                        },
                        icon: Image.asset(appBarIcon),
                      ),
                    ),
                  ],
                ),
          body: RepaintBoundary(child: child),
          bottomNavigationBar: Container(
            color: const Color(0xFFeeeeee),
            height: 125,
            child: Card(
              clipBehavior: Clip.antiAlias,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              margin: const EdgeInsets.all(18),
              child: NavigationBarTheme(
                data: NavigationBarThemeData(
                  indicatorColor: Colors.transparent,
                  labelTextStyle: WidgetStateProperty.resolveWith<TextStyle>(
                        (states) {
                      if (states.contains(WidgetState.selected)) {
                        return const TextStyle(
                          color: Color(0xFF4356B4),
                          fontWeight: FontWeight.w600,
                        );
                      }
                      return const TextStyle(
                        color: Colors.grey,
                      );
                    },
                  ),
                ),
                child: MediaQuery(
                  data: MediaQuery.of(context).removePadding(removeTop: true, removeBottom: true),
                  child: NavigationBar(
                    backgroundColor: Colors.white,
                    selectedIndex: currentIndex,
                    onDestinationSelected: (index) {
                        currentIndex = index;
                        switch (index) {
                          case 0:
                            context.go(Routes.messages);
                            break;
                          case 1:
                            context.go(Routes.friends);
                            break;
                          case 2:
                            context.go(Routes.profile);
                            break;
                        }
                    },
                    destinations: [
                      NavigationDestination(
                        icon: Badge(
                          isLabelVisible: false,
                          label: const Text('3'),
                          child: Image.asset(
                            HomeAssets.messageAppBar,
                            width: 24,
                            height: 24,
                            color: currentIndex == 0
                                ? HomeConstant.appBarIconActiveColor
                                : HomeConstant.appBarIconInactiveColor,
                            colorBlendMode: BlendMode.srcIn,
                          ),
                        ),
                        label: 'Messages',
                      ),
                      NavigationDestination(
                        icon: Badge(
                          label: const Text('3'),
                          child: Image.asset(
                            HomeAssets.friendsAppBar,
                            width: 24,
                            height: 24,
                            color: currentIndex == 1
                                ? HomeConstant.appBarIconActiveColor
                                : HomeConstant.appBarIconInactiveColor,
                            colorBlendMode: BlendMode.srcIn,
                          ),
                        ),
                        label: 'Friends',
                      ),
                      NavigationDestination(
                        icon: Badge(
                          isLabelVisible: false,
                          label: const Text('3'),
                          child: Image.asset(
                            HomeAssets.profileAppBar,
                            width: 24,
                            height: 24,
                            color: currentIndex == 2
                                ? HomeConstant.appBarIconActiveColor
                                : HomeConstant.appBarIconInactiveColor,
                            colorBlendMode: BlendMode.srcIn,
                          ),
                        ),
                        label: 'Profile',
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
