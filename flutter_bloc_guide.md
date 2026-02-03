# Hướng dẫn sử dụng BLoC trong Flutter

## Cài đặt

Thêm dependencies vào `pubspec.yaml`:

```yaml
dependencies:
  flutter_bloc: ^8.1.3
  equatable: ^2.0.5
```

## Cấu trúc BLoC cơ bản

### 1. Event (Sự kiện)

```dart
// counter_event.dart
abstract class CounterEvent extends Equatable {
  const CounterEvent();

  @override
  List<Object> get props => [];
}

class CounterIncremented extends CounterEvent {}

class CounterDecremented extends CounterEvent {}
```

### 2. State (Trạng thái)

```dart
// counter_state.dart
class CounterState extends Equatable {
  final int count;

  const CounterState({this.count = 0});

  CounterState copyWith({int? count}) {
    return CounterState(count: count ?? this.count);
  }

  @override
  List<Object> get props => [count];
}
```

### 3. BLoC

```dart
// counter_bloc.dart
import 'package:flutter_bloc/flutter_bloc.dart';

class CounterBloc extends Bloc<CounterEvent, CounterState> {
  CounterBloc() : super(const CounterState()) {
    on<CounterIncremented>(_onIncrement);
    on<CounterDecremented>(_onDecrement);
  }

  void _onIncrement(CounterIncremented event, Emitter<CounterState> emit) {
    emit(state.copyWith(count: state.count + 1));
  }

  void _onDecrement(CounterDecremented event, Emitter<CounterState> emit) {
    emit(state.copyWith(count: state.count - 1));
  }
}
```

## Sử dụng trong UI

### 1. BlocProvider - Cung cấp BLoC

```dart
class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => CounterBloc(),
      child: MaterialApp(
        home: CounterPage(),
      ),
    );
  }
}
```

### 2. BlocBuilder - Xây dựng UI dựa trên State

```dart
class CounterPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Counter')),
      body: BlocBuilder<CounterBloc, CounterState>(
        builder: (context, state) {
          return Center(
            child: Text(
              '${state.count}',
              style: TextStyle(fontSize: 48),
            ),
          );
        },
      ),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          FloatingActionButton(
            onPressed: () => context.read<CounterBloc>().add(CounterIncremented()),
            child: Icon(Icons.add),
          ),
          SizedBox(height: 8),
          FloatingActionButton(
            onPressed: () => context.read<CounterBloc>().add(CounterDecremented()),
            child: Icon(Icons.remove),
          ),
        ],
      ),
    );
  }
}
```

### 3. BlocListener - Lắng nghe thay đổi State

```dart
BlocListener<CounterBloc, CounterState>(
  listener: (context, state) {
    if (state.count == 10) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Đạt 10!')),
      );
    }
  },
  child: Container(),
)
```

### 4. BlocConsumer - Kết hợp Builder và Listener

```dart
BlocConsumer<CounterBloc, CounterState>(
  listener: (context, state) {
    if (state.count < 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Số âm!')),
      );
    }
  },
  builder: (context, state) {
    return Text('${state.count}');
  },
)
```

## Ví dụ thực tế: Todo App

### Event

```dart
abstract class TodoEvent extends Equatable {
  const TodoEvent();
  @override
  List<Object> get props => [];
}

class LoadTodos extends TodoEvent {}

class AddTodo extends TodoEvent {
  final String title;
  const AddTodo(this.title);
  @override
  List<Object> get props => [title];
}

class ToggleTodo extends TodoEvent {
  final int id;
  const ToggleTodo(this.id);
  @override
  List<Object> get props => [id];
}

class DeleteTodo extends TodoEvent {
  final int id;
  const DeleteTodo(this.id);
  @override
  List<Object> get props => [id];
}
```

### State

```dart
enum TodoStatus { initial, loading, success, failure }

class TodoState extends Equatable {
  final TodoStatus status;
  final List<Todo> todos;
  final String? errorMessage;

  const TodoState({
    this.status = TodoStatus.initial,
    this.todos = const [],
    this.errorMessage,
  });

  TodoState copyWith({
    TodoStatus? status,
    List<Todo>? todos,
    String? errorMessage,
  }) {
    return TodoState(
      status: status ?? this.status,
      todos: todos ?? this.todos,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, todos, errorMessage];
}

class Todo extends Equatable {
  final int id;
  final String title;
  final bool isCompleted;

  const Todo({
    required this.id,
    required this.title,
    this.isCompleted = false,
  });

  Todo copyWith({bool? isCompleted}) {
    return Todo(
      id: id,
      title: title,
      isCompleted: isCompleted ?? this.isCompleted,
    );
  }

  @override
  List<Object> get props => [id, title, isCompleted];
}
```

