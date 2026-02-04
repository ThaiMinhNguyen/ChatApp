import 'package:flutter/material.dart';
import 'package:flutter_study/custom/custom_badge.dart';
import 'package:flutter_study/custom/custom_circular_avatar.dart';
import 'package:go_router/go_router.dart';

import '../../constant/app_constant.dart';
import '../../custom/custom_searchbar.dart';

class MessageScreen extends StatelessWidget {

  const MessageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: CustomSearchBar(
              onChanged: (text) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text(text), duration: Duration(seconds: 2)),
                );
              },
              hintText: HomeConstant.messageSearchHint,
            ),
          ),
          Expanded(
            child: Card(
              clipBehavior: Clip.hardEdge,
              color: Colors.white,
              margin: EdgeInsets.zero,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(20),
                  topRight: Radius.circular(20),
                ),
              ),
              child: ListView.separated(
                padding: EdgeInsets.all(10.0),
                itemCount: 20,
                itemBuilder: (BuildContext context, int index) {
                  return MessageItem(index: index);
                },
                separatorBuilder: (BuildContext context, int index) {
                  return Divider(height: 1, color: Colors.grey.shade300);
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class MessageItem extends StatelessWidget {
  final int index;
  final String? badgeContent;

  const MessageItem({super.key, required this.index, this.badgeContent});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: CustomBadge(
        labelText: '99+',
        child: CustomCircularAvatar(
          imageUrl: 'https://i.pravatar.cc/150?img=${index + 1}',
          borderColor: Color(0xff4356B4),
          hasBorder: (index % 3 == 0),
        ),
      ),

      title: Text('User $index'),
      subtitle: Text('Tin nhắn mẫu từ User $index'),
      trailing: Text('12:3${index % 10} PM'),
      onLongPress: () {
        context.go('/login');
      },
      onTap: () {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Đã lưu dữ liệu'),
            duration: Duration(seconds: 2),
          ),
        );
        context.push('/chat/$index');
      },
    );
  }
}
