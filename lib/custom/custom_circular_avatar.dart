import 'package:flutter/material.dart';

class CustomCircularAvatar extends StatelessWidget {
  final String imageUrl;
  final bool hasBorder;
  final Color borderColor;

  const CustomCircularAvatar({
    super.key,
    required this.imageUrl,
    this.borderColor = Colors.white, this.hasBorder = false,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: hasBorder ? EdgeInsets.all(1.5): EdgeInsets.all(0),
      decoration: hasBorder ? BoxDecoration(
        color: borderColor,
        shape: BoxShape.circle,
      ): null,
      child: Container(
        padding: EdgeInsets.all(2),
        decoration: BoxDecoration(
          color: Colors.white,
          shape: BoxShape.circle,
        ),
        child: CircleAvatar(
            radius: 29,
            backgroundColor: Colors.blueAccent,
            foregroundImage: Image.network(
              imageUrl,
            ).image,
            backgroundImage: Image.asset('assets/images/avatar_placeholder.png').image,
          ),
      ),
    );
  }
}