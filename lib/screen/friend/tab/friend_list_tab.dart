import 'package:flutter/material.dart';

import '../item/alphabet_header.dart';
import '../item/friend_item.dart';

class FriendListTab extends StatelessWidget {
  const FriendListTab({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      itemCount: 20,
      itemBuilder: (BuildContext context, int index) {
        if (index % 5 == 0) return AlphabetHeader();
        return FriendItem();
      },
      separatorBuilder: (BuildContext context, int index) {
        return Divider(height: 1, color: Colors.grey.shade300);
      },
    );
  }
}