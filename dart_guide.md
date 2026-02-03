# Hướng dẫn ngôn ngữ Dart cho Flutter

## 1. Giới thiệu

Dart là ngôn ngữ lập trình được Google phát triển, được tối ưu hóa cho việc xây dựng UI. Dart là ngôn ngữ chính thức của Flutter.

### Đặc điểm chính:
- **Type-safe**: Hỗ trợ cả static và dynamic typing
- **Object-oriented**: Mọi thứ đều là object
- **Null safety**: Bảo vệ khỏi null reference errors
- **Async/await**: Xử lý bất đồng bộ dễ dàng
- **Hot reload**: Phát triển nhanh với Flutter

## 2. Cài đặt và chạy

```bash
# Kiểm tra Dart đã cài chưa
dart --version

# Chạy file Dart
dart run main.dart

# Compile thành executable
dart compile exe main.dart
```

## 3. Biến và kiểu dữ liệu

### Khai báo biến

```dart
// var - tự động suy luận kiểu
var name = 'John';
var age = 25;

// Khai báo kiểu rõ ràng
String city = 'Hanoi';
int year = 2024;
double price = 99.99;
bool isActive = true;

// final - gán 1 lần, runtime constant
final currentTime = DateTime.now();

// const - compile-time constant
const pi = 3.14159;

// dynamic - kiểu động
dynamic value = 'text';
value = 123; // OK
```

### Null Safety

```dart
// Non-nullable (không thể null)
String name = 'John';
// name = null; // ERROR!

// Nullable (có thể null)
String? nickname;
nickname = null; // OK
nickname = 'Johnny'; // OK

// Null-aware operators
String? nullableName;
String displayName = nullableName ?? 'Guest'; // Nếu null thì dùng 'Guest'

// Null-aware access
int? length = nullableName?.length; // Trả về null nếu nullableName là null

// Null assertion (!)
String definitelyNotNull = nullableName!; // Crash nếu null
```

## 4. Kiểu dữ liệu cơ bản

### Numbers

```dart
// int - số nguyên
int count = 42;
int hex = 0xDEADBEEF;

// double - số thực
double temperature = 36.6;
double exponent = 1.42e5;

// Chuyển đổi
int.parse('42'); // String -> int
double.parse('3.14'); // String -> double
123.toString(); // int -> String
3.14159.toStringAsFixed(2); // "3.14"
```

### Strings

```dart
// Single hoặc double quotes
String single = 'Hello';
String double = "World";

// String interpolation
var name = 'Dart';
var message = 'Hello $name'; // "Hello Dart"
var calculation = '1 + 1 = ${1 + 1}'; // "1 + 1 = 2"

// Multi-line strings
String multiLine = '''
Line 1
Line 2
Line 3
''';

// Raw strings (không escape)
String path = r'C:\Users\Documents';

// String methods
'hello'.toUpperCase(); // "HELLO"
'WORLD'.toLowerCase(); // "world"
'  trim  '.trim(); // "trim"
'hello'.contains('ell'); // true
'hello'.startsWith('he'); // true
'hello'.replaceAll('l', 'L'); // "heLLo"
'a,b,c'.split(','); // ['a', 'b', 'c']
```

### Booleans

```dart
bool isValid = true;
bool isEmpty = false;

// Logical operators
bool result = true && false; // AND
bool result2 = true || false; // OR
bool result3 = !true; // NOT
```

## 5. Collections (Tập hợp)

### List (Mảng)

```dart
// Khai báo
List<int> numbers = [1, 2, 3, 4, 5];
var fruits = ['apple', 'banana', 'orange'];

// Truy cập
print(fruits[0]); // "apple"
print(fruits.length); // 3

// Thêm/xóa
fruits.add('grape');
fruits.addAll(['mango', 'kiwi']);
fruits.insert(0, 'strawberry');
fruits.remove('banana');
fruits.removeAt(0);
fruits.clear();

// Các phương thức hữu ích
fruits.first; // Phần tử đầu
fruits.last; // Phần tử cuối
fruits.isEmpty; // Kiểm tra rỗng
fruits.contains('apple'); // Kiểm tra tồn tại
fruits.indexOf('banana'); // Tìm vị trí

// Spread operator
var moreNumbers = [0, ...numbers, 6, 7]; // [0, 1, 2, 3, 4, 5, 6, 7]

// Collection if
var nav = [
  'Home',
  'About',
  if (isLoggedIn) 'Profile',
];

// Collection for
var listOfInts = [1, 2, 3];
var listOfStrings = ['#0', for (var i in listOfInts) '#$i']; // ['#0', '#1', '#2', '#3']
```

