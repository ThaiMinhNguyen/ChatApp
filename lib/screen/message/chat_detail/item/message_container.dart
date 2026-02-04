import 'package:flutter/material.dart';

import '../../../../entity/message/message_content.dart';

class MessageContainer extends StatelessWidget {
  final MessageContent message;
  final bool shouldShowAvatar;
  final bool shouldShowTimestamp;

  const MessageContainer({
    super.key,
    required this.message,
    this.shouldShowAvatar = true,
    this.shouldShowTimestamp = true,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: message.isSentByMe
          ? MainAxisAlignment.end
          : MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (!message.isSentByMe)
          SizedBox(
            width: 32,
            child: shouldShowAvatar
                ? Padding(
              padding: const EdgeInsets.only(right: 8.0),
              child: CircleAvatar(
                radius: 12,
                backgroundImage:
                NetworkImage('https://i.pravatar.cc/150?img=3'),
              ),
            )
                : null,
          ),
        Column(
          crossAxisAlignment: message.isSentByMe
              ? CrossAxisAlignment.end
              : CrossAxisAlignment.start,
          children: [
            Container(
              margin: EdgeInsets.only(bottom: 4),
              padding: EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: message.isSentByMe ? Color(0xff4356B4) : Colors.grey[300],
                borderRadius: BorderRadius.circular(20),
              ),
              child: ConstrainedBox(
                constraints: BoxConstraints(
                  maxWidth: MediaQuery.of(context).size.width * 0.7,
                ),
                child: Text(
                  softWrap: true,
                  message.text,
                  style: TextStyle(
                    color: message.isSentByMe ? Colors.white : Colors.black,
                  ),
                ),
              ),
            ),
            if(shouldShowTimestamp)
              Text(
                '10:05',
                style: TextStyle(fontSize: 12, color: Color(0xff999999)),
              ),
          ],
        ),
      ],
    );
  }
}