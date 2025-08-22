# Thiết kế Kiến trúc ChatApp

## 1. Tổng quan Architecture

ChatApp sử dụng **MVVM pattern** với hybrid backend Firebase + Supabase Storage, được xây dựng với Kotlin và các thư viện hiện đại.

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel Layer │    │   Domain Layer  │
│                 │    │                  │    │                 │
│ • Fragments     │◄──►│ • ViewModels     │◄──►│ • Firebase DB   │
│ • Adapters      │    │ • Repositories   │    │ • Firebase Auth │
│ • View Binding  │    │ • Data Classes   │    │ • Supabase      │
│ • Navigation    │    │ • Utils          │    │   Storage       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 2. Cấu trúc Project

```
ChatApp/
├── app/src/main/java/com/example/chatapp/
│   ├── ui/                          # UI Layer
│   │   ├── MainActivity.kt
│   │   ├── detail_chat_screen/      # Chat conversation screen
│   │   │   ├── DetailChatFragment.kt
│   │   │   ├── MessageDiffCallback.kt
│   │   │   └── MessageListAdapter.kt
│   │   ├── detail_profile_screen/   # Profile detail screen  
│   │   │   └── DetailProfileFragment.kt
│   │   ├── friend_screen/           # Friends management
│   │   │   ├── FriendFragment.kt
│   │   │   ├── FriendItemAdapter.kt
│   │   │   └── FriendListDiffCallback.kt
│   │   ├── home_screen/             # Chat list/overview
│   │   │   ├── HomeFragment.kt
│   │   │   ├── ChatListAdapter.kt
│   │   │   └── ChatOverviewDiffCallback.kt
│   │   ├── new_chat_screen/         # New conversation
│   │   │   ├── NewChatFragment.kt
│   │   │   ├── FriendSelectionAdapter.kt
│   │   │   ├── SelectedFriendsAdapter.kt
│   │   │   └── UserDiffCallback.kt
│   │   ├── profile_screen/          # User profile
│   │   │   └── ProfileFragment.kt
│   │   ├── sign_in/                 # Authentication screens
│   │   │   └── SignInFragment.kt
│   │   └── sign_up/
│   │       └── SignUpFragment.kt
│   ├── view_model/                  # ViewModels (MVVM)
│   │   ├── AuthenticationViewModel.kt
│   │   ├── ChatViewModel.kt
│   │   ├── StorageViewModel.kt
│   │   └── UserViewModel.kt
│   ├── domain/                      # Domain Layer
│   │   ├── data/                    # Data classes & entities
│   │   │   ├── ChatOverview.kt
│   │   │   ├── FriendShip.kt
│   │   │   ├── Message.kt
│   │   │   ├── People.kt            # User relationship wrapper
│   │   │   ├── Room.kt
│   │   │   └── User.kt
│   │   ├── repository/              # Repository interfaces & impl
│   │   │   ├── AuthRepository.kt
│   │   │   ├── ChatRepository.kt
│   │   │   ├── StorageRepository.kt
│   │   │   └── UserRepository.kt
│   │   └── service/                 # Background services
│   │       ├── MyFirebaseMessagingService.kt
│   │       └── NotificationReceiver.kt
│   ├── di/                          # Dependency Injection (Hilt)
│   │   ├── AppModule.kt
│   │   └── SupabaseClient.kt
│   ├── utils/                       # Utilities & extensions
│   │   ├── DateUtils.kt
│   │   ├── FriendListUtils.kt
│   │   ├── ImageBinding.kt
│   │   ├── LanguageManager.kt
│   │   ├── PermissionUtils.kt
│   │   ├── Prefs.kt
│   │   └── UserUtils.kt
│   └── MyApplication.kt
├── chat-backend/                    # Node.js notification service
│   ├── server.js
│   ├── services/
│   │   ├── firebaseAdmin.js
│   │   └── notificationService.js
│   ├── updateUserPhoto.js
│   ├── serviceAccountKey.json
│   └── package.json
└── res/
    ├── layout/                      # XML layouts (20 files)
    ├── values/                      # Strings, colors, themes
    ├── values-vi/                   # Vietnamese translations
    ├── values-en/                   # English translations
    ├── navigation/                  # Navigation graphs
    ├── drawable/                    # Vector drawables & images
    ├── font/                        # Custom fonts (13 TTF files)
    └── ...
```

## 3. Hybrid Backend Architecture

