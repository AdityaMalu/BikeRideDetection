# 🚴 BikeRideDetection

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-30%2B-brightgreen.svg)](https://android-arsenal.com/api?level=30)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Code Quality](https://img.shields.io/badge/Code%20Quality-detekt%20%7C%20ktlint-blue.svg)](https://detekt.dev)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

A safety-focused Android application that **automatically detects cycling activity** and protects riders from phone distractions by blocking incoming calls and sending auto-reply SMS messages.

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Technical Stack](#-technical-stack)
- [Project Structure](#-project-structure)
- [Key Components](#-key-components)
- [Setup Instructions](#-setup-instructions)
- [Permissions](#-permissions)
- [User Workflow](#-user-workflow)
- [Background Services](#-background-services)
- [Code Quality](#-code-quality)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [Known Issues](#-known-issues)
- [License](#-license)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🚲 **Automatic Detection** | Uses Google Activity Recognition API to detect when you start/stop cycling |
| 📵 **Call Blocking** | Automatically rejects incoming calls while bike mode is active |
| 💬 **Auto-Reply SMS** | Sends customizable message to callers: *"I'm riding my bike right now."* |
| 🔔 **Persistent Notification** | Shows actionable notification to quickly disable bike mode |
| 🎛️ **Manual Toggle** | Override automatic detection with manual on/off switch |
| 📋 **Call History** | View missed calls during bike mode with visual distinction for new entries |

### 📋 Call History Visual Distinction

The Call History screen provides clear visual indicators to distinguish between **new (unviewed)** and **previously seen (viewed)** call entries:

| Visual Indicator | Unviewed Entry | Viewed Entry |
|------------------|----------------|--------------|
| **Background** | Primary container color | Surface variant (subdued) |
| **Border** | 2dp primary color border | No border |
| **Indicator Dot** | Primary color dot on icon | None |
| **Phone Number** | **Bold** text | Normal weight |
| **"NEW" Badge** | Primary color badge | None |
| **Icon Tint** | Primary color | Surface variant |

#### Entry Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. Call Rejected During Bike Mode                                │
│    └─▶ Entry saved with isViewed = false                        │
│    └─▶ Displays with all visual indicators (NEW badge, border)  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. User Opens Call History Screen                                │
│    └─▶ Unviewed entries display prominently                     │
│    └─▶ User can identify new missed calls at a glance           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. User Navigates Away from Call History                         │
│    └─▶ All entries marked as viewed (isViewed = true)           │
│    └─▶ viewedAt timestamp recorded                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. Next Visit to Call History                                    │
│    └─▶ Previously seen entries appear in subdued style          │
│    └─▶ Only new entries since last visit show indicators        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏗 Architecture

This application follows **Clean Architecture** principles with the **MVVM** pattern, ensuring separation of concerns, testability, and maintainability.

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                          UI Layer                                │
│  ┌─────────────────────┐    ┌─────────────────────────────────┐ │
│  │    MainActivity     │───▶│         MainViewModel           │ │
│  │   (ViewBinding)     │    │   (StateFlow, viewModelScope)   │ │
│  └─────────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Domain Layer                              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                       Use Cases                             │ │
│  │  • ObserveBikeModeUseCase    • GetBikeModeUseCase          │ │
│  │  • SetBikeModeEnabledUseCase • ToggleBikeModeUseCase       │ │
│  │  • SendAutoReplyUseCase                                     │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Models: BikeMode, SmsResult, CallInfo                      │ │
│  │  Repositories (interfaces): BikeModeRepository, SmsRepository│
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Data Layer                               │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  BikeModeRepositoryImpl ──▶ BikeModeDataStore (DataStore)  │ │
│  │  SmsRepositoryImpl ──▶ SmsManager                          │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### Design Decisions

| Decision | Rationale |
|----------|-----------|
| **MVVM Pattern** | Clear separation between UI and business logic |
| **StateFlow** | Reactive, lifecycle-aware state management (preferred over LiveData) |
| **Hilt DI** | Compile-time dependency injection with Android lifecycle awareness |
| **DataStore** | Type-safe, async preferences storage (replaces SharedPreferences) |
| **Clean Architecture** | Testable layers with clear dependency rules |
| **Coroutines** | Structured concurrency with proper scope management |

---

## 🛠 Technical Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9+ |
| **Min SDK** | 30 (Android 11) |
| **Target SDK** | 36 |
| **Build System** | Gradle with Version Catalogs |
| **DI Framework** | Hilt |
| **Async** | Kotlin Coroutines + Flow |
| **State Management** | StateFlow |
| **Preferences** | Jetpack DataStore |
| **Activity Detection** | Google Play Services Activity Recognition API |
| **Call Screening** | Android CallScreeningService API |
| **Logging** | Timber |
| **Testing** | JUnit, MockK, Turbine, Espresso |

---

## 📁 Project Structure

```
app/src/main/java/com/example/bikeridedetection/
├── BikeRideDetectionApp.kt          # Application class (Hilt entry point)
│
├── data/
│   ├── datasource/
│   │   └── BikeModeDataStore.kt     # DataStore preferences
│   └── repository/
│       ├── BikeModeRepositoryImpl.kt
│       └── SmsRepositoryImpl.kt
│
├── di/
│   ├── AppModule.kt                 # Hilt providers
│   └── RepositoryModule.kt          # Repository bindings
│
├── domain/
│   ├── model/
│   │   ├── BikeMode.kt              # Core domain model
│   │   ├── SmsResult.kt             # Sealed class for SMS results
│   │   ├── CallInfo.kt              # Call information model
│   │   └── Result.kt                # Generic result wrapper
│   ├── repository/
│   │   ├── BikeModeRepository.kt    # Repository interface
│   │   └── SmsRepository.kt
│   └── usecase/
│       ├── GetBikeModeUseCase.kt
│       ├── ObserveBikeModeUseCase.kt
│       ├── SendAutoReplyUseCase.kt
│       ├── SetBikeModeEnabledUseCase.kt
│       └── ToggleBikeModeUseCase.kt
│
├── service/
│   ├── BikeCallScreeningService.kt  # Call screening implementation
│   ├── BikeDetectionService.kt      # Activity recognition service
│   ├── BikeTransitionReceiver.kt    # Broadcast receiver for transitions
│   └── NotificationService.kt       # Foreground notification service
│
├── ui/
│   ├── activity/
│   │   └── MainActivity.kt
│   └── viewmodel/
│       └── MainViewModel.kt
│
└── util/
    └── PermissionHelper.kt          # Permission request utilities
```


---

## 🔧 Key Components

### Services

| Service | Type | Purpose |
|---------|------|---------|
| `BikeDetectionService` | Foreground (location) | Registers for Activity Recognition updates to detect cycling activity |
| `NotificationService` | Foreground (dataSync) | Shows persistent notification when bike mode is active |
| `BikeCallScreeningService` | System-bound | Screens incoming calls and rejects them when bike mode is enabled |

### Broadcast Receiver

| Receiver | Purpose |
|----------|---------|
| `BikeTransitionReceiver` | Receives activity transition events from Google Play Services and updates bike mode state |

### Use Cases

| Use Case | Description |
|----------|-------------|
| `ObserveBikeModeUseCase` | Returns a Flow of BikeMode state changes |
| `GetBikeModeUseCase` | Gets current BikeMode state (suspend function) |
| `SetBikeModeEnabledUseCase` | Enables or disables bike mode |
| `ToggleBikeModeUseCase` | Toggles current bike mode state |
| `SendAutoReplyUseCase` | Sends auto-reply SMS to rejected callers |

### Domain Models

```kotlin
// Core state model
data class BikeMode(
    val isEnabled: Boolean = false,
    val autoReplyMessage: String = "I'm riding my bike right now."
)

// SMS operation result
sealed class SmsResult {
    data class Sent(val phoneNumber: String) : SmsResult()
    data class Failed(val phoneNumber: String, val error: Throwable) : SmsResult()
    data object InvalidNumber : SmsResult()
}
```

---

## 🚀 Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 36
- Google Play Services installed on device/emulator

### Build Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/AdityaMalu/BikeRideDetection.git
   cd BikeRideDetection
   ```

2. **Open in Android Studio**
   - File → Open → Select the project directory

3. **Sync Gradle**
   - Android Studio will automatically sync dependencies
   - Or manually: File → Sync Project with Gradle Files

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run on device**
   ```bash
   ./gradlew installDebug
   ```

### Build Variants

| Variant | Description |
|---------|-------------|
| `debug` | Development build with logging enabled |
| `release` | Production build with ProGuard/R8 minification |

---

## 🔐 Permissions

### Required Permissions

| Permission | Purpose | Category |
|------------|---------|----------|
| `ACTIVITY_RECOGNITION` | Detect cycling activity via Activity Recognition API | **Core** |
| `ACCESS_FINE_LOCATION` | Required for Activity Recognition to function | **Core** |
| `ACCESS_COARSE_LOCATION` | Fallback location access | **Core** |
| `ACCESS_BACKGROUND_LOCATION` | Detect activity when app is in background | **Core** |
| `FOREGROUND_SERVICE` | Run detection service in foreground | **Core** |
| `FOREGROUND_SERVICE_LOCATION` | Location-type foreground service | **Core** |
| `FOREGROUND_SERVICE_DATA_SYNC` | Data sync foreground service type | **Core** |
| `POST_NOTIFICATIONS` | Show notifications (Android 13+) | **UX** |
| `SEND_SMS` | Send auto-reply messages to callers | **Feature** |
| `READ_PHONE_STATE` | Access incoming call information | **Feature** |
| `READ_CALL_LOG` | Read caller details | **Feature** |
| `READ_CONTACTS` | Match caller to contacts | **Feature** |
| `CALL_SCREENING` | Screen and reject incoming calls | **Feature** |

### Special Role

| Role | Purpose |
|------|---------|
| `ROLE_CALL_SCREENING` | Required on Android 10+ to intercept and reject calls |

---

## 👤 User Workflow

### First-Time Setup

```
1. Launch App
   └─▶ MainActivity opens

2. Permission Requests (in sequence)
   ├─▶ Location permissions
   ├─▶ Notification permission (Android 13+)
   ├─▶ SMS permission
   ├─▶ Phone/Contacts permissions
   └─▶ Call Screening Role

3. Grant All Permissions
   └─▶ BikeDetectionService starts monitoring
```

### Normal Usage Flow

```
┌──────────────────────────────────────────────────────────────┐
│ 1. Start Cycling                                              │
│    └─▶ Activity Recognition detects ON_BICYCLE               │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. Bike Mode Auto-Enables                                     │
│    └─▶ BikeTransitionReceiver updates repository             │
│    └─▶ NotificationService shows "Bike Mode Active"          │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. Incoming Call Arrives                                      │
│    └─▶ BikeCallScreeningService intercepts                   │
│    └─▶ Call is REJECTED                                      │
│    └─▶ Auto-reply SMS sent to caller                         │
│    └─▶ Call saved to history (isViewed = false)              │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. Stop Cycling                                               │
│    └─▶ Activity Recognition detects EXIT                     │
│    └─▶ Bike Mode auto-disables                               │
│    └─▶ Calls allowed through normally                        │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. Review Missed Calls                                        │
│    └─▶ Open Call History from menu                           │
│    └─▶ New entries show with visual indicators               │
│    └─▶ Navigate away → entries marked as viewed              │
└──────────────────────────────────────────────────────────────┘
```

### Manual Control

- **Toggle Switch**: Use the switch in MainActivity to manually enable/disable
- **Notification Tap**: Tap the persistent notification to quickly disable bike mode

### Reviewing Call History

1. **Access**: Tap the menu icon (⋮) in the top-right corner and select "Call History"
2. **Identify New Calls**: Unviewed entries display with:
   - A colored border around the card
   - A "NEW" badge next to the phone number
   - Bold phone number text
   - A colored indicator dot on the contact icon
3. **Mark as Viewed**: Simply navigate away from the Call History screen - all entries are automatically marked as viewed
4. **Retention**: Viewed entries are automatically deleted after 24 hours to keep the list manageable


---

## ⚙️ Background Services

### Service Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    BikeDetectionService                          │
│  • Type: FOREGROUND (location)                                  │
│  • Lifecycle: Started when location permissions granted          │
│  • Function: Registers for ON_BICYCLE activity transitions       │
│  • Notification: Low-priority, ongoing                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ Activity Transition Events
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   BikeTransitionReceiver                         │
│  • Type: BroadcastReceiver                                      │
│  • Trigger: ACTIVITY_TRANSITION_ENTER / EXIT                    │
│  • Action: Updates BikeModeRepository                           │
│  • Broadcasts: BIKE_MODE_CHANGED to other components            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ BikeMode State Change
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    NotificationService                           │
│  • Type: FOREGROUND (dataSync)                                  │
│  • Lifecycle: Started when bike mode ENABLED                    │
│  • Lifecycle: Stopped when bike mode DISABLED                   │
│  • Notification: Actionable, tap to disable                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                  BikeCallScreeningService                        │
│  • Type: System-bound (BIND_SCREENING_SERVICE)                  │
│  • Lifecycle: Managed by Android system                         │
│  • Trigger: Every incoming call when app holds ROLE_CALL_SCREENING│
│  • Action: Checks bike mode, rejects call + sends SMS if enabled │
└─────────────────────────────────────────────────────────────────┘
```

### Activity Recognition Flow

```kotlin
// BikeDetectionService registers for these transitions:
val transitions = listOf(
    ActivityTransition.Builder()
        .setActivityType(DetectedActivity.ON_BICYCLE)
        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
        .build(),
    ActivityTransition.Builder()
        .setActivityType(DetectedActivity.ON_BICYCLE)
        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        .build()
)
```

---

## 🔍 Code Quality

This project enforces strict code quality standards using **detekt** and **ktlint** static analysis tools.

### Static Analysis Tools

| Tool | Purpose | Configuration |
|------|---------|---------------|
| **detekt** | Static code analysis for Kotlin | `detekt.yml` |
| **ktlint** | Kotlin linter and formatter | Default rules |

### Running Code Quality Checks

```bash
# Run detekt analysis
./gradlew detekt

# Run ktlint check
./gradlew ktlintCheck

# Auto-format with ktlint
./gradlew ktlintFormat

# Run all checks (recommended before PR)
./gradlew ktlintCheck detekt test
```

### Code Quality Standards

| Rule | Threshold | Rationale |
|------|-----------|-----------|
| **Max Function Length** | 60 lines | Ensures functions remain focused and testable |
| **Max File Length** | 600 lines | Encourages proper separation of concerns |
| **Magic Numbers** | Not allowed | All numeric literals must be named constants |
| **Cyclomatic Complexity** | ≤15 | Keeps code paths manageable |

### Recent Code Quality Improvements (v2)

The following refactoring was performed to resolve detekt violations:

#### PermissionManager.kt
- **Issue**: `LongMethod` - `getPermissionSteps()` exceeded 60 lines (was 62 lines)
- **Solution**: Extracted into smaller, focused helper methods:
  - `createLocationPermissionStep()` - Creates location permission step
  - `addNotificationPermissionStepIfNeeded()` - Conditionally adds notification permission (Android 13+)
  - `createSmsPermissionStep()` - Creates SMS permission step
  - `createPhoneContactsPermissionStep()` - Creates phone/contacts permission step
  - `addCallScreeningRoleIfNeeded()` - Conditionally adds call screening role (Android 10+)

#### BikeModeWidgetProvider.kt
- **Issue 1**: `MagicNumber` - Hardcoded delay value `100` in animation code
- **Solution**: Extracted to named constant `TEXT_UPDATE_DELAY_MS = 100L`

- **Issue 2**: `LongMethod` - `updateWidget()` exceeded 60 lines (was 92 lines)
- **Solution**: Extracted into focused helper methods:
  - `updateWidgetVisuals()` - Updates background, icon tint, and status text
  - `updateWidgetToggle()` - Updates toggle switch track, thumb, and position
  - `setupWidgetClickListeners()` - Configures click intents for toggle and container

#### WidgetAnimationHelper.kt
- **Issue**: `MagicNumber` - Hardcoded divisor `3` in delay calculation
- **Solution**: Extracted to named constant `COLOR_CHANGE_DELAY_DIVISOR = 3`

### Benefits of These Refactorings

| Improvement | Benefit |
|-------------|---------|
| **Smaller functions** | Easier to test, understand, and maintain |
| **Named constants** | Self-documenting code, single source of truth |
| **Single responsibility** | Each method does one thing well |
| **Reduced cognitive load** | Developers can focus on smaller units of code |

---

## 🧪 Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# All tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Test Structure

```
app/src/test/                    # Unit tests
├── domain/usecase/
│   └── SendAutoReplyUseCaseTest.kt
└── ...

app/src/androidTest/             # Instrumentation tests
├── HiltTestRunner.kt
└── ...
```

### Testing Tools

| Tool | Purpose |
|------|---------|
| **JUnit** | Unit test framework |
| **MockK** | Kotlin mocking library |
| **Turbine** | Flow testing |
| **Espresso** | UI testing |
| **Hilt Testing** | DI in tests |

---

## 🤝 Contributing

### Branch Naming Convention

```
feature/   - New features (e.g., feature/custom-auto-reply)
bugfix/    - Bug fixes (e.g., bugfix/sms-not-sending)
refactor/  - Code refactoring (e.g., refactor/repository-layer)
docs/      - Documentation updates (e.g., docs/update-readme)
```

### Pull Request Guidelines

1. **Create a feature branch** from `main`
2. **Write/update tests** for your changes
3. **Ensure all tests pass**: `./gradlew test`
4. **Follow code style**: Run `./gradlew ktlintCheck detekt`
5. **Update documentation** if needed
6. **Create PR** with clear description
7. **Include screenshots** for UI changes

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `ktlint` for formatting and `detekt` for static analysis
- Maximum function length: 60 lines (enforced by detekt)
- No magic numbers - use named constants
- All public functions must have KDoc

---

## ⚠️ Known Issues

| Issue | Status | Workaround |
|-------|--------|------------|
| Activity Recognition may have delay on some devices | Known | Use manual toggle for immediate control |
| SMS may fail if carrier blocks automated messages | Known | Verify SMS permissions and carrier settings |

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Support

For issues and feature requests, please [open an issue](https://github.com/AdityaMalu/BikeRideDetection/issues) on GitHub.

---

<p align="center">
  Made with ❤️ for cyclist safety
</p>
