# Hướng dẫn các file config trong Flutter

## 1. pubspec.yaml - File config chính

File quan trọng nhất, định nghĩa metadata và dependencies của project.

### Cấu trúc cơ bản

```yaml
name: my_app                    # Tên package (bắt buộc)
description: A new Flutter project.
publish_to: 'none'              # Không publish lên pub.dev
version: 1.0.0+1                # Version: major.minor.patch+build

environment:
  sdk: '>=3.0.0 <4.0.0'         # Dart SDK version
  flutter: ">=3.10.0"           # Flutter SDK version

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.2       # Dependencies cho production

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^2.0.0         # Dependencies chỉ dùng khi dev

flutter:
  uses-material-design: true    # Sử dụng Material Design
```

### Version Format

```yaml
# Cách đọc version: 1.0.0+1
# 1 = Major version (thay đổi lớn, breaking changes)
# 0 = Minor version (thêm tính năng mới)
# 0 = Patch version (bug fixes)
# +1 = Build number (tăng mỗi lần build)

version: 1.2.3+42
```

### Dependencies

```yaml
dependencies:
  # Package từ pub.dev
  http: ^1.1.0                  # Caret: >=1.1.0 <2.0.0
  dio: 5.3.2                    # Exact version
  provider: '>=6.0.0 <7.0.0'    # Range
  
  # Package từ Git
  my_package:
    git:
      url: https://github.com/user/repo.git
      ref: main                 # branch/tag/commit
      path: packages/my_package # Nếu package trong subfolder
  
  # Package local
  my_local_package:
    path: ../my_local_package
  
  # Package từ hosted URL khác
  custom_package:
    hosted:
      name: custom_package
      url: https://custom-pub-server.com
    version: ^1.0.0
  
  # Flutter SDK packages
  flutter:
    sdk: flutter
  flutter_localizations:
    sdk: flutter
```

### Dev Dependencies

```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^2.0.0         # Linting rules
  build_runner: ^2.4.0          # Code generation
  mockito: ^5.4.0               # Testing
  integration_test:
    sdk: flutter
```

### Dependency Overrides

```yaml
dependency_overrides:
  # Force version cụ thể (dùng khi có conflict)
  http: 1.0.0
  
  # Override với local package
  my_package:
    path: ../my_package_fork
```

### Assets

```yaml
flutter:
  assets:
    # File cụ thể
    - assets/logo.png
    
    # Tất cả file trong folder
    - assets/images/
    
    # Subfolder
    - assets/icons/
    - assets/data/config.json
    
    # Packages assets
    - packages/my_package/assets/icon.png
```

### Fonts

```yaml
flutter:
  fonts:
    - family: Roboto
      fonts:
        - asset: fonts/Roboto-Regular.ttf
        - asset: fonts/Roboto-Bold.ttf
          weight: 700
        - asset: fonts/Roboto-Italic.ttf
          style: italic
    
    - family: CustomIcon
      fonts:
        - asset: fonts/CustomIcon.ttf
```

Sử dụng:
```dart
Text(
  'Hello',
  style: TextStyle(fontFamily: 'Roboto', fontWeight: FontWeight.bold),
)
```

### Plugin Configuration

```yaml
flutter:
  plugin:
    platforms:
      android:
        package: com.example.my_plugin
        pluginClass: MyPlugin
      ios:
        pluginClass: MyPlugin
      web:
        pluginClass: MyPluginWeb
        fileName: my_plugin_web.dart
```

### Generate Options

```yaml
flutter:
  generate: true                # Bật code generation cho localization
```

### Deferred Components (Android)

```yaml
flutter:
  deferred-components:
    - name: my_component
      libraries:
        - package:my_app/my_component.dart
```

### Ví dụ pubspec.yaml đầy đủ