### Set (Tập hợp không trùng lặp)

```dart
// Khai báo
Set<String> countries = {'Vietnam', 'Thailand', 'Japan'};
var numbers = <int>{1, 2, 3, 3, 3}; // {1, 2, 3}

// Thêm/xóa
countries.add('Korea');
countries.remove('Thailand');

// Set operations
var set1 = {1, 2, 3};
var set2 = {3, 4, 5};
set1.union(set2); // {1, 2, 3, 4, 5}
set1.intersection(set2); // {3}
set1.difference(set2); // {1, 2}
```

### Map (Dictionary/HashMap)

```dart
// Khai báo
Map<String, int> ages = {
  'John': 25,
  'Jane': 30,
  'Bob': 35,
};

var person = {
  'name': 'John',
  'age': 25,
  'city': 'Hanoi',
};

// Truy cập
print(ages['John']); // 25
print(person['name']); // "John"

// Thêm/sửa/xóa
ages['Alice'] = 28; // Thêm
ages['John'] = 26; // Sửa
ages.remove('Bob'); // Xóa

// Các phương thức
ages.keys; // Iterable<String>
ages.values; // Iterable<int>
ages.containsKey('John'); // true
ages.containsValue(25); // true
ages.length; // Số lượng
```

## 6. Functions (Hàm)

### Khai báo cơ bản

```dart
// Function với return type
int add(int a, int b) {
  return a + b;
}

// Arrow function (expression)
int multiply(int a, int b) => a * b;

// Void function
void printMessage(String message) {
  print(message);
}

// Gọi hàm
var sum = add(5, 3); // 8
multiply(4, 2); // 8
printMessage('Hello'); // In ra "Hello"
```

### Optional Parameters

```dart
// Named parameters (tham số có tên)
void greet({String name = 'Guest', int age = 0}) {
  print('Hello $name, age $age');
}

greet(); // "Hello Guest, age 0"
greet(name: 'John'); // "Hello John, age 0"
greet(name: 'Jane', age: 25); // "Hello Jane, age 25"

// Required named parameters
void login({required String username, required String password}) {
  print('Login: $username');
}

login(username: 'john', password: '123'); // Bắt buộc truyền

// Positional optional parameters
String sayHello(String name, [String? greeting]) {
  return '${greeting ?? 'Hello'} $name';
}

sayHello('John'); // "Hello John"
sayHello('Jane', 'Hi'); // "Hi Jane"
```

### Anonymous Functions (Lambda)

```dart
// Anonymous function
var list = [1, 2, 3];
list.forEach((item) {
  print(item);
});

// Arrow function
list.forEach((item) => print(item));

// Gán vào biến
var multiply = (int a, int b) => a * b;
print(multiply(3, 4)); // 12
```

### Higher-order Functions

```dart
// Function nhận function làm tham số
void executeOperation(int a, int b, Function operation) {
  print(operation(a, b));
}

executeOperation(5, 3, (a, b) => a + b); // 8
executeOperation(5, 3, (a, b) => a * b); // 15

// Function trả về function
Function makeAdder(int addBy) {
  return (int i) => addBy + i;
}

var add2 = makeAdder(2);
print(add2(3)); // 5
```

## 7. Control Flow (Luồng điều khiển)

### If-Else

```dart
var age = 18;

if (age >= 18) {
  print('Adult');
} else if (age >= 13) {
  print('Teenager');
} else {
  print('Child');
}

// Ternary operator
String status = age >= 18 ? 'Adult' : 'Minor';
```

### Switch-Case

```dart
var grade = 'A';

switch (grade) {
  case 'A':
    print('Excellent');
    break;
  case 'B':
    print('Good');
    break;
  case 'C':
    print('Average');
    break;
  default:
    print('Invalid grade');
}
```

