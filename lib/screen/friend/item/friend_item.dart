import 'package:flutter/material.dart';

class FriendItem extends StatelessWidget {
  final bool isFriendRequestSend = true;

  const FriendItem({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 10),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8.0,),
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
          SizedBox(
            width: 80,
            child: TextButton(
              onPressed: () {},
              style: ButtonStyle(
                backgroundColor: WidgetStateProperty.resolveWith((states) {
                  if (isFriendRequestSend) {
                    return Colors.white;
                  }

                  return Color(0xff4356B4);
                }),
                foregroundColor: WidgetStateProperty.resolveWith((states) {
                  if (isFriendRequestSend) {
                    return Color(0xff4356B4);
                  }

                  return Colors.white;
                }),
                side: WidgetStateProperty.all(
                  BorderSide(color: Color(0xff4356B4), width: 1),
                ),
                padding: WidgetStatePropertyAll(
                  EdgeInsets.symmetric(vertical: 5, horizontal: 10),
                ),
                tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                minimumSize: WidgetStatePropertyAll(Size.zero),
              ),
              child: Text(
                isFriendRequestSend ? 'Hủy' : 'Kết bạn',
                style: TextStyle(fontSize: 14),
              ),
            ),
          ),
        ],
      ),
    );
  }
}