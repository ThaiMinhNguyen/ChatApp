import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../custom/underline_text_field.dart';

class RegisterScreen extends StatelessWidget {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final bool isButtonEnabled = false;

  RegisterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 50),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              InkWell(
                onTap: () {
                  context.pop();
                },
                child: Image.asset('assets/icons/ic_chevron_left.png', width: 24, height: 24,),
              ),
              SizedBox(height: 30),
              Text(
                'Đăng ký',
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.w800,
                  color: Color(0xff4356B4),
                ),
              ),
              SizedBox(height: 40),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 18.0),
                child: UnderlineTextField(
                  label: 'HỌ VÀ TÊN',
                  controller: nameController,
                  suffixIcon: Icon(
                    Icons.person_outlined,
                    color: Color(0xFF4356B4),
                  ),
                  hintText: 'Your name here',
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 18.0),
                child: UnderlineTextField(
                  label: 'EMAIL',
                  controller: emailController,
                  suffixIcon: Icon(
                    Icons.email_outlined,
                    color: Color(0xFF4356B4),
                  ),
                  hintText: 'yourname@gmail.com',
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 18.0),
                child: UnderlineTextField(
                  label: 'MẬT KHẨU',
                  controller: passwordController,
                  suffixIcon: Icon(Icons.key, color: Color(0xFF4356B4)),
                  hintText: '*******',
                  obscureText: true,
                ),
              ),
              Align(
                alignment: Alignment.center,
                child: Row(
                  children: [
                    Checkbox(
                        value: true,
                        onChanged: (value){

                        },
                      activeColor: Color(0xff4356B4),
                      checkColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                    Text.rich(
                      TextSpan(
                        children: [
                          TextSpan(
                            text: 'Tôi đồng ý với ',
                            style: TextStyle(
                              fontSize: 14,
                              color: Color(0xFF999999),
                            ),
                          ),
                          TextSpan(
                            text: 'chinh sách ',
                            style: TextStyle(
                              fontSize: 14,
                              color: Color(0xff4356B4),
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                          TextSpan(
                            text: 'và ',
                            style: TextStyle(
                              fontSize: 14,
                              color: Color(0xFF999999),
                            ),
                          ),
                          TextSpan(
                            text: 'điều khoản',
                            style: TextStyle(
                              fontSize: 14,
                              color: Color(0xff4356B4),
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                margin: EdgeInsets.symmetric(vertical: 40),
                child: SizedBox(
                  width: double.infinity,
                  child: TextButton(
                    style: ButtonStyle(
                      padding: WidgetStatePropertyAll(
                        EdgeInsets.symmetric(vertical: 14),
                      ),
                      alignment: Alignment.center,
                      backgroundColor: WidgetStateProperty.all(
                        isButtonEnabled ? Color(0xFF4356B4) : Color(0xffCACACA),
                      ),
                    ),
                    onPressed: () {
                      context.go('/messages');
                    },
                    child: Text(
                      'ĐĂNG KÝ',
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.w700,
                        fontSize: 16,
                      ),
                    ),
                  ),
                ),
              ),
              Center(
                child: Text.rich(
                  TextSpan(
                    children: [
                      TextSpan(
                        text: 'Đã có tài khoản? ',
                        style: TextStyle(
                          fontSize: 14,
                          color: Color(0xFF999999),
                        ),
                      ),
                      TextSpan(
                        text: 'Đăng nhập ngay',
                        style: TextStyle(
                          fontSize: 14,
                          color: Color(0xff4356B4),
                          fontWeight: FontWeight.w700,
                        ),
                        recognizer: TapGestureRecognizer()
                          ..onTap = () {
                            context.pop();
                          },
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
