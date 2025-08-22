# ChatApp

Modern real-time chat application được xây dựng với Kotlin, sử dụng Firebase Firestore cho database và Supabase Storage cho file storage, cùng với các công nghệ Android hiện đại.

## Tính năng

### Đăng nhập, đăng ký
- Đăng nhập, đăng ký sử dụng Firebase Authentication (email/password)
- Tự động đồng bộ user data với Firebase Firestore

### Quản lý bạn bè
- Xem tất cả người dùng trong hệ thống
- Xem danh sách bạn bè
- Xem danh sách yêu cầu kết bạn đã nhận/đã gửi
- Gửi/hủy/chấp nhận/từ chối yêu cầu kết bạn
- Xóa bạn bè

### Profile cá nhân
- Xem và chỉnh sửa profile (tên hiển thị, avatar)
- Upload ảnh đại diện lên Supabase Storage
- Cập nhật thông tin cá nhân realtime

### Tin nhắn và Chat
- Danh sách cuộc hội thoại với unread count và badge
- Chi tiết cuộc hội thoại với scroll-to-load-more messaging
- Gửi tin nhắn văn bản (chỉ chat với bạn bè đã kết nối)
- Gửi ảnh/video từ gallery với Image Picker library
- Hỗ trợ emoji và media sharing
- Message status tracking: sending/sent/delivered/read
- Realtime messaging với Firebase Firestore listeners
- Message adapter với DiffUtil callbacks cho performance optimization

### Push Notifications
- Notification khi có tin nhắn mới
- Notification khi có yêu cầu kết bạn
- Deep link từ notification đến màn hình tương ứng
- Node.js backend xử lý FCM notifications

### Offline Support
- Cache dữ liệu với Room Database
- Xem tin nhắn và danh sách bạn bè khi offline
- Đồng bộ tự động khi có mạng

### UI/UX
- Material Design 3
- Dark/Light theme support
- Đa ngôn ngữ (Tiếng Việt, English)
- Navigation Component với Safe Args
- Loading states và error handling

## Công nghệ sử dụng

### Frontend (Android)
- **Kotlin 2.2.10** - Ngôn ngữ chính với latest KSP
- **Android API 35** - Target SDK mới nhất (minSdk 34)
- **Jetpack Navigation** - Navigation với Safe Args
- **Hilt 2.56.2** - Dependency Injection
- **Material Design 3** - Modern UI Components
- **ViewBinding** - Type-safe view references
- **Kotlin Coroutines** - Async programming
- **Glide** - Advanced image loading và caching
- **Room** - Local database/caching với coroutines

### Backend & Services
- **Firebase Services** - Primary backend platform
  - Firebase Authentication - Secure user authentication
  - Firebase Firestore - NoSQL database với real-time listeners
  - Firebase Cloud Messaging (FCM) - Cross-platform notifications
  - Firebase Analytics - Usage tracking
- **Supabase 3.2.2** - Storage service only
  - File storage với CDN cho avatars và chat media
  - Storage policies cho secure access
- **Node.js + Express** - Push notification microservice

### Modern Architecture & Libraries
- **MVVM Pattern** - Clean architecture separation
- **Repository Pattern** - Data abstraction layer
- **DiffUtil Callbacks** - Efficient RecyclerView updates
- **Circle ImageView** - Optimized profile pictures
- **Image Picker** - Modern gallery selection
- **Ktor Client** - HTTP client for network operations
- **Custom Utils** - Date formatting, permissions, language management

## Cài đặt

### Prerequisites
- **Android Studio**: Giraffe+ (2023.2.1+) hoặc mới hơn
- **Android SDK**: API 35 (target), API 34 (minimum)
- **Java**: JDK 11
- **Kotlin**: 2.2.10 (được cấu hình trong project)

### Setup Steps
1. **Clone project về máy**
   ```bash
   git clone <repository-url>
   cd ChatApp
   ```

2. **Tạo `local.properties`** trong root directory:
   ```properties
   SUPABASE_URL="your_supabase_project_url"
   SUPABASE_PUBLISHABLE_KEY="your_supabase_anon_key"
   ```

3. **Cấu hình Firebase**
   - Tạo Firebase project tại [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` và đặt vào `app/` folder
   - Enable Authentication (Email/Password)
   - Enable Cloud Messaging

4. **Cấu hình Firebase Firestore**
   - Tạo Firestore database tại [Firebase Console](https://console.firebase.google.com)
   - Tạo các collections cần thiết (users, rooms, messages, friendships)
   - Cấu hình Security Rules cho data protection

5. **Cấu hình Supabase Storage**
   - Tạo project tại [Supabase Dashboard](https://app.supabase.com)
   - Tạo storage buckets: `avatars`, `chat_images`, `chat_files`
   - Cấu hình storage policies

6. **Setup Node.js Backend** (cho push notifications)
   ```bash
   cd chat-backend
   npm install
   # Đặt serviceAccountKey.json vào thư mục này
   npm start  # hoặc npm run dev cho development
   ```

7. **Build và chạy app**
   ```bash
   # Từ root directory của Android project
   ./gradlew assembleDebug
   ./gradlew installDebug
   
   # Hoặc build qua Android Studio
   ```

## Architecture

### MVVM Pattern với Clean Architecture
```
ChatApp/
├── app/src/main/java/com/example/chatapp/
│   ├── ui/                     # UI Layer
│   │   ├── MainActivity.kt     # Single Activity architecture
│   │   ├── detail_chat_screen/ # Chat conversation UI
│   │   ├── detail_profile_screen/ # Profile details UI  
│   │   ├── friend_screen/      # Friend management UI
│   │   ├── home_screen/        # Chat list overview UI
│   │   ├── new_chat_screen/    # New conversation UI
│   │   ├── profile_screen/     # User profile UI
│   │   ├── sign_in/           # Authentication UI
│   │   └── sign_up/
│   ├── view_model/            # ViewModels (MVVM pattern) 
│   │   ├── AuthenticationViewModel.kt
│   │   ├── ChatViewModel.kt
│   │   ├── StorageViewModel.kt
│   │   └── UserViewModel.kt
│   ├── domain/                # Domain Layer
│   │   ├── data/             # Data classes (User, Message, etc.)
│   │   ├── repository/       # Repository pattern (Firebase & Supabase)
│   │   └── service/          # Background services (FCM, etc.)
│   ├── di/                   # Dependency Injection (Hilt modules)
│   │   ├── AppModule.kt
│   │   └── SupabaseClient.kt
│   └── utils/                # Utilities & helper functions
├── chat-backend/             # Node.js notification microservice
│   ├── server.js
│   ├── services/
│   └── package.json
└── res/                      # Android resources
    ├── layout/              # 20 XML layout files
    ├── values/              # Strings, colors, themes
    ├── values-vi/           # Vietnamese translations
    ├── drawable/            # Vector graphics & images
    └── font/                # Custom typography (13 fonts)
```

## Contributing
Mọi đóng góp, ý kiến đều được chào đón! Vui lòng:
1. Fork repository
2. Tạo feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License
MIT License - xem file LICENSE để biết thêm chi tiết. 