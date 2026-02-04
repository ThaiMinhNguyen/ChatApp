import 'package:flutter/material.dart';

class DateContainer extends StatelessWidget {
  final String date;

  const DateContainer({super.key, required this.date});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.center,
      child: Container(
        margin: EdgeInsets.symmetric(vertical: 10),
        padding: EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: BorderRadius.circular(30),
        ),
        child: Text(date, style: TextStyle(fontSize: 14)),
      ),
    );
  }
}