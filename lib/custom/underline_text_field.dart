import 'package:flutter/material.dart';

class UnderlineTextField extends StatelessWidget {
  final String label;
  final TextEditingController controller;
  final String? suffixAsset;
  final Widget? suffixIcon;
  final String? hintText;
  final TextInputType inputType;
  final bool obscureText;

  const UnderlineTextField({
    super.key,
    required this.label,
    required this.controller,
    this.suffixAsset, this.suffixIcon, this.hintText,
    this.inputType = TextInputType.text,
    this.obscureText = false,
  });

  @override
  Widget build(BuildContext context) {
    return TextField(
      keyboardType: inputType,
      controller: controller,
      obscureText: obscureText,
      decoration: InputDecoration(
        hintText: hintText,
        hintStyle: TextStyle(
          color: Color(0xff676767)
        ),
        labelText: label,
        floatingLabelBehavior: FloatingLabelBehavior.always,
        labelStyle: const TextStyle(color: Color(0xff999999)),

        suffixIcon: suffixIcon ??
            (suffixAsset != null
                ? Padding(
              padding: const EdgeInsets.only(right: 8),
              child: Image.asset(
                suffixAsset!,
                width: 16,
                height: 16,
                fit: BoxFit.contain,
              ),
            )
                : null),

        suffixIconConstraints: const BoxConstraints(
          minWidth: 16,
          minHeight: 16,
        ),

        enabledBorder: const UnderlineInputBorder(
          borderSide: BorderSide(color: Color(0xFFD2D2D2), width: 1),
        ),
        focusedBorder: const UnderlineInputBorder(
          borderSide: BorderSide(color: Color(0xFF4356B4), width: 2),
        ),
      ),
      onTapOutside: (event) {
        FocusManager.instance.primaryFocus?.unfocus();
      },
    );
  }
}
