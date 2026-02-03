import 'package:flutter/material.dart';

class AppConstant {
  static const String appName = "MyFlutterApp";

}

class Routes {
  static const String home = '/';
  static const String messages = '/messages';
  static const String friends = '/friends';
  static const String profile = '/profile';
  static const String login = '/login';
}

class HomeAssets {
  static const String newChat = 'assets/images/new_chat.png';
  static const String newFriend = 'assets/images/add_friend.png';
  static const String messageAppBar = 'assets/images/message_app_bar.png';
  static const String friendsAppBar = 'assets/images/friends_app_bar.png';
  static const String profileAppBar = 'assets/images/profile_app_bar.png';
}

class HomeConstant {
  static const String messageSearchHint = 'Tìm kiếm tin nhắn...';
  static const String friendSearchHint = 'Tìm kiếm bạn bè...';
  static const String messageTitle = 'Tin nhắn';
  static const String friendTitle = 'Bạn bè';
  static const Color appBarIconActiveColor = Color(0xFF4356B4);
  static const Color appBarIconInactiveColor = Colors.grey;
}