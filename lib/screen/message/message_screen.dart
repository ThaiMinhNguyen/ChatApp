import 'package:flutter/material.dart';
import '../../constant/app_constant.dart';
import '../../custom/custom_searchbar.dart';
import 'item/message_item.dart';

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
