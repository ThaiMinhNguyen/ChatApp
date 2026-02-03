# Hướng Dẫn 20 Component Phổ Biến Trong Flutter

## 1. Container
Widget cơ bản nhất để tạo box với padding, margin, border, background.

```dart
Container(
  width: 200,
  height: 100,
  padding: EdgeInsets.all(16),
  margin: EdgeInsets.symmetric(vertical: 8),
  decoration: BoxDecoration(
    color: Colors.blue,
    borderRadius: BorderRadius.circular(12),
    boxShadow: [
      BoxShadow(
        color: Colors.grey.withOpacity(0.5),
        spreadRadius: 2,
        blurRadius: 5,
      ),
    ],
  ),
  child: Text('Hello Flutter'),
)
```

## 2. Text
Hiển thị văn bản với style tùy chỉnh.

```dart
Text(
  'Xin chào Flutter',
  style: TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
    color: Colors.blue,
    letterSpacing: 1.2,
    decoration: TextDecoration.underline,
  ),
  textAlign: TextAlign.center,
  maxLines: 2,
  overflow: TextOverflow.ellipsis,
)
```

## 3. Row & Column
Sắp xếp widget theo hàng ngang (Row) hoặc cột dọc (Column).

```dart
// Row - Hàng ngang
Row(
  mainAxisAlignment: MainAxisAlignment.spaceBetween,
  crossAxisAlignment: CrossAxisAlignment.center,
  children: [
    Icon(Icons.home),
    Text('Trang chủ'),
    Icon(Icons.arrow_forward),
  ],
)

// Column - Cột dọc
Column(
  mainAxisAlignment: MainAxisAlignment.center,
  crossAxisAlignment: CrossAxisAlignment.start,
  children: [
    Text('Tiêu đề'),
    Text('Nội dung'),
    ElevatedButton(onPressed: () {}, child: Text('OK')),
  ],
)
```

## 4. ListView
Danh sách cuộn được, dùng cho nhiều items.

```dart
// ListView cơ bản
ListView(
  children: [
    ListTile(title: Text('Item 1')),
    ListTile(title: Text('Item 2')),
    ListTile(title: Text('Item 3')),
  ],
)

// ListView.builder - Tối ưu cho danh sách dài
ListView.builder(
  itemCount: 100,
  itemBuilder: (context, index) {
    return ListTile(
      leading: CircleAvatar(child: Text('$index')),
      title: Text('Item $index'),
      subtitle: Text('Mô tả item $index'),
      trailing: Icon(Icons.arrow_forward),
      onTap: () => print('Tapped $index'),
    );
  },
)

// ListView.separated - Có divider
ListView.separated(
  itemCount: 20,
  separatorBuilder: (context, index) => Divider(),
  itemBuilder: (context, index) => ListTile(title: Text('Item $index')),
)
```

## 5. GridView
Hiển thị items dạng lưới.

```dart
GridView.count(
  crossAxisCount: 2, // 2 cột
  crossAxisSpacing: 10,
  mainAxisSpacing: 10,
  padding: EdgeInsets.all(16),
  children: List.generate(20, (index) {
    return Container(
      color: Colors.blue[100 * (index % 9)],
      child: Center(child: Text('Item $index')),
    );
  }),
)

// GridView.builder - Tối ưu hơn
GridView.builder(
  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
    crossAxisCount: 3,
    childAspectRatio: 1.0,
  ),
  itemCount: 50,
  itemBuilder: (context, index) {
    return Card(
      child: Center(child: Text('$index')),
    );
  },
)
```

## 6. Stack
Xếp chồng các widget lên nhau.

```dart
Stack(
  children: [
    Container(
      width: 300,
      height: 200,
      color: Colors.blue,
    ),
    Positioned(
      top: 20,
      right: 20,
      child: Icon(Icons.star, color: Colors.yellow, size: 50),
    ),
    Positioned(
      bottom: 10,
      left: 10,
      child: Text('Overlay Text', style: TextStyle(color: Colors.white)),
    ),
  ],
)
```

## 7. Card
Tạo card với shadow và border radius.

```dart
Card(
  elevation: 4,
  margin: EdgeInsets.all(16),
  shape: RoundedRectangleBorder(
    borderRadius: BorderRadius.circular(12),
  ),
  child: Padding(
    padding: EdgeInsets.all(16),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Tiêu đề Card', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
        SizedBox(height: 8),
        Text('Nội dung của card ở đây'),
      ],
    ),
  ),
)
```

## 8. Image
Hiển thị hình ảnh từ nhiều nguồn.

```dart
// Từ assets
Image.asset(
  'assets/images/logo.png',
  width: 200,
  height: 200,
  fit: BoxFit.cover,
)

// Từ network
Image.network(
  'https://picsum.photos/200',
  loadingBuilder: (context, child, loadingProgress) {
    if (loadingProgress == null) return child;
    return CircularProgressIndicator();
  },
  errorBuilder: (context, error, stackTrace) {
    return Icon(Icons.error);
  },
)

// Từ file
Image.file(File('/path/to/image.jpg'))
```

