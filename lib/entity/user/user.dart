import 'package:equatable/equatable.dart';

class User extends Equatable {
  final String uid;
  final String? email;
  final String? displayName;
  final String? photoUrl;
  final String? phoneNumber;
  final String? dateOfBirth;
  final bool isEmailVerified;
  final List<String> fcmTokens;

  const User({
    required this.uid,
    this.email,
    this.displayName,
    this.photoUrl,
    this.phoneNumber,
    this.dateOfBirth,
    required this.isEmailVerified,
    this.fcmTokens = const [],
  });

  const User.empty()
      : uid = '',
        email = null,
        displayName = null,
        photoUrl = null,
        phoneNumber = null,
        dateOfBirth = null,
        isEmailVerified = false,
        fcmTokens = const [];

  @override
  List<Object?> get props => [
    uid,
    email,
    displayName,
    photoUrl,
    phoneNumber,
    dateOfBirth,
    isEmailVerified,
    fcmTokens,
  ];
}