### BLoC

```dart
class TodoBloc extends Bloc<TodoEvent, TodoState> {
  final TodoRepository repository;

  TodoBloc({required this.repository}) : super(const TodoState()) {
    on<LoadTodos>(_onLoadTodos);
    on<AddTodo>(_onAddTodo);
    on<ToggleTodo>(_onToggleTodo);
    on<DeleteTodo>(_onDeleteTodo);
  }

  Future<void> _onLoadTodos(LoadTodos event, Emitter<TodoState> emit) async {
    emit(state.copyWith(status: TodoStatus.loading));
    try {
      final todos = await repository.fetchTodos();
      emit(state.copyWith(
        status: TodoStatus.success,
        todos: todos,
      ));
    } catch (e) {
      emit(state.copyWith(
        status: TodoStatus.failure,
        errorMessage: e.toString(),
      ));
    }
  }

  void _onAddTodo(AddTodo event, Emitter<TodoState> emit) {
    final newTodo = Todo(
      id: state.todos.length + 1,
      title: event.title,
    );
    emit(state.copyWith(
      todos: [...state.todos, newTodo],
    ));
  }

  void _onToggleTodo(ToggleTodo event, Emitter<TodoState> emit) {
    final updatedTodos = state.todos.map((todo) {
      return todo.id == event.id
          ? todo.copyWith(isCompleted: !todo.isCompleted)
          : todo;
    }).toList();
    emit(state.copyWith(todos: updatedTodos));
  }

  void _onDeleteTodo(DeleteTodo event, Emitter<TodoState> emit) {
    final updatedTodos = state.todos.where((todo) => todo.id != event.id).toList();
    emit(state.copyWith(todos: updatedTodos));
  }
}
```

### UI

```dart
class TodoPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Todo List')),
      body: BlocBuilder<TodoBloc, TodoState>(
        builder: (context, state) {
          if (state.status == TodoStatus.loading) {
            return Center(child: CircularProgressIndicator());
          }

          if (state.status == TodoStatus.failure) {
            return Center(child: Text('Lỗi: ${state.errorMessage}'));
          }

          if (state.todos.isEmpty) {
            return Center(child: Text('Chưa có todo nào'));
          }

          return ListView.builder(
            itemCount: state.todos.length,
            itemBuilder: (context, index) {
              final todo = state.todos[index];
              return ListTile(
                leading: Checkbox(
                  value: todo.isCompleted,
                  onChanged: (_) {
                    context.read<TodoBloc>().add(ToggleTodo(todo.id));
                  },
                ),
                title: Text(
                  todo.title,
                  style: TextStyle(
                    decoration: todo.isCompleted
                        ? TextDecoration.lineThrough
                        : null,
                  ),
                ),
                trailing: IconButton(
                  icon: Icon(Icons.delete),
                  onPressed: () {
                    context.read<TodoBloc>().add(DeleteTodo(todo.id));
                  },
                ),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showAddTodoDialog(context),
        child: Icon(Icons.add),
      ),
    );
  }

  void _showAddTodoDialog(BuildContext context) {
    final controller = TextEditingController();
    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text('Thêm Todo'),
        content: TextField(
          controller: controller,
          decoration: InputDecoration(hintText: 'Nhập tiêu đề'),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: Text('Hủy'),
          ),
          TextButton(
            onPressed: () {
              if (controller.text.isNotEmpty) {
                context.read<TodoBloc>().add(AddTodo(controller.text));
                Navigator.pop(dialogContext);
              }
            },
            child: Text('Thêm'),
          ),
        ],
      ),
    );
  }
}
```

## MultiBlocProvider - Nhiều BLoC

```dart
MultiBlocProvider(
  providers: [
    BlocProvider<CounterBloc>(
      create: (context) => CounterBloc(),
    ),
    BlocProvider<TodoBloc>(
      create: (context) => TodoBloc(repository: TodoRepository()),
    ),
  ],
  child: MyApp(),
)
```

## BlocObserver - Theo dõi tất cả BLoC

