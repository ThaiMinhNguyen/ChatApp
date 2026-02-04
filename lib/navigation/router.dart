import 'package:flutter/material.dart';
import 'package:flutter_study/screen/create_new_message/create_new_message_screen.dart';
import 'package:flutter_study/screen/profile/detail_profile_screen.dart';
import 'package:flutter_study/screen/login/login_screen.dart';
import 'package:flutter_study/screen/login/register_screen.dart';
import 'package:go_router/go_router.dart';

import '../constant/app_constant.dart';
import '../screen/message/chat_detail/chat_screen.dart';
import '../screen/friend/friend_screen.dart';
import '../screen/main_scaffold.dart';
import '../screen/message/message_screen.dart';
import '../screen/profile/profile_screen.dart';

final _mainShellNavKey = GlobalKey<NavigatorState>();

final router = GoRouter(
    initialLocation: '/',

    routes: [
      ShellRoute(
        // navigatorKey: _mainShellNavKey,
        builder: (context, state, child) {
          return MainScaffold(child: child);
        },
        routes: [
          GoRoute(
            path: '/messages',
            pageBuilder: (context, state) =>
            const NoTransitionPage(child: MessageScreen()),
          ),
          GoRoute(
            path: '/friends',
            pageBuilder: (context, state) =>
            NoTransitionPage(child: FriendScreen()),
          ),
          GoRoute(
            path: '/profile',
            pageBuilder: (context, state) =>
            const NoTransitionPage(child: ProfileScreen()),
          )
        ]
      ),
      GoRoute(
        path: '/login',
        pageBuilder: (context, state) =>
        const NoTransitionPage(child: Text('Login Screen')),
      ),
      GoRoute(
        path: '/profile/detail',
        pageBuilder: (context, state) =>
        const NoTransitionPage(child: DetailProfileScreen()),
      ),
      GoRoute(
        path: '/chat/:chatId',
        pageBuilder: (context, state) {
          final chatId = state.pathParameters['chatId'];
          return NoTransitionPage(child: ChatScreen(chatId: chatId.toString()));
        },
      ),
      GoRoute(
        path: '/',
        pageBuilder: (context, state) {
          return NoTransitionPage(child: LoginScreen());
        },
      ),
      GoRoute(
        path: '/register',
        pageBuilder: (context, state) {
          return NoTransitionPage(child: RegisterScreen());
        },
      ),
      GoRoute(
        path: '/create_new_message',
        pageBuilder: (context, state) {
          return NoTransitionPage(child: CreateNewMessageScreen());
        },
      ),
    ]
);
