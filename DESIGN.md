# Thiết kế dự án ChatApp

## 1. Cấu trúc thư mục dự án

```
app/
  src/
    main/
      java/com/example/chatapp/
        MainActivity.kt
        ... (các màn hình, viewmodel, repository, adapter, utils, ...)
      res/
        layout/ (giao diện XML)
        values/ (chuỗi, màu sắc, theme)
        ...
  build.gradle.kts
  ...
```

### Gợi ý các package:
- `ui/` - Chứa các màn hình (Activity, Fragment)
- `viewmodel/` - ViewModel cho từng màn hình
- `repository/` - Xử lý logic lấy dữ liệu từ Firebase
- `model/` - Định nghĩa các data class (User, Message, Conversation, ...)
- `adapter/` - Adapter cho RecyclerView
- `utils/` - Các hàm tiện ích

## 2. Thiết kế dữ liệu Firebase

### 2.1. Authentication
- Sử dụng Firebase Authentication (Email/Password)

### 2.2. Firestore Database

#### Collection: `users` - Thông tin người dùng
```json
{
  "users": {
    "abc123def456xyz789": {
      "uid": "abc123def456xyz789",
      "email": "john@example.com",
      "displayName": "John Doe",
      "photoUrl": "https://example.com/avatars/john.jpg",
      "phoneNumber": "+1234567890",
      "dateOfBirth": "1990-01-01",
      "isEmailVerified": true
    },
    "xyz789uvw012abc345": {
      "uid": "xyz789uvw012abc345",
      "email": "jane@example.com",
      "displayName": "Jane Smith",
      "photoUrl": "https://example.com/avatars/jane.jpg",
      "phoneNumber": "+1234567891",
      "dateOfBirth": "1992-05-15",
      "isEmailVerified": true
    }
  }
}
```

#### Collection: `userStatus` - Trạng thái và tùy chọn người dùng
```json
{
  "userStatus": {
    "abc123def456xyz789": {
      "uid": "abc123def456xyz789",
      "isOnline": true,
      "lastSeen": 1703846800000,
      "language": "vi",
      "notificationEnabled": true,
      "theme": "light" 
    },
    "xyz789uvw012abc345": {
      "uid": "xyz789uvw012abc345",
      "isOnline": false,
      "lastSeen": 1703846750000,
      "language": "en",
      "notificationEnabled": true,
      "theme": "dark"
    }
  }
}
```

#### Collection: `friendships` - Quản lý quan hệ bạn bè
```json
{
  "friendships": {
    "abc123def456xyz789_xyz789uvw012abc345": {
      "user1": "abc123def456xyz789",
      "user2": "xyz789uvw012abc345",
      "status": "accepted", // pending, accepted, blocked
      "createdAt": 1703846400000,
      "acceptedAt": 1703846500000
    },
    "abc123def456xyz789_def456ghi789xyz012": {
      "user1": "abc123def456xyz789",
      "user2": "def456ghi789xyz012",
      "status": "pending",
      "createdAt": 1703846600000,
      "requestedBy": "abc123def456xyz789"
    },
    "abc123def456xyz789_ghi789jkl012def345": {
      "user1": "abc123def456xyz789",
      "user2": "ghi789jkl012def345",
      "status": "pending",
      "createdAt": 1703846700000,
      "requestedBy": "ghi789jkl012def345"
    }
  }
}
```

#### Collection: `rooms` - Quản lý danh sách cuộc trò chuyện
```json
{
  "rooms": {
    "abc123def456xyz789_xyz789uvw012abc345": {
      "participants": ["abc123def456xyz789", "xyz789uvw012abc345"],
      "createdAt": 1703846400000,
      "lastMessage": "Hello world",
      "lastMessageTime": 1703846500000,
      "roomType": "private", // private, group
      "roomName": "Chat with Jane", // optional for group
      "roomAvatar": "url_to_image" // optional
    },
    "abc123def456xyz789_def456ghi789xyz012": {
      "participants": ["abc123def456xyz789", "def456ghi789xyz012"],
      "createdAt": 1703846600000,
      "lastMessage": "How are you?",
      "lastMessageTime": 1703846700000,
      "roomType": "private"
    },
    "group_1703846800000_abc123": {
      "participants": ["abc123def456xyz789", "xyz789uvw012abc345", "def456ghi789xyz012"],
      "createdAt": 1703846800000,
      "lastMessage": "Group message",
      "lastMessageTime": 1703846900000,
      "roomType": "group",
      "roomName": "Project Team",
      "roomAvatar": "url_to_group_image"
    }
  }
}
```

