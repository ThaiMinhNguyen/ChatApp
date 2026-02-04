import 'package:flutter/material.dart';

class SelectedUserNewChatItem extends StatelessWidget {
  const SelectedUserNewChatItem({super.key});

  @override
  Widget build(BuildContext context) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        CircleAvatar(
          radius: 30,
          backgroundColor: Colors.blueAccent,
          foregroundImage: Image.network(
            'https://miro.medium.com/v2/resize:fit:1400/1*rIkmavUeqyRySwlQdA9kKg.jpeg',
          ).image,
          backgroundImage: Image.asset(
            'assets/images/avatar_placeholder.png',
          ).image,
        ),
        Positioned(
          right: -10,
          top: -4,
          child: SizedBox(
            height: 28,
            child: Image.asset('assets/icons/ic_close.png',),
          ),
        ),
      ],
    );
  }
}