```yaml
name: my_awesome_app
description: A comprehensive Flutter application
publish_to: 'none'
version: 2.1.0+15

environment:
  sdk: '>=3.0.0 <4.0.0'
  flutter: ">=3.10.0"

dependencies:
  flutter:
    sdk: flutter
  flutter_localizations:
    sdk: flutter
  
  # State Management
  flutter_bloc: ^8.1.3
  hydrated_bloc: ^9.1.2
  equatable: ^2.0.5
  
  # Network
  dio: ^5.3.2
  retrofit: ^4.0.1
  
  # Storage
  shared_preferences: ^2.2.0
  hive: ^2.2.3
  path_provider: ^2.1.1
  
  # UI
  cached_network_image: ^3.3.0
  shimmer: ^3.0.0
  
  # Utils
  intl: ^0.18.1
  logger: ^2.0.2

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^2.0.0
  build_runner: ^2.4.6
  retrofit_generator: ^7.0.8
  mockito: ^5.4.2
  bloc_test: ^9.1.4

flutter:
  uses-material-design: true
  generate: true
  
  assets:
    - assets/images/
    - assets/icons/
    - assets/data/
  
  fonts:
    - family: Roboto
      fonts:
        - asset: fonts/Roboto-Regular.ttf
        - asset: fonts/Roboto-Bold.ttf
          weight: 700
```

## 2. analysis_options.yaml - Linting rules

```yaml
include: package:flutter_lints/flutter.yaml

analyzer:
  exclude:
    - "**/*.g.dart"             # Ignore generated files
    - "**/*.freezed.dart"
    - "build/**"
  
  errors:
    invalid_annotation_target: ignore
    missing_required_param: error
    missing_return: error
  
  language:
    strict-casts: true
    strict-inference: true
    strict-raw-types: true

linter:
  rules:
    # Style
    - always_declare_return_types
    - always_put_required_named_parameters_first
    - always_use_package_imports
    - avoid_print
    - avoid_unnecessary_containers
    - prefer_const_constructors
    - prefer_const_literals_to_create_immutables
    - prefer_final_fields
    - prefer_single_quotes
    - require_trailing_commas
    - sort_child_properties_last
    - use_key_in_widget_constructors
    
    # Error prevention
    - avoid_empty_else
    - avoid_returning_null_for_future
    - cancel_subscriptions
    - close_sinks
    - no_duplicate_case_values
    - valid_regexps
```

## 3. l10n.yaml - Localization config

```yaml
arb-dir: lib/l10n
template-arb-file: app_en.arb
output-localization-file: app_localizations.dart
output-class: AppLocalizations
output-dir: lib/l10n/generated
nullable-getter: false
synthetic-package: false
```

Cấu trúc file ARB:
```json
// lib/l10n/app_en.arb
{
  "@@locale": "en",
  "appTitle": "My App",
  "@appTitle": {
    "description": "The application title"
  },
  "hello": "Hello {name}",
  "@hello": {
    "description": "Greeting message",
    "placeholders": {
      "name": {
        "type": "String"
      }
    }
  }
}
```

## 4. .metadata - Flutter metadata

```yaml
# File tự động tạo, không nên edit thủ công
version:
  revision: 796c8ef79279f9c774545b3771238c3098dbefab
  channel: stable

project_type: app

migration:
  platforms:
    - platform: root
      create_revision: 796c8ef79279f9c774545b3771238c3098dbefab
      base_revision: 796c8ef79279f9c774545b3771238c3098dbefab
```

## 5. .flutter-plugins - Plugin registry

```
# File tự động tạo
path_provider=/path/to/.pub-cache/hosted/pub.dev/path_provider-2.1.1/
shared_preferences=/path/to/.pub-cache/hosted/pub.dev/shared_preferences-2.2.0/
```

## 6. .flutter-plugins-dependencies

```json
{
  "info": "This is a generated file",
  "plugins": {
    "ios": [
      {
        "name": "path_provider_foundation",
        "path": "/path/to/package/",
        "native_build": true,
        "dependencies": []
      }
    ],
    "android": [
      {
        "name": "path_provider_android",
        "path": "/path/to/package/",
        "native_build": true,
        "dependencies": []
      }
    ]
  }
}
```

## 7. build.yaml - Build configuration

```yaml
targets:
  $default:
    builders:
      json_serializable:
        options:
          explicit_to_json: true
          field_rename: snake
          
      retrofit_generator:
        options:
          generate_to_string: true
```

## 8. .gitignore - Git ignore