### 3.1. Firebase Services (Primary Backend)
- **Firebase Authentication**: User authentication & session management
- **Firebase Firestore**: NoSQL database for all app data
- **Firebase Cloud Messaging**: Push notifications
- **Firebase Analytics**: Usage tracking
- **Firebase Security Rules**: Data access control

### 3.2. Supabase Storage
- **File Storage**: Profile pictures và chat media
- **CDN**: Fast global content delivery
- **Storage Policies**: Secure file access control

### 3.3. Node.js Microservice
- **Notification Service**: FCM push notification handling
- **Photo Management**: Handle avatar updates
- **Background Jobs**: Cleanup & maintenance tasks

## 4. Firebase Firestore Database Structure

### 4.1. Collection: `users` - User profiles
```javascript
// Document ID: {firebaseAuthUID}
{
  uid: "abc123def456",              // Firebase Auth UID
  email: "user@example.com",
  displayName: "John Doe",
  photoUrl: "https://supabase-storage-url/avatars/abc123.jpg",
  phoneNumber: "+1234567890",
  dateOfBirth: "1990-01-01",
  isEmailVerified: true,
  createdAt: Timestamp,
  updatedAt: Timestamp
}
```

### 4.2. Collection: `friendships` - Friend relationships
```javascript
// Document ID: {user1UID}_{user2UID} (sorted alphabetically)
{
  id: "abc123def456_xyz789uvw012",
  user1UID: "abc123def456",
  user2UID: "xyz789uvw012",
  status: "pending", // "pending", "accepted", "blocked"
  requestedBy: "abc123def456",
  createdAt: Timestamp,
  acceptedAt: Timestamp // null if pending
}
```

### 4.3. Collection: `rooms` - Chat conversations
```javascript
// Document ID: {user1UID}_{user2UID} for private chats
{
  id: "abc123def456_xyz789uvw012",
  participants: ["abc123def456", "xyz789uvw012"],
  roomType: "private", // "private" or "group"
  roomName: null, // for group chats
  roomAvatar: null, // for group chats
  lastMessage: "Hello!",
  lastMessageTime: Timestamp,
  createdAt: Timestamp,
  updatedAt: Timestamp
}
```

### 4.4. Collection: `messages` - Chat messages
```javascript
// Document ID: auto-generated
// Parent: rooms/{roomId}/messages
{
  uid: "auto-generated-id",
  localId: "client-generated-uuid", // for optimistic updates
  roomId: "abc123def456_xyz789uvw012",
  senderId: "abc123def456",
  senderName: "John Doe",
  senderAvatar: "https://supabase-storage-url/avatars/abc123.jpg",
  content: "Hello there!",
  messageType: "TEXT", // "TEXT", "IMAGE", "VIDEO", "FILE"
  messageStatus: "SENT", // "SENDING", "SENT", "DELIVERED", "READ"
  timestamp: Timestamp,
  isRead: false,
  replyTo: null, // reference to another message
  editedAt: null
}
```


## 5. Supabase Storage Structure
```
Storage Buckets:
├── avatars/                    # User profile pictures
│   └── {user_uid}.{ext}       # e.g., abc123def456.jpg
├── chat_images/               # Chat media files  
│   └── {message_local_id}.{ext} # e.g., uuid-1234.png
```

### 5.1. Storage Policies
```sql
-- Avatars bucket - users can upload own avatar
CREATE POLICY "Users can upload own avatar" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);

-- Chat images - users can upload to their conversations  
CREATE POLICY "Users can upload chat images" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'chat_images' AND auth.uid() IS NOT NULL);

-- Public read access for all files
CREATE POLICY "Public read access" ON storage.objects
  FOR SELECT USING (true);
```

### 5.2. Firebase Storage Integration
- **Avatar URLs**: Stored in Supabase, referenced in Firebase Firestore user documents
- **Chat Media**: Supabase storage URLs saved in Firebase message documents
- **CDN Performance**: Global content delivery through Supabase CDN

## 6. ID Naming Conventions

### 6.1. Room ID Format
- **Private Chat**: `{uid1}_{uid2}` (Firebase Auth UIDs sorted alphabetically)
- **Group Chat**: `group_{timestamp}_{randomString}`
- **Examples**: 
  - `abc123def456_xyz789uvw012` (private)
  - `group_1703846400000_abc123` (group)

### 6.2. Message ID
- **Primary**: Firebase auto-generated document ID
- **Local ID**: Client-generated UUID for optimistic updates
- **Format**: Firebase auto-ID hoặc `550e8400-e29b-41d4-a716-446655440000`

