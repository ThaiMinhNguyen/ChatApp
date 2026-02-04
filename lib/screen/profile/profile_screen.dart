import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        SizedBox(
          child: Image.asset(
            'assets/images/profile_bg_placeholder.png',
            width: double.infinity,
            fit: BoxFit.cover,
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
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  color: Colors.transparent,
                  width: double.infinity,
                  padding: const EdgeInsets.only(right: 20, left: 20, top: 20),
                  child: Column(
                    children: [
                      InkWell(
                        onTap: (){
                          context.push('/profile/detail');
                        },
                        child: Row(
                          children: [
                            Container(
                              padding: const EdgeInsets.all(3),
                              decoration: const BoxDecoration(
                                shape: BoxShape.circle,
                                gradient: LinearGradient(
                                  colors: [Color(0xFF4356B4), Color(0xFF3DCFCF)],
                                  begin: AlignmentGeometry.topCenter,
                                  end: AlignmentGeometry.bottomCenter,
                                ),
                              ),
                              child: const CircleAvatar(
                                radius: 30,
                                backgroundImage: AssetImage(
                                  'assets/images/profile_bg_placeholder.png',
                                ),
                              ),
                            ),
                            const SizedBox(width: 16),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    'Usernameaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
                                    style: TextStyle(
                                      fontSize: 24,
                                      fontWeight: FontWeight.bold,
                                      color: Colors.black,
                                    ),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                  const SizedBox(height: 1),
                                  Text(
                                    'Status or description',
                                    style: TextStyle(
                                      fontSize: 14,
                                      color: Colors.grey[600],
                                    ),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ],
                              ),
                            ),
                            Icon(Icons.edit, color: Color(0xFF4356B4)),
                          ],
                        ),
                      ),
                      const SizedBox(height: 20),
                      ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: Image.asset(
                          'assets/icons/ic_globe.png',
                          width: 24,
                          height: 24,
                        ),
                        title: Text(
                          'Ngôn ngữ',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              'Tiếng Việt',
                              style: TextStyle(
                                fontSize: 16,
                                color: Color(0xFF4356B4),
                              ),
                            ),
                            Icon(
                              Icons.chevron_right,
                              size: 24,
                              color: Color(0xFF999999),
                            ),
                          ],
                        ),
                        onTap: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text('Chọn ngôn ngữ'),
                              duration: Duration(seconds: 2),
                            ),
                          );
                        },
                      ),
                      Divider(
                        color: Color(0xFF999999),
                        height: 1,
                        thickness: 0,
                        indent: 20,
                        endIndent: 20,
                      ),
                      ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: Image.asset(
                          'assets/icons/ic_notice.png',
                          width: 24,
                          height: 24,
                        ),
                        title: Text(
                          'Thông báo',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        trailing: Icon(
                          Icons.chevron_right,
                          color: Color(0xFF999999),
                        ),
                        onTap: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text('Chọn ngôn ngữ'),
                              duration: Duration(seconds: 2),
                            ),
                          );
                        },
                      ),
                      Divider(
                        color: Color(0xFF999999),
                        height: 1,
                        thickness: 0,
                        indent: 20,
                        endIndent: 20,
                      ),
                      ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: Image.asset(
                          'assets/icons/ic_version.png',
                          width: 24,
                          height: 24,
                        ),
                        title: Text(
                          'Phiên bản ứng dụng',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        trailing: Text(
                          '1.0.0',
                          style: TextStyle(
                            fontSize: 16,
                            color: Color(0xFF999999),
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        onTap: () {

                        },
                      ),
                    ],
                  ),
                ),
                Container(
                  color: Color(0xFFEFEEEE),
                  padding: EdgeInsets.only(top: 6),
                  child: Container(
                    padding: EdgeInsets.symmetric(horizontal: 20),
                    color: Colors.white,
                    child: ListTile(
                      contentPadding: EdgeInsets.zero,
                      leading: Image.asset(
                        'assets/icons/ic_logout.png',
                        width: 24,
                        height: 24,
                      ),
                      title: Text(
                        'Đăng xuất',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w500,
                          color: Colors.red,
                        ),
                      ),
                      onTap: () {
                        context.go('/');
                      },
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
