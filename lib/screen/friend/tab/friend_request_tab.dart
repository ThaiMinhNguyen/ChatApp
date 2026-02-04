import 'package:flutter/material.dart';
import '../../../entity/user/people.dart';
import '../item/friend_item.dart';
import '../item/friend_request.dart';

class FriendRequestTab extends StatelessWidget {
  final List<FriendListItem> items;
  const FriendRequestTab({super.key, required this.items});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        var item = items[index];
        if(item is PeopleHeader){
          return FriendRequestHeader(title: item.title,);
        } else {
          return FriendItem();
        }
      },
    );
  }
}