import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../custom/custom_badge.dart';
import '../../../custom/custom_circular_avatar.dart';

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