const { db, messaging, admin } = require('./firebaseAdmin');

class NotificationService {
  async sendMessageNotification(messageData, roomId) {
    try {
      const { senderId, senderName, content, messageType, localId } = messageData;
      
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
      let body = '';
      
      // Xử lý body theo messageType
      if (messageType === 'IMAGE') {
        body = '📷 Đã gửi một hình ảnh';
      } else if (messageType === 'VIDEO') {
        body = '🎥 Đã gửi một video';
      } else if (messageType === 'FILE') {
        body = '📎 Đã gửi một tệp';
      } else {
        body = content ? content.substring(0, 120) : '';
      }


        // Gửi data-only message với click_action
        const payload = {
            tokens,
            data: {
                type: 'chat',
                chatConversationId: roomId,
                senderId: senderId || '',
                messageType: messageType || 'TEXT',
                content: messageType === 'IMAGE' ? (localId || '') : (content || ''), // ← Gửi localId nếu là IMAGE
                title,
                body, // ← Body được format cho hiển thị notification
                click_action: `chatapp://chat/${roomId}`, // Deep link URL
            },
            android: {
                priority: 'high',
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

  async sendFriendRequestNotification(friendshipData) {
    try {
      const { user1, user2, requestedBy } = friendshipData;
      const recipientId = requestedBy === user1 ? user2 : user1;

      const senderDoc = await db.collection('users').doc(requestedBy).get();
      const senderName = senderDoc.exists ? (senderDoc.data().displayName || senderDoc.data().email || 'Người dùng') : 'Người dùng';

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

      const title = 'Yêu cầu kết bạn';
      const body = `${senderName} đã gửi lời mời kết bạn`;

      // Data-only notification với click_action
      const payload = {
        tokens,
        data: {
          type: 'friend_request',
          fromUserId: requestedBy || '',
          toUserId: recipientId || '',
          title,
          body,
          click_action: 'chatapp://friends', // Deep link URL
        },
        android: {
          priority: 'high',
        },
      };

      const response = await messaging.sendEachForMulticast(payload);
      console.log(`✅ Sent ${response.successCount} friend request notifications successfully`);

      const invalidTokens = [];
      response.responses.forEach((result, index) => {
        if (!result.success && result.error?.code === 'messaging/registration-token-not-registered') {
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
      console.error('❌ Error sending friend request notification:', error);
      throw error;
    }
  }
}

module.exports = new NotificationService();