### 6.3. User ID
- **Format**: Firebase Auth UID (used directly)
- **Example**: `abc123def456xyz789`

### 6.4. Friendship ID  
- **Format**: `{uid1}_{uid2}` (sorted alphabetically, same as Room ID)
- **Example**: `abc123def456_xyz789uvw012`

## 7. Real-time Features

### 7.1. Firebase Realtime Listeners
```kotlin
// Listen to new messages in a room
FirebaseFirestore.getInstance()
  .collection("rooms")
  .document(roomId)
  .collection("messages")
  .orderBy("timestamp", Query.Direction.DESCENDING)
  .addSnapshotListener { snapshot, error ->
    // Handle new messages
  }

// Listen to friendship status changes
FirebaseFirestore.getInstance()
  .collection("friendships")
  .whereEqualTo("user1UID", currentUserId)
  .addSnapshotListener { snapshot, error ->
    // Handle friendship updates
  }
```

### 7.2. Optimistic Updates
1. **Send Message**: Insert locally with `SENDING` status
2. **Upload to Firebase**: Update status to `SENT` on success
3. **Real-time Update**: Other users receive via Firestore listeners
4. **Read Receipts**: Update `messageReadStatus` subcollection
5. **Media Upload**: Upload to Supabase Storage, save URL to Firebase

## 8. Modern Android Features & Technologies

### 8.1. Core Android Technologies
- **Kotlin 2.2.10**: Latest Kotlin version với performance improvements
- **Android API 35**: Target và compile SDK mới nhất (minSdk 34)
- **Material Design 3**: Modern UI components và theming
- **ViewBinding**: Type-safe view references thay thế findViewById
- **Navigation Component**: Safe Args navigation với type safety

### 8.2. Dependency Injection & Build
- **Hilt 2.56.2**: Dependency injection built on Dagger
- **KSP (Kotlin Symbol Processing)**: Faster annotation processing
- **Gradle KTS**: Kotlin DSL cho build scripts
- **Module Organization**: Separated DI modules for better maintainability

### 8.3. Modern Libraries & SDK
- **Firebase SDK**: Complete backend solution
  - Firestore for NoSQL database
  - Authentication for user management
  - Cloud Messaging for notifications
  - Analytics for usage tracking
- **Supabase Kotlin SDK 3.2.2**: Storage-only client
  - Storage for file uploads
  - CDN for fast media delivery
- **Room Database**: Local caching with coroutines support
- **Ktor Client**: HTTP client for network operations
- **Glide**: Advanced image loading và caching
- **Circle ImageView**: Optimized circular image displays
- **Image Picker**: Modern gallery selection

### 8.4. UI & User Experience
- **DiffUtil Callbacks**: Efficient RecyclerView updates
- **Splash Screen API**: Modern splash screen implementation
- **Custom Fonts**: 13 TTF font files for typography
- **Multi-language**: Vietnamese và English support
- **Date/Time Utils**: Localized formatting utilities

## 9. Main User Flows

### 9.1. Authentication Flow
```
Sign Up/Sign In (Firebase Auth) 
    ↓
Sync user data to Supabase
    ↓  
Navigate to Home Screen
```

### 9.2. Friend Management Flow
```
View All Users → Send Friend Request → Supabase friendships table
    ↓
Friend receives notification (FCM)
    ↓
Accept/Decline → Update friendship status
    ↓
Real-time UI update via Supabase Realtime
```

### 9.3. Chat Flow
```
Select Friend (status = 'accepted') 
    ↓
Create/Open Room → Generate room_id
    ↓
Send Message → Optimistic UI update (SENDING)
    ↓
Upload to Supabase → Update status (SENT)
    ↓
Real-time delivery to recipient
    ↓
Read Receipt → Update message_read_status
```

### 9.4. Media Sharing Flow
```
Select Image/GIF from Gallery or System Keyboard
    ↓
Generate local_id → Show preview dialog
    ↓
Upload to Supabase Storage → Get public URL
    ↓
Send message with IMAGE type + URL
    ↓
Real-time delivery with media content
```

## 10. Key Data Classes & UI Models

### 10.1. Core Data Models
- **User.kt**: User profile information (uid, email, displayName, photoUrl)
- **Message.kt**: Chat message với type support (TEXT, IMAGE, VIDEO, FILE)
- **Room.kt**: Chat conversation container
- **FriendShip.kt**: Friend relationship status và metadata
- **ChatOverview.kt**: Chat list item với last message preview

