const express = require('express');
const cors = require('cors');
require('dotenv').config();

const { db } = require('./services/firebaseAdmin');
const notificationService = require('./services/notificationService');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Health check
app.get('/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    timestamp: new Date().toISOString(),
    project: process.env.FIREBASE_PROJECT_ID 
  });
});

// Set để track messages đã xử lý (tránh duplicate)
const processedMessages = new Set();
// Track trạng thái friendships để tránh notify khi khởi động và phát hiện thay đổi thật sự
const friendshipState = new Map(); // docId -> { status, requestedBy }
let friendshipsInitialized = false;

// Firestore listener cho messages mới
function startFirestoreListener() {
  console.log('�� Starting Firestore listener for messages...');
  
  const messagesQuery = db.collectionGroup('messages')
    .orderBy('timestamp', 'desc');

  messagesQuery.onSnapshot(async (snapshot) => {
    snapshot.docChanges().forEach(async (change) => {
      if (change.type === 'added') {
        const messageData = change.doc.data();
        const messageId = change.doc.id;
        
        // Tránh xử lý trùng
        if (processedMessages.has(messageId)) {
          return;
        }
        
        processedMessages.add(messageId);
        
        // Chỉ xử lý message mới (trong vòng 1 phút)
        const messageTime = messageData.timestamp?.toDate();
        const now = new Date();
        const oneMinuteAgo = new Date(now.getTime() - 60000);
        
        if (!messageTime || messageTime < oneMinuteAgo) {
          return;
        }
        
        // Lấy roomId từ đường dẫn document
        const roomId = change.doc.ref.parent.parent.id;
        
        console.log('📨 New message detected:', {
          messageId,
          roomId,
          sender: messageData.senderName,
          content: messageData.content?.substring(0, 50),
          messageType: messageData.messageType || 'TEXT',
          localId: messageData.localId || undefined
        });
        
        try {
          await notificationService.sendMessageNotification(messageData, roomId);
        } catch (error) {
          console.error('❌ Error processing message notification:', error);
        }
      }
    });
  }, (error) => {
    console.error('❌ Firestore listener error:', error);
  });

  // Listener cho friend requests
  console.log('👥 Starting Firestore listener for friend requests...');
  const friendshipsRef = db.collection('friendships');
  friendshipsRef.onSnapshot((snapshot) => {
    // Khởi tạo state lần đầu: không gửi notify cho dữ liệu sẵn có
    if (!friendshipsInitialized) {
      snapshot.docs.forEach((doc) => {
        const d = doc.data();
        friendshipState.set(doc.id, { status: d.status, requestedBy: d.requestedBy });
      });
      friendshipsInitialized = true;
      console.log('✅ Friendships listener initialized (no notifications sent on startup)');
      return;
    }

    snapshot.docChanges().forEach(async (change) => {
      const docId = change.doc.id;
      const d = change.doc.data();
      const prev = friendshipState.get(docId);

      // Cập nhật state sau khi xử lý
      const updateState = () => friendshipState.set(docId, { status: d.status, requestedBy: d.requestedBy });

      if (change.type === 'removed') {
        friendshipState.delete(docId);
        return;
      }

      // Xử lý cho cả 'added' (sau khi đã init) và 'modified'
      const isStateChanged = !prev || prev.status !== d.status || prev.requestedBy !== d.requestedBy;
      if (!isStateChanged) {
        updateState();
        return;
      }

      // Điều kiện notify: trạng thái hiện tại là PENDING và có requestedBy
      if (d.status === 'PENDING' && d.requestedBy) {
        try {
          await notificationService.sendFriendRequestNotification(d);
        } catch (e) {
          console.error('❌ Error processing friend request notification:', e);
        }
      }

      updateState();
    });
  }, (error) => {
    console.error('❌ Friendships listener error:', error);
  });
}

// Start server
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
  console.log(`�� Project: ${process.env.FIREBASE_PROJECT_ID}`);
  
  // Bắt đầu lắng nghe Firestore sau khi server start
  setTimeout(() => {
    startFirestoreListener();
  }, 2000);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('🛑 SIGTERM received, shutting down gracefully');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('🛑 SIGINT received, shutting down gracefully');
  process.exit(0);
});