import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../common/gradient_box.dart';
import '../../constant/app_constant.dart';
import '../../custom/custom_searchbar.dart';
import 'item/new_chat_item.dart';
import 'item/selected_user_item.dart';

class CreateNewMessageScreen extends StatelessWidget {
  final bool isAnyItemSelected = true;

  const CreateNewMessageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          GradientBox(),
          SafeArea(
            child: Column(
              children: [
                Padding(
                  padding: const EdgeInsets.only(right: 10, bottom: 2),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      IconButton(
                        icon: const Icon(
                          Icons.chevron_left,
                          size: 32,
                          color: Colors.white,
                        ),
                        onPressed: () {
                          context.pop();
                        },
                      ),
                      Text(
                        'Tạo tin nhắn',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      ),
                      InkWell(
                        onTap: () {
                          context.pop();
                        },
                        child: Text(
                          'Hủy',
                          style: TextStyle(fontSize: 16, color: Colors.white),
                        ),
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: CustomSearchBar(
                    onChanged: (text) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(text),
                          duration: Duration(seconds: 2),
                        ),
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
                    shape: const RoundedRectangleBorder(
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(20),
                        topRight: Radius.circular(20),
                      ),
                    ),
                    child: Column(
                      children: [
                        Align(
                          alignment: Alignment.topLeft,
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                              vertical: 20.0,
                              horizontal: 10,
                            ),
                            child: Text(
                              'DANH SÁCH BẠN BÈ',
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.w800,
                                color: Color(0xff999999),
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 10.0,
                            ),
                            child: ListView.separated(
                              itemCount: 15,
                              itemBuilder: (context, index) {
                                return NewChatItem();
                              },
                              separatorBuilder:
                                  (BuildContext context, int index) {
                                    return Divider(
                                      height: 1,
                                      color: Colors.grey.shade300,
                                    );
                                  },
                            ),
                          ),
                        ),
                        if (isAnyItemSelected)
                          Container(
                            height: 77,
                            padding: const EdgeInsets.symmetric(horizontal: 8),
                            color: const Color(0xffeeeeee),
                            child: Row(
                              children: [
                                Expanded(
                                  child: ListView.builder(
                                    scrollDirection: Axis.horizontal,
                                    itemCount: 5,
                                    itemBuilder: (context, index) {
                                      return Padding(
                                        padding: const EdgeInsets.symmetric(
                                          horizontal: 8,
                                          vertical: 8,
                                        ),
                                        child: InkWell(
                                          onTap: (){
                                            print('Heelo');
                                          },
                                            child: SelectedUserNewChatItem()),
                                      );
                                    },
                                  ),
                                ),
                                IconButton(
                                  style: ButtonStyle(
                                    backgroundColor: WidgetStatePropertyAll(
                                      Color(0xff4356B4),
                                    ),
                                  ),
                                  onPressed: () {
                                    // Navigate to chat
                                  },
                                  icon: const Icon(
                                    Icons.chevron_right,
                                    color: Colors.white,
                                    size: 30,
                                  ),
                                ),
                              ],
                            ),
                          ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

