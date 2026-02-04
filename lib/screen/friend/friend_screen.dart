import 'package:flutter/material.dart';
import 'package:flutter_study/screen/friend/tab/friend_list_tab.dart';
import 'package:flutter_study/screen/friend/tab/friend_request_tab.dart';

import '../../constant/app_constant.dart';
import '../../custom/custom_badge.dart';
import '../../custom/custom_searchbar.dart';
import '../../entity/user/people.dart';
import '../../entity/user/user.dart';

class FriendScreen extends StatelessWidget {

  final List<FriendListItem> items = [
    const PeopleHeader('LỜI MỜI KẾT BẠN'),

    ...List.generate(
      5,
          (index) => PersonItem(
        People(
          user: User(
            uid: 'received_$index',
            displayName: 'User Received $index',
            isEmailVerified: true,
          ),
          isRequestReceived: true,
        ),
      ),
    ),

    const PeopleHeader('ĐÃ GỬI KẾT BẠN'),

    ...List.generate(
      5,
          (index) => PersonItem(
        People(
          user: User(
            uid: 'sent_$index',
            displayName: 'User Sent $index',
            isEmailVerified: true,
          ),
          isRequestSent: true,
        ),
      ),
    ),
  ];


  FriendScreen({super.key});

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
                      Row(
                        children: [
                          Tab(text: 'YÊU CẦU'),
                          CustomBadgeLabel(labelText: '1'),
                        ],
                      ),
                    ],
                  ),
                  Expanded(
                    child: TabBarView(
                      children: [
                        Center(child: FriendListTab()),
                        Center(child: FriendListTab()),
                        Center(child: FriendRequestTab(items: items,)),
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









