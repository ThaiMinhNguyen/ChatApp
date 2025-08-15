const { db, messaging, admin } = require('./firebaseAdmin');

class NotificationService {
  async sendMessageNotification(messageData, roomId) {
    try {
      const { senderId, senderName, content } = messageData;
      
      // Lấy thông tin room để xác định người nhận
      const roomDoc = await db.collection('rooms').doc(roomId).get();
      if (!roomDoc.exists) {
        console.log('❌ Room not found:', roomId);
        return;
      }

      const participants = roomDoc.data().participants || [];
      const recipientId = participants.find(p => p !== senderId);
      
      if (!recipientId) {
        console.log('❌ No recipient found for room:', roomId);
        return;
      }

      // Lấy FCM tokens của người nhận
      const userDoc = await db.collection('users').doc(recipientId).get();
      if (!userDoc.exists) {
        console.log('❌ User not found:', recipientId);
        return;
      }

      let tokens = userDoc.data().fcmTokens || [];
      tokens = [...new Set(tokens)].filter(Boolean);
      
      if (tokens.length === 0) {
        console.log('❌ No FCM tokens found for user:', recipientId);
        return;
      }

      // Tạo notification payload
      const title = senderName || 'Tin nhắn mới';
      const body = content ? content.substring(0, 120) : '';

      const payload = {
        tokens,
        notification: {
          title,
          body,
        },
        data: {
          roomId: roomId,
          senderId: senderId || '',
          type: 'chat',
        },
        android: {
          notification: {
            channelId: 'chat_messages',
            priority: 'high',
          },
        },
      };

      // Gửi notification
      const response = await messaging.sendEachForMulticast(payload);
      console.log(`✅ Sent ${response.successCount} notifications successfully`);

      // Xóa token invalid
      const invalidTokens = [];
      response.responses.forEach((result, index) => {
        if (!result.success && 
            result.error?.code === 'messaging/registration-token-not-registered') {
          invalidTokens.push(tokens[index]);
        }
      });

      if (invalidTokens.length > 0) {
        await db.collection('users').doc(recipientId).update({
          fcmTokens: admin.firestore.FieldValue.arrayRemove(...invalidTokens)
        });
        console.log(`🧹 Removed ${invalidTokens.length} invalid tokens`);
      }

      return response;
    } catch (error) {
      console.error('❌ Error sending notification:', error);
      throw error;
    }
  }
}

module.exports = new NotificationService();