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
          content: messageData.content?.substring(0, 50)
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