### Loops

```dart
// For loop
for (var i = 0; i < 5; i++) {
  print(i);
}

// For-in loop
var fruits = ['apple', 'banana', 'orange'];
for (var fruit in fruits) {
  print(fruit);
}

// While loop
var count = 0;
while (count < 5) {
  print(count);
  count++;
}

// Do-while loop
var num = 0;
do {
  print(num);
  num++;
} while (num < 5);

// Break và Continue
for (var i = 0; i < 10; i++) {
  if (i == 5) break; // Thoát loop
  if (i % 2 == 0) continue; // Skip iteration
  print(i);
}
```

## 8. Classes và Objects

### Khai báo Class

```dart
class Person {
  // Properties
  String name;
  int age;
  
  // Constructor
  Person(this.name, this.age);
  
  // Named constructor
  Person.guest() : name = 'Guest', age = 0;
  
  // Method
  void introduce() {
    print('Hi, I am $name, $age years old');
  }
  
  // Getter
  String get info => '$name ($age)';
  
  // Setter
  set updateAge(int newAge) {
    if (newAge > 0) age = newAge;
  }
}

// Sử dụng
var person = Person('John', 25);
person.introduce(); // "Hi, I am John, 25 years old"
print(person.info); // "John (25)"
person.updateAge = 26;

var guest = Person.guest();
```

### Private Members

```dart
class BankAccount {
  String _accountNumber; // Private (bắt đầu với _)
  double _balance = 0;
  
  BankAccount(this._accountNumber);
  
  double get balance => _balance;
  
  void deposit(double amount) {
    _balance += amount;
  }
}
```

### Inheritance (Kế thừa)

```dart
class Animal {
  String name;
  
  Animal(this.name);
  
  void makeSound() {
    print('Some sound');
  }
}

class Dog extends Animal {
  String breed;
  
  Dog(String name, this.breed) : super(name);
  
  @override
  void makeSound() {
    print('Woof!');
  }
  
  void fetch() {
    print('$name is fetching');
  }
}

var dog = Dog('Buddy', 'Golden Retriever');
dog.makeSound(); // "Woof!"
dog.fetch(); // "Buddy is fetching"
```

### Abstract Classes

```dart
abstract class Shape {
  // Abstract method
  double calculateArea();
  
  // Concrete method
  void display() {
    print('Area: ${calculateArea()}');
  }
}

class Circle extends Shape {
  double radius;
  
  Circle(this.radius);
  
  @override
  double calculateArea() {
    return 3.14 * radius * radius;
  }
}

var circle = Circle(5);
circle.display(); // "Area: 78.5"
```

### Interfaces (Implements)

```dart
class Flyable {
  void fly() {
    print('Flying');
  }
}

class Swimmable {
  void swim() {
    print('Swimming');
  }
}

class Duck implements Flyable, Swimmable {
  @override
  void fly() {
    print('Duck is flying');
  }
  
  @override
  void swim() {
    print('Duck is swimming');
  }
}
```

### Mixins

```dart
mixin Musical {
  void playMusic() {
    print('Playing music');
  }
}

mixin Danceable {
  void dance() {
    print('Dancing');
  }
}

class Performer with Musical, Danceable {
  String name;
  
  Performer(this.name);
}

var performer = Performer('John');
performer.playMusic(); // "Playing music"
performer.dance(); // "Dancing"
```

## 9. Async Programming (Lập trình bất đồng bộ)

### Future

```dart
// Future trả về giá trị sau một khoảng thời gian
Future<String> fetchUserData() {
  return Future.delayed(
    Duration(seconds: 2),
    () => 'User data loaded',
  );
}

// Sử dụng với then
fetchUserData().then((data) {
  print(data);
}).catchError((error) {
  print('Error: $error');
});
```

### Async/Await

```dart
// Async function
Future<void> loadData() async {
  try {
    print('Loading...');
    String data = await fetchUserData();
    print(data);
  } catch (e) {
    print('Error: $e');
  }
}

// Gọi async function
loadData();

// Multiple awaits
Future<void> loadMultipleData() async {
  var user = await fetchUserData();
  var posts = await fetchPosts();
  var comments = await fetchComments();
  
  print('All data loaded');
}
```

