import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_study/custom/underline_text_field.dart';
import 'package:go_router/go_router.dart';

class LoginScreen extends StatelessWidget {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final bool isButtonEnabled = false;

  LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 80),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Image.asset(
                'assets/icons/ic_login.png',
                width: MediaQuery.of(context).size.width * 0.4,
              ),
              Text(
                'Trải nghiệm Awesome chat',
                style: TextStyle(fontSize: 26, fontWeight: FontWeight.w300),
              ),
              Text(
                'Đăng nhập',
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
                alignment: Alignment.topRight,
                child: Text(
                  'Quên mật khẩu?',
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xff4356B4),
                    fontWeight: FontWeight.w700,
                  ),
                ),
              ),
              Container(
                margin: EdgeInsets.symmetric(vertical: 40),
                child: SizedBox(
                  width: double.infinity,
                  child: TextButton(
                    style: ButtonStyle(
                      padding: WidgetStatePropertyAll(EdgeInsets.symmetric(vertical: 14)),
                      alignment: Alignment.center,
                      backgroundColor: WidgetStateProperty.all(
                        isButtonEnabled ? Color(0xFF4356B4) : Color(0xffCACACA)
                      ),
                    ),
                    onPressed: (){
                      context.go('/messages');
                    },
                    child: Text(
                      'ĐĂNG NHẬP',
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
                        text: 'Chưa có tài khoản? ',
                        style: TextStyle(
                          fontSize: 14,
                          color: Color(0xFF999999),
                        ),
                      ),
                      TextSpan(
                        text: 'Đăng ký ngay',
                        style: TextStyle(
                          fontSize: 14,
                          color: Color(0xff4356B4),
                          fontWeight: FontWeight.w700,
                        ),
                        recognizer: TapGestureRecognizer()..onTap = () {
                          context.push('/register');
                        }
                      ),
                    ]
                  )
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