```dart
class AppBlocObserver extends BlocObserver {
  @override
  void onChange(BlocBase bloc, Change change) {
    super.onChange(bloc, change);
    print('${bloc.runtimeType} $change');
  }

  @override
  void onError(BlocBase bloc, Object error, StackTrace stackTrace) {
    print('${bloc.runtimeType} $error');
    super.onError(bloc, error, stackTrace);
  }
}

void main() {
  Bloc.observer = AppBlocObserver();
  runApp(MyApp());
}
```

## Best Practices

1. **Một BLoC cho một màn hình hoặc feature**
2. **Sử dụng Equatable** để so sánh state hiệu quả
3. **Tách logic nghiệp vụ** ra khỏi UI
4. **Sử dụng Repository pattern** để tách data layer
5. **Đặt tên rõ ràng** cho Event và State
6. **Xử lý lỗi** đúng cách trong BLoC
7. **Dispose BLoC** khi không cần thiết (BlocProvider tự động làm điều này)

## Cubit - Phiên bản đơn giản của BLoC

```dart
class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);

  void increment() => emit(state + 1);
  void decrement() => emit(state - 1);
}

// Sử dụng
BlocProvider(
  create: (context) => CounterCubit(),
  child: BlocBuilder<CounterCubit, int>(
    builder: (context, count) {
      return Text('$count');
    },
  ),
)
```

Cubit đơn giản hơn BLoC khi không cần Event phức tạp.


## HydratedBloc - Lưu trữ State tự động

HydratedBloc tự động lưu và khôi phục state của BLoC, giúp duy trì trạng thái khi đóng/mở app.

### Cài đặt

```yaml
dependencies:
  hydrated_bloc: ^9.1.2
  path_provider: ^2.1.1
```

### Khởi tạo HydratedBloc

```dart
import 'package:flutter/material.dart';
import 'package:hydrated_bloc/hydrated_bloc.dart';
import 'package:path_provider/path_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  HydratedBloc.storage = await HydratedStorage.build(
    storageDirectory: await getApplicationDocumentsDirectory(),
  );

  runApp(MyApp());
}
```

### Ví dụ 1: Counter với HydratedBloc

```dart
// counter_bloc.dart
class CounterBloc extends HydratedBloc<CounterEvent, CounterState> {
  CounterBloc() : super(const CounterState()) {
    on<CounterIncremented>(_onIncrement);
    on<CounterDecremented>(_onDecrement);
  }

  void _onIncrement(CounterIncremented event, Emitter<CounterState> emit) {
    emit(state.copyWith(count: state.count + 1));
  }

  void _onDecrement(CounterDecremented event, Emitter<CounterState> emit) {
    emit(state.copyWith(count: state.count - 1));
  }

  // Chuyển state thành JSON để lưu
  @override
  Map<String, dynamic>? toJson(CounterState state) {
    return {'count': state.count};
  }

  // Khôi phục state từ JSON
  @override
  CounterState? fromJson(Map<String, dynamic> json) {
    return CounterState(count: json['count'] as int);
  }
}
```

### Ví dụ 2: Settings với HydratedCubit

```dart
// settings_state.dart
class SettingsState extends Equatable {
  final bool isDarkMode;
  final String language;
  final double fontSize;

  const SettingsState({
    this.isDarkMode = false,
    this.language = 'vi',
    this.fontSize = 14.0,
  });

  SettingsState copyWith({
    bool? isDarkMode,
    String? language,
    double? fontSize,
  }) {
    return SettingsState(
      isDarkMode: isDarkMode ?? this.isDarkMode,
      language: language ?? this.language,
      fontSize: fontSize ?? this.fontSize,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'isDarkMode': isDarkMode,
      'language': language,
      'fontSize': fontSize,
    };
  }

  factory SettingsState.fromJson(Map<String, dynamic> json) {
    return SettingsState(
      isDarkMode: json['isDarkMode'] as bool,
      language: json['language'] as String,
      fontSize: json['fontSize'] as double,
    );
  }

  @override
  List<Object> get props => [isDarkMode, language, fontSize];
}

// settings_cubit.dart
class SettingsCubit extends HydratedCubit<SettingsState> {
  SettingsCubit() : super(const SettingsState());

  void toggleDarkMode() {
    emit(state.copyWith(isDarkMode: !state.isDarkMode));
  }

  void changeLanguage(String language) {
    emit(state.copyWith(language: language));
  }

  void changeFontSize(double fontSize) {
    emit(state.copyWith(fontSize: fontSize));
  }

  @override
  Map<String, dynamic>? toJson(SettingsState state) {
    return state.toJson();
  }

  @override
  SettingsState? fromJson(Map<String, dynamic> json) {
    return SettingsState.fromJson(json);
  }
}
```