### Stream

```dart
// Stream phát ra nhiều giá trị theo thời gian
Stream<int> countStream() async* {
  for (int i = 1; i <= 5; i++) {
    await Future.delayed(Duration(seconds: 1));
    yield i; // Phát ra giá trị
  }
}

// Lắng nghe stream
countStream().listen(
  (data) {
    print('Received: $data');
  },
  onError: (error) {
    print('Error: $error');
  },
  onDone: () {
    print('Stream completed');
  },
);

// Async for
Future<void> processStream() async {
  await for (var value in countStream()) {
    print('Processing: $value');
  }
}
```

## 10. Exception Handling (Xử lý ngoại lệ)

```dart
// Try-catch
try {
  var result = 10 ~/ 0; // Division by zero
} catch (e) {
  print('Error: $e');
}

// Catch specific exception
try {
  var result = int.parse('abc');
} on FormatException catch (e) {
  print('Format error: $e');
} catch (e) {
  print('Unknown error: $e');
}

// Finally
try {
  // Code
} catch (e) {
  print('Error: $e');
} finally {
  print('Always executed');
}

// Throw exception
void checkAge(int age) {
  if (age < 0) {
    throw Exception('Age cannot be negative');
  }
}

// Custom exception
class InvalidEmailException implements Exception {
  String message;
  InvalidEmailException(this.message);
}

void validateEmail(String email) {
  if (!email.contains('@')) {
    throw InvalidEmailException('Invalid email format');
  }
}
```

## 11. Generics (Kiểu tổng quát)

```dart
// Generic class
class Box<T> {
  T value;
  
  Box(this.value);
  
  T getValue() => value;
}

var intBox = Box<int>(42);
var stringBox = Box<String>('Hello');

// Generic function
T getFirst<T>(List<T> list) {
  return list.first;
}

var firstInt = getFirst<int>([1, 2, 3]); // 1
var firstString = getFirst<String>(['a', 'b']); // 'a'

// Generic với constraints
class Cache<T extends Object> {
  Map<String, T> _cache = {};
  
  void add(String key, T value) {
    _cache[key] = value;
  }
  
  T? get(String key) {
    return _cache[key];
  }
}
```

## 12. Enums

```dart
// Khai báo enum
enum Status {
  pending,
  approved,
  rejected,
}

// Sử dụng
var currentStatus = Status.pending;

if (currentStatus == Status.approved) {
  print('Approved');
}

// Switch với enum
switch (currentStatus) {
  case Status.pending:
    print('Waiting');
    break;
  case Status.approved:
    print('Success');
    break;
  case Status.rejected:
    print('Failed');
    break;
}

// Enhanced enums (Dart 2.17+)
enum Color {
  red(0xFF0000),
  green(0x00FF00),
  blue(0x0000FF);
  
  final int value;
  const Color(this.value);
}

print(Color.red.value); // 16711680
```

## 13. Extension Methods

```dart
// Mở rộng class có sẵn
extension StringExtension on String {
  String capitalize() {
    if (isEmpty) return this;
    return '${this[0].toUpperCase()}${substring(1)}';
  }
  
  bool isEmail() {
    return contains('@') && contains('.');
  }
}

// Sử dụng
print('hello'.capitalize()); // "Hello"
print('test@email.com'.isEmail()); // true

// Extension trên int
extension IntExtension on int {
  bool get isEven => this % 2 == 0;
  bool get isOdd => !isEven;
}

print(4.isEven); // true
print(5.isOdd); // true
```

## 14. Cascade Notation

```dart
// Cascade (..) cho phép gọi nhiều method trên cùng object
class Person {
  String? name;
  int? age;
  String? city;
  
  void introduce() {
    print('$name, $age, $city');
  }
}

// Không dùng cascade
var person = Person();
person.name = 'John';
person.age = 25;
person.city = 'Hanoi';
person.introduce();

// Dùng cascade
var person2 = Person()
  ..name = 'Jane'
  ..age = 30
  ..city = 'HCMC'
  ..introduce();
```

## 15. Typedef và Function Types

