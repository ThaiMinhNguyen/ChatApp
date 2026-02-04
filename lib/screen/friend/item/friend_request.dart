import 'package:flutter/material.dart';

class FriendRequestHeader extends StatelessWidget {
  final String title;
  const FriendRequestHeader({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(10),
      child: Align(
        alignment: Alignment.centerLeft,
        child: Text(
          title,
          style: TextStyle(
            fontSize: 14,
            color: Color(0xff999999),
            fontWeight: FontWeight.w800,
          ),
        ),
      ),
    );
  }
}
