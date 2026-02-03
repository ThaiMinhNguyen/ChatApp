import 'package:flutter/material.dart';

class LoadingOverlay {
  OverlayEntry? _overlay;

  bool get isShowing => _overlay != null;

  void show(BuildContext context) {
    if (_overlay != null) return;

    final overlayState = Overlay.of(context, rootOverlay: true);

    _overlay = OverlayEntry(
      builder: (_) => Stack(
        children: [
          const ModalBarrier(
            dismissible: false,
            color: Colors.black45,
          ),
          const Center(
            child: CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          ),
        ],
      ),
    );

    overlayState.insert(_overlay!);
  }

  void hide() {
    _overlay?.remove();
    _overlay = null;
  }
}