```dart
// Typedef cho function type
typedef IntOperation = int Function(int a, int b);

int add(int a, int b) => a + b;
int multiply(int a, int b) => a * b;

void calculate(int x, int y, IntOperation operation) {
  print(operation(x, y));
}

calculate(5, 3, add); // 8
calculate(5, 3, multiply); // 15

// Typedef cho callback
typedef Callback = void Function(String message);

void performTask(Callback onComplete) {
  // Do something
  onComplete('Task completed');
}

performTask((message) => print(message));
```

## 16. Best Practices

### Naming Conventions

```dart
// Classes, enums, typedefs: UpperCamelCase
class MyClass {}
enum Status {}
typedef Callback = void Function();

// Variables, functions, parameters: lowerCamelCase
var myVariable = 10;
void myFunction() {}

// Constants: lowerCamelCase
const maxRetries = 3;
const apiUrl = 'https://api.example.com';

// Private members: _lowerCamelCase
class MyClass {
  int _privateField;
}

// Files: snake_case
// my_widget.dart
// user_service.dart
```

### Code Style

```dart
// Prefer final cho biến không thay đổi
final name = 'John';

// Prefer const cho compile-time constants
const pi = 3.14159;

// Sử dụng ?? và ?. cho null safety
String? nullableName;
var displayName = nullableName ?? 'Guest';
var length = nullableName?.length;

// Prefer arrow function cho expression ngắn
int double(int x) => x * 2;

// Prefer collection literals
var list = [1, 2, 3]; // Thay vì List<int>()
var map = {'key': 'value'}; // Thay vì Map<String, String>()

// Avoid print trong production, dùng logging
// print('Debug message'); // ❌
// log('Debug message'); // ✅
```

## 17. Useful Packages

```yaml
# pubspec.yaml
dependencies:
  # HTTP requests
  http: ^1.1.0
  dio: ^5.4.0
  
  # State management
  provider: ^6.1.1
  riverpod: ^2.4.9
  bloc: ^8.1.3
  
  # Local storage
  shared_preferences: ^2.2.2
  hive: ^2.2.3
  sqflite: ^2.3.0
  
  # JSON serialization
  json_annotation: ^4.8.1
  
  # Utilities
  intl: ^0.18.1 # Internationalization
  uuid: ^4.2.2 # Generate UUID
```

## 18. Ví dụ thực tế

### Model Class

```dart
class User {
  final String id;
  final String name;
  final String email;
  final int age;
  
  User({
    required this.id,
    required this.name,
    required this.email,
    required this.age,
  });
  
  // From JSON
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      email: json['email'],
      age: json['age'],
    );
  }
  
  // To JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'age': age,
    };
  }
  
  // CopyWith
  User copyWith({
    String? id,
    String? name,
    String? email,
    int? age,
  }) {
    return User(
      id: id ?? this.id,
      name: name ?? this.name,
      email: email ?? this.email,
      age: age ?? this.age,
    );
  }
}
```

### API Service

```dart
import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = 'https://api.example.com';
  
  Future<List<User>> fetchUsers() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/users'),
      );
      
      if (response.statusCode == 200) {
        List<dynamic> data = json.decode(response.body);
        return data.map((json) => User.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load users');
      }
    } catch (e) {
      throw Exception('Error: $e');
    }
  }
  
  Future<User> createUser(User user) async {
    final response = await http.post(
      Uri.parse('$baseUrl/users'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(user.toJson()),
    );
    
    if (response.statusCode == 201) {
      return User.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to create user');
    }
  }
}
```

## 19. Tài liệu tham khảo

- **Official Dart**: https://dart.dev
- **DartPad**: https://dartpad.dev (Chạy Dart online)
- **Dart Packages**: https://pub.dev
- **Flutter Docs**: https://flutter.dev/docs
- **Effective Dart**: https://dart.dev/guides/language/effective-dart

## 20. Bước tiếp theo

1. ✅ Học cú pháp Dart cơ bản
2. ✅ Thực hành với DartPad
3. 🎯 Học Flutter widgets
4. 🎯 Xây dựng UI với Flutter
5. 🎯 State management (Provider, Riverpod, Bloc)
6. 🎯 Navigation và routing
7. 🎯 API integration
8. 🎯 Local storage
9. 🎯 Build và deploy app

---

**Chúc bạn học tốt! 🚀**