#### Collection: `messages` - Lưu trữ tin nhắn
```json
{
  "messages": {
    "-NcX1234567890": {
      "id": "-NcX1234567890",
      "roomId": "abc123def456xyz789_xyz789uvw012abc345",
      "senderId": "abc123def456xyz789",
      "senderName": "John Doe",
      "senderAvatar": "https://example.com/avatars/john.jpg",
      "content": "Hello world",
      "messageType": "TEXT",
      "timestamp": 1703846500000,
      "isRead": false
    },
    "-NcX1234567891": {
      "id": "-NcX1234567891",
      "roomId": "abc123def456xyz789_xyz789uvw012abc345",
      "senderId": "xyz789uvw012abc345",
      "senderName": "Jane Smith",
      "senderAvatar": "https://example.com/avatars/jane.jpg",
      "content": "Hi there!",
      "messageType": "TEXT",
      "timestamp": 1703846600000,
      "isRead": true
    },
    "-NcX1234567892": {
      "id": "-NcX1234567892",
      "roomId": "abc123def456xyz789_def456ghi789xyz012",
      "senderId": "abc123def456xyz789",
      "senderName": "John Doe",
      "senderAvatar": "https://example.com/avatars/john.jpg",
      "content": "How are you?",
      "messageType": "TEXT",
      "timestamp": 1703846700000,
      "isRead": false
    }
  }
}
```

### 2.3. Storage
- Lưu trữ avatar, ảnh gửi trong chat
- Cấu trúc: `avatars/{userId}` và `chat_images/{roomId}/{messageId}`

### 2.4. Cloud Messaging
- Gửi notification khi có tin nhắn mới, yêu cầu kết bạn
- Topic: `chat_{roomId}` cho tin nhắn, `friend_request_{userId}` cho yêu cầu kết bạn

### 2.5. Quy tắc đặt tên

#### Room ID:
- **Format:** `{uid1}_{uid2}` (2 Firebase Auth UIDs đã được sort alphabetically)
- **Ví dụ:** 
  - `abc123def456xyz789_xyz789uvw012abc345` (2 Firebase Auth UIDs sorted)
  - `def456ghi789xyz012_ghi789jkl012def345` (Firebase Auth UIDs sorted)
- **Group chat:** `group_{timestamp}_{randomString}`
  - Ví dụ: `group_1703846400000_abc123`

#### Message ID:
- **Sử dụng Firebase auto-generated ID:** `-NcX1234567890`
- **Không tự đặt tên** để tránh conflict
- **Firebase sẽ tự tạo** unique ID khi push message

#### User ID:
- **Format:** `{firebaseAuthUid}` (sử dụng trực tiếp Firebase Auth UID)
- **Ví dụ:** `abc123def456xyz789` (Firebase Auth UID)
- **Không cần prefix:** Sử dụng UID trực tiếp từ Firebase Auth

#### Friendship ID:
- **Format:** `{uid1}_{uid2}` (2 Firebase Auth UIDs đã được sort alphabetically)
- **Ví dụ:** `abc123def456xyz789_xyz789uvw012abc345`
- **Tương tự Room ID** để đảm bảo consistency

### 2.6. Ưu điểm của thiết kế Firestore
- **Performance tốt:** Tách biệt collections theo chức năng
- **Realtime hiệu quả:** Listen đúng collection/document cần thiết
- **Scalable:** Dễ thêm group chat, file sharing
- **Single source of truth:** Friendships collection tránh duplicate data
- **Query mạnh mẽ:** Firestore query capabilities tốt hơn Realtime Database
- **Read status:** Theo dõi trạng thái đọc dễ dàng
- **Flexible relationships:** Dễ thêm status, timestamp, metadata cho friendships
- **Offline support:** Cache và sync tự động tốt hơn

## 3. Luồng chức năng chính

- Đăng nhập/Đăng ký → Home (danh sách user, bạn bè, yêu cầu kết bạn)
- Gửi/nhận yêu cầu kết bạn → Cập nhật Firestore friendships collection
- Chat chỉ với bạn bè (kiểm tra trong friendships với status "accepted")
- Gửi ảnh, emoji qua message type
- Notification khi có sự kiện mới
- Đa ngôn ngữ: lưu lựa chọn trong user, sử dụng resource strings
- Offline: sử dụng Firestore cache và sync tự động

## 4. Đa ngôn ngữ
- Sử dụng `values/strings.xml` và `values-vi/strings.xml`

## 5. Ghi chú
- Cần cấu hình Firebase cho project Android
- Đảm bảo quyền truy cập Storage, Internet 