## 9. TextField
Input nhập liệu từ người dùng.

```dart
TextField(
  decoration: InputDecoration(
    labelText: 'Tên đăng nhập',
    hintText: 'Nhập tên đăng nhập',
    prefixIcon: Icon(Icons.person),
    suffixIcon: Icon(Icons.clear),
    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(8),
    ),
  ),
  keyboardType: TextInputType.text,
  obscureText: false,
  maxLength: 50,
  onChanged: (value) => print('Changed: $value'),
  onSubmitted: (value) => print('Submitted: $value'),
)

// TextField với controller
final controller = TextEditingController();
TextField(
  controller: controller,
  decoration: InputDecoration(labelText: 'Email'),
)
// Lấy giá trị: controller.text
```

## 10. ElevatedButton, TextButton, OutlinedButton
Các loại button khác nhau.

```dart
// ElevatedButton - Nút nổi
ElevatedButton(
  onPressed: () => print('Pressed'),
  style: ElevatedButton.styleFrom(
    backgroundColor: Colors.blue,
    foregroundColor: Colors.white,
    padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(8),
    ),
  ),
  child: Text('Elevated Button'),
)

// TextButton - Nút phẳng
TextButton(
  onPressed: () {},
  child: Text('Text Button'),
)

// OutlinedButton - Nút viền
OutlinedButton(
  onPressed: () {},
  style: OutlinedButton.styleFrom(
    side: BorderSide(color: Colors.blue, width: 2),
  ),
  child: Text('Outlined Button'),
)

// IconButton
IconButton(
  icon: Icon(Icons.favorite),
  color: Colors.red,
  iconSize: 32,
  onPressed: () {},
)
```

## 11. AppBar
Thanh tiêu đề ở đầu màn hình.

```dart
AppBar(
  title: Text('Trang Chủ'),
  centerTitle: true,
  backgroundColor: Colors.blue,
  elevation: 4,
  leading: IconButton(
    icon: Icon(Icons.menu),
    onPressed: () {},
  ),
  actions: [
    IconButton(icon: Icon(Icons.search), onPressed: () {}),
    IconButton(icon: Icon(Icons.more_vert), onPressed: () {}),
  ],
)
```

## 12. Scaffold
Cấu trúc cơ bản của một màn hình.

```dart
Scaffold(
  appBar: AppBar(title: Text('My App')),
  body: Center(child: Text('Nội dung')),
  floatingActionButton: FloatingActionButton(
    onPressed: () {},
    child: Icon(Icons.add),
  ),
  drawer: Drawer(
    child: ListView(
      children: [
        DrawerHeader(
          decoration: BoxDecoration(color: Colors.blue),
          child: Text('Menu', style: TextStyle(color: Colors.white, fontSize: 24)),
        ),
        ListTile(title: Text('Trang chủ'), onTap: () {}),
        ListTile(title: Text('Cài đặt'), onTap: () {}),
      ],
    ),
  ),
  bottomNavigationBar: BottomNavigationBar(
    items: [
      BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
      BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'),
      BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
    ],
    currentIndex: 0,
    onTap: (index) {},
  ),
)
```

## 13. Dialog & AlertDialog
Hiển thị popup dialog.

```dart
// AlertDialog
showDialog(
  context: context,
  builder: (context) => AlertDialog(
    title: Text('Xác nhận'),
    content: Text('Bạn có chắc muốn xóa?'),
    actions: [
      TextButton(
        onPressed: () => Navigator.pop(context),
        child: Text('Hủy'),
      ),
      ElevatedButton(
        onPressed: () {
          // Xử lý xóa
          Navigator.pop(context);
        },
        child: Text('Xóa'),
      ),
    ],
  ),
)

// SimpleDialog
showDialog(
  context: context,
  builder: (context) => SimpleDialog(
    title: Text('Chọn màu'),
    children: [
      SimpleDialogOption(
        child: Text('Đỏ'),
        onPressed: () => Navigator.pop(context, 'red'),
      ),
      SimpleDialogOption(
        child: Text('Xanh'),
        onPressed: () => Navigator.pop(context, 'blue'),
      ),
    ],
  ),
)
```

## 14. SnackBar
Thông báo ngắn ở dưới màn hình.

```dart
ScaffoldMessenger.of(context).showSnackBar(
  SnackBar(
    content: Text('Đã lưu thành công!'),
    duration: Duration(seconds: 3),
    action: SnackBarAction(
      label: 'Hoàn tác',
      onPressed: () {},
    ),
    backgroundColor: Colors.green,
  ),
)
```

## 15. CircularProgressIndicator & LinearProgressIndicator
Hiển thị loading.

