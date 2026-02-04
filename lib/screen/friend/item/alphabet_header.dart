import 'package:flutter/material.dart';

class AlphabetHeader extends StatelessWidget {
  const AlphabetHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 10.0),
      color: Color(0xffF6F6F6),
      child: Text(
        'A',
        style: TextStyle(
          color: Colors.black,
          fontSize: 16,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}