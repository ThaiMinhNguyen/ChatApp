import 'package:flutter/material.dart';
import 'package:flutter_study/util/image_picker_helper.dart';
import 'package:go_router/go_router.dart';

import '../entity/date_header.dart';
import '../entity/message.dart';

class ChatScreen extends StatelessWidget {
  final String? chatId;

  final List<Object> messages = List.generate(30, (index) {
    final List<Object> items = [];

    if (index % 5 == 0) {
      items.add(
        DateHeader('June ${index ~/ 5 + 1}, 2024'),
      );
    }

    items.add(
      Message(
        id: 'msg_$index',
        text: 'Message numberaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa $index',
        isSentByMe: index % 3 == 0,
        createdAt: DateTime.now().subtract(
          Duration(minutes: 30 - index),
        ),
      ),
    );

    return items;
  }).expand((e) => e).toList().reversed.toList();

  ChatScreen({super.key, this.chatId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFF6F6F6),
      appBar: AppBar(
        elevation: 0,
        scrolledUnderElevation: 0,
        surfaceTintColor: Colors.transparent,
        forceMaterialTransparency: true,
        backgroundColor: Color(0xFFF6F6F6),
        titleSpacing: 0,
        title: Row(
          children: [
            CircleAvatar(
              backgroundColor: Colors.blueAccent,
              backgroundImage: Image.network(
                'https://i.pravatar.cc/150?img=5',
              ).image,
            ),
            const SizedBox(width: 10),
            Text('Chat with ${chatId ?? "User"}'),
          ],
        ),
        leading: InkWell(
          child: Icon(Icons.chevron_left, size: 40, color: Color(0xFF4356B4)),
          onTap: () {
            context.pop();
          },
        ),
      ),
      body: Container(
        margin: const EdgeInsets.only(top: 30),
        child: Card(
          clipBehavior: Clip.hardEdge,
          color: Colors.white,
          margin: EdgeInsets.zero,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
              topLeft: Radius.circular(30),
              topRight: Radius.circular(30),
            ),
          ),
          child: Column(
            children: [
              Expanded(
                child: ListView.builder(
                  reverse: true,
                  padding: EdgeInsets.all(10.0),
                  itemCount: messages.length,
                  itemBuilder: (BuildContext context, int index) {
                    final item = messages[index];
                    final nextItem =
                        index > 0 ? messages[index - 1] : null;
                    final prevItem =
                        index < messages.length - 1 ? messages[index + 1] : null;
                    var shouldShowAvatar = false;
                    var shouldShowTimestamp = false;
                    if (item is DateHeader) {
                      return DateContainer(date: item.date);
                    } else if (item is Message) {
                      if(nextItem is! Message){
                        shouldShowTimestamp = true;
                      }
                      if(prevItem is! Message || (prevItem.isSentByMe == true && item.isSentByMe == false)){
                          shouldShowAvatar = true;
                      }
                      if(nextItem is Message){
                        if(nextItem.isSentByMe != item.isSentByMe){
                          shouldShowTimestamp = true;
                        }
                      }
                      return MessageContainer(message: item, shouldShowAvatar: shouldShowAvatar, shouldShowTimestamp: shouldShowTimestamp,);
                    } else {
                      return SizedBox.shrink();
                    }
                  },
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Row(
                  children: [
                    Container(
                      margin: EdgeInsets.only(right: 8),
                      decoration: BoxDecoration(
                        color: Color(0xFFF6F6F6),
                        borderRadius: BorderRadius.circular(50),
                      ),
                      child: IconButton(
                        icon: Icon(Icons.image, color: Color(0xFF999999)),
                        onPressed: () async {
                          final image = await ImagePickerHelper.showImageSourceDialog(context);
                          print("Hello${image!.path}");
                        },
                      ),
                    ),
                    Expanded(
                      child: TextField(
                        decoration: InputDecoration(
                          filled: true,
                          fillColor: Color(0xFFF6F6F6),
                          hintText: 'Nhập tin nhắn...',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(20),
                            borderSide: BorderSide.none,
                          ),
                          contentPadding: EdgeInsets.symmetric(
                            vertical: 10,
                            horizontal: 15,
                          ),
                        ),
                        onTapOutside: (event) {
                          FocusManager.instance.primaryFocus?.unfocus();
                        },
                      ),
                    ),
                    IconButton(
                      icon: Icon(Icons.send, color: Color(0xff4356B4)),
                      onPressed: () {
                        // Handle send action
                      },
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class MessageContainer extends StatelessWidget {
  final Message message;
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

class DateContainer extends StatelessWidget {
  final String date;

  const DateContainer({super.key, required this.date});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.center,
      child: Container(
        margin: EdgeInsets.symmetric(vertical: 10),
        padding: EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: BorderRadius.circular(30),
        ),
        child: Text(date, style: TextStyle(fontSize: 14)),
      ),
    );
  }
}
