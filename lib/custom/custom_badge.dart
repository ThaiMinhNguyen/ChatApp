import 'package:flutter/material.dart';

class CustomBadge extends StatelessWidget {
  final String? labelText;
  final Widget child;

  const CustomBadge({super.key, this.labelText, required this.child});

  @override
  Widget build(BuildContext context) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        child,
        if (labelText != null)
          Positioned(
            right: -5,
            top: -3,
            child: CustomBadgeLabel(labelText: labelText!),
          ),
      ],
    );
  }
}

class CustomBadgeLabel extends StatelessWidget {
  final String labelText;

  const CustomBadgeLabel({super.key, required this.labelText});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(1),
      decoration: const BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
      ),
      child: Container(
        margin: EdgeInsets.all(1),
        padding: const EdgeInsets.all(6),
        decoration: const BoxDecoration(
          color: Colors.red,
          shape: BoxShape.circle,
        ),
        child: Center(
          child: SizedBox(
            height: 10,
            width: 10,
            child: FittedBox(
              fit: BoxFit.scaleDown,
              child: Text(
                labelText,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 15,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
