import 'package:equatable/equatable.dart';

class Message extends Equatable {
  final String id;
  final String text;
  final bool isSentByMe;
  final DateTime createdAt;
  final MessageType type;

  const Message({
    required this.id,
    required this.text,
    required this.isSentByMe,
    required this.createdAt,
    this.type = MessageType.text,
  });

  @override
  List<Object?> get props => [id, text, isSentByMe, createdAt];
}

enum MessageType { text, image, video }