### Ví dụ 3: Todo List với HydratedBloc

```dart
// todo_bloc.dart
class TodoBloc extends HydratedBloc<TodoEvent, TodoState> {
  TodoBloc() : super(const TodoState()) {
    on<LoadTodos>(_onLoadTodos);
    on<AddTodo>(_onAddTodo);
    on<ToggleTodo>(_onToggleTodo);
    on<DeleteTodo>(_onDeleteTodo);
  }

  void _onLoadTodos(LoadTodos event, Emitter<TodoState> emit) {
    // State đã được tự động khôi phục từ storage
    emit(state.copyWith(status: TodoStatus.success));
  }

  void _onAddTodo(AddTodo event, Emitter<TodoState> emit) {
    final newTodo = Todo(
      id: DateTime.now().millisecondsSinceEpoch,
      title: event.title,
    );
    emit(state.copyWith(
      todos: [...state.todos, newTodo],
    ));
  }

  void _onToggleTodo(ToggleTodo event, Emitter<TodoState> emit) {
    final updatedTodos = state.todos.map((todo) {
      return todo.id == event.id
          ? todo.copyWith(isCompleted: !todo.isCompleted)
          : todo;
    }).toList();
    emit(state.copyWith(todos: updatedTodos));
  }

  void _onDeleteTodo(DeleteTodo event, Emitter<TodoState> emit) {
    final updatedTodos = state.todos.where((todo) => todo.id != event.id).toList();
    emit(state.copyWith(todos: updatedTodos));
  }

  @override
  Map<String, dynamic>? toJson(TodoState state) {
    return {
      'todos': state.todos.map((todo) => todo.toJson()).toList(),
    };
  }

  @override
  TodoState? fromJson(Map<String, dynamic> json) {
    try {
      final todos = (json['todos'] as List)
          .map((todoJson) => Todo.fromJson(todoJson))
          .toList();
      return TodoState(
        status: TodoStatus.success,
        todos: todos,
      );
    } catch (_) {
      return null; // Trả về null nếu có lỗi, sẽ dùng initial state
    }
  }
}

// todo.dart - Thêm toJson và fromJson
class Todo extends Equatable {
  final int id;
  final String title;
  final bool isCompleted;

  const Todo({
    required this.id,
    required this.title,
    this.isCompleted = false,
  });

  Todo copyWith({bool? isCompleted}) {
    return Todo(
      id: id,
      title: title,
      isCompleted: isCompleted ?? this.isCompleted,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'isCompleted': isCompleted,
    };
  }

  factory Todo.fromJson(Map<String, dynamic> json) {
    return Todo(
      id: json['id'] as int,
      title: json['title'] as String,
      isCompleted: json['isCompleted'] as bool,
    );
  }

  @override
  List<Object> get props => [id, title, isCompleted];
}
```

### Xóa dữ liệu đã lưu

```dart
// Xóa tất cả dữ liệu
await HydratedBloc.storage.clear();

// Xóa dữ liệu của một BLoC cụ thể
await HydratedBloc.storage.delete('CounterBloc');
```

### Custom Storage ID

Mặc định, HydratedBloc sử dụng tên class làm storage key. Bạn có thể tùy chỉnh:

```dart
class CounterBloc extends HydratedBloc<CounterEvent, CounterState> {
  CounterBloc() : super(const CounterState());

  @override
  String get id => 'my_custom_counter_id';

  @override
  Map<String, dynamic>? toJson(CounterState state) {
    return {'count': state.count};
  }

  @override
  CounterState? fromJson(Map<String, dynamic> json) {
    return CounterState(count: json['count'] as int);
  }
}
```

### Migration - Chuyển đổi dữ liệu cũ

```dart
class CounterBloc extends HydratedBloc<CounterEvent, CounterState> {
  CounterBloc() : super(const CounterState());

  @override
  CounterState? fromJson(Map<String, dynamic> json) {
    // Migration từ version cũ
    if (json.containsKey('value')) {
      // Version cũ dùng key 'value'
      return CounterState(count: json['value'] as int);
    }
    // Version mới dùng key 'count'
    return CounterState(count: json['count'] as int);
  }

  @override
  Map<String, dynamic>? toJson(CounterState state) {
    return {'count': state.count};
  }
}
```

### HydratedBlocOverrides - Custom Storage

```dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final storage = await HydratedStorage.build(
    storageDirectory: await getApplicationDocumentsDirectory(),
  );

  HydratedBlocOverrides.runZoned(
    () => runApp(MyApp()),
    storage: storage,
  );
}
```

### Ví dụ 4: Authentication với HydratedBloc

```dart
// auth_state.dart
class AuthState extends Equatable {
  final bool isAuthenticated;
  final String? userId;
  final String? token;

  const AuthState({
    this.isAuthenticated = false,
    this.userId,
    this.token,
  });

  AuthState copyWith({
    bool? isAuthenticated,
    String? userId,
    String? token,
  }) {
    return AuthState(
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      userId: userId ?? this.userId,
      token: token ?? this.token,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'isAuthenticated': isAuthenticated,
      'userId': userId,
      'token': token,
    };
  }

  factory AuthState.fromJson(Map<String, dynamic> json) {
    return AuthState(
      isAuthenticated: json['isAuthenticated'] as bool,
      userId: json['userId'] as String?,
      token: json['token'] as String?,
    );
  }

  @override
  List<Object?> get props => [isAuthenticated, userId, token];
}

// auth_bloc.dart
class AuthBloc extends HydratedBloc<AuthEvent, AuthState> {
  AuthBloc() : super(const AuthState()) {
    on<LoginRequested>(_onLogin);
    on<LogoutRequested>(_onLogout);
  }

  Future<void> _onLogin(LoginRequested event, Emitter<AuthState> emit) async {
    // Giả lập login
    await Future.delayed(Duration(seconds: 1));
    emit(AuthState(
      isAuthenticated: true,
      userId: event.userId,
      token: 'fake_token_${event.userId}',
    ));
  }

  void _onLogout(LogoutRequested event, Emitter<AuthState> emit) {
    emit(const AuthState());
  }

  @override
  Map<String, dynamic>? toJson(AuthState state) {
    // Không lưu token vì lý do bảo mật (tùy chọn)
    return {
      'isAuthenticated': state.isAuthenticated,
      'userId': state.userId,
    };
  }

  @override
  AuthState? fromJson(Map<String, dynamic> json) {
    return AuthState(
      isAuthenticated: json['isAuthenticated'] as bool,
      userId: json['userId'] as String?,
      // Token sẽ cần refresh khi app khởi động
    );
  }
}
```

### Testing HydratedBloc

```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:hydrated_bloc/hydrated_bloc.dart';
import 'package:mocktail/mocktail.dart';

class MockStorage extends Mock implements Storage {}

void main() {
  late Storage storage;

  setUp(() {
    storage = MockStorage();
    when(() => storage.write(any(), any<dynamic>())).thenAnswer((_) async {});
    HydratedBloc.storage = storage;
  });

  group('CounterBloc', () {
    test('initial state is 0', () {
      expect(CounterBloc().state, const CounterState(count: 0));
    });

    test('toJson/fromJson work correctly', () {
      final bloc = CounterBloc();
      final state = const CounterState(count: 5);
      
      final json = bloc.toJson(state);
      expect(json, {'count': 5});
      
      final restoredState = bloc.fromJson(json!);
      expect(restoredState, state);
    });
  });
}
```

### Lưu ý khi sử dụng HydratedBloc

1. **Chỉ lưu dữ liệu cần thiết** - Không lưu dữ liệu nhạy cảm như password
2. **Xử lý lỗi trong fromJson** - Trả về null nếu không parse được
3. **Migration data** - Xử lý khi thay đổi cấu trúc state
4. **Performance** - Không lưu quá nhiều dữ liệu, có thể làm chậm app
5. **Testing** - Mock storage khi test
6. **Clear data** - Cung cấp cách xóa dữ liệu khi logout hoặc reset app

### So sánh HydratedBloc vs SharedPreferences

| Feature | HydratedBloc | SharedPreferences |
|---------|--------------|-------------------|
| Tự động lưu | ✅ Tự động | ❌ Phải tự code |
| Type-safe | ✅ Có | ❌ Chỉ primitive types |
| Phức tạp | ✅ Lưu object phức tạp | ❌ Chỉ string, int, bool |
| Performance | ⚡ Nhanh | ⚡ Nhanh |
| Setup | 🔧 Cần setup | 🔧 Đơn giản |

HydratedBloc phù hợp khi bạn đã dùng BLoC và muốn tự động persist state mà không cần viết thêm code lưu/đọc dữ liệu.
