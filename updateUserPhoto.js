/**
 * Script để cập nhật photoURL của Firebase User
 * Chạy 1 lần: node updateUserPhoto.js
 */

const { admin } = require('./services/firebaseAdmin');

/**
 * Cập nhật photoURL cho user
 * @param {string} userId - UID của user
 * @param {string} photoUrl - URL ảnh mới
 */
async function updateUserPhoto(userId, photoUrl) {
  try {
    console.log(`🔄 Đang cập nhật photo cho user: ${userId}`);
    console.log(`📷 Photo URL: ${photoUrl}`);
    
    // Validate URL
    try {
      new URL(photoUrl);
    } catch (error) {
      throw new Error('❌ photoUrl không đúng định dạng URL');
    }
    
    // Cập nhật Firebase Auth
    await admin.auth().updateUser(userId, {
      photoURL: photoUrl
    });
    
    console.log('✅ Cập nhật Firebase Auth thành công!');
    
    // Verify cập nhật
    const userRecord = await admin.auth().getUser(userId);
    console.log('📋 Thông tin user sau cập nhật:');
    console.log(`   - UID: ${userRecord.uid}`);
    console.log(`   - Email: ${userRecord.email}`);
    console.log(`   - Display Name: ${userRecord.displayName}`);
    console.log(`   - Photo URL: ${userRecord.photoURL}`);
    
    return {
      success: true,
      userId: userId,
      newPhotoURL: userRecord.photoURL
    };
    
  } catch (error) {
    console.error('❌ Lỗi cập nhật:', error.message);
    
    if (error.code === 'auth/user-not-found') {
      console.error(`❌ Không tìm thấy user với ID: ${userId}`);
    }
    
    throw error;
  }
}

// Main function
async function main() {
  try {
    console.log('🚀 Bắt đầu cập nhật user photo...\n');
    
    // ==========================================
    // THAY ĐỔI CÁC GIÁ TRỊ NÀY:
    // ==========================================
    const USER_ID = 'oR7RT4ZRCOPv0wwfcuNQPBYPlfD2';  // Thay bằng userId thật
    const PHOTO_URL = 'https://sketchok.com/images/articles/06-anime/003-pokemon/01/10.jpg'
    // Kiểm tra input
    if (USER_ID === 'YOUR_USER_ID_HERE') {
      console.log('⚠️  Vui lòng thay đổi USER_ID trong file updateUserPhoto.js');
      console.log('📝 Mở file và sửa dòng: const USER_ID = "YOUR_ACTUAL_USER_ID";');
      return;
    }
    
    if (PHOTO_URL === 'https://example.com/photo.jpg') {
      console.log('⚠️  Vui lòng thay đổi PHOTO_URL trong file updateUserPhoto.js');
      console.log('📝 Mở file và sửa dòng: const PHOTO_URL = "YOUR_ACTUAL_PHOTO_URL";');
      return;
    }
    
    // Thực hiện cập nhật
    const result = await updateUserPhoto(USER_ID, PHOTO_URL);
    
    console.log('\n🎉 Hoàn thành!');
    console.log('📊 Kết quả:', JSON.stringify(result, null, 2));
    
  } catch (error) {
    console.error('\n💥 Script thất bại:', error.message);
    process.exit(1);
  }
}

// Hướng dẫn sử dụng
console.log(`
🔧 HƯỚNG DẪN SỬ DỤNG:

1. Mở file updateUserPhoto.js
2. Thay đổi USER_ID thành userId thật từ Firebase Auth  
3. Thay đổi PHOTO_URL thành URL ảnh thật
4. Chạy: node updateUserPhoto.js

💡 VÍ DỤ:
const USER_ID = 'abc123xyz789';
const PHOTO_URL = 'https://storage.googleapis.com/your-bucket/user-photos/photo.jpg';

📌 LƯU Ý:
- User phải tồn tại trong Firebase Auth
- Photo URL phải là URL hợp lệ và accessible
- Script sẽ cập nhật photoURL trong Firebase Auth
`);

// Chạy script
if (require.main === module) {
  main().then(() => {
    console.log('\n👋 Script hoàn thành, thoát...');
    process.exit(0);
  });
}

module.exports = { updateUserPhoto };
