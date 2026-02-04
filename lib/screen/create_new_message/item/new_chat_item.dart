import 'package:flutter/material.dart';

class NewChatItem extends StatelessWidget {
  const NewChatItem({super.key});

  final bool isFriendRequestSend = true;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 8.0),
          child: CircleAvatar(
            radius: 20,
            backgroundColor: Colors.blueAccent,
            foregroundImage: Image.network(
              'https://miro.medium.com/v2/resize:fit:1400/1*rIkmavUeqyRySwlQdA9kKg.jpeg',
            ).image,
            backgroundImage: Image.asset(
              'assets/images/avatar_placeholder.png',
            ).image,
          ),
        ),
        SizedBox(width: 15),
        Text(
          'User Name',
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w800),
        ),
        Spacer(),
        Transform.scale(
          scale: 1.2,
          child: Checkbox(
            activeColor: Color(0xff4356B4),
            checkColor: Colors.white,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
            value: true,
            onChanged: (value) {

            },
          ),
        ),
      ],
    );
  }
}