```gitignore
# Miscellaneous
*.class
*.log
*.pyc
*.swp
.DS_Store
.atom/
.buildlog/
.history
.svn/

# IntelliJ
*.iml
*.ipr
*.iws
.idea/

# VS Code
.vscode/

# Flutter/Dart/Pub
**/doc/api/
**/ios/Flutter/.last_build_id
.dart_tool/
.flutter-plugins
.flutter-plugins-dependencies
.packages
.pub-cache/
.pub/
/build/

# Android
**/android/**/gradle-wrapper.jar
**/android/.gradle
**/android/captures/
**/android/gradlew
**/android/gradlew.bat
**/android/local.properties
**/android/**/GeneratedPluginRegistrant.java
**/android/key.properties
*.jks

# iOS
**/ios/**/*.mode1v3
**/ios/**/*.mode2v3
**/ios/**/*.moved-aside
**/ios/**/*.pbxuser
**/ios/**/*.perspectivev3
**/ios/**/*sync/
**/ios/**/.sconsign.dblite
**/ios/**/.tags*
**/ios/**/.vagrant/
**/ios/**/DerivedData/
**/ios/**/Icon?
**/ios/**/Pods/
**/ios/**/.symlinks/
**/ios/**/profile
**/ios/**/xcuserdata
**/ios/.generated/
**/ios/Flutter/App.framework
**/ios/Flutter/Flutter.framework
**/ios/Flutter/Flutter.podspec
**/ios/Flutter/Generated.xcconfig
**/ios/Flutter/ephemeral/
**/ios/Flutter/app.flx
**/ios/Flutter/app.zip
**/ios/Flutter/flutter_assets/
**/ios/Flutter/flutter_export_environment.sh
**/ios/ServiceDefinitions.json
**/ios/Runner/GeneratedPluginRegistrant.*

# Web
lib/generated_plugin_registrant.dart

# Symbolication
app.*.symbols

# Obfuscation
app.*.map.json

# Environment
.env
.env.local
.env.production
```

## 9. README.md - Project documentation

```markdown
# My Awesome App

A Flutter application for [purpose].

## Getting Started

### Prerequisites
- Flutter SDK: >=3.10.0
- Dart SDK: >=3.0.0

### Installation
```bash
flutter pub get
```

### Run
```bash
flutter run
```

### Build
```bash
# Android
flutter build apk --release

# iOS
flutter build ios --release

# Web
flutter build web --release
```

### Testing
```bash
flutter test
```

## Project Structure
```
lib/
├── main.dart
├── features/
│   ├── auth/
│   ├── home/
│   └── profile/
├── core/
│   ├── constants/
│   ├── utils/
│   └── widgets/
└── data/
    ├── models/
    ├── repositories/
    └── services/
```

## Dependencies
- flutter_bloc: State management
- dio: HTTP client
- hive: Local storage

## License
MIT
```

## 10. Các lệnh pubspec thường dùng

```bash
# Cài dependencies
flutter pub get

# Upgrade dependencies
flutter pub upgrade

# Upgrade dependency cụ thể
flutter pub upgrade http

# Xem dependencies tree
flutter pub deps

# Xem outdated packages
flutter pub outdated

# Validate pubspec.yaml
flutter pub publish --dry-run

# Clean cache
flutter pub cache clean

# Repair cache
flutter pub cache repair
```

## Best Practices

1. **Version constraints**: Dùng caret (^) cho flexibility
2. **Lock file**: Commit `pubspec.lock` để đảm bảo consistent builds
3. **Assets organization**: Tổ chức assets theo folder rõ ràng
4. **Dev dependencies**: Tách riêng dev tools
5. **Documentation**: Comment các dependencies quan trọng
6. **Regular updates**: Thường xuyên update dependencies
7. **Security**: Không commit sensitive data trong config files
8. **Git ignore**: Ignore generated files và build artifacts

## Troubleshooting

```bash
# Lỗi dependencies conflict
flutter pub upgrade --major-versions

# Lỗi cache
flutter pub cache repair
flutter clean
flutter pub get

# Lỗi version
flutter pub outdated
# Sau đó update version trong pubspec.yaml

# Lỗi assets không load
flutter clean
flutter pub get
# Kiểm tra đường dẫn trong pubspec.yaml
```
