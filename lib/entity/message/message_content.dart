import 'message.dart';

class MessageContent extends Message {
  final String id;
  final String text;
  final bool isSentByMe;
  final DateTime createdAt;
  final MessageType type;

  const MessageContent({
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