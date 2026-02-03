import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

class ImagePickerHelper {
  static final ImagePicker _picker = ImagePicker();

  static Future<XFile?> pickImageFromGallery() async {
    return await _picker.pickImage(source: ImageSource.gallery);
  }

  static Future<XFile?> pickImageFromCamera() async {
    return await _picker.pickImage(source: ImageSource.camera);
  }

  static Future<XFile?> showImageSourceDialog(BuildContext context){
    return showDialog<XFile?>(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text('Select Image Source'),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                ListTile(
                  leading: const Icon(Icons.photo_library),
                  title: const Text('Gallery'),
                  onTap: () async {
                    final image = await pickImageFromGallery();
                    if(context.mounted) Navigator.of(context).pop(image);
                  },
                ),
                ListTile(
                  leading: const Icon(Icons.camera_alt),
                  title: const Text('Camera'),
                  onTap: () async {
                    final image = await pickImageFromCamera();
                    if(context.mounted) Navigator.of(context).pop(image);
                  },
                ),
              ],
            ),
          );
        },
    );
  }
}