### 10.2. UI Helper Classes  
- **People.kt**: Wrapper class cho user relationships
  ```kotlin
  data class People(
    var user: User,
    var isFriend: Boolean = false,
    var isRequestSent: Boolean = false,
    var isRequestReceived: Boolean = false
  )
  ```
- **PeopleAction**: Enum cho friend request actions
- **FriendListItem**: Sealed class cho friend list UI (Header/PersonItem)

### 10.3. DiffUtil Callbacks
- **MessageDiffCallback**: Efficient message list updates
- **FriendListDiffCallback**: Friend list change detection
- **ChatOverviewDiffCallback**: Chat overview updates
- **UserDiffCallback**: User selection updates

### 10.4. Utility Classes
- **DateUtils.kt**: Date formatting và time calculations
- **FriendListUtils.kt**: Friend list management helpers
- **ImageBinding.kt**: Data binding adapters cho image loading
- **LanguageManager.kt**: Multi-language support management
- **PermissionUtils.kt**: Runtime permission handling
- **Prefs.kt**: SharedPreferences wrapper
- **UserUtils.kt**: User-related helper functions

## 11. Offline Support Strategy

### 11.1. Room Database Caching
- **Users**: Cache user profiles từ Firebase Firestore
- **Messages**: Cache recent messages per room từ Firebase
- **Friendships**: Cache friend relationships từ Firebase
- **Sync Strategy**: Background sync với Firebase when network available

### 11.2. Optimistic Updates
- **Message Sending**: Show immediately with SENDING status
- **Friend Requests**: Show pending state instantly
- **Profile Updates**: Apply changes locally first

## 12. Internationalization (i18n)

### 12.1. Supported Languages
- **English** (`values/strings.xml`)
- **Vietnamese** (`values-vi/strings.xml`)
- **System Detection**: Use `Locale.getDefault()`

### 12.2. Implementation
```xml
<!-- values/strings.xml -->
<string name="app_name">ChatApp</string>
<string name="send_message">Send</string>

<!-- values-vi/strings.xml -->
<string name="app_name">Ứng dụng Chat</string>
<string name="send_message">Gửi</string>
```

## 13. Security Considerations

### 13.1. Firebase Security Rules
- **User Data**: Users can only read/write own profile documents
- **Messages**: Users can only access messages in rooms they participate in
- **Friendships**: Users can only manage friendships they're involved in

### 13.2. Authentication
- **Firebase Auth**: Secure token-based authentication
- **Firestore Security**: Rules-based access control
- **API Security**: All requests require valid Firebase authentication

### 13.3. Storage Security
- **Bucket Policies**: Users can only upload to designated buckets
- **Content Validation**: MIME type checking on upload
- **Public URLs**: Read-only access for media files

## 14. Performance Optimizations

### 14.1. Database
- **Firestore Indexes**: Optimized queries on message collections
- **Pagination**: Load messages in batches using Firebase pagination
- **Offline Persistence**: Firebase local caching for better performance

### 14.2. Real-time
- **Selective Listeners**: Only listen to relevant Firebase collections
- **Connection Management**: Proper cleanup of Firestore listeners
- **Batch Operations**: Group related Firestore writes together

### 14.3. Storage
- **Image Compression**: Reduce file sizes before upload
- **Content Deduplication**: Hash-based naming for duplicates
- **CDN**: Fast global content delivery via Supabase

---

## Setup Requirements

### Prerequisites
- **Android Studio**: Giraffe+ (2023.2.1+) with Kotlin 1.9+ support
- **Java**: JDK 11 (configured in build.gradle.kts)
- **Android SDK**: API 35 (target), API 34 (minimum)
- **Firebase Project**: With Auth và FCM enabled
- **Supabase Project**: With storage configured (chỉ dùng storage)
- **Node.js**: v18+ for notification service backend

### Environment Configuration
```properties
# local.properties
SUPABASE_URL="https://your-project.supabase.co"
SUPABASE_PUBLISHABLE_KEY="your_supabase_anon_key"
```

### Required Files
- `app/google-services.json`: Firebase configuration
- `chat-backend/serviceAccountKey.json`: Firebase Admin SDK key
- Firebase Firestore collections setup (users, rooms, messages, etc.)
- Firebase Security Rules configuration
- Supabase Storage bucket policies configuration 