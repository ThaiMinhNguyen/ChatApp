import 'package:flutter/material.dart';

import '../constant/app_constant.dart';
import '../custom/custom_searchbar.dart';

class FriendScreen extends StatelessWidget {

  const FriendScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
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
              hintText: HomeConstant.friendSearchHint,
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
              child: Column(
                children: [
                  const TabBar(
                    labelColor: Color(0xff4356B4),
                    unselectedLabelColor: Colors.grey,
                    indicatorColor: Colors.blue,
                    tabs: [
                      Tab(text: 'BẠN BÈ'),
                      Tab(text: 'TẤT CẢ'),
                      Tab(text: 'YÊU CẦU'),
                    ],
                  ),
                  Expanded(
                    child: TabBarView(
                      children: [
                        Center(child: Text('Tab 1')),
                        Center(child: Text('Tab 2')),
                        Center(child: Text('Tab 3')),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
