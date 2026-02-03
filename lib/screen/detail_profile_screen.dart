import 'package:flutter/material.dart';
import 'package:flutter_study/custom/loading_overlay.dart';
import 'package:go_router/go_router.dart';

import '../custom/underline_text_field.dart';

class DetailProfileScreen extends StatefulWidget {
  const DetailProfileScreen({super.key});

  @override
  State<DetailProfileScreen> createState() => _DetailProfileScreenState();
}

class _DetailProfileScreenState extends State<DetailProfileScreen> {
  final nameTextController = TextEditingController(text: "Awesome Chat User");
  final phoneTextController = TextEditingController(text: "091 234 5678");
  final birthdayTextController = TextEditingController(text: "08/04/1992");
  final _loading = LoadingOverlay();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          SizedBox(
            height: MediaQuery.of(context).size.height * 0.3,
            width: double.infinity,
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF4356B4), Color(0xFF3DCFCF)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
            ),
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.only(right: 10),
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
                    'Chỉnh sửa thông tin',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                  InkWell(
                    onTap: () async {
                      _loading.show(context);
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text('Đã lưu dữ liệu'),
                          duration: Duration(seconds: 2),
                        ),
                      );
                      await Future.delayed(const Duration(seconds: 2));
                      if (!context.mounted) return;
                      _loading.hide();
                      context.pop();
                    },
                    child: Text(
                      'Lưu',
                      style: TextStyle(fontSize: 16, color: Colors.white),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Align(
            alignment: Alignment.bottomCenter,
            child: Card(
              color: Colors.white,
              margin: EdgeInsets.zero,
              shape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(20),
                  topRight: Radius.circular(20),
                ),
              ),
              child: Container(
                height: MediaQuery.of(context).size.height * 0.8,
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    CircleAvatar(
                      radius: 50,
                      backgroundImage: AssetImage(
                        'assets/images/profile_bg_placeholder.png',
                      ),
                    ),
                    const SizedBox(height: 20),
                    UnderlineTextField(
                      label: 'HỌ VÀ TÊN',
                      controller: nameTextController,
                      suffixAsset: 'assets/icons/ic_person.png',
                    ),

                    const SizedBox(height: 20),

                    UnderlineTextField(
                      label: 'SỐ ĐIỆN THOẠI',
                      controller: phoneTextController,
                      suffixAsset: 'assets/icons/ic_phone.png',
                    ),
                    const SizedBox(height: 20),
                    UnderlineTextField(
                      label: 'NGÀY SINH',
                      controller: birthdayTextController,
                      suffixAsset: 'assets/icons/ic_birthday.png',
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