```dart
// Circular - Vòng tròn
CircularProgressIndicator(
  valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
  strokeWidth: 4,
)

// Linear - Thanh ngang
LinearProgressIndicator(
  value: 0.7, // 70% (null = indeterminate)
  backgroundColor: Colors.grey[200],
  valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
)
```

## 16. Checkbox, Radio, Switch
Các widget chọn lựa.

```dart
// Checkbox
bool isChecked = false;
Checkbox(
  value: isChecked,
  onChanged: (value) {
    setState(() => isChecked = value!);
  },
)

// Radio
int selectedValue = 1;
Column(
  children: [
    Radio(value: 1, groupValue: selectedValue, onChanged: (val) => setState(() => selectedValue = val!)),
    Radio(value: 2, groupValue: selectedValue, onChanged: (val) => setState(() => selectedValue = val!)),
  ],
)

// Switch
bool isSwitched = false;
Switch(
  value: isSwitched,
  onChanged: (value) => setState(() => isSwitched = value),
  activeColor: Colors.blue,
)
```

## 17. Slider
Thanh trượt chọn giá trị.

```dart
double sliderValue = 50;
Slider(
  value: sliderValue,
  min: 0,
  max: 100,
  divisions: 10,
  label: sliderValue.round().toString(),
  onChanged: (value) {
    setState(() => sliderValue = value);
  },
)
```

## 18. TabBar & TabBarView
Tạo tabs chuyển đổi giữa các màn hình.

```dart
DefaultTabController(
  length: 3,
  child: Scaffold(
    appBar: AppBar(
      title: Text('Tabs Demo'),
      bottom: TabBar(
        tabs: [
          Tab(icon: Icon(Icons.home), text: 'Home'),
          Tab(icon: Icon(Icons.search), text: 'Search'),
          Tab(icon: Icon(Icons.person), text: 'Profile'),
        ],
      ),
    ),
    body: TabBarView(
      children: [
        Center(child: Text('Home Tab')),
        Center(child: Text('Search Tab')),
        Center(child: Text('Profile Tab')),
      ],
    ),
  ),
)
```

## 19. PageView
Vuốt chuyển trang như carousel.

```dart
PageView(
  children: [
    Container(color: Colors.red, child: Center(child: Text('Page 1'))),
    Container(color: Colors.blue, child: Center(child: Text('Page 2'))),
    Container(color: Colors.green, child: Center(child: Text('Page 3'))),
  ],
)

// PageView.builder
PageView.builder(
  itemCount: 10,
  itemBuilder: (context, index) {
    return Container(
      color: Colors.primaries[index % Colors.primaries.length],
      child: Center(child: Text('Page $index', style: TextStyle(fontSize: 32))),
    );
  },
)
```

## 20. GestureDetector & InkWell
Bắt các gesture như tap, long press, swipe.

```dart
// GestureDetector
GestureDetector(
  onTap: () => print('Tapped'),
  onDoubleTap: () => print('Double tapped'),
  onLongPress: () => print('Long pressed'),
  onPanUpdate: (details) => print('Swiping: ${details.delta}'),
  child: Container(
    width: 200,
    height: 100,
    color: Colors.blue,
    child: Center(child: Text('Tap me')),
  ),
)

// InkWell - Có ripple effect
InkWell(
  onTap: () => print('Tapped'),
  splashColor: Colors.blue.withOpacity(0.3),
  child: Container(
    padding: EdgeInsets.all(16),
    child: Text('InkWell với ripple effect'),
  ),
)
```

## Bonus: Expanded & Flexible
Điều chỉnh kích thước widget trong Row/Column.

```dart
Row(
  children: [
    Expanded(
      flex: 2,
      child: Container(color: Colors.red, height: 50),
    ),
    Expanded(
      flex: 1,
      child: Container(color: Colors.blue, height: 50),
    ),
  ],
)

// Flexible - Linh hoạt hơn
Row(
  children: [
    Flexible(
      fit: FlexFit.tight, // hoặc FlexFit.loose
      child: Container(color: Colors.green, height: 50),
    ),
  ],
)
```

## Tips Quan Trọng

1. **StatelessWidget vs StatefulWidget**: Dùng StatelessWidget khi UI không thay đổi, StatefulWidget khi cần setState()

2. **const constructor**: Thêm `const` trước widget để tối ưu performance
   ```dart
   const Text('Hello') // Tốt hơn Text('Hello')
   ```

3. **Keys**: Dùng key khi cần Flutter phân biệt các widget giống nhau
   ```dart
   ListView(children: items.map((item) => Text(item, key: ValueKey(item))).toList())
   ```

4. **BuildContext**: Luôn truyền context đúng, đặc biệt khi dùng Navigator, Theme, MediaQuery

5. **Dispose**: Nhớ dispose controller trong StatefulWidget
   ```dart
   @override
   void dispose() {
     controller.dispose();
     super.dispose();
   }
   ```

---

**Lưu ý**: Để chạy code, cần thêm vào trong widget tree và có StatefulWidget nếu dùng setState().
